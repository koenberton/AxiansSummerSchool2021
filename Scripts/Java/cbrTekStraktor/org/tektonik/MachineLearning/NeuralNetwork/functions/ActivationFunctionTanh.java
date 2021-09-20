package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class ActivationFunctionTanh implements ActivationFunction {
	private double A = (double)1;
	public void setA( double a)
	{
		if( Double.isNaN(a) ) return;
		this.A =a;
	}
	
	public double runActivationFunction(double x)
	{
		double emin2x = Double.NaN;
		try {
			emin2x = Math.pow(Math.E,(-2*x*A));
		    double dd = (2 / (1 + emin2x)) - 1;
		    return dd;
		}
		catch(Exception e) {
			System.err.println( "x=" + x + "  eminx=" + emin2x );
			return Double.NaN;
		}
	}
	
	public double getDerivative(double x)
	{
		try {
		  double dd  = runActivationFunction( x );
		  return 1 - (dd * dd);
		}
		catch(Exception e) {
			return Double.NaN;
		}
	}
	public double getUpperThreshold()
	{
		return 1;
	}
	public double getLowerThreshold()
	{
		return -1;
	}
	
	public double runActivationFunction( int idx , double[] net)
	{
		System.err.println("Tanh does not support Actionvation(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
	
	public double getDerivative( int row , int col , double[] net)
	{
		System.err.println("Tanh does not support Derivative(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
}
