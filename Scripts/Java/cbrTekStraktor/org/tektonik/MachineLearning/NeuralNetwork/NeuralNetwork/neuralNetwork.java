package org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork;

import java.util.Random;

import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTO;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;
import org.tektonik.tools.linearAlgebra.cmcVectorRoutines;

public class neuralNetwork {
	
	private cmcVectorRoutines vrout = null;
	private Random rnd = new Random();

	private int IncludeOutputInDropOut = 0;    //  0 or 1
	private int NbrOfLayers = -1;
	private int NbrOfInputNeurons = -1;
	private int NbrOfOutputNeurons = -1;
	private int NbrOfHiddenNeurons = -1;
	private int MiniBatchSize = -1;    // mini batch size
	private inputNeuralLayer inputlayer = null;
	private outputNeuralLayer outputlayer = null;
	private hiddenNeuralLayer[] hiddenlayers = null;
	private lossNeuralLayer losslayer = null;
	private String ErrorMsg = "";
	
	private void do_error( String s)
	{
		ErrorMsg += "[" + s + "]";
		System.err.println( s );
	}
	public String getErrorMsg()
	{
		return ErrorMsg;
	}
	//-----------------------------------------------------------------------
	public neuralNetwork(int ilayers , int NbrOfInputs , int NbrOfOutputs , int iNbrOfHiddenNeurons , int NbrOfMiniBatchSamples )
	{
	     NbrOfLayers        = ilayers;	
	     NbrOfInputNeurons  = NbrOfInputs;
	     NbrOfOutputNeurons = NbrOfOutputs;
	     NbrOfHiddenNeurons = iNbrOfHiddenNeurons;
	     MiniBatchSize      = NbrOfMiniBatchSamples;
	     
	     vrout = new cmcVectorRoutines();
	}
	//-----------------------------------------------------------------------
	public boolean initializeNetwork(cmcMLPDTO dto , String[] ClassNameList )
	{
	 if( this.NbrOfLayers < 3) {
		 do_error( "Number of layers must be greater than 2");
		 return false;
	 }
		
	 int uid=0;
	 inputlayer = new inputNeuralLayer( uid++ , NbrOfInputNeurons , MiniBatchSize , dto.getAFactor() );
	 inputlayer.setOptimizationType( dto.getOptimizationType() );
   	 //
   	 hiddenlayers = new hiddenNeuralLayer[ NbrOfLayers - 3];  // input + output + loss
   	 for(int i=0;i<hiddenlayers.length;i++)
   	 {
   	  hiddenlayers[i] = new hiddenNeuralLayer( uid++ , NbrOfHiddenNeurons , MiniBatchSize , dto.getActivationFunction() , dto.getAFactor() , dto.getDropOutRatio() );
   	  hiddenlayers[i].setOptimizationType( dto.getOptimizationType() );
   	 }
	 //
   	 outputlayer = new outputNeuralLayer( uid++ , NbrOfOutputNeurons , MiniBatchSize , dto.getOutputActivationFunction() , dto.getCostFunctionType() , dto.getAssessmentType() );	 
   	 outputlayer.setOptimizationType( dto.getOptimizationType() );
   	 //
   	 losslayer = new lossNeuralLayer( uid++ , NbrOfOutputNeurons , MiniBatchSize , dto.getCostFunctionType() );	 
   	 inputlayer.setOptimizationType( null );
    	 
   	 
   	 // Set previous and next layers
   	 inputlayer.setPreviousLayer( null );
   	 inputlayer.setNextLayer( hiddenlayers[0] );
   	 for(int i=0;i<hiddenlayers.length;i++)
   	 {
   		 hiddenNeuralLayer cur = hiddenlayers[i];
   		 if( i == 0 ) {
   			 cur.setPreviousLayer( inputlayer );
   		 }
   		 else {
   			 cur.setPreviousLayer( hiddenlayers[i-1] );
   		 }
   		 if( i == (hiddenlayers.length - 1) ) {
   			 cur.setNextLayer( outputlayer );
   		 }
   		 else {
   			 cur.setNextLayer( hiddenlayers[i + 1] );
   		 }
   		 
   	 }
   	 //
   	 outputlayer.setPreviousLayer( hiddenlayers[ hiddenlayers.length - 1 ]);
   	 outputlayer.setNextLayer( losslayer );
   	 //
   	 losslayer.setPreviousLayer( outputlayer);
   	 losslayer.setNextLayer( null );
   	 
   	 try {
   	   // Initialize the input matrices
   	   if( inputlayer.initializeInputOutputAndNeuronsFast() == false ) { do_error( outputlayer.getErrMsg()); return false; }
   	   for(int i=0;i<hiddenlayers.length;i++)
   	   {
   		 hiddenNeuralLayer cur = hiddenlayers[i];
   		 if( cur.initializeInputOutputAndNeuronsFast() == false ) { do_error( outputlayer.getErrMsg()); return false; }
   		 if( cur.initializeWeightsFast(dto.getWeightStrategy()) == false ) { do_error( outputlayer.getErrMsg()); return false; }
   	   }
   	   // output
   	   if( outputlayer.initializeInputOutputAndNeuronsFast() == false ) { do_error( outputlayer.getErrMsg()); return false; }
   	   if( outputlayer.initializeWeightsFast(dto.getWeightStrategy()) == false ) { do_error( outputlayer.getErrMsg()); return false; }
   	 
   	   // Loss layer
   	   if( losslayer.initializeInputOutputAndNeuronsFast() == false ) { do_error( losslayer.getErrMsg()); return false; }
  	   if( losslayer.initializeWeightsFast(dto.getWeightStrategy()) == false ) { do_error( losslayer.getErrMsg()); return false; }
  	 
   	   // initialize the confusion matrix routines
   	   if( outputlayer.initializeNominals(ClassNameList) == false ) { do_error("Could not initialize nominals"); return false; }
  
   	   // initialize the signature values (one hot, etc)
   	   if( outputlayer.makeSignatures() == null )  { do_error("Could not create signatures"); return false; }
   	 }
   	 catch(Exception e ) {
   		System.err.println("Initialize network" );
   		e.printStackTrace();
   	 }
   	 return true;
    	
	}
	
	
	public boolean resetInputOutputTargets(int nsamples)
	{
		   this.setMiniBatchSize(nsamples);
		   //
		   inputlayer.setNbrOfMiniBatchSamples(nsamples);
		   for(int i=0;i<hiddenlayers.length;i++) hiddenlayers[i].setNbrOfMiniBatchSamples(nsamples);
		   outputlayer.setNbrOfMiniBatchSamples(nsamples);
		   losslayer.setNbrOfMiniBatchSamples(nsamples);
		   //
	   	   inputlayer.resetInputOutputTargets( nsamples );
	   	   for(int i=0;i<hiddenlayers.length;i++) hiddenlayers[i].resetInputOutputTargets( nsamples );
	   	   outputlayer.resetInputOutputTargets( nsamples );
	   	   losslayer.resetInputOutputTargets( nsamples );
	   	   return true;
	}
	
