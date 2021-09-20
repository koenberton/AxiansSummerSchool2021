package org.tektonik.MachineLearning.NeuralNetwork;

public class cmcMLPenums {

	public enum LAYER_TYPE { UNKNOWN , INPUT , HIDDEN , OUTPUT , LOSS }
	public enum COST_FUNCTION_TYPE { UNKNOWN , CROSS_ENTROPY , SQUARED_ERROR , ABSOLUTE_ERROR }
	public enum ACTIVATION_FUNCTION_TYPE { UNKNOWN , IGNORE , CONSTANT_ONE , SIGMOID , HYPERBOLIC_TANGENT , TANH , RELU , SOFTMAX }
	public enum NEURON_TYPE { COMPLETE , ACTIVATION_ONLY  } 
	public enum OPTIMIZATION_TYPE { NONE , MOMEMTUM , RMSPROP , ADAM }
	public enum ASSESSMENT_TYPE { ONE_HOT_ENCODED_MAXARGS , ONE_HOT_ENCODED_WA_TWIST_MAXARGS , ONE_HOT_ENCODED_EUCLIDIAN , ONE_HOT_ENCODED_WA_TWIST_EUCLIDIAN , STAGGERED } 
	public enum WEIGHT_INITIALIZATION_TYPE { UNKNOWN , XAVIER , HE , ALL_ONES , ALL_ZEROES , SIMPLE }
	
	
	//---------------------------------------------------------------------------------
	public LAYER_TYPE getLayerType(String s)
	//---------------------------------------------------------------------------------
	{
		if( s == null ) return null;
		String sel = s.trim().toUpperCase();
	    int idx = -1;
		for(int i=0;i<LAYER_TYPE.values().length;i++)
		{
			if( sel.compareToIgnoreCase( LAYER_TYPE.values()[i].toString() ) == 0 ) {
				idx=i;
				break;
			}
		}
		if( idx < 0 ) return null;
		return LAYER_TYPE.values()[idx];
	}
	
	//---------------------------------------------------------------------------------
	public String[] getActivationFunctionTypeList()
	//---------------------------------------------------------------------------------
	{
		String lijst[] = new String[ACTIVATION_FUNCTION_TYPE.values().length];
		for(int i=0;i<ACTIVATION_FUNCTION_TYPE.values().length;i++)
		{
		 lijst[i] = ACTIVATION_FUNCTION_TYPE.values()[i].toString();
		}
		return lijst;
	}
	//---------------------------------------------------------------------------------
	public ACTIVATION_FUNCTION_TYPE getActivationFunctionType(String s)
	//---------------------------------------------------------------------------------
	{
		if( s == null ) return null;
		String sel = s.trim().toUpperCase();
	    int idx = -1;
		for(int i=0;i<ACTIVATION_FUNCTION_TYPE.values().length;i++)
		{
			if( sel.compareToIgnoreCase( ACTIVATION_FUNCTION_TYPE.values()[i].toString() ) == 0 ) {
				idx=i;
				break;
			}
		}
		if( idx < 0 ) return null;
		return ACTIVATION_FUNCTION_TYPE.values()[idx];
	}
	//---------------------------------------------------------------------------------
	public String[] getCostFunctionTypeList()
	//---------------------------------------------------------------------------------
	{
		String lijst[] = new String[COST_FUNCTION_TYPE.values().length];
		for(int i=0;i<COST_FUNCTION_TYPE.values().length;i++)
		{
		 lijst[i] = COST_FUNCTION_TYPE.values()[i].toString();
		}
		return lijst;
	}
	//---------------------------------------------------------------------------------
	public COST_FUNCTION_TYPE getCostFunctionType(String s)
	//---------------------------------------------------------------------------------
	{
		if( s == null ) return null;
		String sel = s.trim().toUpperCase();
	    int idx = -1;
		for(int i=0;i<COST_FUNCTION_TYPE.values().length;i++)
		{
			if( sel.compareToIgnoreCase( COST_FUNCTION_TYPE.values()[i].toString() ) == 0 ) {
				idx=i;
				break;
			}
		}
		if( idx < 0 ) return null;
		return COST_FUNCTION_TYPE.values()[idx];
	}
	//---------------------------------------------------------------------------------
	public String[] getOptimizationTypeList()
	//---------------------------------------------------------------------------------
	{
		String lijst[] = new String[OPTIMIZATION_TYPE.values().length];
		for(int i=0;i<OPTIMIZATION_TYPE.values().length;i++)
		{
		 lijst[i] = OPTIMIZATION_TYPE.values()[i].toString();
		}
		return lijst;
	}
	//---------------------------------------------------------------------------------
	public OPTIMIZATION_TYPE getOptimizationType(String s)
	//---------------------------------------------------------------------------------
	{
		if( s == null ) return null;
		String sel = s.trim().toUpperCase();
	    int idx = -1;
		for(int i=0;i<OPTIMIZATION_TYPE.values().length;i++)
		{
			if( sel.compareToIgnoreCase( OPTIMIZATION_TYPE.values()[i].toString() ) == 0 ) {
				idx=i;
				break;
			}
		}
		if( idx < 0 ) return null;
		return OPTIMIZATION_TYPE.values()[idx];
	}
	//---------------------------------------------------------------------------------
	public String[] getAssessmentTypeList()
	//---------------------------------------------------------------------------------
	{
		String lijst[] = new String[ASSESSMENT_TYPE.values().length];
		for(int i=0;i<ASSESSMENT_TYPE.values().length;i++)
		{
		 lijst[i] = ASSESSMENT_TYPE.values()[i].toString();
		}
		return lijst;
	}	
	
	//---------------------------------------------------------------------------------
	public ASSESSMENT_TYPE getAssessmentType(String s)
	//---------------------------------------------------------------------------------
	{
		if( s == null ) return null;
		String sel = s.trim().toUpperCase();
	    int idx = -1;
		for(int i=0;i<ASSESSMENT_TYPE.values().length;i++)
		{
			if( sel.compareToIgnoreCase( ASSESSMENT_TYPE.values()[i].toString() ) == 0 ) {
				idx=i;
				break;
			}
		}
		if( idx < 0 ) return null;
		return ASSESSMENT_TYPE.values()[idx];
	}
	
	//---------------------------------------------------------------------------------
	public String[] getWeightInitializationTypeList()
	//---------------------------------------------------------------------------------
	{
		String lijst[] = new String[WEIGHT_INITIALIZATION_TYPE.values().length];
		for(int i=0;i<WEIGHT_INITIALIZATION_TYPE.values().length;i++)
		{
		 lijst[i] = WEIGHT_INITIALIZATION_TYPE.values()[i].toString();
		}
		return lijst;
	}	
	
	//---------------------------------------------------------------------------------
	public WEIGHT_INITIALIZATION_TYPE getWeightInitializationType(String s)
	//---------------------------------------------------------------------------------
	{
		if( s == null ) return null;
		String sel = s.trim().toUpperCase();
	    int idx = -1;
		for(int i=0;i<WEIGHT_INITIALIZATION_TYPE.values().length;i++)
		{
			if( sel.compareToIgnoreCase( WEIGHT_INITIALIZATION_TYPE.values()[i].toString() ) == 0 ) {
				idx=i;
				break;
			}
		}
		if( idx < 0 ) return null;
		return WEIGHT_INITIALIZATION_TYPE.values()[idx];
	}
			
	
}
