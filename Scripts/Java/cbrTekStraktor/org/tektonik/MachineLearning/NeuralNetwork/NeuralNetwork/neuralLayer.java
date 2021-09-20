package org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork;

import java.util.Random;

import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;
import org.tektonik.tools.generalStatPurpose.gpRandom;
import org.tektonik.tools.linearAlgebra.cmcMatrix;
import org.tektonik.tools.linearAlgebra.cmcVector;
import org.tektonik.tools.linearAlgebra.cmcVectorRoutines;


public class neuralLayer {
	
	private double DropOutRatio = 0;
	
	private double GAMMA   = (double)0.9;
	private double BETA1   = (double)0.9;     // See Intro to computer vision and NN
	private double BETA2   = (double)0.999;   // Google research
	private double EPSILON = (double)0.00000001;
	//private double MIN_ACCELERATION_LEVEL = (double)0.0001;
	
	private cmcVectorRoutines vrout = null;
	private Random rnd = new Random();
	
	private static int ARRAY_TOSTRING_DUMP_LINES = 200;
	private static int ARRAY_TOSTRING_DUMP_WIDTH = 5;
	//
	private int UID = -1;
	private int NbrOfNeuronsInLayer = -1;
	private neuron[] neurons = null;
	//
	private cmcMLPenums.LAYER_TYPE tipe = cmcMLPenums.LAYER_TYPE.UNKNOWN;
	private cmcMLPenums.ACTIVATION_FUNCTION_TYPE activationFunctionTipe = cmcMLPenums.ACTIVATION_FUNCTION_TYPE.UNKNOWN;
	private cmcMLPenums.OPTIMIZATION_TYPE optimizationTipe = cmcMLPenums.OPTIMIZATION_TYPE.NONE;
	private cmcMLPenums.WEIGHT_INITIALIZATION_TYPE weightStrategy = cmcMLPenums.WEIGHT_INITIALIZATION_TYPE.XAVIER;
	private double AFactor = Double.NaN;
	private int NbrOfInputs =-1;
	private int NbrOfMiniBatchSamples = -1;   // size of the minibatch
	private neuralLayer previousLayer = null;
	private neuralLayer nextLayer = null;
	private double[][] inputValues = null;   // kan je pas zetten als je vorige layer kent
	private double[][] outputValues = null;
	private String ErrMsg = "";
	private double LayerLearningRate = 0;
	private double PreviousLayerLearningRate = 0;
	private int LearningStalled=0;
	//
	private cmcVector[] Inputs  = null;
	private cmcVector[] Outputs = null;
	private cmcVector[] Nets    = null;   // w1x1 + w2x2 etc
	private cmcMatrix[] Weights = null;
	private cmcMatrix[] Gradients = null;
	private cmcVector[] PartialDerivativeLosses  = null;
	private cmcVector[] ActivationDerivatives  = null;
	private cmcVector[] Targets = null;  // enkel op output
	private cmcMatrix[] PartialDerivativeSoftmax = null;
	private cmcMatrix   ConsolidatedGradients = null;  // neem gewoon eerste 
	private cmcMatrix   ConsolidatedPreviousGradients = null; // moet geinstantieerd
	private cmcMatrix   PreviousVT; // moet geinstantieerd
	private cmcMatrix   PreviousMT; // moet geinstantieerd
	
	//
	
	
	//-----------------------------------------------------------------------
	public void do_error(String s)
	//-----------------------------------------------------------------------
	{
		ErrMsg += "(" + this.tipe +") [" + s + "]";
		System.err.println( s );
	}
	