	//-----------------------------------------------------------------------
	public boolean initializeInputDataFast( double[][] data )
	{
		boolean ib = inputlayer.fastInitializeInputData( data );
		if( !ib) do_error( inputlayer.getErrMsg() );
		return ib;
	}
	
	//-----------------------------------------------------------------------
	public boolean initializeTargetDataFast( double[][] data , String[] ClassNameList )
	{
		if( outputlayer == null ) { do_error("null output layer"); return false; }
		boolean ib = outputlayer.setTargetValuesFast(data , ClassNameList );
		if( !ib ) do_error( outputlayer.getErrMsg() );
		return ib;
	}
	
	//-----------------------------------------------------------------------
	public boolean FastForwardPropagation(boolean training)
	{
		for(int layer=0;layer<=hiddenlayers.length+1;layer++)  //  HIDDEN(n) + OUTPUT + LOSS layer
		{
			//neuralLayer current = (layer == hiddenlayers.length) ? outputlayer : hiddenlayers[layer];
			neuralLayer current = null;
			if( layer == hiddenlayers.length + 1  ) current = losslayer;
			else if( layer == hiddenlayers.length ) current = outputlayer;
		 	                                   else current = hiddenlayers[layer];
			if( current.goForwardFast(training) == false ) {
					do_error( "Fast Forward Propagation - " + current.getErrMsg() );
					return false;
			}
		}
       	return true;
	}
	
	// debug
	public void checkWeights(String tt)
	{
        //System.err.println(  tt + vrout.printMatrix( hiddenlayers[0].getWeights()[0] ));
	}
	
	//-----------------------------------------------------------------------
	public boolean FastEvaluateOutput()
	{
        // confusion matrix and others
        if( outputlayer.evaluateResultsFast() == false ) {
	        do_error( "Fast evaluate results =" + outputlayer.getErrMsg() );
	        return false;
        }
		return true;
	}
	
	//-----------------------------------------------------------------------
	public double getLastTotalCost()
	{
	    //return outputlayer.getTotalCost();
		return losslayer.getAggregatedLosses();
	}
	
	//-----------------------------------------------------------------------
	public double getAccuracy()
	{
		double dd = outputlayer.getEpochAccuracy();
		if( Double.isNaN(dd) ) do_error( "getaccuracy " + outputlayer.getErrMsg() );
		return dd;
	}
	
