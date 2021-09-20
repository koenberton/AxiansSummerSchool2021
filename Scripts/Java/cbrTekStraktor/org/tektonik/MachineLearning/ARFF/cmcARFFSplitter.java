package org.tektonik.MachineLearning.ARFF;


import org.tektonik.MachineLearning.dao.cmcARFFDAOLight;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalStatPurpose.gpRandom;
import org.tektonik.tools.logger.logLiason;


public class cmcARFFSplitter {
	
	private int MAX_CYCLES = 5;   // mx. number of atetmpts made when creating test data with matching distribution
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	
	private gpRandom  random = null;
	private cmcARFFDAOLight srcdao = null;
	
	private String LastErrorMsg=null;
	private String TempTrainFileName=null;
	private String TempTestFileName=null;
	private int NbrOfDataRows = -1;
		
	//------------------------------------------------------------
    private void do_log(int logLevel , String sIn)
	//------------------------------------------------------------
    {
       if( logger != null ) logger.write( this.getClass().getName() , logLevel , sIn);
       else 
       if (logLevel == 0 ) System.err.println(sIn);
       else System.out.println(sIn);
    }
	//------------------------------------------------------------
    private void do_error(String sIn)
	//------------------------------------------------------------
    {
    	LastErrorMsg=sIn;
    	do_log(0,sIn);
    }

    //------------------------------------------------------------
    public cmcARFFSplitter(cmcProcSettings is,logLiason ilog)
    //------------------------------------------------------------
    {
    	xMSet = is;
		logger=ilog;
		random = new gpRandom();
    }

    public String getLastErrorMsg()
    {
    	return LastErrorMsg;
    }

    //------------------------------------------------------------
    private boolean copyFile( String SourceFile , String TargetFile )
    {
    	try {
    		if( srcdao.RemoveFile( TargetFile ) == false ) return false;
    		xMSet.xU.copyFile( SourceFile , TargetFile );
    		//if( RemoveFile( SourceFile ) == false ) return false;
    		return true;
    	}
    	catch(Exception e ) {
    		do_error ("Cannot move " + SourceFile + " " + TargetFile );
    		return false;
    	}
    }
    
    //------------------------------------------------------------
    public boolean splitARFFInTrainingAndTestNew( String SourceFileName , String TargetFileNameTrainingSet , String TargetFileNameTestSet , ARFFEnums.TEST_DATA_CREATON_TYPE tipe , int percentage)
    //------------------------------------------------------------
    {
        if( xMSet.xU.IsBestand(SourceFileName) == false ) { do_error("Source ARFF is missing [" + SourceFileName + "]"); return false; }
        //
    	srcdao = new cmcARFFDAOLight( xMSet , logger );
    	ARFFLightDTO xa = null;
        if( tipe == ARFFEnums.TEST_DATA_CREATON_TYPE.MATCHING_DISTRIBUTION ) { 	  // gather statistical info on the ARFF
          if( srcdao.performFirstPass( SourceFileName , " splitARFFInTrainingAndTestNew" ) == false ) { do_error("Could not parse 1 [" + SourceFileName + "]"); return false; }
          xa = srcdao.performSecondPass(SourceFileName );
          if( xa == null ) { do_error("Could not parse 2 [" + SourceFileName + "]"); return false; }
          xa = srcdao.performThirdPass();
          if( xa == null ) { do_error("Could not parse 3 [" + SourceFileName + "]"); return false; }
        }
        else if ( tipe == ARFFEnums.TEST_DATA_CREATON_TYPE.ONE_PASS_RANDOM ) {  // just one pass to get the number of rows 
           xa = null;
           if( srcdao.performFirstPass( SourceFileName , " splitARFFInTrainingAndTestNew" ) == false ) { do_error("Could not parse 1 [" + SourceFileName + "]"); return false; }
        }
        else {
          do_error("Unsupported type test data generation [" + tipe + "]"); return false;	
        }
        //
        NbrOfDataRows = srcdao.getNumberOfDataRows();
        if( NbrOfDataRows <= 0 ) { do_error("Could not get number of data rows"); return false; }
      
        // temp
        TempTrainFileName = xMSet.getSandBoxTempDir() + xMSet.xU.ctSlash + "TempTrainARFF.txt";
    	TempTestFileName = xMSet.getSandBoxTempDir() + xMSet.xU.ctSlash + "TempTestARFF.txt";
        
    	//  TODO : there might be other types of data creation, i.e. always the same rows
    	int ncycles =  (tipe == ARFFEnums.TEST_DATA_CREATON_TYPE.ONE_PASS_RANDOM) ? 1 : MAX_CYCLES;
        boolean ib = performEqualDistributionSplit( xa , ncycles , SourceFileName , tipe , percentage );
        if( ib == false ) return false;
        
        // Copy the temp files
        if( copyFile( TempTrainFileName , TargetFileNameTrainingSet) == false ) return false;
        if( copyFile( TempTestFileName , TargetFileNameTestSet) == false ) return false;
        //
        xa = null;
        srcdao=null;
        //	
        do_log( 1 , "Split [Tst=" + percentage + "%] of [" + NbrOfDataRows + " rows] for [" + tipe + "] -> [" + TargetFileNameTestSet + "]");
       	return true;
    } 	
    
