package org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork;


import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTO;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;
import org.tektonik.MachineLearning.dao.cmcMLPDTODAO;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.linearAlgebra.cmcMatrix;
import org.tektonik.tools.linearAlgebra.cmcVector;
import org.tektonik.tools.logger.logLiason;

public class neuralNetworkSupportRoutines {
	
	cmcProcSettings xMSet = null;
	logLiason logger = null;
	
	
	   
	private String LastErrorMsg = null;

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
    	LastErrorMsg = sIn;
    	do_log(0,sIn);
    }
    //------------------------------------------------------------
    public String getLastErrorMessage()
    //------------------------------------------------------------
    {
       return this.LastErrorMsg;	
    }

    //------------------------------------------------------------
  	public neuralNetworkSupportRoutines( cmcProcSettings iM , logLiason ilog )
  	//------------------------------------------------------------
  	{
  		xMSet = iM;
  		logger = ilog;
 
  	}
    
    //------------------------------------------------------------
  	public boolean mergeModels( cmcMLPDTO newmodel )
    //------------------------------------------------------------
  	{
  	  try {	
  		if( xMSet.xU.IsBestand( xMSet.getPreviousEpochStatXMLFileName() ) == false ) { do_error("No previous STAT file"); return false; }
  		if( xMSet.xU.IsBestand( xMSet.getPreviousModelFileName() ) == false ) { do_error("No previous model file"); return false; }
  		
  		// read the previous model
  		cmcMLPDTODAO ntwdao = new cmcMLPDTODAO( xMSet , logger );
  	    cmcMLPDTO oldmodel = ntwdao.readFullMLPDTO( xMSet.getPreviousModelFileName() );
  	    if( oldmodel == null ) return false;
  	    if( oldmodel.getNtw() == null ) { do_error("Could not read old network"); return false; }
  		
  		// ARFF must match
  		if( oldmodel.getLongARFFFileName().compareToIgnoreCase(newmodel.getLongARFFFileName()) != 0 ) {
  			do_error("ARFF Files do not match [" + oldmodel.getLongARFFFileName() + "] [" + newmodel.getLongARFFFileName() + "]");
  			return false;
  		}
  		// #neurons and #layers must match
  		if( oldmodel.getNbrOfHiddenLayers() != newmodel.getNbrOfHiddenLayers() ) { 
			do_error("Nbr of hidden layers do not match [" + oldmodel.getNbrOfHiddenLayers() + "] [" + newmodel.getNbrOfHiddenLayers() + "]");
  			return false;
  		}
  		if( oldmodel.getNbrOfNeuronsPerHiddenLayer() != newmodel.getNbrOfNeuronsPerHiddenLayer() ) { 
			do_error("Nbr of neurons in hidden layers do not match [" + oldmodel.getNbrOfNeuronsPerHiddenLayer() + "] [" + newmodel.getNbrOfNeuronsPerHiddenLayer() + "]");
  			return false;
  		}
  		if( oldmodel.getNbrOfFeatures() != newmodel.getNbrOfFeatures() ) { 
			do_error("Nbr of features do not match [" + oldmodel.getNbrOfFeatures() + "] [" + newmodel.getNbrOfFeatures() + "]");
  			return false;
  		}
  		// network (inputs etc)
  		if( oldmodel.getNtw().getNbrOfHiddenNeurons() != newmodel.getNtw().getNbrOfHiddenNeurons() ) {
			do_error("Nbr of NTW neurons do not match [" + oldmodel.getNtw().getNbrOfHiddenNeurons() + "] [" + newmodel.getNtw().getNbrOfHiddenNeurons() + "]");
  			return false;
  		}
  		if( oldmodel.getNtw().getNbrOfInputNeurons() != newmodel.getNtw().getNbrOfInputNeurons() ) {
			do_error("Nbr of input neurons do not match [" + oldmodel.getNtw().getNbrOfInputNeurons() + "] [" + newmodel.getNtw().getNbrOfInputNeurons() + "]");
  			return false;
  		}
  		if( oldmodel.getNtw().getNbrOfLayers() != newmodel.getNtw().getNbrOfLayers() ) {
			do_error("Nbr of layers do not match [" + oldmodel.getNtw().getNbrOfLayers() + "] [" + newmodel.getNtw().getNbrOfLayers() + "]");
  			return false;
  		}
  		if( oldmodel.getNtw().getNbrOfOutputNeurons() != newmodel.getNtw().getNbrOfOutputNeurons() ) {
			do_error("Nbr of output neurons do not match [" + oldmodel.getNtw().getNbrOfOutputNeurons() + "] [" + newmodel.getNtw().getNbrOfOutputNeurons() + "]");
  			return false;
  		}
  		String[] oldclist = oldmodel.getNtw().getExtendedClassNameList();
  		if( oldclist == null ) { do_error("oldmodel extended class name list is null"); return false; }
  		String[] newclist = newmodel.getNtw().getExtendedClassNameList();
  		if( newclist == null ) { do_error("newmodel extended class name list is null"); return false; }
  	  	if( oldclist.length != newclist.length )	{
  			do_error("Nbr of items on class name list [" + oldclist.length + "] [" + newclist.length + "]");
  			return false;
  	  	}
  		for(int i=0;i<oldclist.length;i++ )
  		{
  			if( oldclist[i].compareToIgnoreCase( newclist[i] ) == 0 ) continue;
  			do_error("Class name mismatch [" + oldclist[i] + "] [" + newclist[i] + "[");
  			return false;
  		}
  		// check layers and copy weights
  		for(int la=0;la<(oldmodel.getNbrOfHiddenLayers()+3);la++)
  		{
  			neuralLayer oldlayer = null;
  			neuralLayer newlayer = null;
  			
  			if( la == 0 ) {
  			   oldlayer = oldmodel.getNtw().getInputlayer();
  			   newlayer = newmodel.getNtw().getInputlayer();
  			}
  			else 
  			if( la == oldmodel.getNbrOfHiddenLayers() + 1 ) {
   			   oldlayer = oldmodel.getNtw().getOutputlayer();
   			   newlayer = newmodel.getNtw().getOutputlayer();
   			}
  			else 
  	  		if( la == oldmodel.getNbrOfHiddenLayers() + 2 ) {
  	   		   oldlayer = oldmodel.getNtw().getLosslayer();
  	   		   newlayer = newmodel.getNtw().getLosslayer();
  	   		}
  			else {
  			   oldlayer = oldmodel.getNtw().getHiddenlayers()[la - 1];
   			   newlayer = newmodel.getNtw().getHiddenlayers()[la - 1];
   			}
  			//
  			if( oldlayer.getTipe() != newlayer.getTipe() ) { do_error( "Layer type mismatch " + oldlayer.getTipe() + " " + newlayer.getTipe() ); return false; }
  			if( oldlayer.getNbrOfInputs() != newlayer.getNbrOfInputs() ) {
  				do_error( "Nbr of inputs mismatch [" + oldlayer.getNbrOfInputs() + "] [" + newlayer.getNbrOfInputs() + "] " + newlayer.getTipe() +" " + la);
  				return false;
  			}
  			if( oldlayer.getNbrOfNeuronsInLayer() != newlayer.getNbrOfNeuronsInLayer() ) {
  				do_error( "Nbr of neurons mismatch [" + oldlayer.getNbrOfNeuronsInLayer() + "] [" + newlayer.getNbrOfNeuronsInLayer() + "]" + " " + la);
  				return false;
  			}
  			//
  			neuron[] oldneurons = oldlayer.getNeurons();
  			neuron[] newneurons = newlayer.getNeurons();
  			if( oldneurons == null ) { do_error( "Null neurons on OLD layer " + la); return false; }
  			if( newneurons == null ) { do_error( "Null neurons on NEW layer " + la); return false; }
  			if( oldneurons.length != newneurons.length ) { do_error( "Nbr od neurons mismatch on layer " + la + " " + oldneurons.length + " " + newneurons.length ); return false; }
  		
  			//
  			for(int n=0;n<oldneurons.length;n++)
  			{
  				neuron oldnrn = oldneurons[n];
  				neuron newnrn = newneurons[n];
  				if( oldnrn == null ) { do_error("NULL old neuron " + n ); return false; }
  				if( newnrn == null ) { do_error("NULL new neuron " + n ); return false; }
  				if( oldnrn.getTipe() != newnrn.getTipe() ) { do_error("neuron type mismatch " + oldnrn.getTipe() + " " + newnrn.getTipe()); return false; }
  				//
  				if( oldlayer.getTipe() == cmcMLPenums.LAYER_TYPE.INPUT ) continue;
  			}
  			
  			// enkel indien geen input de waarden doorgeven
  			if( newlayer.getTipe() != cmcMLPenums.LAYER_TYPE.INPUT )
  			{
  			  cmcMatrix oldwei = oldlayer.getWeights()[0];
  			  cmcMatrix oldgra = oldlayer.getGradients()[0];
  		      int nsamples = oldmodel.getNtw().getMiniBatchSize();
  		      for(int i=0;i<nsamples;i++)
  		      {
  		    	newlayer.getWeights()[i].fillMatrix( oldwei.getValues() );
  		    	newlayer.getGradients()[i].fillMatrix( oldgra.getValues() );
  		      }
  			}
  			
  		}
  		
  		// signatures
  		outputNeuralLayer oldout = oldmodel.getNtw().getOutputlayer();
  		outputNeuralLayer newout = newmodel.getNtw().getOutputlayer();
  		if( oldout.getSignatures() == null ) {
  			do_error( "mergemodel - Old layer - Signatures are null"); return false;
  		}
  		if( oldout.getSignatures().length != newout.getNbrOfNeuronsInLayer() ) {
  			do_error( "mergemodel - Old layer - newlayer - signature numbers mismatch nbr of neurons [o=" +oldout.getSignatures().length + "] [n=" + newout.getNbrOfNeuronsInLayer() + "]"); return false;
  		}
  		if( oldout.getSignatures()[0].getDimension() != newout.getNbrOfNeuronsInLayer() ) {
  			do_error( "mergemodel - Old layer - newlayer - signature numbers mismatch nbr of neurons 2 [o=" +oldout.getSignatures()[0].getDimension() + "] [n=" + newout.getNbrOfNeuronsInLayer() + "]"); return false;
  		}
  		double[][] signatures = new double[ newout.getNbrOfNeuronsInLayer() ][ newout.getNbrOfNeuronsInLayer() ];
  		for(int i=0;i<oldout.getSignatures().length;i++)
  		{
  			cmcVector vec = oldout.getSignatures()[i];
  			if( vec == null  ) { do_error("null vector on signature"); return false; }
  			double[] vals = vec.getVectorValues();
  			if( vals == null ) { do_error("vector without values on signature"); return false; }
  			if( vals.length != newout.getNbrOfNeuronsInLayer() ) { do_error("number of signature values does not match nbr of neurons"); return false; }
  			for(int j=0;j<vals.length;j++) {
  				if( Double.isNaN( vals[j] ) ) { do_error("NAN on signature "+ i + " " + j); return false;}
  				signatures[i][j] = vals[j];
  			}
  		}
  		if( newout.setSignatures( signatures ) == false ) { do_error("cannot set signatures on new model"); return false; }
  		String ss = "";
		// TO BIG for(int i=0;i<signatures.length;i++) ss += newout.getSignatures()[i].show() + "\n";
  		do_log(1,"Reinstated previous signatures [" + newout.getAssessmentTipe() + "] -> \n" + ss);

  		
  		// copy the timings and various other pointers
  		newmodel.setEpochsPerformed( oldmodel.getEpochsPerformed() );
  		//
  		if( newmodel.getRuntimes() == null) { do_error("New runtimes not initialized"); return false; }
  		if( oldmodel.getRuntimes() == null) { do_error("Old runtimes not initialized"); return false; }
  		if( oldmodel.getRuntimes().length != newmodel.getRuntimes().length ) { do_error("Different runtime length"); return false; }
  		if( oldmodel.getRuntimes()[0].length != newmodel.getRuntimes()[0].length ) { do_error("Different runtime length"); return false; }
  		for(int i=0;i<oldmodel.getRuntimes().length;i++)
  		{
  			for(int j=0;j<oldmodel.getRuntimes()[i].length;j++)
  			{
  				newmodel.getRuntimes()[i][j] = oldmodel.getRuntimes()[i][j];
  			}
  		}
  		//
  		do_log( 1 , "Old model merged with new model [#Runs=" + newmodel.getEpochsPerformed() + "]");
  		return true;
  	  }
  	  catch(Exception e ) {
  		  e.printStackTrace();
  		  return false;
  	  }
  	}

 
  	
}
