package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class CostFunctionCrossEntropy implements CostFunction {

	    private static double EPSILON = (double)0.000000001;
	
	    // Cross Entropy
		// - t log o
		public double runCostFunction(double target , double output )
		{
			if( target == 0 ) return 0;
			try {
			   double dd = Math.log( output );
			   if( Double.isInfinite(dd)) {
				   dd = 1 / EPSILON;
				   //System.err.println( "LN(" + output + ") -> Infinite");
			   }
			   return -1 * target * dd;
			}
			catch(Exception e ) {
			   System.err.println( "-" + target + " * LN (" + output + ") -> " + e.getMessage() );
			   return Double.NaN;
			}
		}
		
		// derivative - t / o
		public double getDerivative(double target , double output)
		{
			if( target == 0 ) return 0;
			try {
			  double dd = (output==0) ? EPSILON : output;
			  return  -1 * target / dd;
			}
			catch(Exception e ) {
			   System.err.println( "-" + target + " / " + output  + " -> " + e.getMessage() );
			   return Double.NaN;
			}
		}
}