	public int getHits()
	{
	   return outputlayer.getHits();	
	}
	public int getRowsAssessed()
	{
		return outputlayer.getRowsAssessed();
	}
	
	//-----------------------------------------------------------------------
	public int[][] getConfusionMatrix()
	{
		int[][] cm = outputlayer.getConfusionMatrix();
		if( cm == null ) do_error( "getConfusionMatrix " + outputlayer.getErrMsg() );
		return cm;
	}
	
	//-----------------------------------------------------------------------
	public String[] getExtendedClassNameList()
	{
		return outputlayer.getExtendedClassNameList();
	}
	
    //-----------------------------------------------------------------------
	public boolean backPropagationFast(double LearningRate , double MiniBatchProportion , int ncycles )
	{
		// Calculate the loss backwards
		for(int layer=hiddenlayers.length+1;layer>=0;layer--)  // HIDDEN(n) OUTPUT LOSS
		{
			//neuralLayer current = (layer == hiddenlayers.length) ? outputlayer : hiddenlayers[layer];
			neuralLayer current = null;
			if( layer == hiddenlayers.length + 1  ) current = losslayer;
			else if( layer == hiddenlayers.length ) current = outputlayer;
			                                   else current = hiddenlayers[layer];
		    if( current.CalculateLossFast() == false ) return false;			
		}
		// calculate the changes
		for(int layer=hiddenlayers.length;layer>=0;layer--)
		{
			neuralLayer current = (layer == hiddenlayers.length) ? outputlayer : hiddenlayers[layer];
		    if( current.CalculateGradientsFast() == false ) return false;			
		}
		// propagate changes
		for(int layer=hiddenlayers.length;layer>=0;layer--)
		{
			neuralLayer current = (layer == hiddenlayers.length) ? outputlayer : hiddenlayers[layer];
		    if( current.PropagateGradientsFast(LearningRate,ncycles) == false ) return false;			
		}
		return true;
	}

	//-----------------------------------------------------------------------
	public boolean restoreDropOut( )
	{
		return initializeDropOut( 100 , true );  // caution percentage needs to be > 0
	}
	//-----------------------------------------------------------------------
	public boolean initializeDropOut( double dropoutperc )
	{
		return initializeDropOut( dropoutperc , false );
	}
	//-----------------------------------------------------------------------
	private boolean initializeDropOut( double dropoutperc , boolean restore)
	{
	    if( dropoutperc <= 0 ) return true;
	    // restore
	    int counter = 0;
		for(int layer=0;layer<(hiddenlayers.length + IncludeOutputInDropOut);layer++)  // count and undo  + Include/exclude output
		{
			neuralLayer current = (layer==hiddenlayers.length) ? outputlayer : hiddenlayers[layer];
			for(int i=0;i<current.getNbrOfNeuronsInLayer();i++)
			{
				neuron nr = current.getNeurons()[i];
				if( nr.getTipe() != cmcMLPenums.NEURON_TYPE.COMPLETE ) continue;
				nr.setDropOut( false );
				counter++;
			}
		}
		if( restore ) return true;
		// set
		double dh = ((double)counter * dropoutperc) + 1;
		int ih = (int)dh;
        //		
		
		boolean[] hits = new boolean[ counter ];
		for(int i=0;i<hits.length;i++) hits[i] = false;
		for(int i=0;i<ih;i++)
		{
			int hitted = rnd.nextInt( counter );
			hits[ hitted ] = true;
		}
        //
		counter=0;
		for(int layer=0;layer<(hiddenlayers.length + this.IncludeOutputInDropOut);layer++)
		{
			//neuralLayer current = hiddenlayers[layer];
			neuralLayer current = (layer==hiddenlayers.length) ? outputlayer : hiddenlayers[layer];
			for(int i=0;i<current.getNbrOfNeuronsInLayer();i++)
			{
				neuron nr = current.getNeurons()[i];
				if( nr.getTipe() != cmcMLPenums.NEURON_TYPE.COMPLETE ) continue;
				if( hits[ counter ] == true ) nr.setDropOut( true );
				counter++;
			}
		}
		
		return true;
	}
	
	//-----------------------------------------------------------------------
	public String getDropOutInfo()
	{
		String ss = "";
		int counter = 0;
		for(int layer=0;layer<hiddenlayers.length;layer++)  // count and undo
		{
			neuralLayer current = hiddenlayers[layer];
			for(int i=0;i<current.getNbrOfNeuronsInLayer();i++)
			{
				neuron nr = current.getNeurons()[i];
				if( nr.getDropOut() == false ) continue;
				ss += "[" + nr.getUID() + "]";
				counter++;
			}
		}
		return "(" + counter + ")" + ss;
	}
	
