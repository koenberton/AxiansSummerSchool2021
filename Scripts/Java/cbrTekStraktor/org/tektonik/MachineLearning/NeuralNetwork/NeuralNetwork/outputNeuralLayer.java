package org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork;


import java.util.Random;

import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunction;
import org.tektonik.MachineLearning.NeuralNetwork.functions.CostFunction;
import org.tektonik.MachineLearning.NeuralNetwork.functions.CostFunctionSquaredError;
import org.tektonik.tools.linearAlgebra.cmcVector;

public class outputNeuralLayer extends neuralLayer {
	
	private cmcMLPenums.ASSESSMENT_TYPE assessmenttipe = null;
	private double EPSILON = 0.01;  // 1 hot with a twist => opdat one hot target value niet 1 of 0 maar net iets minder/meer is
	
	private double upperThresholdAbsolute = Double.NaN;
	private String[] nominals = null;
	private int confusionMatrix[][] = null;
	private CostFunction costfunction = null;
	private int[] targetIndex = null;
	private double epochAccuracy = Double.NaN;
	private int currentHits=-1;
	private int currentTotal=-1;
	private cmcVector[] signatures = null;  
	private Random rand = null;
	
	
	//-----------------------------------------------------------------------
	public outputNeuralLayer(int uid , int ineurons , int iminibsamples , cmcMLPenums.ACTIVATION_FUNCTION_TYPE actfunc , 
			                 cmcMLPenums.COST_FUNCTION_TYPE costfunctiontipe  , cmcMLPenums.ASSESSMENT_TYPE assesstp)
	{
	 super( uid , ineurons , iminibsamples);
	 super.setTipe( cmcMLPenums.LAYER_TYPE.OUTPUT );
	 super.setActivationFunctionTipe( actfunc );
	 //
	 switch( costfunctiontipe )
	 {
	 case SQUARED_ERROR : { costfunction = new CostFunctionSquaredError();  break; }
	 default : { costfunction = null; break; }
	 }
	 //
	 assessmenttipe = assesstp;
	 if( assessmenttipe == null ) {
		 do_error("outputNeuralLayer - assessment type not set "); System.exit(1);
	 }
	 
	 //
	 rand = new Random();
	}