    //------------------------------------------------------------
    private boolean performEqualDistributionSplit(ARFFLightDTO xa , int ncycles , String SourceFileName , ARFFEnums.TEST_DATA_CREATON_TYPE tipe , int percentage)
    {
    	int[] testrows = null;
    	cmcARFFDAOLight dao = new cmcARFFDAOLight( xMSet , logger );
        if( srcdao.RemoveFile( TempTestFileName ) == false ) return false;
    	if( srcdao.RemoveFile( TempTrainFileName ) == false ) return false;
        String comment = "";
        //
        int[][] distributions = new int[ ncycles ][];
        double[] distances = new double[ ncycles ];
        int attempts =0 ;
        for( int cyclus=0 ; cyclus<ncycles ; cyclus++)
    	{
        	distances[ cyclus ] = 100000;
         	if( srcdao.RemoveFile( TempTestFileName ) == false ) return false;
    		
         	// other splitters are possible
            testrows = getTestRowIndicesViaSimpleRandomizing( TempTrainFileName , TempTestFileName , percentage );
            if( testrows == null ) return false;
            //
            int NbrOfTestRows = testrows.length;
	        int NbrOfTrainRows = NbrOfDataRows - NbrOfTestRows;
            comment = "[#Actuals=" + NbrOfDataRows + "] [Test=" + percentage + "%] [#Train=" + NbrOfTrainRows +"] [#Test=" + NbrOfTestRows + "]";
      	    //
            /*
            {
              String ss = "";
              for(int i=0;i<testrows.length;i++) ss += testrows[i] + ",";
              do_log( 1 , "Testrows -> {" + ss + "}" );
            }
            */
            //
            if( srcdao.performARFFExtract( SourceFileName , NbrOfDataRows , TempTestFileName , testrows , ARFFEnums.DATA_FILE_TYPE.TESTING , comment) == false ) {
        		do_error("Could  not create TEST data");
        		return false;
        	}
            if( tipe == ARFFEnums.TEST_DATA_CREATON_TYPE.MATCHING_DISTRIBUTION ) { 
              // read test data and extract its statistics
              if( dao.performFirstPass( TempTestFileName , "performEqualDistributionSplit" ) == false ) { do_error("Could not parse 1 [" + SourceFileName + "]"); return false; }
              ARFFLightDTO tst = dao.performSecondPass( TempTestFileName );
              if( tst == null ) { do_error("Could not parse 2 [" + TempTestFileName  + "]"); return false; }
              tst = dao.performThirdPass();
              if( tst == null ) { do_error("Could not parse 3 [" + TempTestFileName  + "]"); return false; }
        	
              //  Distance
              double dist = calculateDistance( xa , tst );
              if( Double.isNaN( dist ) ) { // Het is mogelijk dat de test data alleen maar ? bevat en dus man/stdev NAN
            	  do_error( "Could not calculate distance [" + TempTestFileName  + "] - this attempt will be discarded");
            	  cyclus--;
            	  if( attempts++ > (ncycles*2) ) {
            		 do_error("Could not create a test set comprising valid data - increase percentage or construct file manually");
            		 return false;
            	  }
            	  continue;
              }
              distances[ cyclus ] = dist;
              distributions[ cyclus ] = new int[ NbrOfTestRows ];
              for(int i=0;i<NbrOfTestRows;i++) distributions[ cyclus ][ i ] = testrows[i];
              //   
              tst=null;
            }
            else if( tipe == ARFFEnums.TEST_DATA_CREATON_TYPE.ONE_PASS_RANDOM ) break; 
    	}
    	// end by re-extracting the test data (only if the last file created does not have the required distribution)
        if( tipe == ARFFEnums.TEST_DATA_CREATON_TYPE.MATCHING_DISTRIBUTION ) { 
          double min = Double.NaN;
          int idx = -1;
       	  for(int i=0;i<distances.length;i++)
       	  {
            if(i==0) { min = distances[i]; idx=0; }
            if( distances[i] < min ) {
            	min = distances[i];
            	idx = i;
            }
       do_log( 1 , "Distances -->" + distances[i] + " " + idx );
       	  }
       	  if( idx < 0 ) { do_error("System error III"); return false;  }
       	  testrows = distributions[ idx ];
       	  /* opgepast NIO buffer explodes
       	  {
            String ss = "";
            for(int i=0;i<testrows.length;i++) ss += testrows[i] + ",";
            do_log( 1 , "Testrows -> {" + ss + "}" );
          }
          */
       	  //
       	  if( idx != (ncycles-1) ) {  // indien IDX is de laatste dan staat de file er nog en mag je skippen
       	   if( srcdao.performARFFExtract( SourceFileName , NbrOfDataRows , TempTestFileName , testrows , ARFFEnums.DATA_FILE_TYPE.TESTING , comment) == false ) {
    		do_error("Could  not create TEST data");
    		return false;
    	   }
       	  }
        }
        
    	// end by extracting train data
      	if( srcdao.RemoveFile( TempTrainFileName ) == false ) return false;
        if( srcdao.performARFFExtract( SourceFileName , NbrOfDataRows , TempTrainFileName , testrows , ARFFEnums.DATA_FILE_TYPE.TRAINING , comment ) == false ) {
    		do_error("Could  not create TRAINING data");
    		return false;
    	}
        
        //
        dao=null;
        testrows=null;
    	return true;
    }

