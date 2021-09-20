package org.tektonik.MachineLearning.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.tektonik.MachineLearning.Evaluation.cmcModelEvaluation;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTO;
import org.tektonik.MachineLearning.NeuralNetwork.monitor.EpochMonitorDTO;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalpurpose.gpPrintStream;
import org.tektonik.tools.logger.logLiason;

public class cmcEpochMonitorDAO {
	
	private boolean VERBOSE_MATRIX = false;
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	private String LastErrorMsg=null;
	private cmcModelEvaluation eval = null;
	private String[] nominals=null;
	private cmcMLPDTO embeddedmlpdto = null;
	private EpochAdditional epadd =null;
	
	class EpochAdditional
	{
		int ReportedNumberOfEntries = -1;
		String[] ExtendedClassNameList = null;
		double PercentageCompleted=Double.NaN;
	}
	
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

    public String getLasterrorMsg()
    {
    	return LastErrorMsg;
    }
    
    //------------------------------------------------------------
    public cmcEpochMonitorDAO(cmcProcSettings is,logLiason ilog)
    //------------------------------------------------------------
    {
    	xMSet = is;
		logger=ilog;
    }
    
    //------------------------------------------------------------
    public int getreportedNbrOfEntries()
    //------------------------------------------------------------
    {
        return epadd.ReportedNumberOfEntries;
    }
    
    //------------------------------------------------------------
    private String dumpConfusionMatrix(int[][] confusion)
    //------------------------------------------------------------
    {
    	String ss = "(";
    	for(int i=0;i<confusion.length;i++)
    	{
    		for(int j=0;j<confusion[i].length;j++)
    		{
    		  if( (j+i) != 0) ss += ";";
    	  	  ss += confusion[i][j];
    		}
    	}
    	ss += ")";
    	return ss;
    }
    
    //------------------------------------------------------------
    private String dumpSpeed(double[] speed)
    {
    	if( speed == null )  return "()";
    	String ss = "(";
    	for(int i=0;i<speed.length;i++)
    	{
    		if(i != 0) ss += ";";
    		ss += speed[i];
    	}
    	ss += ")";
    	return ss;
    }
    
    //------------------------------------------------------------
    private String confusionToString(int[][] confusion)
    //------------------------------------------------------------
    {
    	if( VERBOSE_MATRIX == false ) return "";
    	if( eval == null ) return "";
    	String ss = "";
    	eval.injectMatrix( confusion );
    	ss += "\n" + eval.showConfusionMatrix();
       	String sout = "";
    	StringTokenizer sx = new StringTokenizer(ss,"\n");
        while(sx.hasMoreTokens()) {
        	String sl = sx.nextToken();
        	if( sl.trim().length() < 1) continue;
		    sout +=  "\n-- " + sl;
        }    	
    	return sout;
    }
    
    //------------------------------------------------------------
    private boolean writeMLPDTO( cmcMLPDTO mlpdto , gpPrintStream aps)
    {
    	 cmcMLPDTODAO dao = new cmcMLPDTODAO( xMSet , logger );
    	 boolean ib = dao.writeMLPDTOSection( mlpdto, aps);
    	 dao=null;
    	 return ib;
    }
    
    //------------------------------------------------------------
    private boolean doublecheck( double accur , int[][]confusion , long uid)
    {
        if( confusion == null )	return false;
        int hits=0;
        int total=0;
        for(int i=0;i<confusion.length;i++)
        {
        	for(int j=0;j<confusion[i].length;j++)
        	{
        		total += confusion[i][j];
        		if( i == j ) hits += confusion[i][j];
        	}
        }
        double calc = (double)hits / (double)total;
        if( accur != calc) {
        	do_error( "epoch=" + uid + " acc=" + accur + " calc=" + calc + " hits=" + hits + " tot=" + total );
        	return false;
        }
        return true;
    }
    
