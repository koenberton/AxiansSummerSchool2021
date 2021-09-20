package org.tektonik.MachineLearning.NeuralNetwork;

import org.tektonik.MachineLearning.ARFF.ARFFCategoryCoreDTO;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.neuralNetwork;

public class cmcMLPDTO extends cmcMLPDTOCore {

	
	private ARFFCategoryCoreDTO[] features=null;
	private String IgnoredFeatures=null;
	private double[] normalizerMean=null;
	private double[] normalizerStdDev=null;
	private int NbrOfFeatures=-1;
	private int NbrOfRows=-1;
	private long[][] runtimes = null;  // n x 3 :  n x { start - stop - rows }
	private int EpochsPerformed=0;
	private boolean initializedOK=false;
	private String Error=null;
	private neuralNetwork ntw = null;

	
	public cmcMLPDTO(String FName )
	{
		    super( FName );
	    	initializedOK=false;
	    	IgnoredFeatures=null;
	    	NbrOfFeatures=-1;
			NbrOfRows = -1;
			EpochsPerformed=0;
			ntw=null;
			runtimes = new long[100][3];
			for(int i=0;i<runtimes.length;i++) 
			{
				runtimes[i][0] = -1L;
				runtimes[i][1] = -1L;
				runtimes[i][2] = -1L;
			}
			runtimes[0][0] = System.currentTimeMillis();
			runtimes[0][1] = runtimes[0][0];
			runtimes[0][2] = 0L;
	}
	
	public boolean isInitializedOK() {
		isOK();
		return initializedOK;
	}
	
	/*
	//------------------------------------------------------------
	public String checkDTO()
	//------------------------------------------------------------
	{
			if( this.dataMatrix == null ) return "NULL DataMatrix";
	    	if( this.dataMatrix.getNbrOfRows() != this.getNbrOfRows() ) return "Row mismatch Data and DTO";
	    	//
	    	return null;
	}
	*/
	//------------------------------------------------------------
	private void isOK()
	//------------------------------------------------------------
	{
		Error="";
		if( super.getLongARFFFileName() == null ) Error += "\nFileName";
		if( NbrOfRows <= 0) Error += "\nNumber of Rows";
		initializedOK = (Error.trim().length() <= 0) ? true : false;
	}

	public ARFFCategoryCoreDTO[] getFeatures() {
		return features;
	}

	public double[] getNormalizerMean() {
		return normalizerMean;
	}

	public double[] getNormalizerStdDev() {
		return normalizerStdDev;
	}

	public int getNbrOfFeatures() {
		return NbrOfFeatures;
	}

	public int getNbrOfRows() {
		return NbrOfRows;
	}

	public String getError() {
		return Error;
	}

	public void setFeatures(ARFFCategoryCoreDTO[] features ) {
		this.features = features;
	}

	public void setNormalizerMean(double[] normalizerMean) {
		this.normalizerMean = normalizerMean;
	}

	public void setNormalizerStdDev(double[] normalizerStdDev) {
		this.normalizerStdDev = normalizerStdDev;
	}

	public void setNbrOfFeatures(int nbrOfFeatures) {
		NbrOfFeatures = nbrOfFeatures;
	}

	public void setNbrOfRows(int nbrOfRows) {
		NbrOfRows = nbrOfRows;
	}

	public void setInitializedOK(boolean initializedOK) {
		this.initializedOK = initializedOK;
	}

	public void setError(String error) {
		Error = error;
	}

	public neuralNetwork getNtw() {
		return ntw;
	}

	public void setNtw(neuralNetwork ntw) {
		this.ntw = ntw;
	}

	public long[][] getRuntimes() {
		return runtimes;
	}

	public void setRuntimes(long[][] runtimes) {
		this.runtimes = runtimes;
	}
	
	public void setEpochsPerformed(int i) {
		this.EpochsPerformed = i;
	}

	public int getEpochsPerformed() {
		return this.EpochsPerformed;
	}
	
	public String getIgnoredFeatures() {
		return IgnoredFeatures;
	}

	public void setIgnoredFeatures(String ignoredFeatures) {
		IgnoredFeatures = ignoredFeatures;
	}

}