    //------------------------------------------------------------
    private double calculateDistance( ARFFLightDTO one , ARFFLightDTO two )
    {
    	try {
    	 if((one==null)||(two==null)) return Double.NaN;
    	 if( (one.getAttributeList()==null)||(one.getAttributeList()==null)) return Double.NaN;
    	 if( one.getAttributeList().length != two.getAttributeList().length ) return Double.NaN;
    	 double meandist=0;
    	 double stdevdist=0;
    	 for(int i=0;i<one.getAttributeList().length;i++)
    	 {
    		double dd =  one.getAttributeList()[i].getMean() - two.getAttributeList()[i].getMean();
    		meandist +=  dd*dd;
    		dd =  one.getAttributeList()[i].getStDev() - two.getAttributeList()[i].getStDev();
    		stdevdist +=  dd*dd;
 //do_log(1,"M1=" + one.getAttributeList()[i].getMean() + " M2=" + two.getAttributeList()[i].getMean() + " S1=" + one.getAttributeList()[i].getStDev() + " S2=" + two.getAttributeList()[i].getStDev());
    	 }
    	 return Math.sqrt( meandist ) + Math.sqrt( stdevdist );
    	}
    	catch(Exception e ) { return Double.NaN; }
    }
    
    //------------------------------------------------------------
    private int[] getTestRowIndicesViaSimpleRandomizing( String TempTrainFileName , String TempTestFileName , int percentage )
    {
        int nsamples = (int)(((double)NbrOfDataRows * (double)percentage ) / (double)100);
        int[] testset = random.getSimpleRandomList( NbrOfDataRows , nsamples );
        if( testset == null ) { do_error("Cannot get simple random set"); return null; }
    	return testset;
    }

}