	//-----------------------------------------------------------------------
	public neuralLayer(int uid , int ineurons , int isamples )
	{
		tipe = cmcMLPenums.LAYER_TYPE.UNKNOWN;
		UID = uid;
		NbrOfNeuronsInLayer = ineurons;
		NbrOfMiniBatchSamples = isamples;
		vrout = new cmcVectorRoutines();
	}
	
	
	//-----------------------------------------------------------------------
	private void resetOutputs(int nsamples)
	{
		Outputs = new cmcVector[ nsamples ];
		for(int i=0;i< Outputs.length; i++)
		{
			double[] vals = new double[this.getNbrOfNeuronsInLayer()];
			for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
			Outputs[ i ] = new cmcVector( vals );
		}
	}
	//-----------------------------------------------------------------------
	private void resetInputs(int nsamples)
	{
	  Inputs = new cmcVector[ nsamples ];
  	  for(int i=0;i< Inputs.length; i++)
  	  {
  		double[] vals = new double[this.getNbrOfInputs()];
  		for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
  		Inputs[ i ] = new cmcVector( vals );
  	  }
	}
	//-----------------------------------------------------------------------
	private void resetTargets(int nsamples)
	{
		 Targets = new cmcVector[ nsamples ];
		 for(int i=0;i<Targets.length;i++)
		 {
			 double[] vals = new double[ this.getNbrOfNeuronsInLayer() ];
			 for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
			 Targets[i] = new cmcVector(vals);
		 }	
	}
	private void resetNets(int nsamples)
	{
		Nets = new cmcVector[ nsamples ];
		for(int i=0;i<Nets.length; i++)
		{
			double[] vals = new double[this.getNbrOfNeuronsInLayer()];
			for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
			Nets[ i ] = new cmcVector( vals );
		}
	}
	private void resetWeights(int nsamples)
	{
	   double[][] zz = new double[ Weights[0].getNbrOfRows() ][Weights[0].getNbrOfColumns() ];  // all weightg shoudl be same at the end of a run
	   for(int i=0;i<zz.length;i++)
	   {
		   for(int j=0;j<zz[i].length;j++) zz[i][j] = Weights[0].getValues()[i][j];
	   }
	   Weights = new cmcMatrix[ nsamples ];
	   for(int i=0;i<Weights.length;i++) Weights[i] = new cmcMatrix( zz );
	   //System.err.println( vrout.printMatrix( Weights[0] ) );
	}
	//-----------------------------------------------------------------------
	public void resetInputOutputTargets(int nsamples )
	{
		resetOutputs( nsamples );
		if( this.getTipe() != cmcMLPenums.LAYER_TYPE.INPUT  ) {
			resetInputs( nsamples );
			resetNets( nsamples );
			resetWeights( nsamples );
		}
		if( this.getTipe() == cmcMLPenums.LAYER_TYPE.OUTPUT ) resetTargets( nsamples );
	}
	
	
	// MATRIX =   rows is number of neurons (features) and cols is number of records in the sample
	//-----------------------------------------------------------------------
	public boolean initializeInputOutputAndNeuronsFast()
	{
			// Input
			switch( this.getTipe() )
			{
			 case INPUT  : { NbrOfInputs = this.getNbrOfNeuronsInLayer(); break; }
			 case HIDDEN : ;
			 case LOSS   : ;
			 case OUTPUT : {
				neuralLayer lay = this.getPreviousLayer();
				if( lay == null ) {
					System.err.println( "neuralLayer - initialize - Previous layer not set for " + this.getTipe() + " " + this.getUID() );
					return false;
				}
				NbrOfInputs = lay.getNbrOfNeuronsInLayer();
				break;
			 }
			 default : {
				System.err.println("neuralLayer - initialize - Tipe not set for "  + this.getUID());
				return false;
			 }
			}
			
			// initialize neurons and their weights
			neurons = new neuron[ this.getNbrOfNeuronsInLayer() ];
			for(int i=0;i<this.getNbrOfNeuronsInLayer();i++)
			{
			   neurons[i] = new neuron(  this , (this.UID * 100000) + i , 	this.UID , i , this.getActivationFunctionTipe() , this.getNbrOfInputs() ,  this.getAFactor() );
			   // last neuron is the neuron that feeds the bias 
			   // the BIAS will not be set the same for all conntected target neurons, but be maintained per neuron
		       if( i == (this.getNbrOfNeuronsInLayer()-1) ) {
		    	   if( (this.tipe == cmcMLPenums.LAYER_TYPE.INPUT) || (this.tipe == cmcMLPenums.LAYER_TYPE.HIDDEN) ) {
		    		   neurons[i].setTipe( cmcMLPenums.NEURON_TYPE.ACTIVATION_ONLY );
		    		   neurons[i].setActivationFunctionTipe(cmcMLPenums.ACTIVATION_FUNCTION_TYPE.CONSTANT_ONE);
		    		   //neurons[i].setWeights(null);
		    	   }
		       }
		       // a INPUT neuron only needs the output values and no input and weights
		       //if( this.getTipe() == cmcMLPenums.LAYER_TYPE.INPUT ) neurons[i].setWeights(null);
		       
		   	}
			
			// FAST
			resetOutputs( this.getNbrOfMiniBatchSamples() );
			/*
			Outputs = new cmcVector[ this.getNbrOfMiniBatchSamples() ];
			for(int i=0;i< Outputs.length; i++)
			{
				double[] vals = new double[this.getNbrOfNeuronsInLayer()];
				for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
				Outputs[ i ] = new cmcVector( vals );
			}
			*/
			
			 // FAST
	         if( this.getTipe() != cmcMLPenums.LAYER_TYPE.INPUT ) {
	          // Input
	          resetInputs( this.getNbrOfMiniBatchSamples() );
	          /*
	          Inputs = new cmcVector[ this.getNbrOfMiniBatchSamples() ];
	    	  for(int i=0;i< Inputs.length; i++)
	    	  {
	    		double[] vals = new double[this.getNbrOfInputs()];
	    		for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
	    		Inputs[ i ] = new cmcVector( vals );
	    	  }
	    	  */
	    	  // weights - gewoon op NAN zetten van alles
	    	  // op de INPUT en HIDDEN is de laatste neuron een PLACEHOLDER, daar moeten de weights altijd ZERO zijn
	    	  // bij de init weights en update wordt dit gechecked.
	    	  // Gradients en PreviousGradients
	          Weights = new cmcMatrix[ this.getNbrOfMiniBatchSamples() ];
	          Gradients = new cmcMatrix[ Weights.length ];
	          for(int i=0;i<Weights.length; i++)
			  {
	        	double[][] w = new double[ this.getNbrOfNeuronsInLayer() ][ this.getNbrOfInputs() ];  // #neurons - #inputs
	        	double[][] zero = new double[ this.getNbrOfNeuronsInLayer() ][ this.getNbrOfInputs() ];  // #neurons - #inputs
	      	 	for(int j=0;j<w.length;j++)
	        	{
	        	 for(int k=0;k<w[j].length;k++) { w[j][k] = Double.NaN;  zero[j][k] = 0; }
	        	}
	    	    Weights[ i ] = new cmcMatrix( w );
			    Gradients[i] = new cmcMatrix( zero ); // op ZERO zetten
			    if( i == 0 ) {
			    	ConsolidatedGradients = new cmcMatrix( zero );
			    	ConsolidatedPreviousGradients = new cmcMatrix( zero ); // CAUTION - must be a separate object
			    	PreviousVT = new cmcMatrix( zero ); // zero
			    	PreviousMT = new cmcMatrix( zero ); // zero
			    }
			  }
		    }
			
			// FAST - nets
	        resetNets( this.getNbrOfMiniBatchSamples() );
	        /*
			Nets = new cmcVector[ this.getNbrOfMiniBatchSamples() ];
			for(int i=0;i<Nets.length; i++)
			{
				double[] vals = new double[this.getNbrOfNeuronsInLayer()];
				for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
				Nets[ i ] = new cmcVector( vals );
			}
			*/
			// FAST
			ActivationDerivatives = new cmcVector[ this.getNbrOfMiniBatchSamples() ];
			for(int i=0;i<ActivationDerivatives.length; i++)
			{
				double[] vals = new double[this.getNbrOfNeuronsInLayer()];
				for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
				ActivationDerivatives[ i ] = new cmcVector( vals );
			}
			// FAST - errors/losses
			PartialDerivativeLosses = new cmcVector[ this.getNbrOfMiniBatchSamples() ];
			for(int i=0;i<PartialDerivativeLosses.length; i++)
			{
				double[] vals = new double[this.getNbrOfNeuronsInLayer()];
				for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
				PartialDerivativeLosses[ i ] = new cmcVector( vals );
			}
			
			//
			if( this.getTipe() == cmcMLPenums.LAYER_TYPE.OUTPUT ) {
				
				 resetTargets( this.getNbrOfMiniBatchSamples() );
				 /*
				 Targets = new cmcVector[ this.getNbrOfMiniBatchSamples() ];
				 for(int i=0;i<Targets.length;i++)
				 {
					 double[] vals = new double[ this.getNbrOfNeuronsInLayer() ];
					 for(int j=0;j<vals.length;j++) vals[j] = Double.NaN;
					 Targets[i] = new cmcVector(vals);
				 }	
				 */
				 
				 // Softmax
				 if( neurons[0].getActivationFunctionTipe() == cmcMLPenums.ACTIVATION_FUNCTION_TYPE.SOFTMAX )
				 {
					 ActivationDerivatives = null;
					 PartialDerivativeSoftmax = new cmcMatrix[ this.getNbrOfMiniBatchSamples() ];
					 double[][] vals = new double[ this.NbrOfNeuronsInLayer ][ this.NbrOfNeuronsInLayer ];
					 for(int i=0;i<vals.length;i++)
					 {
						 for(int j=0;j<vals[i].length;j++) vals[i][j] = Double.NaN;
					 }
					 for(int i=0;i<PartialDerivativeSoftmax.length;i++)
					 {
						 PartialDerivativeSoftmax[i] = new cmcMatrix( vals );
					 }
				 }
				 else PartialDerivativeSoftmax = null;
			}
			
			return true;
	}
	
