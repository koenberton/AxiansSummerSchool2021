package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class CostFunctionSquaredError implements CostFunction {

	// Mean Square error
	// Order  O-T does not really matter, derivative is the sam
	public double runCostFunction(double target , double output )
	{
		return 0.5 * Math.pow(  target - output , 2 );
	}
	
	// derivative 1/2(O-T)POW2  =  2 1/2 (O-T) * 1 = O - T
	// derivative 1/2(T-O)POW2  = 2 1/2 (T-O) * (-1) =  O -T  
	public double getDerivative(double target , double output)
	{
		return output - target;
	}
}
