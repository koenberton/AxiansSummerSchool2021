package org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork;

import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;

public class inputNeuralLayer extends neuralLayer {
	
	//-----------------------------------------------------------------------
	public inputNeuralLayer(int uid , int ineurons , int isamples , double AFactor)
	{
	super( uid, ineurons , isamples);
	super.setTipe( cmcMLPenums.LAYER_TYPE.INPUT );
	super.setAFactor(AFactor); // before function
	super.setActivationFunctionTipe( cmcMLPenums.ACTIVATION_FUNCTION_TYPE.IGNORE );
	}

	//-----------------------------------------------------------------------
	public boolean fastInitializeInputData( double[][] data  )
	{
		if( data == null ) {
			do_error( "Null data");
			return false;
		}
		if( data.length < 0 ) {
			do_error( "Empty data file");
			return false;
		}
		if( data.length != this.getNbrOfMiniBatchSamples() ) {
			do_error( "(Number of records in data [" + data.length + "] differs from number of records in initialized minibatch [" + this.getNbrOfMiniBatchSamples() +"]");
			return false;
		}
		// assume that inputs=outputs on the inputlayer
		if( (data[0].length + 1) != this.getNbrOfInputs() ) {
			do_error( "Number of features in data differs from number of neurons");
			return false;
		}
		if( this.getNbrOfInputs() != this.getNbrOfNeuronsInLayer() ) {
			do_error( "Number of inputs differs from number of outputs on input node");
			return false;
		}
        //		
		for(int i=0;i<data.length;i++)
		{
			for(int j=0;j<data[i].length;j++)
			{
				if( super.setOutputsFast( i  , j , data[i][j] ) == false ) {
					do_error("Setting the output values [Row=" + i + "] [" + j + " [#Rows="+data.length + "] [#Cols==" + data[0].length + "]" );
					return false;
				}
			}
		}
		for(int i=0;i<this.getNbrOfMiniBatchSamples();i++)
		{
			super.setOutputsFast( i , this.getNbrOfInputs() - 1 ,  1 ); // enkel de Output zetten
		}
		//
		return true;
	}
	
}
