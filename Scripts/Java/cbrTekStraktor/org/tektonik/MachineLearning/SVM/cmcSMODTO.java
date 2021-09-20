package org.tektonik.MachineLearning.SVM;


import org.tektonik.MachineLearning.cmcMachineLearningEnums;
import org.tektonik.MachineLearning.ARFF.ARFFCategoryCoreDTO;
import org.tektonik.tools.linearAlgebra.cmcMatrix;

public class cmcSMODTO extends cmcSMODTOCore {
	
	private boolean FULL_PLATT_ALGORITHM = true;
	private ARFFCategoryCoreDTO[] features=null;
	private double[] normalizerMean=null;
	private double[] normalizerStdDev=null;
	private int NbrOfFeatures=-1;
	private int NbrOfRows=-1;
	private int NbrOfSupportVectors=0;
	private boolean initializedOK=false;
	private String Error=null;
	private cmcMatrix dataMatrix = null;
	private cmcMatrix resultMatrix = null;
    private cmcMatrix lagrangeMultiplierMatrix = null;   // M,1
    private double[] errorCache = null;
    private cmcMatrix weightMatrix = null;   //  dimensionality 1,N
    
    public cmcSMODTO(String FName , cmcMachineLearningEnums.KernelType itp)
	{
    	super(FName , itp);
    	initializedOK=false;
    	NbrOfFeatures=-1;
		NbrOfRows = -1;
		resultMatrix = null;
	}

	public cmcSMODTO(String FName, int inrows , double cin , double itol , int icycl , cmcMachineLearningEnums.KernelType itp)
	{
		super( FName, inrows , cin , itol , icycl , itp);
		//
		initializedOK=false;
		NbrOfFeatures=-1;
		NbrOfRows = inrows;
		resultMatrix = null;
	    //	
	    initializeMatrices( NbrOfRows ); 
	}
	//------------------------------------------------------------
	public void initializeMatrices(int inrows)
	//------------------------------------------------------------
	{
		// Lagrande multipliers - 1 for each row
    	double[][] alp = new double[ inrows ][ 1 ];
    	for(int i=0;i<inrows;i++) alp[i][0] = 0;
    	lagrangeMultiplierMatrix = new cmcMatrix( alp );
    	// error cache
    	this.errorCache = new double[ inrows ];
    	for(int i=0;i<inrows;i++) this.errorCache[i] = 0;
    	NbrOfSupportVectors=0;
	}
	//------------------------------------------------------------
	public String checkDTO()
	//------------------------------------------------------------
	{
		if( this.dataMatrix == null ) return "NULL DataMatrix";
    	if( this.resultMatrix == null ) return "NULL resultMatrix";
    	if( this.dataMatrix.getNbrOfRows() != this.getNbrOfRows() ) return "Row mismatch Data and DTO";
    	if( this.resultMatrix.getNbrOfRows() != this.getNbrOfRows() ) return "Row mismatch Data and Result matrix"; 
    	if( this.resultMatrix.getNbrOfColumns() != 1 ) return "Number of columns is not 1 on resultmatrix";
    	if( this.lagrangeMultiplierMatrix.getNbrOfColumns() != resultMatrix.getNbrOfColumns() )  return "Alpha en result col mismatch";    
    	if( this.lagrangeMultiplierMatrix.getNbrOfRows() != resultMatrix.getNbrOfRows() )  return "Alpha and result row mismatch"; 
    	if( this.lagrangeMultiplierMatrix.getNbrOfColumns() != 1)  return "lagrangemultipliers - nbr of cols must be 1"; 
    	if( this.lagrangeMultiplierMatrix.getNbrOfRows() != dataMatrix.getNbrOfRows() ) return "Alpha and data - rows mismatch"; 
    	//
    	return null;
	}
	//------------------------------------------------------------
	private void isOK()
	//------------------------------------------------------------
	{
		Error="";
		if( super.getKernelTrickTipe() == cmcMachineLearningEnums.KernelType.UNKNOWN ) Error += "\nKernelTrickType";
		if( super.getLongARFFFileName() == null ) Error += "\nFileName";
		if( super.getMaxCycles() <= 0 ) Error += "\nMaxCyles";
		//if( NbrOfFeatures <= 0) Error += "\nNumber of Features";  only used later on
		if( NbrOfRows <= 0) Error += "\nNumber of Rows";
		if( Double.isNaN( super.getC() ) ) Error += "\nSoftmargin";
		if( Double.isNaN( super.getBias()))Error += "\nBias";
		if( Double.isNaN( super.getTolerance())) Error += "\nTolerance";
		if( super.getC() < 0 ) Error += "\nSoft Margin";
		if( super.getBias() < 0 ) Error += "\nBias";
		if( super.getTolerance() <= 0 ) Error += "\nTolerance";
		if( Double.isNaN(super.getGamma()))Error += "\nGamma";	
		if( Double.isNaN(super.getSigma()))Error += "\nSigma";
		if( super.getGamma() <= 0 ) Error += "\ngamma";
		if( super.getSigma() <= 0 ) Error += "\nsigma";
		initializedOK = (Error.trim().length() <= 0) ? true : false;
	}
	//------------------------------------------------------------
	public boolean setErrorCacheIdxValue( int idx , double dd )
	//------------------------------------------------------------
	{
		try {
			this.errorCache[ idx ] = dd;
			return true;
		}
		catch(Exception e ) {
			return false;
		}
	}
	//------------------------------------------------------------
	public boolean calculateNbrOfSupportVectors()
	//------------------------------------------------------------
	{
		NbrOfSupportVectors=0;
		if( lagrangeMultiplierMatrix == null ) return false;  // M,1
		for(int i=0;i<lagrangeMultiplierMatrix.getNbrOfRows();i++)
		{
			if( this.lagrangeMultiplierMatrix.getValues()[i][0] != 0 ) NbrOfSupportVectors++;
		}
		return true;
	}
	