	//-----------------------------------------------------------------------
	public boolean initializeNominals(String[] ClassNameList )
	{
		if( ClassNameList == null ) return false;
		nominals = new String[ ClassNameList.length ];
		for(int i=0;i<ClassNameList.length;i++) nominals[i] = ClassNameList[i];
		for(int i=0;i<nominals.length;i++)
		{
			if( nominals[i] == null ) { do_error("Null value on classnamelist"); return false; }
			nominals[i] = nominals[i].trim().toUpperCase();
		}
		confusionMatrix = new int[ nominals.length ][ nominals.length ];  // Predicted - Anticipated
		return true;
	}
	
	
	private boolean createStaggeredSignatures()
	{
	  try {
		int NbrOfNeurons = super.getNbrOfNeuronsInLayer();
		if( NbrOfNeurons < 1 ) {
       	    do_error("Neurons not initialized in output layer");
       	    return false;
        }
		if( this.getNeurons() == null ) {
			do_error("Cannot determine neurons");
		    return false;
		}
		if( this.getNeurons().length < 1 ) {
			do_error("There are no neurons");
		    return false;
		}
		ActivationFunction func = this.getNeurons()[0].getActivationFunction();
		if( func == null ) {
		    do_error("Cannot determine activation function on output");
	        return false;
		}
		//
		upperThresholdAbsolute = func.getUpperThreshold();
		double upper = func.getUpperThreshold() - EPSILON;
		double lower = func.getLowerThreshold() + EPSILON;
		double step = (upper - lower) / ((double)NbrOfNeurons - 1);
		//
		double[] items = new double[ NbrOfNeurons ];
		double x = lower;
		for(int i=0;i<NbrOfNeurons;i++)
		{
			if( i == (NbrOfNeurons-1)) x = upper;  // enkel voor laatste
			items[i] = x;
			x += step;
		}
		
		/*
		String ss = "";
		for(int i=0;i<NbrOfNeurons;i++) ss += "[" + items[i] + "]";
		System.out.println( ss );
		*/
		
		double[][] sigs = new double[ NbrOfNeurons ][ NbrOfNeurons ];
		int attempts = NbrOfNeurons * 100000;
		int current = 0;
		boolean OK = false;
		for(int trials=0;trials<attempts;trials++)
		{
			boolean[] drawn = new boolean[ NbrOfNeurons ];
			for(int i=0;i<drawn.length;i++) drawn[i] = false;
			for(int i=0;i<NbrOfNeurons;i++)   // draw cards
			{
			   sigs[ current ][ i ] = -1;	
			   for(int zz=0;zz<attempts;zz++)  // proberen
			   {
				   int card = rand.nextInt(NbrOfNeurons);
				   if( drawn[card] ) continue;
				   drawn[ card ] = true;
				   sigs[ current ][ i ] = card;
 //System.out.println( "slot" + i + " -> " + sigs[ current ][i] + " " + card );
                   break;
			   }
			   if( sigs[ current ][ i ] < 0 ) {
				   do_error("Could not drawn different cards in [" + attempts + "] [slot=" + i +"]"); return false;
			   }
			}
			//  does this combination already exist ?
			OK = true;
			for(int i=0;i<current;i++)
			{
			  int counter = 0;
			  for(int j=0;j<NbrOfNeurons;j++) 
			  {
				  if( sigs[i][j] == sigs[current][j] ) counter++;
			  }
			  if( counter ==NbrOfNeurons ) OK = false;
			}
			if( OK ) {
				current++;
				if( current == NbrOfNeurons ) break;
			}
		}
		if( OK == false ) { do_error("Could not create signatures in [" + attempts + "] [reached=" + current + "]"); return false; }
		
		/*
		{
		 String ss = "";
		 for(int i=0;i<NbrOfNeurons;i++)
		 {
		  for(int j=0;j<NbrOfNeurons;j++) ss += "(" + sigs[i][j] + ")";
		  ss += "\n";	
		 }
		 System.out.println( ss );
		}
		*/
		
		// substitute items
		for(int i=0;i<NbrOfNeurons;i++)
		{
			for(int j=0;j<NbrOfNeurons;j++)
			{
			  double subs = items[ (int)sigs[i][j] ];
			  double orig = subs;
			  double percent = (double)rand.nextInt(100) - (double)50;
			  double wiggle = step * (percent / (double)100);
			  //
			  if( subs == lower ) wiggle = Math.abs( wiggle );
			  if( subs == upper ) wiggle = Math.abs( wiggle ) * -1; 
			  //
			  subs = ((int)((subs + wiggle) * (double)100000)) / (double)100000;
			  if( subs < lower ) { do_error("make signature system 1 x=" + subs + " lower=" + lower); return false; }
			  if( subs > upper ) { do_error("make signature system 2 x=" + subs + " upper=" + upper + " " + percent + " " + wiggle + " " + orig); return false; }
			  sigs[i][j] = subs;
			}
		}

		// make vectors
		signatures = new cmcVector[ NbrOfNeurons ];
		for(int i=0;i<NbrOfNeurons;i++)
		{
			double[] vals = new double[ NbrOfNeurons ];
			for(int j=0;j<NbrOfNeurons;j++) vals[j] = sigs[i][j];
            signatures[i] = new cmcVector( vals );			
		}
		
		/*
		{
		 String ss = "";
		 for(int i=0;i<NbrOfNeurons;i++)
		 {
		  ss += signatures[i].show() + "\n";	
		 }
		 System.out.println( ss );
		}
		*/
		return true;
	  }
	  catch(Exception e ) {
		  e.printStackTrace();
		  do_error("cannot create staggered " + e.getMessage());
		  return false;	
	  }
	}
	
	
	
