package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class ActivationFunctionRELU implements ActivationFunction {

	private double A = (double)1;
	private double POSITIVE_INCLINE = (double)0.1;
	private double NEGATIVE_INCLINE = (double)0.00001;
	private double positiveIncline  = Double.NaN;
	private double negativeIncline  = Double.NaN;
	
	public ActivationFunctionRELU()
	{
		reset();
	}
	
	private void reset()
	{
		positiveIncline = POSITIVE_INCLINE * A;
		negativeIncline = NEGATIVE_INCLINE * A;
	}
	
	public void setA( double a)
	{
		if( Double.isNaN(a) ) return;
		this.A =a;
		reset();
	}
	
	public double runActivationFunction(double x)
	{
		return ( x < 0) ? (x * negativeIncline) : (x * positiveIncline);
	}
	public double getDerivative(double x)
	{
		return ( x < 0) ? negativeIncline : positiveIncline;
	}
	public double getUpperThreshold()
	{
		System.err.println("SYSTEM ERROR - calling upper threshold on RELU");
		System.exit(1);
		return Double.NaN;
	}
	public double getLowerThreshold()
	{
		System.err.println("SYSTEM ERROR - calling upper threshold on RELU");
		System.exit(1);
		return Double.NaN;
	}
	
	public double runActivationFunction( int idx , double[] net)
	{
		System.err.println("RELU does not support Actionvation(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
	
	public double getDerivative( int row , int col , double[] net)
	{
		System.err.println("RELU does not support Derivative(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
}
