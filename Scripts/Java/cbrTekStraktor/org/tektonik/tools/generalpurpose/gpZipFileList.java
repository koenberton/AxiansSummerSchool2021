package org.tektonik.tools.generalpurpose;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.tektonik.tools.logger.logLiason;


public class gpZipFileList 
{
	logLiason logger = null;
    private boolean isOK=true;
    
	//------------------------------------------------------------
    private void do_log(int logLevel , String sIn)
	//------------------------------------------------------------
    {
       if( logger != null ) logger.write( this.getClass().getName() , logLevel , sIn);
       else 
       if (logLevel == 0 ) System.err.println(sIn);
       else System.out.println(sIn);
    }
    private void do_error(String sIn)
    {
    	do_log(0,sIn);
    }
	//-----------------------------------------------------------------------
    public gpZipFileList(String FNaam , ArrayList<String> list , logLiason ilog) 
    //-----------------------------------------------------------------------
    {      
    	logger=ilog;
   	    isOK = zipit( FNaam , list , null);
    }
     
    //-----------------------------------------------------------------------
    public gpZipFileList(String FNaam , ArrayList<String> list , logLiason ilog , String RootFolder ) 
    //-----------------------------------------------------------------------
    {      
    	logger=ilog;
   	    isOK = zipit( FNaam , list , RootFolder);
    }
    
    //-----------------------------------------------------------------------
    public boolean completedOK()
    //-----------------------------------------------------------------------
    {
    	return isOK;
    }
    
    //-----------------------------------------------------------------------
    private boolean zipit(String FDest , ArrayList<String> list , String RootFolder )
    //-----------------------------------------------------------------------
    {
            // indien RootFolder gespecifieerd wordt die verwijderd uit absolute pad waardoor je een korte hierarchie hebt
            try {
                  byte[] buffer = new byte[1024];
                  FileOutputStream fos = new FileOutputStream(FDest);
                  ZipOutputStream zos = new ZipOutputStream(fos);
                  //
                  for (int i=0; i < list.size(); i++) {
                        File srcFile = new File( list.get(i) );
                        FileInputStream fis = new FileInputStream(srcFile);
                        // begin writing a new ZIP entry, positions the stream to the start of the entry data
                        // JAN 2021
                        String FileName = srcFile.getName();
                        if( RootFolder != null ) {
                        	String FullName = srcFile.getAbsolutePath().toUpperCase().trim();
                        	String ToRemove = RootFolder.trim().toUpperCase();
                        	if( FullName.startsWith( ToRemove ) == true ) {
                                FileName = srcFile.getAbsolutePath().substring( ToRemove.length() + 1 );
                                //System.err.println( Shortened + " " + FullName );
                        	}
                        }
                        zos.putNextEntry(new ZipEntry(FileName ));
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                             zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                        // close the InputStream
                        fis.close();
                  }
                  // close the ZipOutputStream
                  zos.close();
                  return true;
            }
            catch (IOException ioe) {
                  do_error("Error creating zip file: " + ioe.getMessage());
                  return false;
            }
      }

}
