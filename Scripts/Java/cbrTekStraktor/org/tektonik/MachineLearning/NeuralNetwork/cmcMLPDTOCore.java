package org.tektonik.MachineLearning.NeuralNetwork;

import org.tektonik.MachineLearning.cmcMachineLearningConstants;

public class cmcMLPDTOCore {
	
	private String LongARFFFileName = null;
	private int NbrOfHiddenLayers = -1;
	private int NbrOfNeuronsPerHiddenLayer=-1;
	private int SizeOfMiniBatch = -1;
	private cmcMLPenums.ACTIVATION_FUNCTION_TYPE ActivationFunction = cmcMLPenums.ACTIVATION_FUNCTION_TYPE.UNKNOWN;
	private cmcMLPenums.COST_FUNCTION_TYPE CostFunctionType = cmcMLPenums.COST_FUNCTION_TYPE.UNKNOWN;
	private cmcMLPenums.OPTIMIZATION_TYPE optimizationType = cmcMLPenums.OPTIMIZATION_TYPE.NONE;
	private cmcMLPenums.ACTIVATION_FUNCTION_TYPE OutputActivationFunction = cmcMLPenums.ACTIVATION_FUNCTION_TYPE.UNKNOWN;
	private cmcMLPenums.ASSESSMENT_TYPE AssessmentType = cmcMLPenums.ASSESSMENT_TYPE.ONE_HOT_ENCODED_MAXARGS;
	private cmcMLPenums.WEIGHT_INITIALIZATION_TYPE WeightStrategy = cmcMLPenums.WEIGHT_INITIALIZATION_TYPE.UNKNOWN;
	private double LearningRate = 0.1;
	private int MaximumNumberOfEpochs = cmcMachineLearningConstants.DEFAULT_NBR_OF_EPOCHS;
	private double AFactor = (double)1;
	private double DropOutRatio = (double)0.02;
	
	

	
	public cmcMLPDTOCore( String FName)
	{
		LongARFFFileName = FName;
		SizeOfMiniBatch = 50;
		MaximumNumberOfEpochs = cmcMachineLearningConstants.DEFAULT_NBR_OF_EPOCHS;
	}
	
	public void kloonCore( cmcMLPDTOCore x)
	{
		this.LongARFFFileName           = x.getLongARFFFileName();
		this.NbrOfHiddenLayers          = x.getNbrOfHiddenLayers();
		this.NbrOfNeuronsPerHiddenLayer = x.getNbrOfNeuronsPerHiddenLayer();
		this.SizeOfMiniBatch            = x.getSizeOfMiniBatch();
		this.CostFunctionType           = x.getCostFunctionType();
		this.ActivationFunction         = x.getActivationFunction();
		this.OutputActivationFunction   = x.getOutputActivationFunction();
		this.optimizationType           = x.getOptimizationType();
		this.LearningRate               = x.getLearningRate();
		this.MaximumNumberOfEpochs      = x.getMaximumNumberOfEpochs();
		this.AFactor					= x.getAFactor();
		this.DropOutRatio               = x.getDropOutRatio();
		this.AssessmentType             = x.getAssessmentType();
		this.WeightStrategy             = x.getWeightStrategy();
		}
	
	//========================================================
	public String getLongARFFFileName() {
		return LongARFFFileName;
	}

	public void setLongARFFFileName(String s) {
		LongARFFFileName = s;
	}
	public int getNbrOfHiddenLayers() {
		return NbrOfHiddenLayers;
	}
	
	public void setNbrOfHiddenLayers(int nbr) {
		NbrOfHiddenLayers = nbr;
	}
	public int getNbrOfNeuronsPerHiddenLayer() {
		return NbrOfNeuronsPerHiddenLayer;
	}
	
	public void setNbrOfNeuronsPerHiddenLayer(int nbr) {
		NbrOfNeuronsPerHiddenLayer = nbr;
	}

	public int getSizeOfMiniBatch() {
		return SizeOfMiniBatch;
	}

	public void setSizeOfMiniBatch(int p) {
		SizeOfMiniBatch = p;
	}

	public cmcMLPenums.COST_FUNCTION_TYPE getCostFunctionType() {
		return CostFunctionType;
	}

	public void setCostFunctionType(cmcMLPenums.COST_FUNCTION_TYPE costFunctionType) {
		CostFunctionType = costFunctionType;
	}
	public double getLearningRate() {
		return LearningRate;
	}

	public void setLearningRate(double learningRate) {
		LearningRate = learningRate;
	}
	public cmcMLPenums.ACTIVATION_FUNCTION_TYPE getActivationFunction() {
		return ActivationFunction;
	}

	public void setActivationFunction(cmcMLPenums.ACTIVATION_FUNCTION_TYPE activationFunction) {
		ActivationFunction = activationFunction;
	}
	public int getMaximumNumberOfEpochs() {
		return MaximumNumberOfEpochs;
	}

	public void setMaximumNumberOfEpochs(int maximumNumberOfEpochs) {
		MaximumNumberOfEpochs = maximumNumberOfEpochs;
	}
	public double getAFactor() {
		return AFactor;
	}

	public void setAFactor(double dd) {
		AFactor = dd;
	}
	
	public double getDropOutRatio() {
		return DropOutRatio;
	}

	public void setDropOutRatio(double dd) {
		DropOutRatio = dd;
	}
	
	public cmcMLPenums.OPTIMIZATION_TYPE getOptimizationType() {
		return optimizationType;
	}

	public void setOptimizationType(cmcMLPenums.OPTIMIZATION_TYPE optimizationTipe) {
		this.optimizationType = optimizationTipe;
	}
	public cmcMLPenums.ACTIVATION_FUNCTION_TYPE getOutputActivationFunction() {
		return OutputActivationFunction;
	}

	public void setOutputActivationFunction(cmcMLPenums.ACTIVATION_FUNCTION_TYPE outputActivationFunction) {
		OutputActivationFunction = outputActivationFunction;
	}
	
	public cmcMLPenums.ASSESSMENT_TYPE getAssessmentType() {
		return AssessmentType;
	}

	public void setAssessmentType(cmcMLPenums.ASSESSMENT_TYPE assessmentType) {
		AssessmentType = assessmentType;
	}
	
	public cmcMLPenums.WEIGHT_INITIALIZATION_TYPE getWeightStrategy() {
		return WeightStrategy;
	}

	public void setWeightStrategy(cmcMLPenums.WEIGHT_INITIALIZATION_TYPE weightStrategy) {
		WeightStrategy = weightStrategy;
	}
	
}
