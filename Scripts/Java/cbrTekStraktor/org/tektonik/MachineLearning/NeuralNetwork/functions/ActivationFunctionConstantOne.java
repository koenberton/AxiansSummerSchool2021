package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class ActivationFunctionConstantOne implements ActivationFunction {

	private double A = (double)1;
	public void setA( double a)
	{
		this.A = 1; // cannot be set
	}
	
	public double runActivationFunction(double x)
	{
		return 1;
	}
	
	public double getDerivative(double x)
	{
		return 0;
	}
	
	public double getUpperThreshold()
	{
		return A;
	}
	public double getLowerThreshold()
	{
		return A;
	}
	
	public double runActivationFunction( int idx , double[] net)
	{
		System.err.println("Constant one does not support Actionvation(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
	
	public double getDerivative( int row , int col , double[] net)
	{
		System.err.println("Constant one does not support Derivative(idx,net)");
		System.exit(1);
		return Double.NaN;
	}

}
