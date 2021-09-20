package org.tektonik.MachineLearning.NeuralNetwork.functions;

public interface ActivationFunction {
	
	public double runActivationFunction(double x);
	public double runActivationFunction( int idx , double[] net);
	public double getDerivative(double x);
	public double getDerivative( int row , int col , double[] net);
	public void setA( double a);
	public double getUpperThreshold();
	public double getLowerThreshold();
}
