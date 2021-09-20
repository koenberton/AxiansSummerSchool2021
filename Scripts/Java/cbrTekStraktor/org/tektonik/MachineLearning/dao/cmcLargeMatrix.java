package org.tektonik.MachineLearning.dao;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.logger.logLiason;


public class cmcLargeMatrix {
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	
	private String LongMemoryFileName = null;
	private int rows = -1;
	private int cols = -1;
	private int memMappedSize = -1;
	private String LastErrorMsg = null;
	private RandomAccessFile memMappedFile = null;
    private MappedByteBuffer memMappedBuffer = null;
  
    //------------------------------------------------------------
    private void do_log(int logLevel , String sIn)
    {
       if( logger != null ) logger.write( this.getClass().getName() , logLevel , sIn);
       else 
       if (logLevel == 0 ) System.err.println(sIn);
       else System.out.println(sIn);
    }
	//------------------------------------------------------------
    private void do_error(String sIn)
    {
    	LastErrorMsg=sIn;
    	do_log(0,sIn);
    }
    //------------------------------------------------------------
    public String getLastErrorMsg()
    {
    	return LastErrorMsg;
    }

    
    public String getLongMemoryFileName() {
		return LongMemoryFileName;
	}
	public int getNbrOfRows() {
		return rows;
	}
	public int getNbrOfCols() {
		return cols;
	}
	public int getMemMappedSize() {
		return memMappedSize;
	}
	
    
    //------------------------------------------------------------
    public cmcLargeMatrix(cmcProcSettings is , logLiason ilog , String MemMapFileName , int irow , int icol)
    {
    	xMSet = is;
		logger=ilog;
		LongMemoryFileName = MemMapFileName;
		rows = irow;
		cols = icol;
    }
    
