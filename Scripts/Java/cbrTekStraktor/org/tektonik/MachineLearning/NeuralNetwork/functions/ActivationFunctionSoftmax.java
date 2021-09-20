package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class ActivationFunctionSoftmax implements ActivationFunction {
	
	private double A = (double)1;
	private double lastDivider = Double.NaN;
	private double[] lastNet= new double[1]; // do not change
	
	public void setA( double a)
	{
		if( Double.isNaN(a) ) return;
		//this.A=a;
	}
	public double runActivationFunction(double x)
	{
		System.err.println("Softmax does not support Actionvation(x)");
		System.exit(1);
		return Double.NaN;
	}
	
	public double getDerivative(double x)
	{
		System.err.println("Softmax does not support Derivative(x)");
		System.exit(1);
		return Double.NaN;
	}
	
	public double getUpperThreshold()
	{
		return 1;
	}
	public double getLowerThreshold()
	{
		return 0;
	}

	private boolean isTheSameNet( double[] net )
	{
	  if( net.length != lastNet.length ) return false;
	  for(int i=0;i<net.length;i++)
	  {
		  if( net[i] != lastNet[i] ) return false;
	  }
	  return true;
	}
	
	private boolean getDivider( double[] net)
	{
		if( isTheSameNet( net ) ) return true;
		if( lastNet.length != net.length ) {
			lastNet = null;
			lastNet = new double[ net.length ];
		}
		lastDivider = 0;
		for(int i=0;i<net.length;i++)
		{
		  lastNet[i] = net[i];
		  lastDivider += Math.pow( Math.E , net[i] );
		}
	 	return true;
	}
	
	public double runActivationFunction( int idx , double[] net)
	{
		double ret = Double.NaN;
		try {
		   if( getDivider(net) == false ) return Double.NaN;
           ret = Math.pow( Math.E , net[idx] ) / lastDivider;	
		}
		catch(Exception e ) {
		   System.err.println( "SOFTMAX - Activation" + e.getMessage() );
		   ret = Double.NaN;		
		}
		if( Double.isNaN(ret) ) {
			System.err.println("SOFTMAX - Activation - NaN");
		}
		return ret;
	}
	
	public double getDerivative( int row , int col , double[] net)
	{
		double ret = Double.NaN;
		try {
		   if( getDivider(net) == false ) return Double.NaN;
		   if( row == col ) {
			   double dd = Math.pow( Math.E , net[row] ) / lastDivider;
			   ret = dd * (1 - dd);
		   }
		   else {
			   double dd1 = Math.pow( Math.E , net[row] ) / lastDivider;
			   double dd2 = Math.pow( Math.E , net[col] ) / lastDivider;
			   ret = -1 * dd1 * dd2;
		   }
		}
		catch( Exception e) {
			System.err.println( "SOFTMAX - Derivative" + e.getMessage() );
			ret = Double.NaN;	
		}
		if( Double.isNaN(ret) ) {
			System.err.println("SOFTMAX - Derivative - NaN");
		}
		return ret;
	}
	
}
