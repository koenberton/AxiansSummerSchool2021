package org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork;

import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunction;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunctionConstantOne;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunctionConstantZero;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunctionHyperbolicTangent;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunctionRELU;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunctionSigmoid;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunctionSoftmax;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunctionTanh;
import org.tektonik.tools.linearAlgebra.cmcVector;

public class neuron {
	
	private neuralLayer parentlayer = null;
	private int UID  = -1;
	private cmcMLPenums.NEURON_TYPE tipe = null;
	private cmcMLPenums.ACTIVATION_FUNCTION_TYPE activationFunctionTipe = cmcMLPenums.ACTIVATION_FUNCTION_TYPE.UNKNOWN;
	private double AFactor = Double.NaN;
	
	private boolean dropout = false;
	//
	private int layerUID = -1;
	private int index = -1;
	private ActivationFunction activationFunction = null;
	private ActivationFunction originalActivationFunction = null;
	
    
	//-----------------------------------------------------------------------
	public neuron(neuralLayer parent , int uid , int ilayeruid , int idx , cmcMLPenums.ACTIVATION_FUNCTION_TYPE itp , int NbrOfInputs , double aFactor )
	{
		parentlayer = parent;
		UID = uid;
		layerUID =ilayeruid;
		index = idx;
		tipe = cmcMLPenums.NEURON_TYPE.COMPLETE;
		dropout=false;
		activationFunctionTipe = itp;
		activationFunction = null;
		AFactor = aFactor;
		//
		instantiateActivationFunction();
	}

	//-----------------------------------------------------------------------
	private boolean instantiateActivationFunction()
	{
		switch( activationFunctionTipe )
		{
		 case IGNORE             : { activationFunction = null; break; }
		 case SIGMOID            : { activationFunction = new ActivationFunctionSigmoid(); break; }
		 case HYPERBOLIC_TANGENT : { activationFunction = new ActivationFunctionHyperbolicTangent(); break; }
		 case TANH               : { activationFunction = new ActivationFunctionTanh(); break; }
		 case CONSTANT_ONE       : { activationFunction = new ActivationFunctionConstantOne(); break; }
		 case RELU               : { activationFunction = new ActivationFunctionRELU(); break; }
		 case SOFTMAX            : { activationFunction = new ActivationFunctionSoftmax(); break; }
		 default : {
			System.err.println("neuron - unsupported activation tipe - " + activationFunctionTipe + " " + this.getUID() + " " + parentlayer.getTipe());
		    System.err.println( "abort");	
			System.exit(1); // drama
			break;
		 }
		}
		originalActivationFunction = activationFunction;
		if( Double.isNaN( AFactor ) ) return true;
		if( activationFunction != null ) activationFunction.setA(AFactor);
	    //System.err.println( "" + this.getUID() + " " + this.getActivationFunctionTipe() + " " + this.getActivationFunction() );
        return true;
	}
	
	//-----------------------------------------------------------------------
	public void setActivationFunctionTipe(cmcMLPenums.ACTIVATION_FUNCTION_TYPE activationFunctionTipe) {
		this.activationFunctionTipe = activationFunctionTipe;
		this.instantiateActivationFunction();
	}
	//-----------------------------------------------------------------------
	public double runActivationFunction(double x)
	{
	   try {
		   return this.activationFunction.runActivationFunction(x);
	   }
	   catch(Exception e) {
		   System.err.println( "runActivation error [N" + this.getUID() + "]" + e.getMessage() );
		   return Double.NaN;
	   }
	}
	//-----------------------------------------------------------------------
	public double runActivationFunction(int idx , double[] net)
	{
	   try {
		   return this.activationFunction.runActivationFunction( idx , net);
	   }
	   catch(Exception e) {
		   System.err.println( "runActivation error [N" + this.getUID() + "]" + e.getMessage() );
		   return Double.NaN;
	   }
	}
	//-----------------------------------------------------------------------
	public double runActivationDerivative(double x)
	{
	   try {
		   return this.activationFunction.getDerivative(x);
	   }
	   catch(Exception e) {
		   System.err.println( "runDerivative error [N" + this.getUID() + "]" + e.getMessage() );
		   return Double.NaN;
	   }
	}
	//-----------------------------------------------------------------------
	public double runActivationDerivative(int row , int col , double[] net)
	{
	   try {
		   return this.activationFunction.getDerivative(row,col,net);
	   }
	   catch(Exception e) {
		   System.err.println( "runDerivative error [N" + this.getUID() + "]" + e.getMessage() );
		   return Double.NaN;
	   }
	}
	
	//-----------------------------------------------------------------------
	public cmcVector getFastWeights(boolean verbose)
	{
		// just take the first sample's weights - all weight should be set the same at the end of backprop
		try {
		 double[] val = parentlayer.getWeights()[ 0 ].getValues()[ this.index ];
		 return new cmcVector( val );
		}
		catch(Exception e ) {
			if( verbose ) System.err.println( "Neuron - getFastWeights " + e.getMessage() );
			return null;
		}
	}
	
	// consolidated gradients contains the average gradients over all samples
	//-----------------------------------------------------------------------
	public cmcVector getFastGradients() {
		try { 
		 //double[] val = parentlayer.getGradients()[ 0 ].getValues()[ this.index ];
	     double[] val = parentlayer.getConsolidatedGradients().getValues()[ this.index ];
		 return new cmcVector( val );
		}
		catch(Exception e ) {
			System.err.println( "Neuron - getFastGradients " + e.getMessage() );
			return null;
		}
	}

	//-----------------------------------------------------------------------
	public double[] getWeightsArray(boolean verbose)
	{
		try {
		 double[] val = parentlayer.getWeights()[ 0 ].getValues()[ this.index ];
		 return val;
		}
		catch(Exception e ) {
			if( verbose ) System.err.println( "Neuron - getWeightsArray " + e.getMessage() );
			return null;
		}
	}

	//-----------------------------------------------------------------------
	public double[] getGradientsArray() {
		try {
			 //double[] val = parentlayer.getGradients()[ 0 ].getValues()[ this.index ];
			 double[] val = parentlayer.getConsolidatedGradients().getValues()[ this.index ];
			 return val;
			}
			catch(Exception e ) {
				System.err.println( "Neuron - getGradientsArray " + e.getMessage() );
				return null;
			}
	}
	
	//-----------------------------------------------------------------------
	public void setDropOut( boolean b )
	{
		if( (b == false) && (this.dropout == true) ) { // unset 
			activationFunction = null;
			activationFunction = originalActivationFunction;
			this.dropout = false;
		}
		if( (b == true) && (this.dropout == false) ) { // set 
			activationFunction = new ActivationFunctionConstantZero();
			this.dropout = true;
		}	
	}
	
	
	
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	public int getUID() {
		return UID;
	}

	public void setUID(int uID) {
		UID = uID;
	}

	public int getLayerUID() {
		return layerUID;
	}

	public void setLayerUID(int layerUID) {
		this.layerUID = layerUID;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public cmcMLPenums.ACTIVATION_FUNCTION_TYPE getActivationFunctionTipe() {
		return activationFunctionTipe;
	}
	
	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	public void setActivationFunction(ActivationFunction activationFunction ) {
		this.activationFunction = activationFunction;
	}
	public cmcMLPenums.NEURON_TYPE getTipe() {
		return tipe;
	}

	public void setTipe(cmcMLPenums.NEURON_TYPE tipe) {
		this.tipe = tipe;
	}

	public double getA()
	{
		return this.AFactor;
	}
	
	public boolean getDropOut()
	{
		return this.dropout;
	}
	
	
	
	
	
}