	//-----------------------------------------------------------------------
	public cmcVector[] makeSignatures( )
	{
 //System.out.println( "makeSignatures called");
         if( super.getNbrOfNeuronsInLayer() < 1 ) {
        	 do_error("Neurons not initialized in output layer");
        	 return null;
         }
         
		 signatures = null;
		 switch( assessmenttipe )
	     {
	    	 case ONE_HOT_ENCODED_MAXARGS : ;
	    	 case ONE_HOT_ENCODED_WA_TWIST_MAXARGS :
	    	 case ONE_HOT_ENCODED_EUCLIDIAN : ;
	    	 case ONE_HOT_ENCODED_WA_TWIST_EUCLIDIAN :   	 {
	    		 signatures = new cmcVector[  super.getNbrOfNeuronsInLayer() ];
	    		 boolean wiggle = ((assessmenttipe == cmcMLPenums.ASSESSMENT_TYPE.ONE_HOT_ENCODED_WA_TWIST_MAXARGS)||(assessmenttipe == cmcMLPenums.ASSESSMENT_TYPE.ONE_HOT_ENCODED_WA_TWIST_EUCLIDIAN) ) ? true : false;
	    		 for(int neuronIndex=0;neuronIndex<signatures.length;neuronIndex++)
	    		 {
	    			    double epsilon = wiggle ? EPSILON : 0;
	    				double[] val = new double[ super.getNbrOfNeuronsInLayer()  ];
	    	    		for(int j=0;j<val.length;j++) 
	    	 			{
	    	 				ActivationFunction func = this.getNeurons()[j].getActivationFunction();
	    	 				upperThresholdAbsolute = func.getUpperThreshold();
	    	 				double spread = (func.getUpperThreshold() - func.getLowerThreshold()) * (double)0.80;
	    	 				double zwieber = wiggle ? ((double)(j+1) * spread) / (double)val.length : 0;
	    	 				val[j] = (j==neuronIndex) ? func.getUpperThreshold() - epsilon : func.getLowerThreshold() + epsilon + zwieber;  // niet NAN
	    	 			}	 
	    	    		signatures[ neuronIndex ] = new cmcVector( val );
	    		 }
	    		 break;
	         }
	    	 case STAGGERED : {
	    		 if( createStaggeredSignatures() == false ) return null;
	    		 break;
	    	 }
	    	 default : {
	    		 do_error("Unsupported assessment type " + assessmenttipe ); return null;
	    	 }
	     }
		 
         // debug		
		 /*
		 String ss = "";
		 for(int i=0;i<signatures.length;i++) ss += signatures[i].show() + "\n";
		 System.out.println("Signatures:\n" + ss);
		 */
		 
		 return signatures;
	}
	
