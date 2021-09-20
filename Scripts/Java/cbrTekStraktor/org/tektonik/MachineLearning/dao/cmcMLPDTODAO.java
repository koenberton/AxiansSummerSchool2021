package org.tektonik.MachineLearning.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.tektonik.MachineLearning.ARFF.ARFFCategoryCoreDTO;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTO;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.hiddenNeuralLayer;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.inputNeuralLayer;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.lossNeuralLayer;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.neuralLayer;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.neuralNetwork;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.neuron;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.outputNeuralLayer;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalpurpose.gpPrintStream;
import org.tektonik.tools.linearAlgebra.cmcVector;
import org.tektonik.tools.logger.logLiason;

public class cmcMLPDTODAO {

	cmcProcSettings xMSet=null;
	logLiason logger=null;
	cmcMLPenums penums = null;
	private cmcMachineLearningModelDAO mldao = null;
	

	private cmcMLPenums.ACTIVATION_FUNCTION_TYPE daoActivationFunctionType = null;
	private cmcMLPenums.COST_FUNCTION_TYPE daoCostFunctionType = null;
	private cmcMLPenums.ASSESSMENT_TYPE daoAssessmentType = null;
	private double daoAFactor = Double.NaN;
	private double daoDropOutRatio = Double.NaN;
	private String[] daoNominals = null;
	private neuralLayer daoPreviousLayer = null;
	private String LastErrorMsg=null;
	private double daoNeuronWeights[][] = null;
    private double daoNeuronGradients[][] = null;
    private double daoNeuronSignatures[][] = null;
	
	//------------------------------------------------------------
    private void do_log(int logLevel , String sIn)
	//------------------------------------------------------------
    {
       if( logger != null ) logger.write( this.getClass().getName() , logLevel , sIn);
       else 
       if (logLevel == 0 ) System.err.println(sIn);
       else System.out.println(sIn);
    }
	//------------------------------------------------------------
    private void do_error(String sIn)
	//------------------------------------------------------------
    {
    	LastErrorMsg=sIn;
    	do_log(0,sIn);
    }
    //------------------------------------------------------------
    public String getLasterrorMsg()
    //------------------------------------------------------------
    {
    	return LastErrorMsg;
    }
    
    //------------------------------------------------------------
    public cmcMLPDTODAO(cmcProcSettings is,logLiason ilog)
    //------------------------------------------------------------
    {
    	xMSet = is;
		logger=ilog;
		penums = new cmcMLPenums();
		mldao = new cmcMachineLearningModelDAO( xMSet , logger );
    }
    
    //------------------------------------------------------------
    public boolean writeMLPDTO(  String XMLFileName , cmcMLPDTO mlpdto )
    //------------------------------------------------------------
    {
    	gpPrintStream aps = new gpPrintStream( XMLFileName , xMSet.getCodePageString());
		aps.println(xMSet.getXMLEncodingHeaderLine());
		aps.println("<!-- Application : " + xMSet.getApplicDesc() + " -->");
		aps.println("<!-- Start : " + 	(xMSet.xU.prntStandardDateTime(System.currentTimeMillis())).toUpperCase() + " -->" );
		
		aps.println("<MLPContainer>");
		//
        if( writeMLPDTOSection( mlpdto , aps) == false) return false;
		//
        if( writeNeuralNetwork ( mlpdto.getNtw() , aps ) == false ) return false;
        //  
        if( writeTimings ( mlpdto , aps ) == false ) return false;
        //
		aps.println("</MLPContainer>");
		aps.close();

    	return true;
    }
    
    //------------------------------------------------------------
    private boolean dumpCategories(ARFFCategoryCoreDTO[] flist , gpPrintStream aps )
    //------------------------------------------------------------
    {
    	try {
    	  boolean ib = mldao.dumpARFFCategoryCoreDTOList(flist, aps);
    	  return ib;
    	}
    	catch( Exception e ) {
    	  do_error("Dumping features"); 
    	  do_error( xMSet.xU.LogStackTrace(e));
    	  return false;	
    	}
    }
    
    //------------------------------------------------------------
    private boolean writeTimings( cmcMLPDTO mlpdto , gpPrintStream aps)
    //------------------------------------------------------------
    {
    	if( mlpdto == null ) return false;
    	int naant = 0;
    	for(int i=0;i<mlpdto.getRuntimes().length;i++)
    	{
          if( mlpdto.getRuntimes()[i][0] > 0L ) naant++;	
    	}
    	aps.println("<Timings>");
    	aps.println("<Runs>" + naant + "</Runs>");
    	aps.println("<![CDATA[");
    	for(int i=0;i<naant;i++)
    	{
    	  String ss="";
    	  for(int j=0;j<mlpdto.getRuntimes()[i].length;j++)
    	  {
    		  ss += "[" + mlpdto.getRuntimes()[i][j] + "]";
    	  }
    	  aps.println(ss);
    	}
    	aps.println("]]>");
    	aps.println("</Timings>");
    	return true;
    }
    
    //------------------------------------------------------------
    public boolean writeMLPDTOSection( cmcMLPDTO mlpdto , gpPrintStream aps)
    //------------------------------------------------------------
    {
    	try {
    		
    	 aps.println("<MLPDTO>");
    	 aps.println("<ModelApplicationDomain>General</ModelApplicationDomain>");
    	 //
  		 aps.println("<MLPModelEvaluation>");
  		 aps.println("<Evaluation>EMPTY</Evaluation>");
  		 aps.println("</MLPModelEvaluation>");
    	 //
  		 aps.println("<NbrOfEpochsPerformed>" + mlpdto.getEpochsPerformed() + "</NbrOfEpochsPerformed>");
    	 aps.println("<NbrOfFeatures>" + mlpdto.getNbrOfFeatures()  + "</NbrOfFeatures>" );
		 aps.println("<NbrOfRows>" + mlpdto.getNbrOfRows() + "</NbrOfRows>" );
    	 //
    	 aps.println("<MLPDTOCore>");
		 aps.println("<ARFFFileName>" + mlpdto.getLongARFFFileName()  + "</ARFFFileName>" );
		 aps.println("<NbrOfHiddenLayers>" + mlpdto.getNbrOfHiddenLayers()  + "</NbrOfHiddenLayers>" );
		 aps.println("<NbrOfNeuronsPerHiddenLayer>" + mlpdto.getNbrOfNeuronsPerHiddenLayer()  + "</NbrOfNeuronsPerHiddenLayer>" );
		 aps.println("<MiniBatchSize>" + mlpdto.getSizeOfMiniBatch()  + "</MiniBatchSize>" );
		 aps.println("<ActivationFunctionType>" + mlpdto.getActivationFunction()  + "</ActivationFunctionType>" );
		 aps.println("<OutputActivationFunctionType>" + mlpdto.getOutputActivationFunction()  + "</OutputActivationFunctionType>" );
		 aps.println("<ActivationAFactor>" + mlpdto.getAFactor()  + "</ActivationAFactor>" );
		 aps.println("<DropOutPercentage>" + mlpdto.getDropOutRatio()  + "</DropOutPercentage>" );
		 aps.println("<CostFunctionType>" + mlpdto.getCostFunctionType()  + "</CostFunctionType>" );
		 aps.println("<OptimizationType>" + mlpdto.getOptimizationType()  + "</OptimizationType>" );
		 aps.println("<AssessmentType>" + mlpdto.getAssessmentType()  + "</AssessmentType>" );
		 aps.println("<WeightInitializationType>" + mlpdto.getWeightStrategy()  + "</WeightInitializationType>" );
		 aps.println("<LearningRate>" + mlpdto.getLearningRate()  + "</LearningRate>" );
		 aps.println("<MaxNbrOfEpochs>" + mlpdto.getMaximumNumberOfEpochs()  + "</MaxNbrOfEpochs>" );
		 aps.println("<IgnoredFeatures>" + (mlpdto.getIgnoredFeatures()==null?"":mlpdto.getIgnoredFeatures()) + "</IgnoredFeatures>" );
		 aps.println("</MLPDTOCore>");
		 //
		 if( dumpCategories( mlpdto.getFeatures() , aps ) == false ) return false;
		 // normalisation
		 aps.println("<!-- 2021 version has stat values for all features -->");
 		 aps.println("<Normalisation>");
 		 String sm = "";
 		 String ss = "";
 		 for(int i=0;i<mlpdto.getNormalizerMean().length;i++)
 		 {
 			sm += "[" + mlpdto.getNormalizerMean()[i] + "]";
 			ss += "[" + mlpdto.getNormalizerStdDev()[i] + "]";
 		 }
 		 aps.println("<NormalisationMean>" + sm + "</NormalisationMean>");
 		 aps.println("<NormalisationStdDev>" + ss + "</NormalisationStdDev>");
     	 aps.println("</Normalisation>");
 	     //	 
     	 aps.println("</MLPDTO>");
		 return true;
    	}
    	catch(Exception e) {
    		do_error("Writing MLPDTO " + e.getMessage() );
    		return false;
    	}
    }
   
