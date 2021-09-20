package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class ActivationFunctionConstantZero implements ActivationFunction {

	private double A = (double)0;
	public void setA( double a)
	{
		this.A = 0; // cannot be set
	}
	
	public double runActivationFunction(double x)
	{
		return 0;
	}
	
	public double getDerivative(double x)
	{
		return 0;
	}
	
	public double getUpperThreshold()
	{
		return 0;
	}
	public double getLowerThreshold()
	{
		return 0;
	}
	
	public double runActivationFunction( int idx , double[] net)
	{
		System.err.println("Constant zero does not support Actionvation(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
	
	public double getDerivative( int row , int col , double[] net)
	{
		System.err.println("Constant zero does not support Derivative(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
}