    //------------------------------------------------------------
    public boolean writeEpochMonitorStats(  String XMLFileName , cmcMLPDTO mlpdto , EpochMonitorDTO[] mondata , int dataptr , String[] extendedClassNameList)
    {
    	nominals = extendedClassNameList;
    	if( nominals == null ) { do_error("NULL nominals"); return false; }
    	eval = new cmcModelEvaluation( nominals );
    	
    	try  {
    		long avgelap = 10000L;
    		double estimatedProgress = 0;
    		long elapsedt = 0L;
    		int valid=0;
        	if( dataptr > 0) {
        		 for(int i=0;i<dataptr;i++) {
        			if( mondata[i] == null ) continue;
        		    elapsedt += mondata[i].getTimeElapsed();
        		    valid++;
        		 }
    			 avgelap = elapsedt / (long)valid;
        		 if( avgelap > 0 ) {
        		     long estimatedTotalTime = mlpdto.getSizeOfMiniBatch() * avgelap;  // nanoseconds
         		     estimatedProgress = (double)elapsedt / (double)estimatedTotalTime;
         		     if( estimatedProgress > 1) estimatedProgress = 1;
        		 }
        	}	
    		    		
    	 gpPrintStream aps = new gpPrintStream( XMLFileName , xMSet.getCodePageString());
		 aps.println(xMSet.getXMLEncodingHeaderLine());
		 aps.println("<!-- Application : " + xMSet.getApplicDesc() + " -->");
		 aps.println("<!-- Start : " + 	(xMSet.xU.prntStandardDateTime(System.currentTimeMillis())).toUpperCase() + " -->" );
		 aps.println("<EpochStats>");
	     aps.println("<EpochParameters>");
		 aps.println("<NbrOfEntries>" + dataptr  + "</NbrOfEntries>" );
		 aps.println("<AllocatedNbrOfEntries>" + mondata.length  + "</AllocatedNbrOfEntries>" );
		 String s1 = "";
		 for(int i=0;i<extendedClassNameList.length;i++) s1 += "[" + extendedClassNameList[i].trim().toUpperCase() + "]";
		 aps.println("<ExtendedClassNameList>" + s1  + "</ExtendedClassNameList>" );
		 aps.println("<Timing>");
		 aps.println("<ElapsedTimeNanoSec>" + elapsedt + "</ElapsedTimeNanoSec>");
		 aps.println("<AverageEpochTimeNanoSec>" + avgelap + "</AverageEpochTimeNanoSec>");
		 aps.println("<ProgressPercentage>" + estimatedProgress + "</ProgressPercentage>");
		 aps.println("</Timing>");
		 //
		 aps.println("</EpochParameters>");
		 //
		 if( writeMLPDTO( mlpdto , aps ) == false ) return false;
		 // 
		 aps.println("<!-- 1=UID 2=ElapsedTime - 3=Cost - 4=Accuracy (ConfusionMatrix) (learningSpeed) 7=TestedAccuracy -->");
		 aps.println("<![CDATA[");
		 String ss = "";
		 for(int i=0;i<dataptr;i++)
		 {
			ss = "" + mondata[i].getUID() + "," + mondata[i].getTimeElapsed() + "," + mondata[i].getEpochCost() + "," + mondata[i].getEpochAccuracy();
		 	int[][] confusion = mondata[i].getConfusionMatrix();
			if( confusion == null ) { do_error("Cannot fetch confusion matrix"); return false; }
			ss += "," + dumpConfusionMatrix( confusion )  + "," + dumpSpeed( mondata[i].getLearningSpeed());
			ss += "," + mondata[i].getTestAccuracy(); 
			ss += confusionToString(confusion);
			aps.println( ss  );
			// check
			if( doublecheck(  mondata[i].getEpochAccuracy() , confusion , mondata[i].getUID() ) == false ) {
				do_error("System error : confusion matrix and accuracy do not match");
				return false;
			}
		 }
		 aps.println("]]>");
		 //
		 aps.println("</EpochStats>");
		 aps.close();
		 aps=null;
		 return true;
    	}
    	catch(Exception e ) {
    		do_error( "Could not write [" + XMLFileName + "]" + e.getMessage());
    		e.printStackTrace();
    		return false;
    	}
    }
    
