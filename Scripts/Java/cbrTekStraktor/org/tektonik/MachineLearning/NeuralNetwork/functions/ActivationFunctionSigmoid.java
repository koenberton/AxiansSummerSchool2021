package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class ActivationFunctionSigmoid implements ActivationFunction {
	
	private double A = (double)1;
	
	public void setA( double a)
	{
		if( Double.isNaN(a) ) return;
		this.A=a;
	}
	public double runActivationFunction(double x)
	{
		try {
		    return (1/( 1 + Math.pow(Math.E,(-1*x*A))));	
		}
		catch(Exception e) {
			System.err.println("Sigmoid error - " + x );
			System.exit(1);
			return Double.NaN;
		}
	}
	
	public double getDerivative(double x)
	{
		try {
		  double dd  = runActivationFunction( x );
		  return dd * (1 - dd);
		}
		catch(Exception e) {
			System.err.println("Sigmoid derivative error - " + x );
			System.exit(1);
			return Double.NaN;
		}
	}
	
	public double getUpperThreshold()
	{
		return 1;
	}
	public double getLowerThreshold()
	{
		return 0;
	}

	public double runActivationFunction( int idx , double[] net)
	{
		System.err.println("Sigmoid does not support Actionvation(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
	
	public double getDerivative( int row , int col , double[] net)
	{
		System.err.println("Sigmoid does not support Derivative(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
}
