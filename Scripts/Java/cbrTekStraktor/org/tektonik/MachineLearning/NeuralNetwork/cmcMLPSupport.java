package org.tektonik.MachineLearning.NeuralNetwork;

import java.util.ArrayList;

import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalpurpose.gpInterrupt;
import org.tektonik.tools.generalpurpose.gpPrintStream;
import org.tektonik.tools.generalpurpose.gpZipFileList;
import org.tektonik.tools.logger.logLiason;

public class cmcMLPSupport {
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	private gpInterrupt interrupt = null;
	
	String LastErrorMsg=null;
	
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
    public String getLastErrorMsg()
	//------------------------------------------------------------
    {
    	return LastErrorMsg;
    }
    
    //------------------------------------------------------------
    public cmcMLPSupport( cmcProcSettings is,logLiason ilog )
	//------------------------------------------------------------
    {
    	xMSet = is;
		logger=ilog;
 	    interrupt = new gpInterrupt( xMSet.getEpochInterruptFileName() );
    }

    //-----------------------------------------------------------------------
    public String getXMLModelName(String ARFFileName )
    //------------------------------------------------------------
    {
        if( ARFFileName == null ) return null;
        String StrippedName = xMSet.xU.getFileNameWithoutSuffix( ARFFileName );
        if( StrippedName == null ) return null;
        return StrippedName + "_mlp.xml";
    }
    
    //------------------------------------------------------------
    public boolean requestInterrupt()
    //------------------------------------------------------------
    {
    	return interrupt.requestInterrupt();
    }
    
    //------------------------------------------------------------
    public boolean resetInterrupt()
    //------------------------------------------------------------
    {
    	return interrupt.resetInterrupt();
    }
   
	//------------------------------------------------------------
	public boolean checkMLPRestartability( String ARFFFileName )
	//------------------------------------------------------------
	{
	   if( xMSet.xU.IsBestand( ARFFFileName ) == false ) {
		   do_log( 9 , "(checkMLPRestartability) Missing ARFF " + ARFFFileName );
		   return false;
	   }
	   if( xMSet.xU.IsBestand(xMSet.getEpochWeightDumpFileName()) == false ) {
		   do_log( 9 , "(checkMLPRestartability) Missing EpochWeight " + xMSet.getEpochWeightDumpFileName() );
		   return false;
	   }
	   if( xMSet.xU.IsBestand(xMSet.getEpochStatXMLFileName() ) == false ) {
		   do_log( 9 , "(checkMLPRestartability) Missing EpochStat " + xMSet.getEpochStatXMLFileName() );
		   return false;
	   }
	   if( xMSet.xU.IsBestand(this.getXMLModelName( ARFFFileName) ) == false ) {
		   do_log( 9 , "(checkMLPRestartability) Previous model is missing model [" + this.getXMLModelName( ARFFFileName ) + "]");
		   return false;
	   }
	   /*
	   if( xMSet.xU.IsBestand( xMSet.getARFFFileNameTrainingSet(ARFFFileName) ) == false ) {
		   do_log( 9 , "Previous TRAIN file is missing [" + xMSet.getARFFFileNameTrainingSet(ARFFFileName) + "]");
		   return false;
	   }
	   */
	   if( xMSet.xU.IsBestand( xMSet.getARFFMemoryMappedTrainFileName(ARFFFileName) ) == false ) {
		   do_log( 9 , "(checkMLPRestartability) Previous Memory Mapped Train File is missing [" + xMSet.getARFFMemoryMappedTrainFileName(ARFFFileName) + "]");
		   return false;
	   }
	   if( xMSet.xU.IsBestand( xMSet.getARFFMemoryMappedTestFileName(ARFFFileName) ) == false ) {
		   do_log( 9 , "(checkMLPRestartability) Previous Memory Mapped Test File is missing [" + xMSet.getARFFMemoryMappedTestFileName(ARFFFileName) + "]");
		   return false;
	   }
       return true;
	}
    
    
    //------------------------------------------------------------
  	public boolean RemoveEpochFile(String RemoveFileName)
  	//------------------------------------------------------------
  	{
  		 if( xMSet.xU.IsBestand(RemoveFileName) ) {
  			 boolean ib = xMSet.xU.VerwijderBestand(RemoveFileName);
  			 if( ib == false ) do_error( "Removing [" + RemoveFileName + "] did not succeed");
  			 return ib;
  		 }
  		 return true;
  	}
  	
