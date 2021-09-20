package org.tektonik.MachineLearning.DecisionTree;

import org.tektonik.MachineLearning.cmcMachineLearningEnums;
import org.tektonik.MachineLearning.ARFF.ARFFCategoryCoreDTO;
import org.tektonik.MachineLearning.ARFF.ARFFEnums.ARFF_TYPE;
import org.tektonik.MachineLearning.ARFF.cmcARFF;


public class DecisionTreeDTO {
	
	public int level=-1;
	public long UID=-1L;
	public long parentUID=-1L;
	public cmcMachineLearningEnums.BranchOrientation orientation = cmcMachineLearningEnums.BranchOrientation.UNKNOWN;
	public String[] ClassNameList;
	public cmcMachineLearningEnums.DecisionTreeType treetipe = cmcMachineLearningEnums.DecisionTreeType.UNKNOWN;
	public double TotalEntropy;
	public int splitCategoryIndex=-1;
	public String splitOnCategoryName=null;
	public ARFF_TYPE splitOnARFFTipe = ARFF_TYPE.UNKNOWN;
	int splitValueIndex=-1;
	public double splitOnValue=Double.NaN;
	public String splitOnNominalName=null;
	public int NumberOfRows=-1;
	public String LeafClassName=null;
	public boolean FlippedACoin=false;
	public ARFFCategoryCoreDTO[] categories=null;
	
	public DecisionTreeDTO()
	{
		
	}

}