	//  Number of neurons x Nbr of inputs previous layer (= nbr of neurons previosu layer)
	//-----------------------------------------------------------------------
	private int getNbrOfFansIns()
	{
		try {
		  int NbrNeuronsToInit = this.getNbrOfNeuronsInLayer();
		  neuron last = neurons[ neurons.length - 1 ];
		  if( last.getTipe() == cmcMLPenums.NEURON_TYPE.ACTIVATION_ONLY ) NbrNeuronsToInit--;
		  int NbrInputs = -1;
		  neuralLayer previouslayer = this.getPreviousLayer();
		  if( (previouslayer == null) && ( this.getTipe() == cmcMLPenums.LAYER_TYPE.INPUT) ) NbrInputs = this.getNbrOfNeuronsInLayer();
		       else NbrInputs = previouslayer.getNbrOfNeuronsInLayer();
		  return NbrNeuronsToInit * NbrInputs;
		}
		catch(Exception e ) {
			do_error("getNbrOfFanIns - " + e.getMessage() );
			return -1;
		}
	}
    //  Number of neurons x Nbr of neurons next layer
	//-----------------------------------------------------------------------
	private int getNbrOfFansOuts()
	{
		try {
		  int NbrNeuronsToInit = this.getNbrOfNeuronsInLayer();
		  neuron last = neurons[ neurons.length - 1 ];
		  if( last.getTipe() == cmcMLPenums.NEURON_TYPE.ACTIVATION_ONLY ) NbrNeuronsToInit--;
		  int NbrOutputs = -1;
		  neuralLayer nextlayer = this.getNextLayer();
		  if( (nextlayer == null) && ( this.getTipe() == cmcMLPenums.LAYER_TYPE.LOSS) ) NbrOutputs = this.getNbrOfNeuronsInLayer();
		       else NbrOutputs = nextlayer.getNbrOfNeuronsInLayer();
		  return NbrNeuronsToInit * NbrOutputs;
		 
		}
		catch(Exception e ) {
			do_error("getNbrOfFanOuts - " + e.getMessage() );
			return -1;
		}
	}
	//-----------------------------------------------------------------------
	public boolean initializeWeightsFast(cmcMLPenums.WEIGHT_INITIALIZATION_TYPE ws)
	{
	//System.out.println( "[Layer=" + this.getUID() + "] [" + this.getTipe() + "] [Strategy=" + weightStrategy + "]");
		try {
		  this.weightStrategy = ws;
		  if( neurons == null ) { do_error("fast - Null neurons"); return false; }
		  int NbrNeuronsToInit = this.getNbrOfNeuronsInLayer();
		  if( NbrNeuronsToInit != neurons.length ) { do_error("fast - Number of neurons mismatch"); return false; }
		  neuron last = neurons[ neurons.length - 1 ];
		  if( last.getTipe() == cmcMLPenums.NEURON_TYPE.ACTIVATION_ONLY ) NbrNeuronsToInit--;

		  // The weights on the LOSS layer are the IDENTITY Matrix
		  if( this.getTipe() == cmcMLPenums.LAYER_TYPE.LOSS ) { 
	    	  for(int j=0;j<NbrNeuronsToInit;j++)
	    	  {
	    		  for(int k=0;k<this.getNbrOfInputs();k++) {
	    			  double ww = (j==k) ? (double)1 : (double)0;
	    			  for(int i=0;i<this.getNbrOfMiniBatchSamples();i++) Weights[i].setValue( j , k , ww );
	    		  }
	    	  }
	    	  return true;
	      }

		  // create random weights for the entire layer
		  // the weights must be same for all samples
		  gpRandom gpr = new gpRandom();
		  int NbrOfFanIns = this.getNbrOfFansIns();
		  int NbrOfFanOuts = this.getNbrOfFansOuts();
		  if( NbrOfFanIns != (NbrNeuronsToInit * this.getNbrOfInputs()) ) {
			  do_error( "Fan-ins number mismatch " + NbrOfFanIns + " " + NbrNeuronsToInit + " " + this.getNbrOfInputs() );
			  return false;
		  }
		  double[] randomWeights = new double[ NbrOfFanIns ];
		  //for(int i=0;i< randomWeights.length;i++) randomWeights[i] = Double.NaN;
		  
		  // RELU should have a HE initialization, others no need to change
		  cmcMLPenums.WEIGHT_INITIALIZATION_TYPE  adjustedWeightStrategy = weightStrategy;
		  cmcMLPenums.ACTIVATION_FUNCTION_TYPE activationfunctiontipe = neurons[0].getActivationFunctionTipe();
		  if( activationfunctiontipe == null ) activationfunctiontipe = cmcMLPenums.ACTIVATION_FUNCTION_TYPE.UNKNOWN;
		  // UNKNOWN , IGNORE , CONSTANT_ONE , SIGMOID , HYPERBOLIC_TANGENT , TANH , RELU
		  switch( activationfunctiontipe )
		  {
		  case SOFTMAX : ;
		  case SIGMOID : ;
		  case TANH    : ;
		  case HYPERBOLIC_TANGENT : break;  // do not change
		  case RELU : { adjustedWeightStrategy = cmcMLPenums.WEIGHT_INITIALIZATION_TYPE.HE; break; }
		  case CONSTANT_ONE : ;
		  case IGNORE : ;
		  case UNKNOWN : ;
		  default : {
			  System.err.println("There never should be an activation of tipe " + activationfunctiontipe + " on first neuron of layer " + this.getUID());
			  return false;
		  }
		  //
		  }
		  switch( adjustedWeightStrategy )
		  {
		   case SIMPLE : { // random bewteen -05 and 0.5
			  for(int i=0;i< randomWeights.length;i++) randomWeights[i] = (((rnd.nextDouble() * (double)2) - (double)1)) / 2;  // -0.5 en 0.5 
              System.out.println( "[Layer=" + this.getUID() + "] [" + this.getTipe() + "] [SIMPLE-WEIGHT-INIT]");
 			  break;
		   }
		   case ALL_ONES : {
			   for(int i=0;i< randomWeights.length;i++) randomWeights[i] = (double)1; 
			   System.err.println("ALL ONES strategy - really?");
			   break;
		   }
		   case ALL_ZEROES : {
			   for(int i=0;i< randomWeights.length;i++) randomWeights[i] = (double)0; 
			   System.err.println("ALL ZEROES strategy - really?");
			   break;
		   }
		   case XAVIER : {
			   double XavierVariance = Math.sqrt((double)6) / Math.sqrt(NbrOfFanIns + NbrOfFanOuts);
			   System.out.println( "[Layer=" + this.getUID() + "] [" + this.getTipe() + "] [Requested Var=" + XavierVariance + "]");
			   randomWeights = gpr.getGaussianWithMeanAndVariance( NbrOfFanIns , 0 , XavierVariance );
			   if( randomWeights == null ) return false;
			   break;
		   }
		   case HE : {
			   double HeVariance = Math.sqrt((double)2) / Math.sqrt(NbrOfFanIns);
			   System.out.println( "[Layer=" + this.getUID() + "] [" + this.getTipe() + "] [Requested Var=" + HeVariance + "]");
			   randomWeights = gpr.getGaussianWithMeanAndVariance( NbrOfFanIns , 0 , HeVariance );
			   if( randomWeights == null ) return false;
			   // A HE distribution has the bias weights set to ZERO
               // NbrofNeurons x NbrOfInputs => so every % NbrOfInputs == laatste op nul
			   for(int i=0;i<randomWeights.length;i++)
			   {
				  if( (i % this.getNbrOfInputs()) == (this.getNbrOfInputs()-1) ) randomWeights[i] = 0;
			   }
			   break;
		   }
		   default : {
			   System.err.println("Unsupported weight initialization strategy " + adjustedWeightStrategy);
			   return false; 
		   }
		  }
		  
	      // If the last neuron is a placeholder/activation only (so a node that creates biasses) set all weight to ZERO
	      if( last.getTipe() == cmcMLPenums.NEURON_TYPE.ACTIVATION_ONLY ) {
	    		  for(int k=0;k<this.getNbrOfInputs();k++) {
	    			  for(int i=0;i<this.getNbrOfMiniBatchSamples();i++) Weights[i].setValue( NbrNeuronsToInit , k , 0 );
	    		  }
	      }
	      int counter = 0;
	      for(int j=0;j<NbrNeuronsToInit;j++)   // If the last one is ACTIVATRIONONLY NbrNeurs will have been decreased by 1, ie excluded
	      {
	    		  for(int k=0;k<this.getNbrOfInputs();k++) 
	    		  {
	    			double ww = randomWeights[ counter ]; counter++;
	    			for(int i=0;i<this.getNbrOfMiniBatchSamples();i++) Weights[i].setValue( j , k , ww );
	    		  }
	      }
	      System.out.println( "[Layer=" + this.getUID() + "] [" + this.getTipe() + "] [" + adjustedWeightStrategy + "] [Returned Var=" + gpr.getGeneratedVariance() + "]") ;
		  return true;
		}
		catch(Exception e ) {
			do_error( e.getMessage() +  "init weights FAST");
			return false;
		}
	}
	