  	//------------------------------------------------------------
  	public boolean fileCleanUp()
  	//------------------------------------------------------------
  	{
  		 if( RemoveEpochFile( xMSet.getEpochWeightDumpFileName() ) == false ) return false;
  		 if( RemoveEpochFile( xMSet.getEpochDebugFileName() ) == false ) return false;
  		 if( RemoveEpochFile( xMSet.getEpochStatXMLFileName() ) == false ) return false;
  		 if( RemoveEpochFile( xMSet.getEpochPictureFileName() ) == false ) return false;
  		 if( RemoveEpochFile( xMSet.getEpochROCImageFileName() ) == false ) return false;
  		 interrupt.resetInterrupt();
  		 return true;
  	}
  	
    //------------------------------------------------------------
  	public String getArchiveFileName(String ShortName)
    //------------------------------------------------------------
  	{
   	   return xMSet.getBayesResultDir() + xMSet.xU.ctSlash + ShortName + ".zip";
  	}
  	
	//------------------------------------------------------------
  	private String createManifestFile(cmcMLPDTO model)
	//------------------------------------------------------------
  	{
  		String LongFileName = xMSet.getSandBoxTempDir() + xMSet.xU.ctSlash + "manifest.xml";
  		xMSet.xU.VerwijderBestand( LongFileName );
  		if( xMSet.xU.IsBestand(LongFileName) ) { do_error("Cannot delete [" + LongFileName + "]"); return null; }
  		gpPrintStream cout = new gpPrintStream( LongFileName , xMSet.getCodePageString() );
  		cout.println (xMSet.getXMLEncodingHeaderLine());
		cout.println ("<!-- Application : " + xMSet.getApplicDesc() + " -->");
		cout.println ("<!-- File Created: " + (xMSet.xU.prntStandardDateTime(System.currentTimeMillis())).toUpperCase() + " -->" );
        //
		cout.println ( "<Manifest>" );	
  		cout.println( "<ARFFFileName>" + model.getLongARFFFileName() + "</ARFFFileName>" );
  		cout.println( "<EpochsPerformed>" + model.getEpochsPerformed() + "</EpochsPerformed>" );
  		long[][] rt = model.getRuntimes();
  		if( rt != null ) {
  			cout.println ( "<RunStatistics>" );	
  			for(int i=0;i<rt.length;i++)
  			{
  				if( rt[i][0] < 0L ) continue;
  				String sL = "";
  				sL += "<Start>" + xMSet.xU.prntStandardDateTime(rt[i][0]) + "</Start>";
  				sL += "<Stop>"  + xMSet.xU.prntStandardDateTime(rt[i][1]) + "</Stop>";
  				sL += "<StartTimeStampMSec>" + rt[i][0] + "</StartTimeStampMSec>";
  				sL += "<ElapsedMSec>"  + (rt[i][1] -rt[i][0]) + "</ElapsedMSec>";
  				sL += "<Rows>"  + rt[i][2] + "</Rows>";
  			    cout.println( "<Run>" + sL.trim() + "</Run>" );	
  			}
  			cout.println ( "</RunStatistics>" );	
  		}
  		cout.println ( "</Manifest>" );	
  	    cout.close();
  	    //
  		if( xMSet.xU.IsBestand(LongFileName) == false ) { do_error("Cannot create [" + LongFileName + "]"); return null; }
  		return LongFileName;
  	}
  	
    //------------------------------------------------------------
  	public String extractARFFFileNameFromManifest(String LongFileName)
    //------------------------------------------------------------
  	{
  	    if( xMSet.xU.IsBestand( LongFileName ) == false ) {
  	    	do_error("Manifest file cannot be found [" + LongFileName + "]");
  	    	return null;
  	    }
  		String list = xMSet.xU.ReadContentFromFile( LongFileName , 20 , xMSet.getCodePageString() );
  		if( list == null )  {
  	    	do_error("Content of manifest file could not be read [" + LongFileName + "]");
  	    	return null;
  	    }
  		try {
  		  int idx = list.indexOf("<ARFFFileName>");
  		  int pdx = list.indexOf("</ARFFFileName>");
  		  if( (idx < 0) || (pdx <= idx) ) return null;
  		  return list.substring( idx + "<ARFFFileName>".length() , pdx );
  		}
  		catch(Exception e ) {
  		   do_error("substringing [" + LongFileName + "]");
  		   return null;	
  		}
  	}
  	