	//------------------------------------------------------------
	//------------------------------------------------------------
	public String getError()
	{
		return Error;
	}
	
	public boolean isInitializedOK() {
		isOK();
		return initializedOK;
	}

	public void setInitializedOK(boolean initializedOK) {
		this.initializedOK = initializedOK;
	}
	
	public int getNbrOfSupportVectors() {
		return NbrOfSupportVectors;
	}

	public void setNbrOfSupportVectors(int i)
	{
		NbrOfSupportVectors = i;
	}
	
	public cmcMatrix getWeightMatrix() {
		return weightMatrix;
	}

	public void setWeightMatrix(cmcMatrix weightMatrix) {
		this.weightMatrix = weightMatrix;
	}
	public boolean isFULL_PLATT_ALGORITHM() {
		return FULL_PLATT_ALGORITHM;
	}
	
	public double[] getErrorCache() {
		return errorCache;
	}

	public void setErrorCache(double[] errorCache) {
		this.errorCache = errorCache;
	}
	
	public cmcMatrix getDataMatrix() {
		return dataMatrix;
	}

	public cmcMatrix getResultMatrix() {
		return resultMatrix;
	}

	public void setDataMatrix(cmcMatrix dataMatrix) {
		this.dataMatrix = dataMatrix;
	}

	public void setResultMatrix(cmcMatrix resultMatrix) {
		this.resultMatrix = resultMatrix;
	}

	public cmcMatrix getAlphaMatrix() {
		return lagrangeMultiplierMatrix;
	}

	public void setAlphaMatrix(cmcMatrix lagrangeMultiplierMatrix) {
		this.lagrangeMultiplierMatrix = lagrangeMultiplierMatrix;
	}
	
	public cmcMatrix getLagrangeMultiplierMatrix() {
		return lagrangeMultiplierMatrix;
	}

	public void setLagrangeMultiplierMatrix(cmcMatrix lagrangeMultiplierMatrix) {
		this.lagrangeMultiplierMatrix = lagrangeMultiplierMatrix;
	}

	public int getNbrOfRows() {
		return NbrOfRows;
	}

	public void setNbrOfRows(int nbrOfRows) {
		NbrOfRows = nbrOfRows;
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

	public void setFeatures(ARFFCategoryCoreDTO[] categories) {
		this.features = categories;
	}

	public void setNormalizerMean(double[] normalizerMean) {
		this.normalizerMean = normalizerMean;
	}

	public void setNormalizerStdDev(double[] normalizerStdDev) {
		this.normalizerStdDev = normalizerStdDev;
	}
	
	public int getNbrOfFeatures() {
		return NbrOfFeatures;
	}

	public void setNbrOfFeatures(int nbrOfFeatures) {
		NbrOfFeatures = nbrOfFeatures;
	}
	
}
