package org.tektonik.historyMiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.tektonik.tools.generalpurpose.gpPrintStream;
import org.tektonik.tools.generalpurpose.gpUtils;

public class coarseSearch {
	
	private int SLIDER_SIZE = 35;  //  Must be UNEVEN
	
	gpUtils xU = null;
	
	private gpPrintStream pout = null;
	private int sweetspot = -1;
	
	private void do_log( String s)
	{
	     System.out.println( s );	
	}
	private void do_error( String s)
	{
	     System.err.println( s );	
	}
	
	coarseSearch(gpUtils u)
	{
		if( (SLIDER_SIZE % 2) == 0 ) {
			do_error( "System Error : SLIDER_SIZE must be uneven");
			System.exit(1);;
		}
	 	sweetspot = SLIDER_SIZE / 2;
		
		xU=u;
	}

	
	public void maakBatchFile(String foldername )
	{
		if( foldername == null ) return;
		if( xU.IsDir( foldername ) == false ) { do_error( "Cannot locate folder [" + foldername + "]"); return; }

		ArrayList<String> list = xU.GetFilesInDir( foldername , null);
		if( list.size() < 1 ) { do_error( "Nothing to process [" + foldername + "]"); return; }
		//pout = new gpPrintStream( "c:\\temp\\junk.txt" , "LATIN1" );
		for(int i=0;i<list.size();i++)
		{
			String ShortFileName = list.get(i);
			if( ShortFileName == null ) continue;
			if( ShortFileName.trim().toUpperCase().endsWith(".PDF") == false ) continue;
			String LongFileName = foldername + xU.ctSlash + list.get(i);
		    String TargetFileName = xU.RemplaceerIgnoreCase( ShortFileName , ".PDF" , ".txt" );
			String command = "java -Xmx6144M -d64 -jar pdfbox-app-2.0.17.jar ExtractText -rotationMagic -encoding ISO-8859-1  %SOURCE_DIR%\\" + ShortFileName +
					         " %TARGET_DIR%\\Extract_" + TargetFileName;
			do_log( command );
		}
		//pout.close();
	}
	
	
	public void doProximitySearchInFolder( String foldername , String target)
	{
		if( foldername == null ) return;
		if( target == null ) return;
		if( xU.IsDir( foldername ) == false ) { do_error( "Cannot locate folder [" + foldername + "]"); return; }
		//
		ArrayList<String> list = xU.GetFilesInDir( foldername , null);
		if( list.size() < 1 ) { do_error( "Nothing to process [" + foldername + "]"); return; }
		//
		pout = new gpPrintStream( "c:\\temp\\junk.txt" , "LATIN1" );
		for(int i=0;i<list.size();i++)
		{
			String ss = list.get(i);
			if( ss == null ) continue;
			if( ss.trim().toUpperCase().startsWith("EXTRA") == false ) continue;
			if( ss.trim().toUpperCase().endsWith(".TXT") == false ) continue;
			String LongFileName = foldername + xU.ctSlash + list.get(i);
if( i == (list.size()-2) ) {do_error( "ABORTED - concurrency");	break;}
		    doProximitySearch( LongFileName , target );	
		}
		pout.close();
	}
	/*
	public void doProximitySearch( String[] files , String target)
	{
		if( files == null ) return;
		for(int i=0;i<files.length;i++)
		{
		    doProximitySearch( files[0] , target );	
		}
	}
	*/
	private void pushToSlidingWindow( String[] slidingwindow , String sline)
	{
	   if( sline == null ) return;
	   for(int i=0;i<slidingwindow.length-1;i++) slidingwindow[i] = slidingwindow[i+1];	
	   slidingwindow[ slidingwindow.length - 1] = sline;
	}
	
	private boolean slidingWindowContains( String[] slidingwindow , String sline)
	{
		// verify the mid row identified by sweetspot
	 
		// TO DO TOKENIZER en ignore vase
		
		if( slidingwindow[sweetspot].indexOf( sline ) >= 0 ) {
			return true;
		}
		return false;
	}
	
	private void dump( String s)
	{
		do_log(s);
		pout.println(s);
	}
	
	private void showSlidingWindow( String[] slidingwindow , int row)
	{
	   dump( "== " + row + " =================================================================");
	   for(int i=0;i<slidingwindow.length;i++)
	   {
		   String filler = (i == sweetspot ) ? " > " : "   ";
		   dump( filler + slidingwindow[i] );
	   }
	   do_log( " ");
	}
	
	public void doProximitySearch( String LongFileName , String target)
	{
	    if( LongFileName == null ) { do_error( "NULL filename"); return; }
	    if( xU.IsBestand( LongFileName ) == false ) { do_error( "Cannot find [" + LongFileName + "]"); return; }
	    dump( LongFileName + " " + target);	
	    
	    String[] slidingwindow = new String[ SLIDER_SIZE ];
	    for(int i=0;i<slidingwindow.length;i++) slidingwindow[i] = "";
	    
	    
	    
	    int rowInFile=-1;
	    int validRow=-1;
	    int spotted = -1;
	    BufferedReader reader=null;
	   	try {
				File inFile  = new File(LongFileName);  // File to read from.
		        reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "UTF-8"));
		       	String sLine=null;
		       	while ((sLine=reader.readLine()) != null) {
		           String sl = sLine.toUpperCase().trim();
		           rowInFile++;
//if( rowInFile > 1000 ) break;
		           if( sl.length() == 0 ) continue;
		           validRow++;
		           //
		           pushToSlidingWindow( slidingwindow , sLine );
		           if( slidingWindowContains( slidingwindow , target ) ) {
		        	   if( (rowInFile - spotted) <= sweetspot ) continue;  // overlap
		        	   spotted = rowInFile;
		        	   showSlidingWindow( slidingwindow , rowInFile);
		           }
		       	}
		       	
	   	}
	   	catch(Exception e ) {
	    		do_error( "Reading file [" + LongFileName + "]");
	    		return;
	   	}
	   	finally {
	   		try {
	   			reader.close();
	   		}
	   		catch(Exception e ) {
	    		do_error( "Closing file [" + LongFileName + "]");
	    		return;
	   	    }
	   	}
	}
	
}