    //------------------------------------------------------------
    private cmcMLPDTO readMLPDTO(BufferedReader reader , String FName)
    {
    	try {
    		 cmcMLPDTODAO dao = new cmcMLPDTODAO( xMSet , logger );
        	 cmcMLPDTO mlpdto = dao.readMLPDTOSection(reader,FName);
        	 dao= null;
        	 return mlpdto;
    	}
    	catch(Exception e) {
    		do_error("read MLPDTO" + xMSet.xU.LogStackTrace(e));
    		return null;
    	}
    }

    //------------------------------------------------------------
    public cmcMLPDTO getEmbeddedMLPDTO()
    {
    	return this.embeddedmlpdto;
    }
    
    //------------------------------------------------------------
    private double[] extractSpeeds(String sin, int nbr)
    {
    	try {
    		 if( sin == null ) { do_error("NULL speed"); return null; }
        	 String sline = sin.trim().toUpperCase();
        	 if( sline.startsWith("(") == false ) { do_error("Speed does not start with (" + sin); return null; }
        	 if( sline.endsWith(")") == false ) { do_error("Speed does not end with )" + sin); return null; }
        	 double[] ret = new double[ nbr ];
        	 int idx =-1;
        	 StringTokenizer sx = new StringTokenizer(sline,"();");
        	 while(sx.hasMoreTokens()) {
    		  String snum = sx.nextToken();
    		  if( snum == null ) continue;
    		  idx++;
    		  ret[ idx ] = xMSet.xU.NaarDoubleNAN(snum);
    		  if( Double.isNaN( ret[idx] ) ) {
    			  { do_error("Speeds contans NAN " + sin); return null; }  
    		  }
        	 }
        	 if( idx != (nbr-1) ) {
        		 { do_error("Number Speeds does not match [DAO=" + nbr + "] [List" + idx + "]"); return null; } 
        	 }
        	 return ret;
    	}
    	catch(Exception e ) {
    		do_error( "Error parsing speed information " + xMSet.xU.LogStackTrace(e));
    		return null;
    	}
    }
    
    //------------------------------------------------------------
    private int[][] extractConfusionMatrix( String sin)
    //------------------------------------------------------------
    {
    	try {
    	 if( sin == null ) { do_error("NULL confusion"); return null; }
    	 String sline = sin.trim().toUpperCase();
    	 if( sline.startsWith("(") == false ) { do_error("Confusion does not start with ("); return null; }
    	 if( sline.endsWith(")") == false ) { do_error("Confusion does not end with )"); return null; }
    	 int nsize = epadd.ExtendedClassNameList.length;
    	 int[][] conf = new int[ nsize ][ nsize ];
    	 for(int i=0;i<conf.length;i++)
    	 {
    		for(int j=0;j<conf[i].length;j++) conf[i][j] = -99;
    	 }
    	 int idx =-1;
    	 StringTokenizer sx = new StringTokenizer(sline,"();");
    	 while(sx.hasMoreTokens()) {
		  String snum = sx.nextToken();
		  if( snum == null ) continue;
	      snum = snum.trim().toUpperCase();
	      if( snum.length() <= 0) continue;
	      idx++;
	      if( idx >= (nsize*nsize) ) { do_error("Too many entries"); return null; }
	      int zz = xMSet.xU.NaarInt(snum);
	      if( zz < 0 )  { do_error("negative entry"); return null; }
	      conf[ idx / nsize ][ idx % nsize ] = zz;
		 } 	
    	 for(int i=0;i<conf.length;i++)
    	 {
    		for(int j=0;j<conf[i].length;j++)
    		{
    			if( conf[i][j] < 0) { do_error("Entry not parsed " + i + " " + j); return null; }
    		}
    	 }
    	 return conf;
    	}
    	catch(Exception e ) {
    		do_error( "Error parsing confusion details " + xMSet.xU.LogStackTrace(e));
    		return null;
    	}
    }
    
