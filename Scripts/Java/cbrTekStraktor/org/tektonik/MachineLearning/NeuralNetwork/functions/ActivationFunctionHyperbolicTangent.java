package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class ActivationFunctionHyperbolicTangent implements ActivationFunction {

	private double A = (double)1;
	public void setA( double a)
	{
		if( Double.isNaN(a) ) return;
		this.A = a;
	}
	
	public double runActivationFunction(double x)
	{
		double ex = Double.NaN;
		double eminx = Double.NaN;
		try {
			ex = Math.pow(Math.E,x*A);
			eminx = Math.pow(Math.E,(-1*x*A));
		    //double dd = (ex - eminx) / (ex + eminx);
		    double dd = (1 - eminx) / (1 + eminx);
		    
		    if( Double.isNaN(dd) ) {
		    	//System.err.println( "x=" + x + "  ex=" + ex + "  eminx=" + eminx + " dd=" + dd);
		    	if( x < 0 )  return -1; else return 1;
		    }
		    return dd;
		}
		catch(Exception e) {
			System.err.println( "x=" + x + "  ex=" + ex + "  eminx=" + eminx );
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
		System.err.println("HypTan does not support Actionvation(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
	
	public double getDerivative( int row , int col , double[] net)
	{
		System.err.println("HypTan does not support Derivative(idx,net)");
		System.exit(1);
		return Double.NaN;
	}
	
}
