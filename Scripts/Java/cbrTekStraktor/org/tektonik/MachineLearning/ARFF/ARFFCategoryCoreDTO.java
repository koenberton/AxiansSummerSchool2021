package org.tektonik.MachineLearning.ARFF;

import org.tektonik.MachineLearning.ARFF.ARFFEnums.ARFF_TYPE;

public class ARFFCategoryCoreDTO {

	private String categoryName;
	private ARFF_TYPE tipe = ARFF_TYPE.UNKNOWN;
	private String[] NominalValueList=null;
		
	public ARFFCategoryCoreDTO(String sname)
	{
		categoryName = sname;
	}

	/*
	public boolean isEqual(ARFFCategoryCoreDTO cmp)
	{
		if( cmp == null ) return false;
		if( cmp.getCategoryName().compareToIgnoreCase(this.categoryName) != 0 ) return false;
		if( cmp.getTipe() != this.tipe ) return false;
		if( (cmp.getNominalValueList() == null) && (this.NominalValueList == null)) return true;
		if( (cmp.getNominalValueList() != null) && (this.NominalValueList == null)) return false;
		if( (cmp.getNominalValueList() == null) && (this.NominalValueList != null)) return false;
		if( cmp.getNominalValueList().length != this.NominalValueList.length ) return false;
		for(int i=0;i<cmp.getNominalValueList().length;i++)
		{
			if( cmp.getNominalValueList()[i].compareToIgnoreCase(this.NominalValueList[i]) != 0) return false;
		}
        return true;
	}
	*/
	
	public String getCategoryName() {
		return categoryName;
	}
	public String getName() {
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
	
	
}
