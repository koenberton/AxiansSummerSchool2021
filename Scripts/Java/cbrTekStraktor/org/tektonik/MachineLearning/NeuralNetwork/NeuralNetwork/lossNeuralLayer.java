package org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork;

import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;
import org.tektonik.MachineLearning.NeuralNetwork.functions.ActivationFunction;
import org.tektonik.MachineLearning.NeuralNetwork.functions.CostFunction;
import org.tektonik.MachineLearning.NeuralNetwork.functions.CostFunctionAbsoluteError;
import org.tektonik.MachineLearning.NeuralNetwork.functions.CostFunctionCrossEntropy;
import org.tektonik.MachineLearning.NeuralNetwork.functions.CostFunctionSquaredError;
import org.tektonik.tools.linearAlgebra.cmcVector;



public class lossNeuralLayer extends neuralLayer {

	private cmcMLPenums.COST_FUNCTION_TYPE costfunctiontipe = null;
	private CostFunction costfunction = null;
	private double MinLoss = Double.NaN;
	private double MaxLoss = Double.NaN;
	private int ticker = 0;

	//-----------------------------------------------------------------------
	public lossNeuralLayer(int uid , int ineurons , int iminibsamples , cmcMLPenums.COST_FUNCTION_TYPE icostfunctiontipe )
	{
		 super( uid , ineurons , iminibsamples);
		 super.setTipe( cmcMLPenums.LAYER_TYPE.LOSS );
		 super.setActivationFunctionTipe( cmcMLPenums.ACTIVATION_FUNCTION_TYPE.IGNORE );

		 this.costfunctiontipe = icostfunctiontipe;
		 switch( costfunctiontipe )
		 {
		 case SQUARED_ERROR  : { costfunction = new CostFunctionSquaredError();  break; }
		 case ABSOLUTE_ERROR : { costfunction = new CostFunctionAbsoluteError();  break; }
		 case CROSS_ENTROPY  : { costfunction = new CostFunctionCrossEntropy();  break; }
		 default : { costfunction = null; do_error("Unsupported cost function type " + costfunctiontipe + " - aborting."); System.exit(1); break; }
		 }
	}
	
	//-----------------------------------------------------------------------
	public double runCostFunction(double target , double output)
	{
		try {
		  return this.costfunction.runCostFunction(target, output);
		}
		catch(Exception e )  {
			do_error( "NULL costfunction");
			return Double.NaN;
		}
	}
	//-----------------------------------------------------------------------
	public double runCostFunctionDerivative(double target , double output)
	{
		try {
		  return this.costfunction.getDerivative(target, output);
		}
		catch(Exception e )  {
			do_error( "NULL costfunction");
			return Double.NaN;
		}
	}
	//-----------------------------------------------------------------------
	public double getAggregatedLosses()
	{
		if( Double.isNaN(MinLoss) ) calculateMinMaxLosses();
		try {
		   double ret = 0;
		   cmcVector[] outs = this.getOutputsFast();
		   if( outs == null )  {
			   do_error("Loss layer - output is null");
			   return Double.NaN;
		   }
		   for(int i=0;i<outs.length;i++)
		   {
			   for(int j=0;j<outs[i].getDimension();j++)
			   {
				   ret += outs[i].getVectorValues()[j];
//do_error( "" + ret + " " + outs[i].getVectorValues()[j] );
			   }
		   }
		   if( (Double.isNaN(MinLoss)==false) && (Double.isNaN(MaxLoss)==false) ) {
			   try {
			      double max = MaxLoss * this.getNbrOfNeuronsInLayer() * this.getNbrOfMiniBatchSamples();
			      double min = MinLoss * this.getNbrOfNeuronsInLayer() * this.getNbrOfMiniBatchSamples();
 			      double zz = (ret - min) / (max - min);
//if( ((ticker++) % 8) == 1) System.out.println("(lossNeuralLayer) Min=" + min + " Max=" + max + " In=" + ret  + " Ret=" + zz);
			      ret = zz * 100;  // maak er percentage van
			   }
			   catch(Exception e ) {
				  do_error( "(neuralLossLayer) getAggregatedLoss - exception while calculating ZZ  " + e.getMessage());
			   }
		   }
		   if( Double.isNaN(ret) ) {
			   do_error("(neuralLossLayer) getAggregatedLoss - Total cost is NaN");
		   }
		   return ret;	
		}
		catch(Exception e )  {
			do_error( "(neuralLossLayer) getAggregatedLoss - exception " + e.getMessage());
			return Double.NaN;
		}
	}
	//-----------------------------------------------------------------------
	private boolean calculateMinMaxLosses()
	{
		// haal de min en max resultaten van de voorgaande laag
		// doe dat door de activatie functie te halen
		// daarvan de lower/upper en die te runnen tegen de costfunctie
		MinLoss = Double.NaN;
        MaxLoss = Double.NaN;
        if( this.costfunctiontipe == cmcMLPenums.COST_FUNCTION_TYPE.CROSS_ENTROPY ) return true;
		try {
		  neuralLayer prev = this.getPreviousLayer();
		  if( prev == null ) return false;
		  ActivationFunction func = prev.getNeurons()[0].getActivationFunction();
		  double lowerlimit = func.getLowerThreshold();
		  double upperlimit = func.getUpperThreshold();
		  //
		  double min = Double.NaN;
		  double max = Double.NaN;
		  for(int target=0;target<2;target++)
	  	  {
			for(int j=0;j<2;j++)
			{
				double x = (j==0) ? lowerlimit : upperlimit;
				double y = this.runCostFunction(target,x);
//System.out.println("(lossNeuralLayer) target=" + target + " X=" + x + " Y=" + y );
				if( Double.isNaN(min) ) {
					min = y;
					max = y;
				}
				if( min > y ) min = y;
				if( max < y ) max = y;
			}
		  }
          MinLoss = min;
          MaxLoss = max;
          if(Double.isNaN(MinLoss) || Double.isInfinite(MinLoss)) MinLoss = Double.NaN;
          if(Double.isNaN(MaxLoss) || Double.isInfinite(MaxLoss)) MaxLoss = Double.NaN;
System.out.println("(lossNeuralLayer) MinLoss=" + MinLoss + " MaxLoss=" + MaxLoss );
		  return true;
		}
		catch(Exception e ) {
			MinLoss = Double.NaN;
	        MaxLoss = Double.NaN;
			return false;	
		}
	}
	

}