	//------------------------------------------------------------
  	public boolean createArchive( cmcMLPDTO model , String ArchiveLabel )
	//------------------------------------------------------------
  	{
  	   String LongZipFileName = this.getArchiveFileName(ArchiveLabel);
  	   //
  	   ArrayList<String> list = new ArrayList<String>();
       String ARFFFileName = model.getLongARFFFileName();
       /* DO NOT ADD 
 	   if( xMSet.xU.IsBestand( ARFFFileName ) == false ) {
		   do_log( 9 , "Missing " + ARFFFileName );
		   return false;
	   }
 	   else list.add(ARFFFileName);    
 	   */
 	   //
	   if( xMSet.xU.IsBestand( xMSet.getARFFFileNameTrainingSet(ARFFFileName) ) == false ) {    // Train data
		   do_log( 9 , "TRAIN file is missing [" + xMSet.getARFFFileNameTrainingSet(ARFFFileName) + "]");
		   return false;
	   }
	   else list.add(xMSet.getARFFFileNameTrainingSet(ARFFFileName));
	   //
	   if( xMSet.xU.IsBestand( xMSet.getARFFFileNameTestSet(ARFFFileName) ) == false ) {
		   do_log( 9 , "Test file is missing [" + xMSet.getARFFFileNameTestSet(ARFFFileName) + "]");
		   return false;
	   }
	   else list.add(xMSet.getARFFFileNameTestSet(ARFFFileName));
 	   
 	   //
	   if( xMSet.xU.IsBestand(xMSet.getEpochWeightDumpFileName()) == false ) {
		   do_log( 9 , "Missing " + xMSet.getEpochWeightDumpFileName() );
		   return false;
	   }
	   else list.add(xMSet.getEpochWeightDumpFileName());
	   //
	   if( xMSet.xU.IsBestand(xMSet.getEpochStatXMLFileName() ) == false ) {
		   do_log( 9 , "Missing " + xMSet.getEpochStatXMLFileName() );
		   return false;
	   }
	   else list.add(xMSet.getEpochStatXMLFileName());
	   //
	   if( xMSet.xU.IsBestand(this.getXMLModelName( ARFFFileName) ) == false ) {
		   do_log( 9 , "Model is missing [" + this.getXMLModelName( ARFFFileName ) + "]");
		   return false;
	   }
	   else list.add( getXMLModelName( ARFFFileName) );
	   // create manifest file
	   String manif = createManifestFile( model );
	   if( manif == null ) {  
	   	 do_log( 9 , "could not create manifest file");
	     return false;
       }
       else list.add( manif );
	  
	   //
//do_error( LongZipFileName ); for(int i=0;i<list.size();i++) do_error( "->" + i + " " + list.get(i) );	
	   gpZipFileList zipper = new gpZipFileList( LongZipFileName , list , logger , xMSet.getSandBoxDir() );  // FULLPATH
	   boolean ib = zipper.completedOK();
	   zipper = null;
	   do_log( 1 , "Archive [" + LongZipFileName + "] has been created");
	   // 
	   return ib;
  	}
  	
    //------------------------------------------------------------
  	public boolean removeClutter(String CurrentModel , String CurrentDir)
    //------------------------------------------------------------
  	{
  	   if( CurrentModel == null ) return false;
  	   ArrayList<String> list = xMSet.xU.GetFilesInDir( CurrentDir , null );
       if( list == null ) { do_error("Cannot determine files in cache"); return false; }
       ArrayList<String> removeList = new ArrayList<String>();
       for(int i=0;i<list.size();i++)
       {
    	   String FName =list.get(i);
    	   if( FName == null ) continue;
    	   if( FName.trim().toUpperCase().endsWith(".ARFF") == false ) continue;
    	   if( FName.trim().toUpperCase().endsWith("-TESTSET.ARFF")) {
    	       if (FName.compareToIgnoreCase( CurrentModel.trim() + "-testset.arff" ) == 0 ) continue;   
    	       removeList.add( FName );
    	   }
           if( FName.trim().toUpperCase().endsWith("-TRAINSET.ARFF")) {
        	   if (FName.compareToIgnoreCase( CurrentModel.trim() + "-trainset.arff" ) == 0 ) continue;   
    	       removeList.add( FName );
    	   }
           if( FName.trim().toUpperCase().endsWith("-TEST.ARFF")) {
    	       if (FName.compareToIgnoreCase( CurrentModel.trim() + "-test.arff" ) == 0 ) continue;   
    	       removeList.add( FName );
    	   }
           if( FName.trim().toUpperCase().endsWith("-TRAIN.ARFF")) {
        	   if (FName.compareToIgnoreCase( CurrentModel.trim() + "-train.arff" ) == 0 ) continue;   
    	       removeList.add( FName );
    	   }
       }
       for(int i=0;i<removeList.size();i++)
       {
    	   if( i == 0 ) do_log( 9 , "Decluttering [" + CurrentModel + "]" );
    	   String FName = CurrentDir + xMSet.xU.ctSlash + removeList.get(i);
    	   do_log( 9 , "Removing [" + FName + "]");
    	   xMSet.xU.VerwijderBestand( FName );
       }
       return true;
  	}
  	
}
