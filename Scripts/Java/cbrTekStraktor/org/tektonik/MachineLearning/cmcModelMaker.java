package org.tektonik.MachineLearning;

import org.tektonik.MachineLearning.ARFF.ARFFCategory;
import org.tektonik.MachineLearning.ARFF.cmcARFF;

public interface cmcModelMaker {
	
	public boolean trainModel(	ARFFCategory[] categories , int nbins , String[] ClassNameList , String ModelFileName );
	public boolean trainModel(	String OriginalFileName , String ModelFileName );
	public boolean testModel( String ModelFileName , String FileNameTestSet , cmcARFF xa);

}