	//-----------------------------------------------------------------------
	public boolean setTargetValuesFast( double[][] data , String[] ClassNameList )
	{
		if( ClassNameList == null ) { do_error("Null classnames"); return false; }
		if( nominals == null ) { do_error( "Nominal list not intialized"); return false; }
		if( nominals.length != (ClassNameList.length) ) { do_error( "Nominals and classlist error"); return false; }
		//			
		try {
		  if( data == null ) { do_error("Null data"); return false; }
		  if( data.length !=  super.getNbrOfMiniBatchSamples() ) { do_error( "Number of samples mismatch") ; return false; }
		  if( data[0].length != 1 ) { do_error( "Number of neurons " + data.length + " " + super.getOutputValues().length) ; return false; }
		  if( ClassNameList.length != super.getNbrOfNeuronsInLayer() ) { do_error( "Number of neurons does not match number of nominal values") ; return false; }

		  // Targets [ samples ] Vector met lengte gelijk aan #neuronen
		  // Data [ samples ] [ 0 ]
		  // class index geeft aan welke neuron op 1 moet komen
		  targetIndex = new int[data.length ];
		  
		  // Maak N vectoren die overeenstemmen met de targetwaarden
		  if( signatures == null ) { do_error("Signatures not initialized - null"); return false; }
		  //
		  for(int i=0 ; i<data.length ; i++)
		  {
			int neuronIndex = (int)data[i][0];
			if( neuronIndex < 0 )  { do_error("Invalid neuronindex - less 0"); return false; }
			if( neuronIndex >= super.getNbrOfNeuronsInLayer() )  { do_error("Invalid neuronindex - too large "); return false; }
			
			for(int j=0;j<super.getNbrOfNeuronsInLayer();j++) super.getTargets()[i].setValue(j, signatures[ neuronIndex ].getVectorValues()[j] );
			targetIndex[ i ] = neuronIndex;  // accelerator
		  }
		  
	      // check the target
		  cmcVector[] tgt = super.getTargets();
		  if( tgt == null ) {
			  do_error("Set target issue " + this.getTipe() + " Target array null" );
			  return false;  
		  }
		  if( tgt.length !=  super.getNbrOfMiniBatchSamples() ) { do_error( "Number of Targets mismatch") ; return false; }
		  for(int i=0;i<tgt.length;i++)
		  {
			cmcVector vec = tgt[i];
			if( tgt[i] == null ) { do_error("Target " + i + " vector is null"); return false; }
			if( vec.getDimension() != super.getNbrOfNeuronsInLayer() ) { do_error( "Number of neurons " + vec.getDimension() + " " + super.getNeurons() ) ; return false; }
			for(int j=0;j<vec.getDimension();j++)
			{
				if( Double.isNaN( vec.getVectorValues()[j] ) ) {
					do_error( "NAN on the target vector");
					return false;
				}
			}
		  }
		  //
		  return true;
		}
		catch(Exception e) {
			do_error("Set target issue " + this.getTipe() + " " + e.getMessage() );
			return false;
		}
	}
	
	//-----------------------------------------------------------------------
	private boolean checkTargetsAndOutputsFast( cmcVector[] targets , cmcVector[] outputs )
	{
		if( targets == null ) { do_error("Null targets"); return false; }
		if( targets.length != this.getNbrOfMiniBatchSamples() ) { do_error("Number of minibatchsamples and target values mismatch"); return false; }
		if( targets[0].getDimension() != this.getNbrOfNeuronsInLayer() ) { do_error("Number of targets does not match number of neurons"); return false; }
	    //
		if( outputs == null ) { do_error("Null outputs"); return false; }
		if( outputs.length != targets.length ) { do_error("Number of samples in output and target do not match"); return false; }
		if( outputs[0].getDimension() != targets[0].getDimension() ) { do_error("Number of rows in output and target do not match"); return false; }
		//
		return true;
	}
	   
