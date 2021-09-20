package org.tektonik.MachineLearning.Evaluation;


public class cmcModelEvaluation {

	private static String ctEOL =  System.getProperty("line.separator");
			
	String[] ClassNames=null;
	int[][] confusion = null;
	double  Accuracy=0;
	int totalEntries=0;
	double[] precision           = null;  // PPV
	double[] recall              = null;  // True Positive Rate
	double[] fallout             = null;  // False Positive Rate
	int[] truePositivesPerClass  = null;
	int[] falsePositivesPerClass = null;
	int[] trueNegativesPerClass  = null;
	int[] falseNegativesPerClass = null;
	
	//------------------------------------------------------------
	public cmcModelEvaluation(String[] classnames )
	//------------------------------------------------------------
	{
		if( classnames != null ) {
		  int nclasses = classnames.length;
		  ClassNames = new String[ nclasses ];
		  precision  = new double[ nclasses ];
		  recall     = new double[ nclasses ];
		  fallout    = new double[ nclasses ];
		  confusion  = new int[ nclasses ][ nclasses ];
		  for(int i=0 ; i<nclasses ; i++) 
		  { 
			  ClassNames[i] = classnames[i];
			  precision[i] = recall [i] = fallout[i] = 0;
			  for( int j=0;j<nclasses;j++) { confusion[i][j]=0; }
		  }
	    }
	}
	//------------------------------------------------------------
	public boolean addToConfusion( String calculated , String anticipated)
	//------------------------------------------------------------
	{
		int predicted = -1; // calculated-predicted
		int actual = -1; // actual
		for(int i=0;i<this.ClassNames.length;i++)
		{
			if( anticipated.compareToIgnoreCase( ClassNames[i] ) == 0 ) actual = i;
			if( calculated.compareToIgnoreCase( ClassNames[i] )  == 0 ) predicted = i;
		}
		if( (actual < 0) || (predicted < 0) ) {
			//do_error("cannot allocate [" + anticipated + "," + calculated + "]");
			return false;
		}
		confusion[ predicted ][ actual ]++;
		return true;
	}
	//------------------------------------------------------------
	public boolean injectMatrix( int[][] inj)
	//------------------------------------------------------------
	{
			confusion = inj;
			return true;
	}
	//------------------------------------------------------------
	public boolean calculate()
	//------------------------------------------------------------
	{
		int nclasses = confusion.length;
		truePositivesPerClass = new int[ nclasses ];
		falsePositivesPerClass = new int[ nclasses ];
		trueNegativesPerClass = new int[ nclasses ];
		falseNegativesPerClass = new int[ nclasses ];
		int[] elementsPerRow = new int[ nclasses ];
	    for(int i=0 ; i < nclasses ; i++) 
    	{
			truePositivesPerClass[i] = elementsPerRow[i] = falsePositivesPerClass[i] = trueNegativesPerClass[i] = falseNegativesPerClass[i] = 0;
    	}
	    // Process per row
		for(int i=0 ; i < nclasses ; i++)  // vertical -> row = predicted
    	{
			for(int j=0;j<nclasses;j++)  // horizontal -> cols = actual
			{
				if( i == j ) truePositivesPerClass[i] += confusion[i][j];  
				        else falsePositivesPerClass[i] += confusion[i][j];
			}
    	}
		// process per column
		for(int j=0;j<nclasses;j++)
		{
			for(int i=0;i<nclasses;i++) 
			{
			   if( i != j) falseNegativesPerClass[ j ] += confusion[i][j];
			}
		}
		//
		totalEntries = 0;
		for(int i=0 ; i < nclasses ; i++)  // row = predicted
    	{
			for(int j=0;j<nclasses;j++)  // cols = actual
			{
				totalEntries += confusion[i][j];
			}
    	}
        //
		int truePos = 0;
		for(int i=0;i<nclasses;i++)
		{
			truePos += truePositivesPerClass[i];
			int ctot = truePositivesPerClass[i] + falsePositivesPerClass[i] + falseNegativesPerClass[i];
			trueNegativesPerClass[i] = totalEntries - ctot;
		}
		
	    this.Accuracy = (double)truePos / (double)totalEntries;
		for(int i=0;i<nclasses;i++)
	    {
		 int noe = truePositivesPerClass[i] + falsePositivesPerClass[i];	// TP + FP
	     precision[i] = (noe == 0) ? Double.NaN : (double) truePositivesPerClass[i] / (double)noe; 
		 noe = truePositivesPerClass[i] + falseNegativesPerClass[i];	// TP + FN
	     recall[i] = (noe == 0) ? Double.NaN : (double)truePositivesPerClass[i] / (double)noe;	
	     noe = falsePositivesPerClass[i] + trueNegativesPerClass[i];   //  FP + TN
	     fallout[i] = (noe == 0) ? Double.NaN : (double)falsePositivesPerClass[i] / (double)noe;	
	    }
		return true;
	}
	//------------------------------------------------------------
	public String showConfusionMatrix()
	//------------------------------------------------------------
	{
		this.calculate();  // just in case
		//
		int len = 15;
		for(int i=0 ; i < confusion.length ; i++) if( ClassNames[i].length() > len) len = ClassNames[i].length() + 2;
		String sform = "%-" + len + "s";
		String sFiller = "";
		for(int i=0;i<=len;i++) sFiller += " ";
	    //	    
		String sRet = ctEOL + sFiller;
		for(int i=0 ; i < confusion.length ; i++) sRet += String.format("%12s", ClassNames[i] );
		sRet += " ---> Actual" + ctEOL +  sFiller; 
	    for(int i=0 ; i < confusion.length ; i++) sRet += "----------- ";
	    for(int i=0 ; i < confusion.length ; i++)
    	{
    		String sl= String.format( sform , ClassNames[i]);
    		for(int j=0;j<confusion[i].length;j++) sl += "[" + String.format( "%10d" , confusion[i][j] ) + "]";
    		sRet += ctEOL + sl;
    	}
	    //    	
		sRet += ctEOL + ctEOL + sFiller + "  Precision          TPR         FPR    FMeasure";
	    sRet += " ---> Actual" + ctEOL +  sFiller; 
	    for(int i=0 ; i < 4 ; i++) sRet += " -----------";
		for(int i=0;i<ClassNames.length;i++)
		{
	   		String sl= String.format( sform , ClassNames[i]) + "[";
			sl += String.format("% 10.6f", getPrecisionPerClass(ClassNames[i])) + "][ " + 
			      String.format("% 10.6f", getRecallPerClass(ClassNames[i])) + "][" + 
			      String.format("% 10.6f", getFallOutPerClass(ClassNames[i])) + "][" + 
				  String.format("% 10.6f", getFValuePerClass(ClassNames[i])) + "]";
			sRet += ctEOL + sl;
		}
        //		
		return sRet;
	}
	public double getAccuracy()
	{
 		return this.Accuracy;
	}
	public int getTotalEntries()
	{
 		return this.totalEntries;
	}
	private int getClassIdx( String name )
	{
		for(int i=0;i<ClassNames.length;i++)
		{
			if( ClassNames[i].compareToIgnoreCase(name) == 0 ) return i;
		}
		return -1;
	}
	public double getPrecisionPerClass( String name)
	{
       int idx = getClassIdx( name );
       if( idx < 0 ) return Double.NaN;
       return this.precision[ idx ];
	}
	public double getRecallPerClass( String name)
	{
       int idx = getClassIdx( name );
       if( idx < 0 ) return Double.NaN;
       return this.recall[ idx ];
	}
	public double getTPRPerClass( String name)
	{
		return  getRecallPerClass(name);
    }
	public double getFallOutPerClass( String name)
	{
       int idx = getClassIdx( name );
       if( idx < 0 ) return -1;
       return this.fallout[ idx ];
    }
	public double getFPRPerClass( String name)
	{
		return getFallOutPerClass(name);
	}
	public double getFValuePerClass( String name)
	{
	   try {
       int idx = getClassIdx( name );
       if( idx < 0 ) return Double.NaN;
       double p = precision[idx];
       double r = recall[idx];
       if( (Double.isNaN(r)) || (Double.isNaN(p)) ) return Double.NaN;
       double n = p + r;
       if( n == 0 ) return Double.NaN;
       return  2 * p * r / n;
	   }
	   catch(Exception e ) { return Double.NaN; }
	}
	public double getMeanPrecision()
	{
	   try {
		    double sum = 0;
		    for(int i=0;i<ClassNames.length;i++)
			{
			   double p = precision[i];
		       if(  Double.isNaN(p) ) return Double.NaN;
		       sum +=  p;
			}
            return sum / ClassNames.length;     
	   }
	   catch(Exception e ) { return Double.NaN; }
	}
	public double getMeanFValue()
	{
	   try {
		    double sum = 0;
		    for(int i=0;i<ClassNames.length;i++)
			{
			   double p = precision[i];
		       double r = recall[i];
		       if( (Double.isNaN(r)) || (Double.isNaN(p)) ) return Double.NaN;
		       double n = p + r;
		       if( n == 0 ) return Double.NaN;
		       sum +=  2 * p * r / n;
			}
            return sum / ClassNames.length;     
	   }
	   catch(Exception e ) { return Double.NaN; }
	}

}