	public double[] getLearningRates()
	{
		double[] ret = new double[ hiddenlayers.length + 1 ];
		for(int layer=0;layer<=hiddenlayers.length;layer++)  // count and undo
		{
			neuralLayer current = (layer==hiddenlayers.length) ? outputlayer : hiddenlayers[layer] ;
			ret[layer] = current.getLearningRate();
		}
		return ret;
	}
	
	//-----------------------------------------------------------------------
	public String getLearningRateInfo()
	{
		String ss = "";
		double[] vals = this.getLearningRates();
		for(int i=0;i<vals.length;i++) ss += "[" + String.format("%10.5f",vals[i]).trim() + "]";
		return ss;
	}
	
	//-----------------------------------------------------------------------
	public String getInfo(boolean showDelta)
	{
		try {
		  String s= "Network \n";
		  s += this.inputlayer.getInfo(showDelta) + "\n";
		  for(int i=0;i<hiddenlayers.length;i++)
		  {
			s += hiddenlayers[i].getInfo(showDelta) + "\n";
		  }
		  s += this.outputlayer.getInfo(showDelta) + "\n";
		  s += "Target Results\n" + vrout.arrayToString( this.outputlayer.getTargetDump() , 1000 , 5 );
		  return s;
		}
		catch(Exception e ) {
			return "ERROR dumping info on network" + e.getMessage();
		}
	}
	
	//-----------------------------------------------------------------------
	public String getAbbreviatedInfo()
	{
		try {
			  String s= "";
			  s += this.inputlayer.getAbbreviatedNeuronInfo() + "\n";
			  for(int i=0;i<hiddenlayers.length;i++)
			  {
				s += hiddenlayers[i].getAbbreviatedNeuronInfo() + "\n";
			  }
			  s += this.outputlayer.getAbbreviatedNeuronInfo() + "\n";
			  s += "--> nominals : " + this.outputlayer.getExplicitedClassListVerbose() + "\n";
			  return s;
			}
			catch(Exception e ) {
				return "ERROR dumping info on network" + e.getMessage();
			}
	}
	
	//-----------------------------------------------------------------------
	public String getAbbreviatedWeightInfo()
	{
		try {
			  String s= "";
			  s += this.inputlayer.getAbbreviatedWeightInfo();
			  for(int i=0;i<hiddenlayers.length;i++)
			  {
				s += hiddenlayers[i].getAbbreviatedWeightInfo();
			  }
			  s += this.outputlayer.getAbbreviatedWeightInfo();
			  s += this.losslayer.getAbbreviatedWeightInfo();
			  return s;
		}
		catch(Exception e ) {
			e.printStackTrace();
			return "ERROR dumping info on network" + e.getMessage();
		}
	}
	
	
	//-----------------------------------------------------------------------
	public int getNbrOfLayers() {
		return NbrOfLayers;
	}
	public void setNbrOfLayers(int nbrOfLayers) {
		NbrOfLayers = nbrOfLayers;
	}
	public int getNbrOfInputNeurons() {
		return NbrOfInputNeurons;
	}
	public void setNbrOfInputNeurons(int nbrOfInputNeurons) {
		NbrOfInputNeurons = nbrOfInputNeurons;
	}
	public int getNbrOfOutputNeurons() {
		return NbrOfOutputNeurons;
	}
	public void setNbrOfOutputNeurons(int nbrOfOutputNeurons) {
		NbrOfOutputNeurons = nbrOfOutputNeurons;
	}
	public int getNbrOfHiddenNeurons() {
		return NbrOfHiddenNeurons;
	}
	public void setNbrOfHiddenNeurons(int nbrOfHiddenNeurons) {
		NbrOfHiddenNeurons = nbrOfHiddenNeurons;
	}
	public int getMiniBatchSize() {
		return MiniBatchSize;
	}
	public void setMiniBatchSize(int miniBatchSize) {
		MiniBatchSize = miniBatchSize;
	}
	public inputNeuralLayer getInputlayer() {
		return inputlayer;
	}
	public void setInputlayer(inputNeuralLayer inputlayer) {
		this.inputlayer = inputlayer;
	}
	public outputNeuralLayer getOutputlayer() {
		return outputlayer;
	}
	public lossNeuralLayer getLosslayer() {
		return losslayer;
	}
	public void setOutputlayer(outputNeuralLayer outputlayer) {
		this.outputlayer = outputlayer;
	}
	public void setLossLayer(lossNeuralLayer ll) {
		this.losslayer = ll;
	}
	public hiddenNeuralLayer[] getHiddenlayers() {
		return hiddenlayers;
	}
	public void setHiddenlayers(hiddenNeuralLayer[] hiddenlayers) {
		this.hiddenlayers = hiddenlayers;
	}

}
