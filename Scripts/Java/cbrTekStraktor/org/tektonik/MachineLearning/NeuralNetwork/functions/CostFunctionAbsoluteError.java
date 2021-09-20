package org.tektonik.MachineLearning.NeuralNetwork.functions;

public class CostFunctionAbsoluteError implements CostFunction {

	    // Least Absolute 
		// Order  O-T does not matter, it is an absolute
	    //  | T - O |
		public double runCostFunction(double target , double output )
		{
			return ( target > output ) ? target-output : output - target;
		}
		
		// d |T-O| / d(o) => d|-O |/d(o) => |-1| => 1 
		public double getDerivative(double target , double output)
		{
			return 1;
		}
}
