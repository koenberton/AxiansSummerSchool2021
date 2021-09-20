package org.tektonik.MachineLearning.SVM;

import org.tektonik.MachineLearning.cmcMachineLearningConstants;
import org.tektonik.MachineLearning.cmcMachineLearningEnums;

public class cmcSMODTOCore {

	private String LongARFFFileName = null;
	private int MaxCycles=-1;
	private cmcMachineLearningEnums.KernelType KernelTrickTipe = cmcMachineLearningEnums.KernelType.UNKNOWN;
	private double gamma = Double.NaN;     // Kerneltrick Gaussian gamme
	private double sigma = Double.NaN;     // KernelTrick RBF sigma
	private double C = Double.NaN;         // SoftMargin  0.6
	private double Bias = Double.NaN;      // bias  0
	private double tolerance = Double.NaN; // 0.001
	
	public cmcSMODTOCore(String FName , cmcMachineLearningEnums.KernelType itp)
	{
		this.setLongARFFFileName( FName );
		this.setKernelTrickTipe( itp );
		//
		MaxCycles=cmcMachineLearningConstants.SMO_NUMBER_OF_CYCLES;
		C = cmcMachineLearningConstants.SMO_SOFTMARGIN;
		tolerance = cmcMachineLearningConstants.SMO_TOLERANCE;
		gamma = cmcMachineLearningConstants.SMO_RBF_GAMMA;
		sigma = cmcMachineLearningConstants.SMO_GAUSSIAN_SIGMA;
		Bias = 0;
	}

	public cmcSMODTOCore(String FName, int inrows , double cin , double itol , int icycl , cmcMachineLearningEnums.KernelType itp)
	{
		this.setLongARFFFileName( FName );
		this.setKernelTrickTipe( itp );
		this.setMaxCycles(icycl);
		this.setC(cin);
		this.setTolerance(itol);
		//
		gamma = cmcMachineLearningConstants.SMO_RBF_GAMMA;
		sigma = cmcMachineLearningConstants.SMO_GAUSSIAN_SIGMA;
		Bias = 0;
	}
	
	public String getLongARFFFileName() {
		return LongARFFFileName;
	}

	public void setLongARFFFileName(String longARFFFileName) {
		LongARFFFileName = longARFFFileName;
	}
	public double getC() {
		return C;
	}

	public double getBias() {
		return Bias;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setC(double c) {
		C = c;
	}

	public void setBias(double bias) {
		Bias = bias;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public double getSigma() {
		return sigma;
	}

	public double getGamma() {
		return gamma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
	
	public cmcMachineLearningEnums.KernelType getKernelTrickTipe() {
		return KernelTrickTipe;
	}

	public void setKernelTrickTipe(cmcMachineLearningEnums.KernelType kernelTrickTipe) {
		KernelTrickTipe = kernelTrickTipe;
	}

	
	public int getMaxCycles() {
		return MaxCycles;
	}

	public void setMaxCycles(int maxCycles) {
		MaxCycles = maxCycles;
	}
}