    // read until /MLPDTO
    //------------------------------------------------------------
    public cmcMLPDTO readMLPDTOSection( BufferedReader reader , String FName )
    //------------------------------------------------------------
    {
    	cmcMLPDTO dto = new cmcMLPDTO( FName );
    	dto.setLongARFFFileName( null );
    	dto.setActivationFunction( null );
    	dto.setCostFunctionType( null );
    	dto.setOptimizationType( null );
    	dto.setNbrOfHiddenLayers( -1 );
    	dto.setNbrOfNeuronsPerHiddenLayer( -1 );
    	dto.setSizeOfMiniBatch( -1 );
    	dto.setLearningRate( Double.NaN );
    	dto.setMaximumNumberOfEpochs( -1 );
    	dto.setAFactor( Double.NaN );
    	dto.setDropOutRatio( Double.NaN );
    	dto.setAssessmentType( null );
    	dto.setWeightStrategy(null);
        //
    	try {
    		
    		String sLijn=null;
    		boolean foundend=false;
    		boolean inCore=false;
    		boolean inCategories=false;
        	ARFFCategoryCoreDTO[] categories = null;
    	    
        	while ((sLijn=reader.readLine()) != null) {
    	     if( sLijn.trim().startsWith("--") ) continue;
    	     if( sLijn.indexOf("</MLPDTO>") >= 0 ) { foundend=true; break; }
    	   	 
    	     // MLP 
    	   	 if( sLijn.indexOf("<NbrOfFeatures>") >= 0 ) {
 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NbrOfFeatures"));
 			    if( ni < 0 ) { do_error("Invalid number of features [" + ni + "]"); return null; }
 			    dto.setNbrOfFeatures(ni);
 				continue;	 
 			 }
    	     if( sLijn.indexOf("<NbrOfRows>") >= 0 ) {
 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NbrOfRows"));
 			    if( ni < 0 ) { do_error("Invalid number of rows [" + ni + "]"); return null; }
 			    dto.setNbrOfRows(ni);
 				continue;	 
 			 }
    	     if( sLijn.indexOf("<NbrOfEpochsPerformed>") >= 0 ) {
  				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NbrOfEpochsPerformed"));
  			    if( ni < 0 ) { do_error("Invalid number of rows [" + ni + "]"); return null; }
  			    dto.setEpochsPerformed(ni);
  				continue;	 
  			 }
    	     // MLP DTO Core
    	     if( sLijn.indexOf("<MLPDTOCore>") >= 0 ) { inCore=true; continue; }
    	     if( sLijn.indexOf("</MLPDTOCore>") >= 0 ) { inCore=false; continue; }
    	     
    	     if( inCore  ) {
    	   	  if( sLijn.indexOf("<ARFFFileName>") >= 0 ) {
  				dto.setLongARFFFileName( xMSet.xU.extractXMLValue(sLijn,"ARFFFileName").trim() );
  				continue;	 
  			  }
    	      if( sLijn.indexOf("<NbrOfHiddenLayers>") >= 0 ) {
  				int nbr = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NbrOfHiddenLayers"));
  			    if( nbr < 0 ) { do_error("Invalid number of hidden layers [" + sLijn + "]"); return null; }
  			    dto.setNbrOfHiddenLayers(nbr);
  				continue;	 
  			  }
    	      if( sLijn.indexOf("<NbrOfNeuronsPerHiddenLayer>") >= 0 ) {
   				int nbr = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NbrOfNeuronsPerHiddenLayer"));
   			    if( nbr < 0 ) { do_error("Invalid number of neurons [" + sLijn + "]"); return null; }
   			    dto.setNbrOfNeuronsPerHiddenLayer(nbr);
   				continue;	 
   			  }
    	      if( sLijn.indexOf("<IgnoredFeatures>") >= 0 ) {
    	    	    String ss = xMSet.xU.extractXMLValue(sLijn,"IgnoredFeatures").trim();
    	    	    if( ss.length() == 0) ss = null;
    				dto.setIgnoredFeatures( ss );
    				continue;	 
   			  }
    	      if( sLijn.indexOf("<MiniBatchSize>") >= 0 ) {
    				int nbr = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"MiniBatchSize"));
    			    if( nbr < 0 ) { do_error("Invalid minibatch size [" + sLijn + "]"); return null; }
    			    dto.setSizeOfMiniBatch(nbr);
    				continue;	 
    		  }
    	      if( sLijn.indexOf("<MaxNbrOfEpochs>") >= 0 ) {
 				int nbr = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"MaxNbrOfEpochs"));
 			    if( nbr < 0 ) { do_error("Invalid max nbr epoch [" + sLijn + "]"); return null; }
 			    dto.setMaximumNumberOfEpochs(nbr);
 				continue;	 
 		      }
    	      if( sLijn.indexOf("<ActivationAFactor>") >= 0 ) {
    				double dd = xMSet.xU.NaarDoubleNAN( xMSet.xU.extractXMLValue(sLijn,"ActivationAFactor"));
    			    if( Double.isNaN(dd) ) { do_error("Invalid activation A factor [" + sLijn + "]"); return null; }
    			    dto.setAFactor(dd);
    				continue;	 
    		  }
    	      // 
    	      if( sLijn.indexOf("<DropOutPercentage>") >= 0 ) {
  				double dd = xMSet.xU.NaarDoubleNAN( xMSet.xU.extractXMLValue(sLijn,"DropOutPercentage"));
  			    if( Double.isNaN(dd) ) { do_error("Invalid Dropout ratio [" + sLijn + "]"); return null; }
  			    dto.setDropOutRatio(dd);
  				continue;	 
  		      }
    	      if( sLijn.indexOf("<LearningRate>") >= 0 ) {
  				double dd = xMSet.xU.NaarDoubleNAN( xMSet.xU.extractXMLValue(sLijn,"LearningRate"));
  			    if( Double.isNaN(dd) ) { do_error("Invalid learning rate [" + sLijn + "]"); return null; }
  			    dto.setLearningRate(dd);
  				continue;	 
  		      }
    	      if( sLijn.indexOf("<ActivationFunctionType>") >= 0 ) {
   				String ss = xMSet.xU.extractXMLValue(sLijn,"ActivationFunctionType").trim();
   				cmcMLPenums.ACTIVATION_FUNCTION_TYPE at = penums.getActivationFunctionType(ss);
   				if( at == null ) { do_error("Invalid activation [" + sLijn + "]"); return null; }
   				dto.setActivationFunction(at);
   				continue;
   			  }
    	      if( sLijn.indexOf("<OutputActivationFunctionType>") >= 0 ) {
     				String ss = xMSet.xU.extractXMLValue(sLijn,"OutputActivationFunctionType").trim();
     				cmcMLPenums.ACTIVATION_FUNCTION_TYPE at = penums.getActivationFunctionType(ss);
     				if( at == null ) { do_error("Invalid output activation [" + sLijn + "]"); return null; }
     				dto.setOutputActivationFunction(at);
     				continue;
     			  }
    	      if( sLijn.indexOf("<CostFunctionType>") >= 0 ) {
    				String ss = xMSet.xU.extractXMLValue(sLijn,"CostFunctionType").trim();
    				cmcMLPenums.COST_FUNCTION_TYPE at = penums.getCostFunctionType(ss);
    				if( at == null ) { do_error("Invalid cost function [" + sLijn + "]"); return null; }
    				dto.setCostFunctionType(at);
    				continue;
    		  }
    	      if( sLijn.indexOf("<OptimizationType>") >= 0 ) {
  				String ss = xMSet.xU.extractXMLValue(sLijn,"OptimizationType").trim();
  				cmcMLPenums.OPTIMIZATION_TYPE at = penums.getOptimizationType(ss);
  				if( at == null ) { do_error("Invalid optimization tipe [" + sLijn + "]"); return null; }
  				dto.setOptimizationType(at);
  				continue;
  		      }
    	      if( sLijn.indexOf("<AssessmentType>") >= 0 ) {
    				String ss = xMSet.xU.extractXMLValue(sLijn,"AssessmentType").trim();
    				cmcMLPenums.ASSESSMENT_TYPE at = penums.getAssessmentType(ss);
    				if( at == null ) { do_error("Invalid assessment tipe [" + sLijn + "]"); return null; }
    				dto.setAssessmentType(at);
    				daoAssessmentType = at;
    				continue;
    		  }
    	      if( sLijn.indexOf("<WeightInitializationType>") >= 0 ) {
  				String ss = xMSet.xU.extractXMLValue(sLijn,"WeightInitializationType").trim();
  				cmcMLPenums.WEIGHT_INITIALIZATION_TYPE at = penums.getWeightInitializationType(ss);
  				if( at == null ) { do_error("Invalid weight init tipe [" + sLijn + "]"); return null; }
  				dto.setWeightStrategy(at);
  				continue;
  		  }
    	     } // incore
    	     
    	     //
   	         if( sLijn.indexOf("<NormalisationMean>") >= 0 ) {
	  				String[] swlist = xMSet.xU.getSquaredItems(xMSet.xU.extractXMLValue(sLijn,"NormalisationMean"));
	  				if( swlist == null ) { do_error("Cannot read norm mean" + sLijn); return null; }
	  				if( swlist.length <= 0 ) { do_error("Empty norm mean" + sLijn); return null; }
	  				double[] dlist = new double[ swlist.length ];
	  				for(int i=0;i<swlist.length;i++)
	  				{
	  					double dd = xMSet.xU.NaarDoubleNAN( swlist[i] );
	  					if( Double.isNaN(dd) ) { do_error("conversion error on norm mean" + sLijn ); return null; }
	  					dlist[i] = dd;
	  				}
	  				dto.setNormalizerMean(dlist);
	  				continue;	 
	  		  }
   	          //
   	          if( sLijn.indexOf("<NormalisationStdDev>") >= 0 ) {
	  				String[] swlist = xMSet.xU.getSquaredItems(xMSet.xU.extractXMLValue(sLijn,"NormalisationStdDev"));
	  				if( swlist == null ) { do_error("Cannot read norm stddev" + sLijn); return null; }
	  				if( swlist.length <= 0 ) { do_error("Empty norm stddev" + sLijn); return null; }
	  				double[] dlist = new double[ swlist.length ];
	  				for(int i=0;i<swlist.length;i++)
	  				{
	  					double dd = xMSet.xU.NaarDoubleNAN( swlist[i] );
	  					if( Double.isNaN(dd) ) { do_error("conversion error on norm stdev" + sLijn ); return null; }
	  					dlist[i] = dd;
	  				}
	  				dto.setNormalizerStdDev(dlist);
	  				continue;	 
	  		  }
    	     
   	          //
    	      if( sLijn.indexOf("<Categories>") >= 0 ) {
    	    	 int ncats =  dto.getNbrOfFeatures();
    	    	 if( ncats < 0 ) { do_error("Number of features not found"); return null; }
  	       		 inCategories = true;
  	       		 categories = new ARFFCategoryCoreDTO[ncats];
   	          }
  	       	  if( inCategories ) {
  	       		if( mldao.parseCategory(  sLijn , categories ) == false ) return null;
  	       	  }
  	          if( sLijn.indexOf("</Categories>") >= 0 ) {
  	        	inCategories=false;
  	        	for(int i=0;i<categories.length;i++) 
  	        	{
  	        		if( categories[i] == null ) { do_error( "found uninitialized category"); return null; }
  	        	}
  	        	dto.setFeatures( categories );
  	          }
    	      // categories
  	          
    	    }
    	    if( !foundend ) { do_error("Not closed correctly"); return null; }
    	}
    	catch(Exception e ) {
    		do_error("Writing MLPDTO " + xMSet.xU.LogStackTrace(e) );
    		return null;
    	}
  	    
  	    // Check Core
    	if( dto.getLongARFFFileName() == null ) { do_error("Filename is null"); return null; }
    	if( dto.getActivationFunction() == null ) { do_error("Activation function is null"); return null; }
    	if( dto.getCostFunctionType() == null ) { do_error("Cost function is null"); return null; }	
    	if( dto.getOptimizationType() == null ) { do_error("Optimization type is null"); return null; }	
    	if( dto.getAssessmentType() == null ) { do_error("Assessment type is null"); return null; }	
    	if( dto.getWeightStrategy() == null ) { do_error("Weight Initialization type is null"); return null; }	
    	if( dto.getNbrOfHiddenLayers() < 0 ) { do_error("Nbr of hidden layers not set"); return null; }
    	if( dto.getNbrOfNeuronsPerHiddenLayer() < 0 ) { do_error("Nbr of neurons not set"); return null; }
    	if( dto.getSizeOfMiniBatch() < 0 ) { do_error("minibatch not set"); return null; }
    	if( dto.getMaximumNumberOfEpochs() < 0 ) { do_error("Max nubr of eapochs"); return null; }
    	if( Double.isNaN( dto.getLearningRate() )) { do_error("Learning rate not set"); return null; }
    	if( Double.isNaN( dto.getAFactor() )) { do_error("Learning rate not set"); return null; }
    	if( Double.isNaN( dto.getDropOutRatio() )) { do_error("Dropout ratio not set"); return null; }
  	    // Features
    	int NbrOfFeatures = dto.getNbrOfFeatures();
    	if( dto.getFeatures() == null ) { do_error("Features is NULL"); return null; }
     	if( dto.getFeatures().length != NbrOfFeatures ) { do_error("Features length mismatch"); return null; }
	    if (mldao.checkCategories( dto.getFeatures() , NbrOfFeatures) == false ) return null;
	    //  counts
	    if( dto.getNormalizerMean().length < 0 ) { do_error( "Empty normalizer mean"); return null; }
	    if( dto.getNormalizerStdDev().length < 0 ) { do_error( "Empty normalizer stddev"); return null; }
	    if( dto.getNormalizerStdDev().length != dto.getNormalizerMean().length ) { do_error( "Normaliser mean and sttdev number mismatch"); return null; }
	    if( dto.getNormalizerMean().length != (NbrOfFeatures -1) ) { 
	    	if( dto.getNormalizerMean().length != NbrOfFeatures )  { // 2021 version supports stat for each feature including the last class feature 
	    	  do_error( "Normaliser count is different from feature count minus one"); return null;
	    	}
	    }
	    //
	    for(int i=0;i<dto.getNormalizerMean().length;i++) {
	        	if( Double.isNaN( dto.getNormalizerMean()[i] ) ) {
	        		do_error( "NAN on NormalizedMean "); return null;
	        	}
	        	if( Double.isNaN( dto.getNormalizerStdDev()[i] ) ) {
	        		do_error( "NAN on Normalized STDDEV"); return null;
	        	}
	        	if( dto.getNormalizerStdDev()[i] == 0 ) {
	        		do_error( "Zero value on Normalized StdDev - ignore"); //return null;
	        	}
	    }
        //
  	    return dto;
    }
    
    //------------------------------------------------------------
    private boolean writeNeuralNetwork( neuralNetwork ntw , gpPrintStream aps )
    //------------------------------------------------------------
    {
        if( ntw == null ) { do_error( "NULL network"); return false; }
        //
        aps.println("<NeuralNetwork>");
        //
        aps.println("<NbrOfLayers>" + ntw.getNbrOfLayers() + "</NbrOfLayers>");
        aps.println("<ActualNbrOfNeuronsInInputLayer>" + ntw.getNbrOfInputNeurons() + "</ActualNbrOfNeuronsInInputLayer>");
        aps.println("<ActualNbrOfNeuronsInHiddenLayer>" + ntw.getNbrOfHiddenNeurons() + "</ActualNbrOfNeuronsInHiddenLayer>");
        aps.println("<ActualNbrOfNeuronsInOutputLayer>" + ntw.getNbrOfOutputNeurons() + "</ActualNbrOfNeuronsInOutputLayer>");
        aps.println("<ActualMiniBatchSize>" + ntw.getMiniBatchSize() + "</ActualMiniBatchSize>");
        aps.println("<NeuralLayers>");
        //
        if( writeNeuralLayer( ntw.getInputlayer() , aps ) == false ) return false;
        //
        if( ntw.getHiddenlayers() == null ) { do_error("Cannot fetch hidden layers"); return false; }
        if( ntw.getHiddenlayers().length != (ntw.getNbrOfLayers()-3) ) { do_error("Number of layer mismatch " + ntw.getHiddenlayers().length); return false; }
        for(int i=0;i<ntw.getHiddenlayers().length;i++)
        {
          if( writeNeuralLayer( ntw.getHiddenlayers()[i] , aps ) == false ) return false;	
        }
        //
        if( writeNeuralLayer( ntw.getOutputlayer() , aps ) == false ) return false;
        //
        if( writeNeuralLayer( ntw.getLosslayer() , aps ) == false ) return false;
        aps.println("</NeuralLayers>");
        //
        aps.println("</NeuralNetwork>");
        //
    	return true;
    }
    
    //------------------------------------------------------------
    private boolean writeNeuralLayer( neuralLayer layer , gpPrintStream aps)
    //------------------------------------------------------------
    {
    	if( layer == null ) { do_error("Null layer"); return false; }
    	aps.println("<NeuralLayer>");
    	aps.println("<LayerType>" + layer.getTipe() + "</LayerType>");
    	aps.println("<LayerUID>" + layer.getUID() + "</LayerUID>");
    	aps.println("<NbrOfInputsInLayer>" + layer.getNbrOfInputs() + "</NbrOfInputsInLayer>");
    	aps.println("<NbrOfNeuronsInLayer>" + layer.getNbrOfNeuronsInLayer() + "</NbrOfNeuronsInLayer>");
    	//
    	if( layer.getTipe() == cmcMLPenums.LAYER_TYPE.OUTPUT )
    	{
    		outputNeuralLayer olay = (outputNeuralLayer)layer;
    	    if( olay.getExtendedClassNameList() == null ) { do_error("No extended class name list"); return false; }
            String ss = "";
            for(int i=0;i<olay.getExtendedClassNameList().length;i++)
            {
        	  ss += "[" + olay.getExtendedClassNameList()[i] + "]";
            }
            aps.println("<ExtendedClassNameList>" + ss + "</ExtendedClassNameList>");
            cmcVector[] sigvecs = olay.getSignatures();
            if( olay.getSignatures() == null ) { do_error("DAO - null signatures"); return false; }
            if( sigvecs.length != olay.getNbrOfNeuronsInLayer() ) { do_error("DAO - nbr of signatures does snot match nbr of neurons"); return false; }
            aps.println("<Signatures>"); 
            for(int i=0;i<sigvecs.length;i++)
            {
               cmcVector vec = sigvecs[i];	
               double[] sigs = vec.getVectorValues(); 
           	   if( sigs == null ) { do_error( "Null signatures vector " + i); return false; }
               if( sigs.length != layer.getNbrOfNeuronsInLayer() ) { do_error("SIG number error"); return false; }
               String sl = "";
               for(int j=0;j<sigs.length;j++)
               {
               	sl += "[" + sigs[j] + "]";
               }
               aps.println("<SignatureItem>" + sl + "</SignatureItem>");
            }
            aps.println("</Signatures>");
    	}
    	
    	//
    	if( layer.getNeurons() == null ) { do_error( "Null neurons"); return false; }
    	if( layer.getNeurons().length != layer.getNbrOfNeuronsInLayer() ) { do_error("Neuron number mismatch"); return false; }
     	
    	aps.println("<Neurons>");
        for(int i=0;i<layer.getNeurons().length;i++)
    	{
    		if( writeNeuron( layer.getNeurons()[i] , aps , layer) == false )  return false;
    	}
    	aps.println("</Neurons>");
        //
    	aps.println("</NeuralLayer>");
        //    
    	return true;
    }
    
    //------------------------------------------------------------
    private boolean writeNeuron( neuron nrn , gpPrintStream aps , neuralLayer layer)
    //------------------------------------------------------------
    {
    	if( nrn == null ) { do_error("NULL neuron"); return false; }
    	aps.println("<Neuron>");
    	aps.println("<NeuronIndex>" + nrn.getIndex() + "</NeuronIndex>");
    	aps.println("<LayerUID>" + nrn.getLayerUID() + "</LayerUID>");
    	aps.println("<NeuronUID>" + nrn.getUID() + "</NeuronUID>");
    	aps.println("<NeuronType>" + nrn.getTipe() + "</NeuronType>");
    	//aps.println("<A>" + nrn.getTipe() + "</NeuronType>");
        //if( (nrn.getTipe() != cmcMLPenums.NEURON_TYPE.ACTIVATION_ONLY) && (layer.getTipe() != cmcMLPenums.LAYER_TYPE.INPUT) ) {
        if( layer.getTipe() != cmcMLPenums.LAYER_TYPE.INPUT ) {
        	cmcVector vec = nrn.getFastWeights(true);
        	if( vec == null ) { do_error( "NULL Weight vector"); return false; }
            double[] wei = vec.getVectorValues(); 
        	if( wei == null ) { do_error( "Null Weights"); return false; }
            if( wei.length != layer.getNbrOfInputs() ) { do_error("Weight number error"); return false; }
            String sl = "";
            for(int i=0;i<wei.length;i++)
            {
            	sl += "[" + wei[i] + "]";
            }
        	aps.println("<NeuronWeights>" + sl + "</NeuronWeights>");
        	//
        	vec = nrn.getFastGradients();
        	if( vec == null ) { do_error( "NULL Gradient vector"); return false; }
            wei = vec.getVectorValues(); 
        	if( wei == null ) { do_error( "Null Gradient"); return false; }
            if( wei.length != layer.getNbrOfInputs() ) { do_error("Gradient number error"); return false; }
            sl = "";
            for(int i=0;i<wei.length;i++)
            {
            	sl += "[" + wei[i] + "]";
            }
        	aps.println("<NeuronGradients>" + sl + "</NeuronGradients>");
        }
    	aps.println("</Neuron>");
    	//
    	return true;
    }
    
    //------------------------------------------------------------
    public cmcMLPDTO readFullMLPDTO( String XMLFileName )
    //------------------------------------------------------------
    {
    	 daoActivationFunctionType = null;
    	 daoCostFunctionType = null;
    	 daoAFactor = Double.NaN;
    	 daoDropOutRatio = Double.NaN;
    	 //
    	 if( xMSet.xU.IsBestand( XMLFileName ) == false ) {
    		 do_error( "Cannot find file [" + XMLFileName + "]");
    		 return null;
    	 }
    	 cmcMLPDTO dto = null;
    	 BufferedReader reader=null;
    	 try {
    		File inFile  = new File(XMLFileName);  // File to read from.
  	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), xMSet.getCodePageString()));
  	       	String sLijn=null;
  	       	while ((sLijn=reader.readLine()) != null) {
  	       	  if( sLijn.trim().length() == 0 ) continue;
  	       	  if( sLijn.trim().startsWith("--") ) continue;
  	       	  //
  	       	  if( sLijn.indexOf("<MLPDTO>") >= 0 ) {
	       		   dto = readMLPDTOSection( reader , XMLFileName);
	       		   if( dto == null ) { do_error("Parsing MLPDTO section"); return null; }
	       		   dto.setNtw(null);
	       		   daoActivationFunctionType = dto.getActivationFunction();
	       	       daoCostFunctionType = dto.getCostFunctionType();
	       	       daoAFactor = dto.getAFactor();
	       	       daoDropOutRatio = dto.getDropOutRatio();
	       	  }
  	       	  //
  	   	      if( sLijn.indexOf("<NeuralNetwork>") >= 0 ) {
  	   	    	   if( dto == null ) { do_error("DTO must exist"); return null; }
      		       neuralNetwork ntw = readNeuralNetworkSection( reader , XMLFileName);
      		       if( ntw == null ) { do_error("Parsing Neural network section"); return null; }
      		       dto.setNtw(ntw);
      	      }
  	   	      //
  	   	      if( sLijn.indexOf("<Timings>") >= 0 ) {
  	   	    	   if( dto == null ) { do_error("DTO must exist"); return null; }
      		       if (readTimingsSection( reader , XMLFileName , dto) == false) return null;
      	      }
  	       	}
  		 }
    	 catch (Exception e) {
      		do_error("Could not read model file [" + XMLFileName + "]" + e.getMessage() );
      		e.printStackTrace();
      		return null;
      	 }
      	 finally {
      		try {
      		reader.close();
      		}
      		catch(Exception e ) {
      			do_error("Could not close [" + XMLFileName + "]");
          		return null;
      		}
      	 }
    	 // checks
    	 if( dto == null )  { do_error("DL DTO not read"); return null; }
    	 if( dto.getNtw() == null ) { do_error("Network not read"); return null; }
    	 //
    	 return dto;
    }
    
     //------------------------------------------------------------ 
    private neuralNetwork readNeuralNetworkSection( BufferedReader reader , String FName )
     //------------------------------------------------------------
    {
    	boolean foundend = false;
    	neuralNetwork ntw = new neuralNetwork( -1 , -1 , -1 , -1 , -1 );
        try {
        	String sLijn = null;
        	while ((sLijn=reader.readLine()) != null) {
	       	  	if( sLijn.trim().length() == 0 ) continue;
	       	  	if( sLijn.indexOf("</NeuralNetwork>") >= 0 ) { foundend=true; break; }
	       	    //
	       	  	if( sLijn.indexOf("<NbrOfLayers>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NbrOfLayers"));
	 			    if( ni < 3 ) { do_error("Invalid number of layers [" + ni + "]"); return null; }
	 			    ntw.setNbrOfLayers(ni);
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<ActualNbrOfNeuronsInInputLayer>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"ActualNbrOfNeuronsInInputLayer"));
	 			    if( ni < 1 ) { do_error("Invalid number of neurons 1 [" + ni + "]"); return null; }
	 			    ntw.setNbrOfInputNeurons(ni);
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<ActualNbrOfNeuronsInHiddenLayer>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"ActualNbrOfNeuronsInHiddenLayer"));
	 			    if( ni < 1 ) { do_error("Invalid number of neurons 2 [" + ni + "]"); return null; }
	 			    ntw.setNbrOfHiddenNeurons(ni);
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<ActualNbrOfNeuronsInOutputLayer>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"ActualNbrOfNeuronsInOutputLayer"));
	 			    if( ni < 1 ) { do_error("Invalid number of neurons 3 [" + ni + "]"); return null; }
	 			    ntw.setNbrOfOutputNeurons(ni);
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<ActualMiniBatchSize>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"ActualMiniBatchSize"));
	 			    if( ni < 1 ) { do_error("Invalid minibatch size[" + ni + "]"); return null; }
	 			    ntw.setMiniBatchSize(ni);
	 			 	continue;	 
	       	    }
	       	    
	       	    //
	       		if( sLijn.indexOf("<NeuralLayers>") >= 0 ) { 
	       		   if( ntw.getMiniBatchSize() < 0 ) {
	       			   do_error("Minibatch size unknown");
	       			   return null;
	       		   }
	       		   if( readNeuralLayers( reader , FName , ntw ) == false ) return null;
	       		}
	    	}
        	if( foundend == false ) { do_error("No closing </NeuralNetwork>"); return null; }
        }
        catch(Exception e ) {
    		do_error( "Reading <NeuralNetwork> [" + FName + "] " + e.getMessage() );
    		return null;
        }
    	//
    	if( ntw.getNbrOfLayers() < 3) { do_error("Too few layers"); return null; }
    	if( ntw.getNbrOfInputNeurons() < 1) { do_error("Neurons input"); return null; }
    	if( ntw.getNbrOfOutputNeurons() < 1) { do_error("Neurons output"); return null; }
    	if( ntw.getNbrOfHiddenNeurons() < 1) { do_error("Neurons hidden"); return null; }
    	if( ntw.getMiniBatchSize() < 1) { do_error("MinibatchSize"); return null; }
    	//
    	return ntw;
    }
    
    //------------------------------------------------------------
    private boolean readNeuralLayers( BufferedReader reader , String FName , neuralNetwork ntw )
    //------------------------------------------------------------
    {
    	boolean foundend = false;
        int layerCounter = -1;
        hiddenNeuralLayer[] hlist = null;
    	try {
        	String sLijn = null;
        	while ((sLijn=reader.readLine()) != null) {
	       	  	if( sLijn.trim().length() == 0 ) continue;
	       	  	if( sLijn.indexOf("</NeuralLayers>") >= 0 ) { foundend=true; break; }
	       	    if( sLijn.indexOf("<NeuralLayer>") >= 0 ) {
	       			layerCounter++;
	       			if( layerCounter >= ntw.getNbrOfLayers() ) {
	       				do_error("Too many layers");  return false;
	       			}
	       			// layers
	       			if( layerCounter == 0 ) {
	       				ntw.setInputlayer(null);
	       				ntw.setOutputlayer(null);
	       				int naant = ntw.getNbrOfLayers() - 3;
	       				hlist = new hiddenNeuralLayer[ naant ];
	       				for(int i=0;i<hlist.length;i++) hlist[i]=null;
	       				ntw.setHiddenlayers( hlist );
	       				//
	       				//do_log( 1 , "Parsing input layer");
	       				ntw.setInputlayer( readInputNeuralLayerSection( reader , FName , ntw ) );
	       				if( ntw.getInputlayer() == null ) return false;
	       				daoPreviousLayer = (neuralLayer)ntw.getInputlayer();
	       			}
	       			else
	       			if( (layerCounter+2) ==  ntw.getNbrOfLayers() ) {
	       //do_log( 1 , "Parsing output layer");
	       				ntw.setOutputlayer( readOutputNeuralLayerSection( reader , FName , ntw ) );
	       				if( ntw.getOutputlayer() == null ) return false;
	       				daoPreviousLayer = (neuralLayer)ntw.getOutputlayer();
	       			}
	       			else
	       			if( (layerCounter+1) ==  ntw.getNbrOfLayers() ) {
	       //do_log( 1 , "Parsing loss layer");
	       				ntw.setLossLayer( readLossNeuralLayerSection( reader , FName , ntw) );
	       				if( ntw.getLosslayer() == null ) return false;
	       				daoPreviousLayer = (neuralLayer)ntw.getLosslayer();
	       			}
	       			else {
	       				if( (layerCounter-1) >= hlist.length ) { do_error("Too many hidden layers"); return false; }
	      //do_log( 1 , "Parsing hidden layer [" + (layerCounter-1) + "] " + layerCounter + " " + ntw.getNbrOfLayers() +" " +ntw.getMiniBatchSize() );
	       				hlist[ layerCounter - 1 ] = readHiddenLayerSection( reader , FName , ntw);
	       				daoPreviousLayer = (neuralLayer)hlist[ layerCounter - 1 ];
	       				if( hlist[ layerCounter - 1] == null ) {
	       					do_error("hidden layer error");
	       					return false;
	       				}
	       				ntw.setHiddenlayers(hlist);
	       			}
	   //do_log( 1 , " -> " + daoPreviousLayer.getTipe() + " " + daoPreviousLayer.getWeights() );
	       		}
        	}
        	if( foundend == false ) { do_error("Did not find closing tag <NeuralLayers>"); return false; }
        	if( (layerCounter+1) != ntw.getNbrOfLayers() ) { do_error("Did not read all layers [" + layerCounter + "] [" + ntw.getNbrOfLayers() + "]"); return false; }
    	}
    	catch(Exception e ) {
    		e.printStackTrace();
    		do_error( "Read neural layers"  + e.getMessage() );
    		return false;
        }
    	//
    	if( ntw.getInputlayer() == null ) { do_error( "no input layer"); return false; }
    	if( ntw.getOutputlayer() == null ) { do_error( "no output layer"); return false; }
    	if( ntw.getHiddenlayers() == null ) { do_error( "no hiddenlayer"); return false; }
    	if( ntw.getHiddenlayers().length != (ntw.getNbrOfLayers() - 3) ) { do_error("Number of hidden layer mismatch"); return false; }
    	for(int i=0;i<ntw.getHiddenlayers().length;i++)
    	{
    		if( ntw.getHiddenlayers()[i] == null ) {do_error("Hidden layer [" + i + "] not initialized"); return false; }
    	}
    	return true;
    }
    
    //------------------------------------------------------------
    private inputNeuralLayer readInputNeuralLayerSection( BufferedReader reader , String FName , neuralNetwork ntw)
    {
    	neuralLayer layer = readNeuralLayerSection( reader , FName , ntw );
    	if( layer == null ) return null;
    	inputNeuralLayer ret = new inputNeuralLayer( layer.getUID() , layer.getNbrOfNeuronsInLayer() , ntw.getMiniBatchSize() , daoAFactor );
    	ret.setNbrOfInputs( layer.getNbrOfInputs() );
    	ret.setNeurons( layer.getNeurons() );
    	ret.setPreviousLayer(null);
    	//
    	ret.initializeInputOutputAndNeuronsFast();
        ret.setWeights( layer.getWeights() );
        ret.setGradients( layer.getGradients() );
        //
    	return ret;
    }	
    //------------------------------------------------------------
    private outputNeuralLayer readOutputNeuralLayerSection( BufferedReader reader , String FName , neuralNetwork ntw)
    {
    	neuralLayer layer = readNeuralLayerSection( reader , FName , ntw);
    	if( layer == null ) return null;
    	outputNeuralLayer ret = new outputNeuralLayer ( layer.getUID() , layer.getNbrOfNeuronsInLayer() , 
    			                    ntw.getMiniBatchSize() , daoActivationFunctionType , daoCostFunctionType , daoAssessmentType );
    	ret.setNeurons( layer.getNeurons() );
    	ret.setNbrOfInputs( layer.getNbrOfInputs() );
    	if( daoNominals == null ) { do_error("Nominals not found"); return null; }
    	ret.setNominals(daoNominals);
    	ret.setPreviousLayer( this.daoPreviousLayer );
    	//
    	ret.initializeInputOutputAndNeuronsFast();
        ret.setWeights( layer.getWeights() );
        ret.setGradients( layer.getGradients() );
        if( ret.setSignatures( daoNeuronSignatures ) == false ) { do_error("setting signatures"); return null; }
        //
    	return ret;
    }
    //------------------------------------------------------------
    private lossNeuralLayer readLossNeuralLayerSection( BufferedReader reader , String FName , neuralNetwork ntw)
    {
    	neuralLayer layer = readNeuralLayerSection( reader , FName , ntw);
    	if( layer == null ) return null;
    	lossNeuralLayer ret = new lossNeuralLayer ( layer.getUID() , layer.getNbrOfNeuronsInLayer() , ntw.getMiniBatchSize() , daoCostFunctionType );
    	ret.setNeurons( layer.getNeurons() );
    	ret.setNbrOfInputs( layer.getNbrOfInputs() );
    	ret.setPreviousLayer( this.daoPreviousLayer );
    	//
    	ret.initializeInputOutputAndNeuronsFast();
        ret.setWeights( layer.getWeights() );
        ret.setGradients( layer.getGradients() );
        //
    	return ret;
    }
    //------------------------------------------------------------
    private hiddenNeuralLayer readHiddenLayerSection( BufferedReader reader , String FName , neuralNetwork ntw)
    {
    	neuralLayer layer = readNeuralLayerSection( reader , FName , ntw );
    	if( layer == null ) return null;
        hiddenNeuralLayer ret = new hiddenNeuralLayer( layer.getUID() , layer.getNbrOfNeuronsInLayer() , ntw.getMiniBatchSize() , daoActivationFunctionType , daoAFactor , daoDropOutRatio ); 	
        ret.setNeurons( layer.getNeurons() );
        ret.setNbrOfInputs( layer.getNbrOfInputs() );
        ret.setPreviousLayer( this.daoPreviousLayer );
        //
        ret.initializeInputOutputAndNeuronsFast();
        ret.setWeights( layer.getWeights() );
        ret.setGradients( layer.getGradients() );
        //
        return ret;
    }
    //------------------------------------------------------------
    private neuralLayer readNeuralLayerSection( BufferedReader reader , String FName , neuralNetwork ntw)
    //------------------------------------------------------------
    {
    	neuralLayer layer = null;
    	boolean foundend = false;
    	int UID = -1;
    	int nbr = -1;
    	int ips = -1;
    	int sigs = -1;
    	cmcMLPenums.LAYER_TYPE tipe = null;
    	daoNominals=null;
        try {
        	String sLijn = null;
        	while ((sLijn=reader.readLine()) != null) {
	       	  	if( sLijn.trim().length() == 0 ) continue;
	       	  	if( sLijn.indexOf("</NeuralLayer>") >= 0 ) { foundend=true; break; }
	       	  	
	       	    if( sLijn.indexOf("<LayerType>") >= 0 ) {
	 				tipe = penums.getLayerType(xMSet.xU.extractXMLValue(sLijn,"LayerType"));
	 				if( tipe == null ) { do_error("Invalid layer type [" + sLijn + "]"); return null; }
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<LayerUID>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"LayerUID"));
	 			    if( ni < 0 ) { do_error("Invalid layer UID [" + sLijn + "]"); return null; }
	 			    UID = ni;
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<NbrOfNeuronsInLayer>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NbrOfNeuronsInLayer"));
	 			    if( ni < 1 ) { do_error("Invalid number of neurons in layer [" + sLijn + "]"); return null; }
	 			    nbr = ni;
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<NbrOfInputsInLayer>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NbrOfInputsInLayer"));
	 			    if( ni < 1 ) { do_error("Invalid number of inputs in layer [" + sLijn + "]"); return null; }
	 			    ips = ni;
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<ExtendedClassNameList>") >= 0 ) {
	 			    String[] list = xMSet.xU.getSquaredItems( xMSet.xU.extractXMLValue(sLijn,"ExtendedClassNameList"));
	 				if( list == null ) { do_error("reading extended class name list [" + sLijn + "]"); return null; }
	 				if( list.length < 1) { do_error("No items on extended class name list"); return null; }
	 				daoNominals = list;
	 			    continue;	 
	       	    }
	       	    if( sLijn.indexOf("<Signatures>") >= 0 ) {
	       	       daoNeuronSignatures = new double[  nbr ] [ nbr ];
	               for(int i=0;i<daoNeuronSignatures.length;i++)
	               {
	            	for(int j=0;j<daoNeuronSignatures[i].length;j++) 
	            	{
	            		daoNeuronSignatures[i][j] = Double.NaN;
	            	}
	               }
	            }
	       	    if( sLijn.indexOf("<SignatureItem>") >= 0 ) {
	       	    	String[] list = xMSet.xU.getSquaredItems( xMSet.xU.extractXMLValue(sLijn,"SignatureItem"));
	 				if( list == null ) { do_error("reading signature [" + sLijn + "]"); return null; }
	 				if( list.length < 1) { do_error("No items on signature"); return null; }
	 				sigs++;
	 				for(int i=0;i<list.length;i++) {
		       		   double dd = xMSet.xU.NaarDoubleNAN( list[i] );
		       		   if(Double.isNaN( dd )) { do_error("parsing item [" + i + "] " + sLijn); return null; }
		       		   daoNeuronSignatures[ sigs ][ i ] = dd;
		       		}
	 		    }	
	       	    if( sLijn.indexOf("</Signatures>") >= 0 ) {
	       	    	if( daoNeuronSignatures == null ) { do_error("No signatures known [" + sLijn + "]"); return null; }
	       	    	for(int i=0;i<daoNeuronSignatures.length;i++)
		            {
		            	for(int j=0;j<daoNeuronSignatures[i].length;j++) 
		            	{
		                  if( Double.isNaN( daoNeuronSignatures[i][j] ))  { do_error("NAN signature[" + sLijn + "]"); return null; }
		            	}
		            }
	       	    }   	    
	       	    
	       	    if( sLijn.indexOf("<Neurons>") >= 0 ) { // you need to create layer it is referred to by neurons
	       	    	do_log( 1 , "Creating layer [" + tipe + "] [UID=" + UID + "] [#Neurons=" + nbr + "] [#Inputs=" + ips + "]");
	       	        // checks
	       	        if( UID < 0 ) { do_error("No layer UID"); return null; }
	       	    	if( nbr < 0 ) { do_error("Nbr of neurons not found"); return null; }
	       	    	if( ips < 0 ) { do_error("Nbr of inputs not found"); return null; }
	       	    	if( tipe == null ) { do_error("Null tipe"); return null; }
	       	    	if ( (tipe != cmcMLPenums.LAYER_TYPE.INPUT) && (tipe != cmcMLPenums.LAYER_TYPE.HIDDEN) && 
	       	    		 (tipe != cmcMLPenums.LAYER_TYPE.OUTPUT) && (tipe != cmcMLPenums.LAYER_TYPE.LOSS) ) { do_error("Invalid layertype " + tipe); return null; }
	       	    	layer = new neuralLayer( UID , nbr , ntw.getMiniBatchSize() );
	       	    	layer.setNbrOfInputs( ips );
	       	    	layer.setTipe(tipe);
	       	    	layer.setPreviousLayer(this.daoPreviousLayer);
	       	    	layer.setActivationFunctionTipe( this.daoActivationFunctionType );
	       	    	if ( tipe == cmcMLPenums.LAYER_TYPE.OUTPUT ) {
	       	    		if( daoNeuronSignatures == null ) { do_error("signatures not loaded"); return null; }
	       	    		if( daoNeuronSignatures.length != layer.getNbrOfNeuronsInLayer() ) { do_error("signature number mismatch with layer "); return null; }
	       	    	}
	       	        //	 
	       	    	if( readNeurons( reader , FName , layer ) == false ) { do_error("Weights/gradients not read"); return null; }
	       	    	//
	       	    	layer.initializeInputOutputAndNeuronsFast();	
	     //do_log( 1 , " 1 -> " + layer.getUID() + " " + layer.getWeights() );
	       	    	//
	       	    	if( PropagateNeuronWeightsToLayer( layer ) == false ) { do_error("Could not overwrite layer weights"); return null; }
	     //do_log( 1 , " 2 -> " + layer.getUID() + " " + layer.getWeights() );
	       	   }
        	}
        	if( foundend == false ) { do_error("Did not find closing tag <NeuralLayer>"); return null; }
        }
        catch(Exception e ) {
        	e.printStackTrace();
    		do_error( "Reading <NeuralLayer> [" + FName + "] " + e.getMessage() );
    		return null;
        }
        //
        return layer;
    }
    
    //------------------------------------------------------------
    private boolean readNeurons( BufferedReader reader , String FName , neuralLayer layer )
    //------------------------------------------------------------
    {
    	boolean foundend = false;
        int neuronCounter = -1;
        boolean[] nlist = new boolean[ layer.getNbrOfNeuronsInLayer() ];
        for(int i=0;i<nlist.length;i++) nlist[i] = false;
        //
        daoNeuronWeights = new double[  layer.getNbrOfNeuronsInLayer() ] [ layer.getNbrOfInputs() ];
        daoNeuronGradients = new double[  layer.getNbrOfNeuronsInLayer() ] [ layer.getNbrOfInputs() ];
        for(int i=0;i<daoNeuronWeights.length;i++)
        {
        	for(int j=0;j<daoNeuronWeights[i].length;j++) 
        	{
        		daoNeuronWeights[i][j] = Double.NaN;
        		daoNeuronGradients[i][j] = Double.NaN;
        	}
        }
        //
    	try {
        	String sLijn = null;
        	while ((sLijn=reader.readLine()) != null) {
	       	  	if( sLijn.trim().length() == 0 ) continue;
	       	  	if( sLijn.indexOf("</Neurons>") >= 0 ) { foundend=true; break; }
	       	    if( sLijn.indexOf("<Neuron>") >= 0 ) {
	       	  	  neuronCounter++;
	       	  	  if( neuronCounter >= layer.getNbrOfNeuronsInLayer() ) { do_error("Too many neurons"); return false; }
	       	      //do_log( 1 , "Parsing neuron [" + neuronCounter + "]");
	       	  	  nlist[neuronCounter] =  readSingleNeuron( reader , FName , layer );
	       	  	  if( nlist[ neuronCounter] == false )  { do_error("Null single neuron"); return false; }
	       	    }	
	    	}
         	if( foundend == false ) { do_error("Did not find closing tag <Neurons>"); return false; }
        }
    	catch(Exception e ) {
    		do_error( "Reading <Neurons> [" + FName + "] " + xMSet.xU.LogStackTrace(e) );
    		return false;
        }
    	// all neurons processed
    	for(int i=0;i<nlist.length;i++)
    	{
    		if( nlist[i] == false ) { do_error("Null/False neuron in list"); return false; }
    	}
    	// Check weights and gradients
    	if( layer.getTipe() != cmcMLPenums.LAYER_TYPE.INPUT ) {
    	 for(int i=0;i<daoNeuronWeights.length;i++)
         {
        	for(int j=0;j<daoNeuronWeights[i].length;j++) 
        	{
        		if( Double.isNaN(daoNeuronWeights[i][j]) ) {
        			do_error( "Reading <Neurons> - weight NAN " + i + " " + j + " " + layer.getTipe() );
            		return false;
        		}
        		if( Double.isNaN(daoNeuronGradients[i][j]) ) {
        			do_error( "Reading <Neurons> - gradient NAN " + i + " " + j + " " + layer.getTipe() );
            		return false;
        		}
        	}
         }
    	}
    	//
    	return true;
    }
    
    //------------------------------------------------------------
    private boolean readSingleNeuron( BufferedReader reader , String FName , neuralLayer layer )
    //------------------------------------------------------------
    {
    	boolean foundend = false;
      	int uid=-1;
    	int layeruid=-1;
    	int idx=-1;
    	cmcMLPenums.NEURON_TYPE tipe = null;
    	double[] weights = new double[ layer.getNbrOfInputs() ];
    	double[] grads = new double[ layer.getNbrOfInputs() ];
    	for(int i=0;i<weights.length;i++) { weights[i] = Double.NaN; grads[i] = Double.NaN; }
    	try {
        	String sLijn = null;
        	while ((sLijn=reader.readLine()) != null) {
	       	  	if( sLijn.trim().length() == 0 ) continue;
	       	  	if( sLijn.indexOf("</Neuron>") >= 0 ) { foundend=true; break; }
	       	    //  
	       	    if( sLijn.indexOf("<NeuronIndex>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NeuronIndex"));
	 			    if( ni < 0 ) { do_error("Invalid neuron index"); return false; }
	 			    idx=ni;
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<LayerUID>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"LayerUID"));
	 			    if( ni < 0 ) { do_error("Invalid neuron layer uid"); return false; }
	 			    layeruid=ni;
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<NeuronUID>") >= 0 ) {
	 				int ni = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NeuronUID"));
	 			    if( ni < 0 ) { do_error("Invalid neuron uid"); return false; }
	 			    uid=ni;
	 				continue;	 
	       	    }	
	       	    if( sLijn.indexOf("<NeuronType>") >= 0 ) {
	 				String stip = xMSet.xU.extractXMLValue(sLijn,"NeuronType");
	 			    if( stip == null ) { do_error("NULL type"); return false; }
	 			    tipe = null;
	 			    if (stip.trim().compareToIgnoreCase("COMPLETE") == 0 ) tipe = cmcMLPenums.NEURON_TYPE.COMPLETE;
	 			    if (stip.trim().compareToIgnoreCase("ACTIVATION_ONLY") == 0 ) tipe = cmcMLPenums.NEURON_TYPE.ACTIVATION_ONLY;
	 			    if( tipe == null ) { do_error("Not supported type " + sLijn); return false; }
	 			    continue;	 
	       	    }	
	       	    if( sLijn.indexOf("<NeuronWeights>") >= 0 ) {
	       		    String[] sq = xMSet.xU.getSquaredItems( sLijn.trim() );
	       		    if( sq == null ) { do_error( "Extracting weights " + sLijn); return false; }
	       		    if( sq.length != layer.getNbrOfInputs() ) { do_error("Number of weights does not match nbr of inputs"); return false; }
	       		    for(int i=0;i<sq.length;i++) {
	       		    	double dd = xMSet.xU.NaarDoubleNAN( sq[i] );
	       		    	if(Double.isNaN( dd )) { do_error("parsing item [" + i + "] " + sLijn); return false; }
	       		    	weights[ i ] = dd;
	       		    }
	       		    continue;
	       	    }
	       	    if( sLijn.indexOf("<NeuronGradients>") >= 0 ) {
	       		    String[] sq = xMSet.xU.getSquaredItems( sLijn.trim() );
	       		    if( sq == null ) { do_error( "Extracting gradients " + sLijn); return false; }
	       		    if( sq.length != layer.getNbrOfInputs() ) { do_error("Number of gradients does not match nbr of inputs"); return false; }
	       		    for(int i=0;i<sq.length;i++) {
	       		    	double dd = xMSet.xU.NaarDoubleNAN( sq[i] );
	       		    	if(Double.isNaN( dd )) { do_error("parsing item [" + i + "] " + sLijn); return false; }
	       		    	grads[ i ] = dd;
	       		    }
	       		    continue;
	       	    }
	    	}
         	if( foundend == false ) { do_error("Did not find closing tag </Neuron>"); return false; }
        }
    	catch(Exception e ) {
    		do_error( "Reading <neuron> [" + FName + "] " + xMSet.xU.LogStackTrace(e) );
    		return false;
        }
    	// check
    	if( uid < 0 ) { do_error("Neuron UID not found"); return false; }
    	if( layeruid < 0 ) { do_error("Neuron Layer UID not found"); return false; }
    	if( idx < 0 ) { do_error("Neuron Index not found"); return false; }
    	if( tipe == null ) { do_error("Neuron type unknown"); return false; }
    	//
    	//if( tipe == cmcMLPenums.NEURON_TYPE.ACTIVATION_ONLY ) {
    	//    weights = null;	
    	//}
    	//else
    	if( layer.getTipe() == cmcMLPenums.LAYER_TYPE.INPUT ) {
        	weights = null;
        }
        else {
    	 for(int i=0;i<weights.length;i++) 
    	 {
    	 	 if( Double.isNaN( weights[i] ) ) { do_error( "NAN weight " + i + " " + tipe + " " + layer.getTipe() ); return false; }
    	 	 if( Double.isNaN( grads[i] ) ) { do_error( "NAN gradient " + i + " " + tipe + " " + layer.getTipe() ); return false; }
    	 }
        }
    	
    	// 
    	try { 
    		if( weights != null ) {
    			for(int i=0;i<weights.length;i++) 
    			{
    				 daoNeuronWeights[ idx ][ i ] = weights[i];
    				 daoNeuronGradients[ idx ][ i ] = weights[i];
    			}
    		}
    		return true;
    	}
    	catch(Exception e) {
    		do_error( "Filling layer - creating neurons[" + FName + "] " + xMSet.xU.LogStackTrace(e) );
    		return false;
    	}
    }
    
    //------------------------------------------------------------
    private boolean readTimingsSection( BufferedReader reader , String FName , cmcMLPDTO dto)
    //------------------------------------------------------------
    {
    	boolean foundend = false;
    	boolean indata=false;
    	int nruns=-1;
    	int runcounter=-1;
    	try {
    	    if( dto.getRuntimes() == null ) { do_error("Timings not initialized"); return false; }
    	    String sLijn = null;
        	while ((sLijn=reader.readLine()) != null) {
	       	  	if( sLijn.trim().length() == 0 ) continue;
	       		if( sLijn.indexOf("</Timings>") >= 0 ) { foundend=true; break; }
	       	    if( sLijn.indexOf("<Runs>") >= 0 ) {
	 				nruns = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"Runs"));
	 			    if( nruns < 0 ) { do_error("Invalid number of runs"); return false; }
	 				continue;	 
	       	    }
	       	    if( sLijn.indexOf("<![CDATA[") >= 0 ) { indata=true; continue; }
	       	    if( sLijn.indexOf("]]>") >= 0 ) { indata=false; continue; }
	       	    if( indata ) {
	       	    	if( sLijn.trim().startsWith("[") == false ) continue;
	       	    	if( sLijn.trim().endsWith("]") == false ) continue;
	       		    String[] sq = xMSet.xU.getSquaredItems( sLijn.trim() );
	       		    if( sq == null ) { do_error( "Extracting times " + sLijn); return false; }
	       		    if( sq.length != 3 ) { do_error("Number of timinds msut be 3"); return false; }
	       		    runcounter++;
	       		    if( runcounter >= nruns ) { do_error("Too many runs"); return false; }
	       		    for(int i=0;i<sq.length;i++) {
	       		    	dto.getRuntimes()[runcounter][i] = xMSet.xU.NaarLong( sq[i] );
	       		    }
	       	    }
        	}
         	if( foundend == false ) { do_error("Did not find closing tag </Timings>"); return false; }
         	if( runcounter != (nruns-1) ) { do_error("Could not parse all runs"); return false; }
    	}
    	catch(Exception e ) {
    		do_error( "Reading <Timings> [" + FName + "] " + xMSet.xU.LogStackTrace(e) );
    		return false;
        }
    	return true;
    }
    
    // neem de gewichten en gradients per neuro en zet die op de layer
    //------------------------------------------------------------
    private boolean PropagateNeuronWeightsToLayer( neuralLayer layer  )
    //------------------------------------------------------------
    {
     try {
    	if( layer.getTipe() == cmcMLPenums.LAYER_TYPE.INPUT ) return true;
    	if( (layer.getTipe() != cmcMLPenums.LAYER_TYPE.HIDDEN) &&
    		(layer.getTipe() != cmcMLPenums.LAYER_TYPE.OUTPUT) && 
    		(layer.getTipe() != cmcMLPenums.LAYER_TYPE.LOSS) ) {
    		do_error("unsupported layer to propagate to " + layer.getTipe());
    		return false;
    	}
        if( layer.getNbrOfInputs() < 1 ) {
        	do_error("No inputs" + layer.getTipe() );
    		return false;
        }
        if( layer.getNbrOfNeuronsInLayer() < 1 ) {
        	do_error("No neurons" + layer.getTipe() );
    		return false;
        }
        if( layer.getNbrOfMiniBatchSamples() < 1 ) {
        	do_error("No samples known" + layer.getTipe() );
    		return false;
        }
        // weight and gradients are the same voor all samples, i.e are consolidated after each epoch
        for(int k=0;k<layer.getNbrOfMiniBatchSamples();k++)
        {
            layer.getWeights()[k].fillMatrix( this.daoNeuronWeights );
            layer.getGradients()[k].fillMatrix( this.daoNeuronGradients  );
        }
//do_log( 1 , "Propagated " + layer.getTipe() + " " + layer.getNbrOfMiniBatchSamples() + " "  );
     	return true;
     }
     catch(Exception e ) {
    	 e.printStackTrace();
    	 do_error("Propagate weights to layer" + e.getMessage());
    	 return false;
     }
    }
}