	//-----------------------------------------------------------------------
	public boolean setInputsFast( int sample , int index , double dd )
	{
		try {
			Inputs[ sample ].setValue( index , dd );
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	//-----------------------------------------------------------------------
	public boolean setOutputsFast( int sample , int index , double dd )
	{
		try {
			Outputs[ sample ].setValue( index , dd );
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	//-----------------------------------------------------------------------
	private boolean transferInputValuesFast( neuralLayer fromLayer , neuralLayer toLayer)
	{
	    if( fromLayer == null ) { do_error("Null fromlayer"); return false; }
	    if( toLayer == null)  { do_error("Null tolayer"); return false; }
	    if( fromLayer.getOutputsFast() == null )  { do_error("Null Output vectors"); return false; }
	    if( toLayer.getInputsFast() == null )  { do_error("Null Input Vectors" + toLayer.getTipe() ); return false; }
	    if( fromLayer.getOutputsFast().length != toLayer.getInputsFast().length )  { 
	    	do_error("number of samples differs"); return false; 
		}
	    if( fromLayer.getOutputsFast()[0].getDimension() != toLayer.getInputsFast()[0].getDimension() )  {
	    	do_error( "[" + fromLayer.getUID() + "->" + toLayer.getUID() + "] output length " + 
	                   fromLayer.getOutputsFast()[0].getDimension() + " differs from inputlength " +   toLayer.getInputsFast()[0].getDimension()); 
	    	return false; 
	  	}
	    // faster
	    toLayer.Inputs = fromLayer.Outputs;
	    return true;
	}
	
	//-----------------------------------------------------------------------
	private boolean calculateOutputFast(boolean training)
	{
		lossNeuralLayer lola = null;
		double[][] targetarray = null;
		try {
		  if( this.getTipe() == cmcMLPenums.LAYER_TYPE.INPUT ) { // output op INPUT layer is geinitialiseerd/fixed
			  if( this.getOutputsFast() == null ) { System.err.println( "getoutputsfast is Null for input");  return false; }
			  return true;
		  }
		  // Indien LOSS layer dan wordt de activation de cost function
		  if( this.getTipe() == cmcMLPenums.LAYER_TYPE.LOSS ) { // COST functie ipv activatie runnen
			  lola = (lossNeuralLayer)this;
			  outputNeuralLayer oula = (outputNeuralLayer)lola.getPreviousLayer();
			  targetarray = oula.getTargetDump();
		     //System.err.println( "OULA" + oula.getTipe() + " target"+ targetarray );
		  }          	
		  // 
		  int NbrOfNeurons = this.getNbrOfNeuronsInLayer();
		  for(int i=0;i<this.getNbrOfMiniBatchSamples();i++)
		  {
			  //  NetVector = WeightMatrix x Inputvector
	          Nets[i] = MatrixVectorMult( Weights[i] , Inputs[i] );
	          //
      //System.err.println( "Net->" + Nets[i].getVectorValues()[0] );
      //System.err.println( "Inp->" + Inputs[i].getVectorValues()[0] );
      //System.err.println( "Wei->" + Weights[i].getValues()[0][0]);
              //	               
              if( Nets[i] == null )  { System.err.println( "Nets are null");  return false; }
              // Activation( Net )  of indien de losslayer Cost( target, net )
              if( this.getTipe() == cmcMLPenums.LAYER_TYPE.LOSS ) { // COST functie ipov activatie runnen
            	  for(int j=0;j<NbrOfNeurons;j++)
                  {
            	   double outval = lola.runCostFunction( targetarray[i][j], Nets[i].getVectorValues()[j] );
              	   double deriva = lola.runCostFunctionDerivative( targetarray[i][j], Nets[i].getVectorValues()[j] );
              	   if( (Double.isNaN( outval) || (Double.isNaN(deriva)) ) ) {
               	       System.err.println("NaN result on activation or derivative [" + outval + "] [" + deriva + "]");
               	       return false;
                   }
            	   Outputs[i].setValue( j , outval );
            	   if( training ) {
              	     ActivationDerivatives[i].setValue( j , deriva );
              	     PartialDerivativeLosses[i].setValue( j , deriva );
            	   }
                  }
              }
              else {
            	  // SOFTMAX - aangezien enkel op output moeten alle activaties zelfde (teznij een dropout - doch kan niet
            	  if( neurons[0].getActivationFunctionTipe() == cmcMLPenums.ACTIVATION_FUNCTION_TYPE.SOFTMAX ) {
            	
            		  double check = 0;  // moet 1 zijn
            		  double[][] derivArr = new double[ NbrOfNeurons ][ NbrOfNeurons ];
            		  for(int k=0;k<derivArr.length;k++)
            		  {
            			  for(int j=0;j<derivArr[k].length;j++) derivArr[k][j] = Double.NaN;
            		  }
            		  for(int j=0;j<NbrOfNeurons;j++)
                      {
                       double outval = neurons[j].runActivationFunction( j , Nets[i].getVectorValues() );
                       check += outval;
                       Outputs[i].setValue( j , outval );
                       //
                       for(int k=0;k<NbrOfNeurons;k++)
                       {
                    	 if( Double.isNaN(derivArr[k][j]) ) {
                    	   double deriva =  neurons[j].runActivationDerivative( j , k , Nets[i].getVectorValues() );
                    	   derivArr[j][k] = deriva;
                    	 }
                    	 else derivArr[j][k] = derivArr[k][j];
       	               }
                      }  		 
            		  if( training ) PartialDerivativeSoftmax[i].fillMatrix(derivArr);
            		  
  //System.err.println( "-->" + Outputs[i].showRound() + "-->" + check + vrout.printMatrix(this.PartialDerivativeSoftmax[i]));
    
            		  
            		  // TODO check op NAN en check !=1
            	  }
            	  else { // NON softmax
                    for(int j=0;j<NbrOfNeurons;j++)
                    {
                     double outval = neurons[j].runActivationFunction( Nets[i].getVectorValues()[j] );
                     double deriva = neurons[j].runActivationDerivative( Nets[i].getVectorValues()[j] );
                     if( (Double.isNaN( outval) || (Double.isNaN(deriva)) ) ) {
                	     System.err.println("(non softmax) NaN result on activation or derivative [" + outval + "] [" + deriva + "][i=" + i + "]");
                	     System.err.println( vrout.printMatrix( Weights[i] ) );
                	   
                	     return false;
                     }
            	     Outputs[i].setValue( j , outval );
            	     if( training ) ActivationDerivatives[i].setValue( j , deriva );
                    }
            	  }
              }
		  }
		  return  true;
				  
		}
		catch(Exception e ) {
			e.printStackTrace();
	        do_error( "Calculate Fast Output [" + this.getUID() + "] O=" + lola + " N=e" + Nets + "T=" + targetarray + " E=" + e.getMessage() ); return false;		
		}
	}
	
	//-----------------------------------------------------------------------
	private cmcVector MatrixVectorMult( cmcMatrix mat , cmcVector vec )
	{
		// Matrix = [M][N]
		// Vector = [N][1]
		// res = [M][1], dus een vector
		if( mat.getNbrOfColumns() != vec.getDimension() ) {
			do_error("MatrixVectorMult - dimensions differ " + mat.getNbrOfColumns() + " " + vec.getDimension() );
			return null;
		}
		double[][] vals = new double[ vec.getDimension() ][1];
		for(int i=0;i<vals.length;i++) vals[i][0] = vec.getVectorValues()[i]; 
		cmcMatrix two = new cmcMatrix( vals );
		cmcMatrix res = vrout.multiplyMatrix( mat , two );
		if( res == null ) {
			do_error("MatrixVectorMult - matrix multiplacation error");
			return null;
		}
		if( res.getNbrOfColumns() != 1 ){
			do_error("MatrixVectorMult - number of cols is not 1" );
			return null;
		}
		if( res.getNbrOfRows() != mat.getNbrOfRows() ){
			do_error("MatrixVectorMult - number of rows is not " + mat.getNbrOfRows() );
			return null;
		}
		double[] retval = new double[ res.getNbrOfRows() ];
		for(int i=0;i<retval.length;i++) retval[i] = res.getValues()[i][0];
		cmcVector vecres = new cmcVector( retval );
		return vecres;
	}
	
	//-----------------------------------------------------------------------
	public boolean goForwardFast(boolean training)
	{
		if( transferInputValuesFast( this.getPreviousLayer() , this ) == false ) {
			do_error("Could not transfer inout " + this.getUID() + " " + this.getTipe());
			return false;
		}
		if( calculateOutputFast(training) == false ) {
			do_error("Null for calculateOutputfast for layer [" + this.getUID() + "] [" + this.getTipe());
			return false;
		}
		/* debug
  	    for(int i=0;i<Outputs.length;i++)
		{
			cmcVector iv = Inputs[i];
			cmcVector nv = Nets[i];
			cmcVector ov = Outputs[i];
			cmcVector dv = ActivationDerivatives[i];
			cmcMatrix we = Weights[i];
            System.out.println( "[INPUT=" + iv.showRound() + "] [NET=" + nv.showRound() + "] [OUT=" + ov.showRound() + "] [ADV=" + dv.showRound() + "]" + vrout.printMatrix(we));			
		}
	    */
		return true;
	}
	
	//-----------------------------------------------------------------------
	public boolean CalculateLossFast()
	{
	    // Loss =  Transposed weight previous layer * loss previous layer . derivative activation
		if( this.getTipe() == cmcMLPenums.LAYER_TYPE.LOSS ) {
			return true;  // de Loss op de Loss layer werd berekend in de forward pass
		}
		
		// next layer
		neuralLayer NextLayer = this.getNextLayer();
		if( NextLayer == null ) {
			if( this.getTipe() != cmcMLPenums.LAYER_TYPE.LOSS ) {
				do_error( "Cannot determine next layer for " + this.getTipe() );
				return false;
			}
		}
		//
		cmcMatrix[] WeightMatrixes = null;
		cmcVector[] PDLossVectors = null;
		//cmcVector[] ActivationDiffs = this.ActivationDerivatives;
		WeightMatrixes = NextLayer.Weights;
		PDLossVectors = NextLayer.PartialDerivativeLosses;
		//
		for(int i=0;i<this.getNbrOfMiniBatchSamples();i++)
		{
			//cmcMatrix W = (this.getTipe() == cmcMLPenums.LAYER_TYPE.OUTPUT) ? WeightMatrixes[0] : WeightMatrixes[i];
			cmcMatrix W = WeightMatrixes[i];
			cmcVector L = PDLossVectors[i];
			
			//
			cmcMatrix WT = vrout.transposeMatrix( W );
			if( WT == null ) {
				do_error("cannot transpose"); 
				return false;
			}
			//
			cmcVector WTLoss = MatrixVectorMult( WT, L );
			if( WTLoss == null ) {
				do_error("cannot multiply WT and Losses"); 
				return false;
			}
			// Er zou nu een vector moeten zijn met #Neuronen elementen
			if( WTLoss.getDimension() != this.getNbrOfNeuronsInLayer() ) {
				do_error("WT*Loss should dimension incorrect " + WTLoss.getDimension() + " " + this.getNbrOfNeuronsInLayer() ); 
				return false;
			}
			
		    // switch between Sotmax and non-softmax processsing
			if( (PartialDerivativeSoftmax == null) && (ActivationDerivatives != null) ) {  // NON-Softmax
			    cmcVector ActDev = this.ActivationDerivatives[i];	
			    // hadamard product  V1 en V2
			    for(int j=0;j<this.getNbrOfNeuronsInLayer();j++)
			    {
				  double dd = WTLoss.getVectorValues()[j] * ActDev.getVectorValues()[j];
				  if( Double.isNaN(dd) ) {
					  do_error("(neuralLayer) - Hadamard multiplication issue " + this.getUID() + " " + this.getTipe()); 
					  return false;
				  }
				  PartialDerivativeLosses[i].setValue( j , dd );
			    }
			}
			else if( (PartialDerivativeSoftmax != null) && (ActivationDerivatives == null) ) {  // SOFTMAX
				cmcMatrix actMat =  this.PartialDerivativeSoftmax[i];
				
				// (WT(t+1) x L(t+1)) T x Partialderivativesoftmax
				// eerste 2 bewerkingen zijn al uitgevoerd begin dus met WTxL te transponeren
				// maak gewoon een matrix van 1 x N
				double[][] mals = new double[1][ this.getNbrOfNeuronsInLayer() ];
				for(int j=0;j<WTLoss.getDimension();j++) mals[0][j] = WTLoss.getVectorValues()[j];
				cmcMatrix WTL = new cmcMatrix( mals );
				cmcMatrix WTLD = vrout.multiplyMatrix( WTL , actMat );
//System.err.println("->" + vrout.printMatrix(WTL) + " " + vrout.printMatrix(actMat) );
				if( WTLD == null ) {   // 1N x NN => 1N
					do_error("Error multiplying WTL and DerivativesSoftmax");
					return false;
				}
				for(int j=0;j<this.getNbrOfNeuronsInLayer();j++)
				{
				  double dd = WTLD.getValues()[0][j];
				  if( Double.isNaN(dd) ) {
					  do_error("NAN value on WTLD)");
					  return false;
				  }
				  PartialDerivativeLosses[i].setValue( j , dd );
				}
//System.err.println("->" + PartialDerivativeLosses[i].show() );
				
			}
			else {
				System.err.println("System error" + PartialDerivativeSoftmax + " " + ActivationDerivatives );
				return false;
			}
				
			// DEBUG
//System.out.println("" + vrout.printMatrix(WT) + " " + L.showRound() + " " + ActDev.showRound() + " -> " +Losses[i].showRound() );
		}
		
		return true;
	}
	
	//-----------------------------------------------------------------------
	public boolean CalculateGradientsFast()
	{
		//  Gradient( Laag L )Neuron( J )Weight( K ) =   Out(L-1)Neuron(K)   x   PartialDerivativeLosses(L)( J )
		cmcVector[] Outputs = this.previousLayer.Outputs;
		if( PartialDerivativeLosses[0].getDimension() != this.getNbrOfNeuronsInLayer() ) {
			do_error("aantal neuron klopt niet met aantal losses");
			return false;
		}
		if( Weights[0].getNbrOfColumns() != Outputs[0].getDimension() ) {
			do_error(" aantal gewichten klopt niet met aantal outputs op previous laag");
			return false;
		}
		if( Weights[0].getNbrOfColumns() != this.getPreviousLayer().getNbrOfNeuronsInLayer() ) {
			do_error(" aantal gewichten klopt niet met aantal neurons op previous laag");
			return false;
		}
		for(int i=0;i<this.getNbrOfMiniBatchSamples();i++)
		{
			for(int j=0;j<this.getNbrOfNeuronsInLayer();j++)
			{
				for(int k=0;k<this.getPreviousLayer().getNbrOfNeuronsInLayer();k++)
				{
				  double gr = Outputs[i].getVectorValues()[k] * PartialDerivativeLosses[i].getVectorValues()[j];
				  Gradients[i].setValue( j , k , gr );
				}
			}
		}
		
		return true;
	}
	
	//-----------------------------------------------------------------------
	public boolean PropagateGradientsFast(double LearningRate , int ncycles)
	{
		
		//  G(t)
		cmcMatrix GT = vrout.AverageMatrix( Gradients );
		if( GT == null ) return false;
		// doorschuiven 
		ConsolidatedPreviousGradients.fillMatrix( ConsolidatedGradients.getValues() );
		ConsolidatedGradients.fillMatrix( GT.getValues() );
		//
		cmcMatrix deltaWeights = null;
		if( this.optimizationTipe == cmcMLPenums.OPTIMIZATION_TYPE.NONE ) {
			//  Scalaire vermenigvuldiging ETA en averagegradients    ETA*Gt
			deltaWeights = vrout.scalarMultiplyMatrix( LearningRate , GT );
		}
		else
		if( this.optimizationTipe == cmcMLPenums.OPTIMIZATION_TYPE.MOMEMTUM ) {
		      // V(t) =  GAMMA*V(t-1) + ETA*G(t)
			  cmcMatrix GammaVtMin1 = vrout.scalarMultiplyMatrix( GAMMA , PreviousVT );
			  cmcMatrix EtaGt = vrout.scalarMultiplyMatrix( LearningRate , GT ); 
			  deltaWeights =  vrout.addMatrix( GammaVtMin1 , EtaGt );
			  PreviousVT.fillMatrix( deltaWeights.getValues() );
		}	
		else
		if( this.optimizationTipe == cmcMLPenums.OPTIMIZATION_TYPE.RMSPROP ) {
			//  V(t) = 0.9 V(t-1) + 0.1 GT.GT
			cmcMatrix GAMMAVT = vrout.scalarMultiplyMatrix( GAMMA , PreviousVT );
			cmcMatrix GTGT = vrout.HadamardProduct( GT , GT );
			cmcMatrix ONEMINGAMMAGTGT = vrout.scalarMultiplyMatrix( (1-GAMMA) , GTGT ); 
			cmcMatrix EGTSQUARE = vrout.addMatrix( GAMMAVT , ONEMINGAMMAGTGT  );
            // (eta / squareroot (EGTSQUARE - epsilon)) . GT
			cmcMatrix EGTSQUAREMINUSEPSILON = vrout.scalarAddMatrix( EPSILON , EGTSQUARE );
			cmcMatrix EGTSQROOT = vrout.SquareRootMatrix(  EGTSQUAREMINUSEPSILON );
			cmcMatrix GTDIVSQRT = vrout.HadamardQuotient( GT , EGTSQROOT );
			deltaWeights = vrout.scalarMultiplyMatrix( LearningRate , GTDIVSQRT  );
			//
			PreviousVT.fillMatrix( EGTSQUARE.getValues() );
			
//System.out.println( vrout.printMatrix( GT ) + "----" + vrout.printMatrix( deltaWeights) + "===============");
		}	
		else
		if( this.optimizationTipe == cmcMLPenums.OPTIMIZATION_TYPE.ADAM ) {
		    cmcMatrix ONEMINUSBETA1GT = vrout.scalarMultiplyMatrix( (1-BETA1) , GT ); 
			cmcMatrix BETA1MT = vrout.scalarMultiplyMatrix( BETA1 , PreviousMT );
			cmcMatrix MT = vrout.addMatrix( BETA1MT , ONEMINUSBETA1GT );
			//
			cmcMatrix GTGT = vrout.HadamardProduct( GT , GT );
		    cmcMatrix ONEMINUSBETA2GTGT = vrout.scalarMultiplyMatrix( (1-BETA1) , GTGT ); 
			cmcMatrix BETA2VT = vrout.scalarMultiplyMatrix( BETA2 , PreviousVT );
		    cmcMatrix VT = vrout.addMatrix( BETA2VT , ONEMINUSBETA2GTGT );
            //					
		   
			double B1 = 1 / (1 - Math.pow( BETA1 , ncycles+1 ));
            cmcMatrix MHOEDJE = vrout.scalarMultiplyMatrix( B1 , MT );
			double B2 = 1 / (1 - Math.pow( BETA2 , ncycles+1 ));
            cmcMatrix VHOEDJE = vrout.scalarMultiplyMatrix( B2 , VT );
            //
            cmcMatrix SQRTVT = vrout.SquareRootMatrix( VHOEDJE );
            cmcMatrix SQRTPLUSEPSILON = vrout.scalarAddMatrix( EPSILON , SQRTVT );
            cmcMatrix MX = vrout.HadamardQuotient( MHOEDJE ,  SQRTPLUSEPSILON );
			//
			deltaWeights = vrout.scalarMultiplyMatrix( LearningRate , MX );
		    //			  
		    PreviousVT.fillMatrix( VT.getValues() );
		    PreviousMT.fillMatrix( MT.getValues() );
		}
		else {
			System.err.println( "Unsupported SGD optimization");
			System.exit(1);
		}
		//
		
		
		//   W - delta
		for(int i=0;i<this.getNbrOfMiniBatchSamples();i++)
		{
			cmcMatrix som = vrout.substractMatrix( Weights[i] , deltaWeights );
			Weights[i].fillMatrix( som.getValues() );
		}
		
		GatherStats();
		return true;
	}
	
	
	private void GatherStats()
	{
		// Aggregate all gradients, this indicates the amount of learning
		double dd = 0;
	    double[][] vals = this.ConsolidatedGradients.getValues();
	    for(int i=0;i<vals.length;i++)
	    {
	    	for(int j=0;j<vals[i].length;j++)  dd += Math.abs( vals[i][j] );
	    }
	    //
	    int NbrOfContributingNeurons = 0;
		neuron[] neurons = this.getNeurons();
		for(int i=0;i<neurons.length;i++)
		{
			neuron nr = neurons[i];
			if( nr.getTipe() == cmcMLPenums.NEURON_TYPE.ACTIVATION_ONLY ) continue;
			if( nr.getDropOut() ) continue;
			NbrOfContributingNeurons++;
		}
	    //
	    PreviousLayerLearningRate = LayerLearningRate;
		LayerLearningRate = dd / NbrOfContributingNeurons;
		double acceleration = LayerLearningRate - PreviousLayerLearningRate;
		//System.out.println( this.getTipe() + " " + LayerLearningRate + " " + acceleration );
		
		/*
		if( acceleration < MIN_ACCELERATION_LEVEL ) {
          if( Math.abs(acceleration) < MIN_ACCELERATION_LEVEL ) {
        	  LearningStalled++;
        	  //if( LearningStalled == 25 ) System.out.println( this.getTipe() + " " + this.getUID() + " STALLED" );
          }
		}
		else LearningStalled = 0;
		*/
	
	}
	
	
	//-----------------------------------------------------------------------
	public String getInfo(boolean showDelta)
	{
		String s= "[" + UID + "][" + tipe + "]";
		s += "[#Neurons=" + this.getNbrOfNeuronsInLayer() + "]";
		s += "[Prev=";
		if( this.getPreviousLayer() != null ) s += ""+this.getPreviousLayer().getUID();
		s += "][Next=";
		if( this.getNextLayer() != null ) s += ""+this.getNextLayer().getUID();
		s += "]";
		s += "[#Inputs=" + this.getNbrOfInputs() + "]";
		s += "[#Outputs=" + this.getNbrOfNeuronsInLayer() + "]";
				
		//
		s += "\n" + this.getInputValueInfo();
		s += "\n" + this.getOutputValueInfo();
		s += "\n" + this.getNeuronInfo(showDelta);
		
		return s;
	}
	//-----------------------------------------------------------------------
	private String dumpArray( double[][] data)
	{
			return vrout.arrayToString( data , ARRAY_TOSTRING_DUMP_LINES , ARRAY_TOSTRING_DUMP_WIDTH );
	}
	//-----------------------------------------------------------------------
	private String getInputValueInfo()
	{
			return "INPUT\n" + dumpArray( this.getInputValues() );
	}
	//-----------------------------------------------------------------------
	private String getOutputValueInfo()
	{
			return "OUTPUT\n" + dumpArray( this.getOutputValues() );
	}
	//-----------------------------------------------------------------------
	private String getNeuronInfo(boolean showDelta)
	{
			String s = "";
		    if( this.neurons == null ) return s;	
		    for(int i=0;i<neurons.length;i++)
		    {
		   		s += "\nneuron [UID=" + neurons[i].getUID() + "] [Layer=" + neurons[i].getLayerUID() + "] [" + neurons[i].getActivationFunctionTipe() + "] [" + neurons[i].getTipe() + "] [A=" +  neurons[i].getA() + "]";
		   	   	if( neurons[i].getWeightsArray(false) != null ) {
		   	   	  double[][] data = new double[ 1 ][ neurons[i].getWeightsArray(true).length];
		   	   	  for(int k=0;k<data[0].length;k++) data[0][k] = neurons[i].getWeightsArray(true)[k];
		    	  s += "\nCurrent Weights  [" + data[0].length + "] : " + vrout.arrayToString( data , ARRAY_TOSTRING_DUMP_LINES , 16 ); 
		        }
		    	else s += " \n -- NO WEIGHTS\n";
		   	   	//
		   	   	if( showDelta ) {
		   	      double[][] deltas = new double[ 1 ][ neurons[i].getGradientsArray().length ];
	  	   	      for(int k=0;k<deltas[0].length;k++) deltas[0][k] = neurons[i].getGradientsArray()[k];
	   	          s += "\nDeltas [" + deltas[0].length + "] : " + vrout.arrayToString( deltas , ARRAY_TOSTRING_DUMP_LINES , 16 ); 
		   	   	}
		    }
		    return s;
	}
	
	//-----------------------------------------------------------------------
	public String getAbbreviatedNeuronInfo()
	{
		String s = "[Layer=" + this.UID + "] [Type=" + this.getTipe() + "] [#Neurons=" + neurons.length + "]";
	    if( this.neurons == null ) return s;	
	    for(int i=0;i<neurons.length;i++)
	    {
	      int nWeights = (neurons[i].getWeightsArray(false) != null ) ? neurons[i].getWeightsArray(true).length : 0;
		  s += "\n[Neuron=" + neurons[i].getUID() + "] [" + neurons[i].getActivationFunctionTipe() + "] [A=" + neurons[i].getA() + "] [" + neurons[i].getTipe() + "] [#Weights=" + nWeights + "]";
	    }
	    return s;
	}
	//-----------------------------------------------------------------------
	public String getAbbreviatedWeightInfo()
	{
		try {
		  String s = "";
	      if( this.neurons == null ) return s;	
	      for(int i=0;i<neurons.length;i++)
	      {
	    	cmcVector vec = neurons[i].getFastWeights(false);
	    	if( vec == null ) continue;
	        double[] weights = vec.getVectorValues();
	        if( weights == null ) continue;
	        s += "[" + this.UID + "][" + neurons[i].getUID() + "]";
		    for(int j=0;j<weights.length;j++) s += "[" + weights[j] + "]";
		    s += "\n";
	      }
	      return s;
		}
		catch( Exception e ) {
		  return "## No weights " + this.getTipe() + " " + this.getUID() + " \n";	
		}
	    
	}
		
	//=========================================
	
	public cmcMLPenums.LAYER_TYPE getTipe() {
		return tipe;
	}

	public void setTipe(cmcMLPenums.LAYER_TYPE tipe) {
		this.tipe = tipe;
	}
	public int getUID() {
		return UID;
	}
	public int getNbrOfInputs() {
		return NbrOfInputs;
	}

	public void setNbrOfInputs(int nbrOfInputs) {
		NbrOfInputs = nbrOfInputs;
	}
	public int getNbrOfNeuronsInLayer() {
		return NbrOfNeuronsInLayer;
	}

	public int getNbrOfMiniBatchSamples() {
		return NbrOfMiniBatchSamples;
	}

	public neuralLayer getPreviousLayer() {
		return previousLayer;
	}

	public neuralLayer getNextLayer() {
		return nextLayer;
	}

	public double[][] getInputValues() {
		return inputValues;
	}
	public double[][] getOutputValues() {
		return outputValues;
	}
	
	public cmcVector[] getInputsFast() {
		return Inputs;
	}
	public cmcVector[] getOutputsFast() {
		return Outputs;
	}
    public void setInputsFast( cmcVector[] inp)
    {
	  Inputs = inp;
    }
    public void setOutputsFast( cmcVector[] inp)
    {
	  Outputs = inp;
    }
	
	public void setNbrOfNeuronsInLayer(int nbrOfNeuronsInLayer) {
		NbrOfNeuronsInLayer = nbrOfNeuronsInLayer;
	}

	public void setPreviousLayer(neuralLayer previousLayer) {
		this.previousLayer = previousLayer;
	}

	public void setNextLayer(neuralLayer nextLayer) {
		this.nextLayer = nextLayer;
	}

	public void setInputValues(double[][] inputValues) {
		this.inputValues = inputValues;
	}

	public void setOutputValues(double[][] outputValues) {
		this.outputValues = outputValues;
	}

	public neuron[] getNeurons() {
		return neurons;
	}

	public void setNeurons(neuron[] neurons) {
		this.neurons = neurons;
	}
	
	public void setNbrOfMiniBatchSamples(int i) {
		this.NbrOfMiniBatchSamples = i;
	}
	
	public cmcMLPenums.ACTIVATION_FUNCTION_TYPE getActivationFunctionTipe() {
		return activationFunctionTipe;
	}

	public void setActivationFunctionTipe(cmcMLPenums.ACTIVATION_FUNCTION_TYPE activationFunction) {
		this.activationFunctionTipe = activationFunction;
	}

	public void setOptimizationType(cmcMLPenums.OPTIMIZATION_TYPE oti) {
		this.optimizationTipe = oti;
	}
	
	public String getErrMsg() {
		return ErrMsg;
	}

	public void setErrMsg(String errMsg) {
		ErrMsg = errMsg;
	}
	public double getAFactor() {
		return AFactor;
	}

	public void setAFactor(double aFactor) {
		AFactor = aFactor;
	}
	
	public double getDropOutRatio() {
		return DropOutRatio;
	}

	public void setDropOutRatio(double dd) {
		DropOutRatio = dd;
	}

	public cmcMatrix[] getWeights() {
		return Weights;
	}

	public void setWeights(cmcMatrix[] w) {
		Weights = w;
	}
	
	public void setGradients(cmcMatrix[] g) {
		Weights = g;
	}
	public cmcVector[] getPartialDerivativeLosses() {
		return PartialDerivativeLosses;
	}

	public void setPartialDerivativeLosses(cmcVector[] pdlv) {
		PartialDerivativeLosses = pdlv;
	}

	public cmcMatrix[] getGradients() {
		return Gradients;
	}
	
	public cmcVector[] getTargets() {
		return Targets;
	}
	
	public cmcMatrix getConsolidatedGradients()
	{
		return this.ConsolidatedGradients;
	}
	
	public double getLearningRate()
	{
		return this.LayerLearningRate;
	}
	
	public int getLearningStalled()
	{
		return this.LearningStalled;
	}
	
}
