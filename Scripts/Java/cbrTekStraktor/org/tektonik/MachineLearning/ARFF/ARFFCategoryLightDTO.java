package org.tektonik.MachineLearning.ARFF;

import org.tektonik.MachineLearning.ARFF.ARFFEnums.ARFF_TYPE;

public class ARFFCategoryLightDTO {

	private String categoryName;
	private ARFF_TYPE tipe = ARFF_TYPE.UNKNOWN;
	private String classesDefinition=null;  // comma separated list
	private String[] NominalValueList=null;
	private int[] Frequencies=null;
	private double mean = Double.NaN;
	private double max  = Double.NaN;
	private double min  = Double.NaN;
	private double StDev = Double.NaN;
	private String DateFormat = null;


	public ARFFCategoryLightDTO(String CatName , ARFF_TYPE tp )
	{
		 categoryName = CatName;
		 tipe = tp;
		 NominalValueList=null;
		 Frequencies=null;
		 classesDefinition=null;
	}

	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public ARFF_TYPE getTipe() {
		return tipe;
	}

	public void setTipe(ARFF_TYPE tipe) {
		this.tipe = tipe;
	}

	public String[] getNominalValueList() {
		return NominalValueList;
	}

	public void setNominalValueList(String[] nominalValueList) {
		NominalValueList = nominalValueList;
	}
	public String getClassesDefinition() {
		return classesDefinition;
	}

	public void setClassesDefinition(String classesDefinition) {
		this.classesDefinition = classesDefinition;
	}
	
	public int[] getFrequencies() {
		return Frequencies;
	}

	public void setFrequencies(int[] frequencies) {
		Frequencies = frequencies;
	}
	
	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}
	
	public double getStDev() {
		return StDev;
	}

	public void setStDev(double stDev) {
		StDev = stDev;
	}	
	
	public String getDateFormat() {
		return DateFormat;
	}

	public void setDateFormat(String dateFormat) {
		DateFormat = dateFormat;
	}	
}