		//-----------------------------------------------------------------------
		public boolean evaluateResultsFast()
		{
		    cmcVector[] outvals = super.getOutputsFast();
		    if( checkTargetsAndOutputsFast( super.getTargets(), outvals ) == false ) { do_error("Check Targets"); return false; }
		    
		    if( nominals == null ) { do_error("Strange - nominal list not initialized"); return false; }
		    if( nominals.length == 0 ) { do_error("No nominal entries"); return false; }
		    //
		    double[] outStack = new double[ this.getNbrOfNeuronsInLayer() ];
		    double[] targetStack = new double[ outStack.length ];
		    //
		    confusionMatrix = new int[ nominals.length ][ nominals.length ];
		    for(int i=0;i<confusionMatrix.length;i++)
		    {
		    	for(int j=0;j<confusionMatrix[i].length;j++) confusionMatrix[i][j]=0;
		    }
		    //
		    int sum = 0;
		    for(int k=0;k<this.getNbrOfMiniBatchSamples();k++)
		    {
		    	for(int i=0;i<this.getNbrOfNeuronsInLayer();i++)
		        {
			       outStack[i] = outvals[k].getVectorValues()[i];    
			       targetStack[i] = super.getTargets()[k].getVectorValues()[i];
		        }
		    	//
		    	int predicted = -1;
		    	int anticipated = -1;
		    	switch( assessmenttipe )
		    	{
		    	 case ONE_HOT_ENCODED_MAXARGS : ;
		    	 case ONE_HOT_ENCODED_WA_TWIST_MAXARGS : {
			    	predicted   = getItemHighestScore( outStack );
			    	anticipated = getItemHighestScore( targetStack );
		    		break;
		    	 }
		    	 case STAGGERED : ;
		    	 case ONE_HOT_ENCODED_EUCLIDIAN : ;
		    	 case ONE_HOT_ENCODED_WA_TWIST_EUCLIDIAN : {
			    	predicted   = getClosestItem( outStack );
			    	anticipated = getClosestItem( targetStack );
		    		break;
		    	 }

		    	 default : {
		    		 do_error("Unsupported assessment type " + assessmenttipe ); return false;
		    	 }
		    	}
		    	if( (predicted < 0) || (predicted >= confusionMatrix.length) ) { do_error("outputlayer.evaluateResultsFast() - Assessing output [" + predicted + "]"); return false; }
		    	
		    	int pdx = targetIndex[ k ];
//  TODO - calculate anticipated via targetstack can be remved and use PDX instead
		    
		        if( pdx != anticipated ) {
		        	do_error("outputlayer.evaluateResultsFast() - systeeem fout TargetIndex differs from recalculated getItemHighestscore");
		        	return false;
		        }
		    	
		    	
		    	if( (anticipated < 0) || (anticipated >= (confusionMatrix.length)) ) { 
		        	//if( anticipated == this.INCONCLUSIVE_VALUE ) { do_error( "error - anticipated idx cannot be inconclusive [" + anticipated + "["); return false; }
		    		do_error("outputlayer.evaluateResultsFast() - getting highest score target [" + anticipated + "]"); return false; 
		    	}
		    	confusionMatrix[ predicted ][ anticipated ] = confusionMatrix[ predicted ][ anticipated ] + 1;
		    	sum++;
		    }
		    //
		    int hits = 0;
		    int total = 0;
		    for(int i=0;i<confusionMatrix.length;i++)
		    {
		    	for(int j=0;j<confusionMatrix[i].length;j++)
		    	{
		    		total += confusionMatrix[i][j];
		    		if( i == j ) hits += confusionMatrix[i][j];
		    	}
		    }
		    if( total != sum ) { do_error("outputlayer.evaluateResultsFast() - Internal error " + sum + " " + total); return false; }
		    //
		    epochAccuracy =  (double)hits / (double)total;
		    // debug
		    /*
		    if( this.calcAccuracy() != epochAccuracy ){ do_error("Internal error accuracy"); return false; }
		    if( this.calcAccuracy() > 1) {
		    	do_error("System error - Accuracy exceeds 1 [#Hits=" + hits + "] [#Total=" + total + "]");
		    	System.exit(1);
		    }
		    */
		    currentHits = hits;
		    currentTotal = total;
		    
//System.err.println( "ACC->" + epochAccuracy );
		 	return true;
		}