    //------------------------------------------------------------
    public boolean initializeLargeMatrix()
	{
		if( (rows <= 0) || (cols <=0) ) {
    		do_error("Rows or Cols less than 1");
    		return false;
    	}
    	memMappedSize = rows * cols * 8;
    	//
    	String sDir = xMSet.xU.getParentFolderName(LongMemoryFileName);
    	if( xMSet.xU.IsDir(sDir) == false ) {
    		do_error("Memory mapped folder is not accessible [" + sDir + "] for [" + LongMemoryFileName + "]");
    		return false;
    	}
    	if( xMSet.xU.IsBestand( LongMemoryFileName ) ) {
    		if( xMSet.xU.VerwijderBestand( LongMemoryFileName ) == false ) {
    		  do_error("Memory mapped file cannot be removed [" + LongMemoryFileName + "]");
    		  return false;
    		}
    	}
    	try {
    		 memMappedFile = new RandomAccessFile(LongMemoryFileName,"rw");
    		 memMappedBuffer = memMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, memMappedSize );
    		 memMappedBuffer.position(0);
    	}
    	catch(Exception e ) {
    		do_error("CREATED Memory Mapped file [" + LongMemoryFileName + "][Sz=" + memMappedSize + "][Rows=" + rows + "][cols=" + cols + "]" +  xMSet.xU.LogStackTrace(e));
    		return false;
    	}
      	do_log( 1 , "CREATED Memory Mapped file [" + LongMemoryFileName + "][Sz=" + memMappedSize + "][Rows=" + rows + "][Cols=" + cols + "]");
      return true;
	}

    //------------------------------------------------------------
    public boolean openLargeMatrixForRead()
    {
    	if( (rows <= 0) || (cols <=0) ) {
    		do_error("Rows or Cols less than 1");
    		return false;
    	}
    	memMappedSize = rows * cols * 8;
        // 	
    	if( LongMemoryFileName == null ) { do_error("Null memory mapped file name"); return false; }
    	if( xMSet.xU.IsBestand( LongMemoryFileName ) == false ) {
    		do_error("Memory Mapped File not found [" + LongMemoryFileName + "]");
    		return false;
    	}
    	try 
        {
    		memMappedFile = new RandomAccessFile(new File(LongMemoryFileName), "r");
            FileChannel fileChannel = memMappedFile.getChannel(); //Get file channel in read-only mode
            memMappedBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());   //Get direct byte buffer access using channel.map() operation
            memMappedBuffer.position( 0 );
            //System.out.println("Loaded ->" + memMappedBuffer.isLoaded());  //prints false
            //System.out.println("Size   ->" + memMappedBuffer.capacity());  //Get the size based on content size of file
            if( memMappedSize !=  memMappedBuffer.capacity() ) {
            	do_error("The actual size of Memory Mapped File does not match " + memMappedSize + " " +  memMappedBuffer.capacity() );
            	return false;
            }
            /*
            for (int i = 0; i < memMappedBuffer.limit(); i++)
            {
                //System.out.print((char) buffer.get()); //Print the content of file
            }
            */
        }
        catch(Exception e ) {
          do_error( "OPEN " + xMSet.xU.LogStackTrace(e) );
   		  return false;
   	    }
        do_log( 1 , "OPENED Memory Mapped File [Sz=" + memMappedSize + "][" + LongMemoryFileName + "]");
        return true;
    }
    
    //------------------------------------------------------------
    public boolean closeMemoryMappedFile()
    {
    	try {
    		 //do_log( 1, "Loaded ->" + memMappedBuffer.isLoaded());  //prints false
             memMappedFile.close();
    		 memMappedBuffer = null;
    	}
    	catch(Exception e ) {
    		do_error( "CLOSE " + xMSet.xU.LogStackTrace(e) );
    	    return false;
    	}
    	do_log( 1, "CLOSED Memory Mapped file [" + LongMemoryFileName + "]");
    	return true;
    }
	
    //------------------------------------------------------------
    public boolean writeSequentiallyToMemoryFile( double[] linebuffer , int rownumber )
    {
        if( linebuffer == null ) { do_error("Null linebuffer"); return false; }
        if( linebuffer.length == 0 ) return true;
        try {
        	for( int i=0;i<linebuffer.length;i++) memMappedBuffer.putDouble( linebuffer[i] );
        }
        catch(Exception e ) {
        	do_error("WriteSequentially [" + rownumber + "]" + e.getMessage() + " " + linebuffer.length + " " + LongMemoryFileName + " " + xMSet.xU.LogStackTrace(e) );
        	return false;
    	}
        return true;
    }
   
    //------------------------------------------------------------
    public double[] readRow( int rowindex )
    {
    	int position = rowindex * this.cols * 8;
    	if( rowindex < 0 ) { do_error("Rowindex out of bound - less than 0 [" + rowindex + "]"); return null; }
    	if( rowindex > rows ) { do_error("Rowindex out of bound [" + rowindex + "] exceeds rows [" + rows + "]"); return null; }
    	if( position >= memMappedSize ) { do_error("Rowposition out of bound [" + position + "] exceeds buffersize [" + memMappedSize + "]"); return null; }
    	double[] ret = new double[ this.cols];
    	for(int i=0;i<cols;i++) ret[i] = Double.NaN;
		try {
    		for(int i=0;i<cols;i++)
    		{
        	  memMappedBuffer.position( position );
			  ret[i] = memMappedBuffer.getDouble(position);
    		  position += 8;
    		}
    	}
    	catch(Exception e ) {
    		do_error("READROW [" + rowindex + "]" + xMSet.xU.LogStackTrace(e));
    		return null;
    	}
    	return ret;
    }

    
    /*
    private void clean()
    {
    	((DirectBuffer) memMappedBuffer).cleaner().clean();
    }
    */
    
    /*
    public void unmapMmaped() {
    	  if (memMappedBuffer instanceof sun.nio.ch.DirectBuffer) {
    	    sun.misc.Cleaner cleaner = ((sun.nio.ch.DirectBuffer) buffer).cleaner();
    	    cleaner.clean();
    	  }
    }
    */
   
    /*
    // DEBUG
    //------------------------------------------------------------
    public cmcMatrix getDEBUGDataMatrix()
    {
          if( memMappedBuffer == null ) return null;
          if( (rows <= 0) || (cols <=0) ) {
      		do_error("Rows or cols less than 1");
      		return null;
      	  }
          //
          double[][] val = new double[ rows ][ cols-1 ];  // exclude CLASS
      	  for(int i=0;i<rows;i++)
      	  {
      		double[] ret = this.readRow(i);
      		if( ret ==  null ) { do_error("Reading " + i); return null; }
      		for(int j=0;j<ret.length-1;j++) val[i][j] = ret[j];
      	  }
          cmcMatrix z = new cmcMatrix( val );      
          return z;
    }
    
    //-- DEBUG ----------------------------------------------------------
    public cmcMatrix getDEBUGResultMatrix()
    {
    	  if( memMappedBuffer == null ) return null;
    	  if( (rows <= 0) || (cols <=0) ) {
        		do_error("Rows or cols less than 1");
        		return null;
          }
    	  //
          double[][] val = new double[ rows ][ 1 ];
      	  for(int i=0;i<rows;i++)
      	  {
      		double[] ret = this.readRow(i);
      		if( ret ==  null ) { do_error("reading " + i); return null; }
      		val[i][0] = ret[ret.length-1];
      	  }
          cmcMatrix z = new cmcMatrix( val );      
          return z;
    }
    */
    
    
    
}
