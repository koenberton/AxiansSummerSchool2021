package org.tektonik.MachineLearning.NeuralNetwork.functions;

public interface CostFunction {

	public double runCostFunction(double target , double output );
	public double getDerivative(double target , double output );
}