    //------------------------------------------------------------
    public double getPercentageCompleted()
    //------------------------------------------------------------
    {
    	if( epadd == null ) return Double.NaN;
    	return epadd.PercentageCompleted;
    }
    //------------------------------------------------------------
    public String[] getExtendedClassNameList()
    //------------------------------------------------------------
    {
    	if( epadd == null ) return null;
    	return epadd.ExtendedClassNameList;
    }
    
    //------------------------------------------------------------
    public EpochMonitorDTO[] readEpochMonitorStats( String XMLFileName )
    //------------------------------------------------------------
    {
    	 EpochMonitorDTO[] mondata = null;
    	 int NbrOfEntries = -1;
    	 boolean indata=false;
    	 int dataptr=-1;
    	 BufferedReader reader=null;
    	 embeddedmlpdto=null;
    	 epadd = new EpochAdditional();
    	 // 
     	 try {
     	
 			File inFile  = new File(XMLFileName);  // File to read from.
 	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), xMSet.getCodePageString()));
 	       	//
 	       	String sLijn=null;
 	       	while ((sLijn=reader.readLine()) != null) {
 	       	  if( sLijn.trim().startsWith("--") ) continue;
 	       	  //
 	       	  if( sLijn.indexOf("<NbrOfEntries>") >= 0 ) {
 				NbrOfEntries = xMSet.xU.NaarInt( xMSet.xU.extractXMLValue(sLijn,"NbrOfEntries"));
 			    epadd.ReportedNumberOfEntries = NbrOfEntries;
 	//do_error("READ " + this.getreportedNbrOfEntries() + " "+ sLijn + " " + NbrOfEntries);
 				continue;	 
 			  }
 	       	  //
 	       	  if( sLijn.indexOf("<ExtendedClassNameList>") >= 0 ) {
 				String ss = xMSet.xU.extractXMLValue(sLijn,"ExtendedClassNameList");
 			    if( ss == null ) { do_error("Reading extended class name list 1 " + sLijn); return null; }
 			    String[] sl = xMSet.xU.getSquaredItems( ss );
 			    if( sl == null ) { do_error("Reading extended class name list 2 " + sLijn); return null; }
 			    if( sl.length < 1 ) { do_error("Reading extended class name list 3 " + sLijn); return null; }
			    epadd.ExtendedClassNameList = sl;
 				continue;	 
 			  }
 	       	 //
 	       	  if( sLijn.indexOf("<ProgressPercentage>") >= 0 ) {
 				double dd = xMSet.xU.NaarDoubleNAN( xMSet.xU.extractXMLValue(sLijn,"ProgressPercentage"));
 			    if( Double.isNaN(dd)) { do_error("Percentage completed" + sLijn); return null; }
 			    epadd.PercentageCompleted = dd;
 				continue;	 
 			  }
 	       	  // 
 	       	  if( sLijn.indexOf("<MLPDTO>") >= 0 ) {
 	       		   embeddedmlpdto = readMLPDTO( reader , XMLFileName);
 	       		   if( embeddedmlpdto == null ) { do_error("Parsing MLPDTO XML"); return null; }
 	       	  }
 	       	  if( sLijn.indexOf("<![CDATA[") >= 0 ) {
 				if( NbrOfEntries > 0) {
 					mondata = new EpochMonitorDTO[ NbrOfEntries ];
 					for(int i=0;i<mondata.length;i++) mondata[i]=null;
 				}
 				dataptr=-1;
 				indata=true;
 				continue;	 
 			  }
 	       	  if( sLijn.indexOf("]]>") >= 0 ) {
 	       		  if( epadd == null )  { do_error("Extended attributes not read"); return null; }
 	       	      if( epadd.ExtendedClassNameList == null ) { do_error("extended class list is unknown"); return null; }
 	       		  indata = false; 
 	       		  continue; }
 	          if( indata ) {
 	        	  sLijn = sLijn.trim().toUpperCase();
 	        	  if( sLijn.length() == 0) continue;
 	        	  dataptr++;
 	        	  if( dataptr > NbrOfEntries ) break; // error
 	        	  StringTokenizer sx = new StringTokenizer(sLijn.trim(),",");
 	        	  int k=-1;
 	        	  EpochMonitorDTO mx = null;
 	 		      while(sx.hasMoreTokens()) {
 	 		    	k++;
 	 		    	String snum = sx.nextToken().trim().toUpperCase();
 	 		    	if( k==0 ) {
 	 		    		long ll = xMSet.xU.NaarLong(snum);
 	 		    		if( ll < 0L) { do_error("Error parsing uid " + sLijn ); return null; }
 	 		    		mx = new EpochMonitorDTO(dataptr);
 	 		    		mx.setTimeElapsed(ll);
 	 		    	}
 	 		    	else if( k==1 ) {
 	 		    		long tt = xMSet.xU.NaarLong(snum);
 	 		    		if( tt < 0L) { do_error("Error parsing time " + sLijn ); return null; }
 	 		    		mx.setTimeElapsed(tt);
 	 		    	}
 	 		    	else if( k == 2) {
 	 		    		double dd = xMSet.xU.NaarDoubleNAN(snum);
 	 		    		if( Double.isNaN(dd) ) { do_error("Error parsing cost " + sLijn ); return null; }
 	 		    		mx.setEpochCost(dd);
 	 		    	}
 	 		    	else if( k == 3) {
 	 		    		double dd = xMSet.xU.NaarDoubleNAN(snum);
 	 		    		if( Double.isNaN(dd) ) { do_error("Error parsing accuracy " + sLijn ); return null; }
 	 		    		mx.setEpochAccuracy(dd);
 	 		    	}
 	 		    	else if( k == 4 ) { // confusion matrix details
 	 		    		int[][] cf = extractConfusionMatrix( snum );
 	 		    		if( cf == null ) { do_error("Error parsing confusion matrix [" + sLijn + "]"); return null; }
 	 		    		mx.setConfusionMatrix( cf );
 	 		    	}
 	 		    	else if( k == 5 ) { // Speed info
 	 		    		double[] ret = extractSpeeds( snum , embeddedmlpdto.getNbrOfHiddenLayers() + 1);
 	 		    	    if( ret == null ) {
 	 		    	    	do_error("error parsing speed " + sLijn); return null;
 	 		    	    }
 	 		    	    mx.setLearningSpeed( ret );
 	 		    	}
 	 		    	else if( k == 6 ) { // Test accuracy
 	 		    	    double dd = xMSet.xU.NaarDoubleNAN(snum);
 	 		    		if( Double.isNaN(dd) ) { do_error("Error parsing Training accuracy " + sLijn ); return null; }
 	 		    		mx.setTestAccuracy( dd );
 	 		    	}
 	 		    	else { // drop of the edge
 	 		    		do_error("Too many fields [" + k + "]-> [" + sLijn + "]"); return null;
 	 		    	}
 	     	      }
 	 		      if( mx == null ) { do_error("not parsed"); return null; }
 	 		      mondata[ dataptr ] = mx;	  
 	 		      
 	          }
     	    } // while
 	       	// EOF
 	       	if( ( dataptr + 1) != NbrOfEntries ) {
 	       		do_error( "Number of lines read [" + (dataptr+1) + "] does not match anticipated [" + NbrOfEntries + "]");
 	       		return null;
 	       	}
     	 }   	
     	catch (Exception e) {
     		do_error("Could not read model file [" + XMLFileName + "]" + e.getMessage() );
     		e.printStackTrace();
     		return null;
     	 }
     	 finally {
     		try {
     		reader.close();
     		}
     		catch(Exception e ) {
     			do_error("Could not close [" + XMLFileName + "]");
         		return null;
     		}
     	 }
     	 //
     	 if( embeddedmlpdto == null )  { do_error("Could not find MLDTO"); return null; }
     	 if( epadd == null )  { do_error("Extended attributes not read"); return null; }
   	     if( epadd.ExtendedClassNameList == null ) { do_error("extended class list is unknown"); return null; }
     	 //
     	 return mondata;
    }
    
}
