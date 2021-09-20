package org.tektonik.MachineLearning.ARFF;

import org.tektonik.MachineLearning.ARFF.ARFFEnums.ARFF_TYPE;


public class ARFFLightDTO {
	
	private enum STAT_TYPE { MEAN , STDEV , MAX , MIN }
	
	private String ARFFName=null;
	private int NbrOfDataRows=-1;
	private String LongFileName=null;
	private ARFFCategoryLightDTO[] attributeList=null;
	 

	
    //
	public ARFFLightDTO(String FullFileName, int nattribs , int nlines)
	{
		LongFileName = FullFileName;
		NbrOfDataRows = nlines;
		attributeList = new ARFFCategoryLightDTO[ nattribs ];
		for(int i=0;i<attributeList.length;i++) attributeList[i] = new ARFFCategoryLightDTO( "CAT"+i , ARFF_TYPE.UNKNOWN );
	}

	private double[] getStat( STAT_TYPE tipe)
	{
		if( attributeList == null ) return null;
		double[] ret = new double[ attributeList.length ];
		for(int i=0;i<attributeList.length;i++) 
		{
			ARFFCategoryLightDTO cat = attributeList[i];
			if( cat == null ) return null;
			switch( tipe )
			{
			case MEAN  : { ret[i] = cat.getMean(); break; }
			case STDEV : { ret[i] = cat.getStDev(); break; }
			case MIN   : { ret[i] = cat.getMin(); break; }
			case MAX   : { ret[i] = cat.getMax(); break; }
			default : return null;
			}
		}
		return ret;
	}
	
	public double[] getNormalizerMean()
	{
		return getStat( STAT_TYPE.MEAN );
	}
	
	public double[] getNormalizerStdDev()
	{
		return getStat( STAT_TYPE.STDEV );
	}
	
	public String getARFFName() {
		return ARFFName;
	}

	public void setARFFName(String aRFFName) {
		ARFFName = aRFFName;
	}

	public int getNbrOfDataRows() {
		return NbrOfDataRows;
	}

	public void setNbrOfDataRows(int nbrOfDataRows) {
		NbrOfDataRows = nbrOfDataRows;
	}
	
	public String getLongFileName() {
		return LongFileName;
	}

	public void setLongFileName(String longFileName) {
		LongFileName = longFileName;
	}

	public int getNbrOfLines() {
		return this.NbrOfDataRows;
	}

	public void setNbrOfLines(int nbrOfLines) {
		this.NbrOfDataRows = nbrOfLines;
	}
	
	public ARFFCategoryLightDTO[] getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(ARFFCategoryLightDTO[] attributeList) {
		this.attributeList = attributeList;
	}	

}