		// ARGMAX
		//-----------------------------------------------------------------------
		private int getItemHighestScore( double[] stack )
		{
		    int idx = 0;
		    double mx = stack[0];
		    for(int i=0;i<stack.length;i++)
		    {
		    	if( mx >= stack[i] ) continue;
		    	mx = stack[i];
		    	idx=i;
		    }
		    // has threshold been passed
		    if( mx > upperThresholdAbsolute ) {
		    	do_error( "getItemHighestScore - upper threshold reached - should not happen " + mx + " " + upperThresholdAbsolute );
		    }
		    // does it occur more than once
		    /*
		    int counter=0;
		    for(int i=0;i<stack.length;i++)
		    {
		    	if( mx == stack[i] ) counter++;
		    }
		    */
		    /*
		    if( counter != 1 ) {
		    	if( extender == 1 )	return this.INCONCLUSIVE_VALUE;
		    }
		    */
		    //
		    /*
		    {
		    	String ss = "";
		    	for(int i=0;i<stack.length;i++) ss += "[" + stack[i] + "]";
		    	System.err.println( ss + " " + idx + " " + counter);
		    }
		    */
		    //
		    return idx;
		}
	
		
	// Euclidean distance
   //------------------------------------------------------
	private int getClosestItem( double[] stack )
	{
		double[] distances = new double[ signatures.length ];
	    for(int i=0;i<signatures.length;i++)
	    {
	    	double dist = 0;
	    	for(int j=0;j<signatures[i].getDimension();j++)
	    	{
	    		dist += Math.pow( signatures[i].getVectorValues()[j] - stack[j] , 2 );
	    	}
	    	distances[i] = Math.sqrt( dist );
	    }
	    double min = distances[0];
	    int index = 0;
	    for(int i=1;i<signatures.length;i++)  // 1 hoeft niet
	    {
	    	if( min > distances[i]) {
	    		min = distances[i];
	    		index = i;
	    	}
	    }
	    int counter = 1;
	    if( counter > 1 ) {
	    	System.out.println( "Euclidean - multiple signatures are close");
	    }
	    return index;
	}
	
	
    //-----------------------------------------------------------------------
	public String getExplicitedClassListVerbose()
	{
		if( nominals == null ) return "NULL nominals";
		String ss="";
		for(int i=0;i<nominals.length;i++)
		{
			ss += "[" + nominals[i] + "]";
		}
		return ss;
	}
	
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	public CostFunction getCostfunction() {
		return costfunction;
	}

	public void setCostfunction(CostFunction costfunction) {
		this.costfunction = costfunction;
	}
	
	/*
	public double getTotalCost()
	{
		return this.calculateTotalCostFast();
	}
	*/
	public double getEpochAccuracy()
	{
			return this.epochAccuracy;
	}
	public int[][] getConfusionMatrix()
	{
		return confusionMatrix;
	}
	
	public String[] getExtendedClassNameList() 
	{
		return nominals;
	}
	public String[] getNominals()  // only used by DAO
	{
		return nominals;
	}
	public void setNominals(String[] ll) // only used by DAO
	{
		this.nominals = ll;
	}
	
	public int getHits()
	{
		return this.currentHits;
	}
	public int getRowsAssessed()
	{
		return this.currentTotal;
	}
	
	public double[][] getTargetDump()
	{
		if( super.getTargets() == null ) return null;
		double[][] ret = new double[ super.getTargets().length ][ super.getTargets()[0].getDimension() ];
		for(int i=0;i<ret.length;i++)
		{
			for(int j=0;j<ret[i].length;j++ ) ret[i][j] = super.getTargets()[i].getVectorValues()[j];
		}
		return ret;
	}
	
	public cmcVector[] getSignatures()
	{
		return this.signatures;
	}
	
	public cmcMLPenums.ASSESSMENT_TYPE getAssessmentTipe()
	{
		return this.assessmenttipe;
	}
	
	public boolean setSignatures( double[][] vals)
	{
		if( vals == null )  return false;
		if( vals.length != this.getNbrOfNeuronsInLayer() ) { do_error("setSignature - nbr of neuron mismatch"); return false; }
		if( vals[0].length != this.getNbrOfNeuronsInLayer() ) { do_error("setSignature - nbr of neuron mismatch II"); return false; }
		if( this.getSignatures() == null ) {
			signatures = new cmcVector[ this.getNbrOfNeuronsInLayer() ];
		}
		for(int i=0;i<vals.length;i++)
		{
			if( signatures[i] == null ) signatures[i] = new cmcVector( vals[i] ); 
			for(int j=0;j<vals[i].length;j++)
			{
				signatures[i].setValue( j , vals[i][j] );
			}
		}
		return true;
	}
	
	
}
