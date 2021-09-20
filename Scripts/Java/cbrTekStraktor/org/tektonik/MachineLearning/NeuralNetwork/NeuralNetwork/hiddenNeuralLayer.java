package org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork;

import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;

public class hiddenNeuralLayer extends neuralLayer {
	
	public hiddenNeuralLayer(int uid , int ineurons , int isamples , cmcMLPenums.ACTIVATION_FUNCTION_TYPE actfunc , double AFactor , double dropooutratio)
	{
		super( uid , ineurons , isamples);
		super.setTipe( cmcMLPenums.LAYER_TYPE.HIDDEN );
		super.setAFactor(AFactor);
		super.setActivationFunctionTipe( actfunc );
		super.setDropOutRatio(dropooutratio);
	}

}
