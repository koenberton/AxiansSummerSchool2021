package org.tektonik.tekStraktor.thread;

import java.util.concurrent.Semaphore;

import org.tektonik.tekStraktor.TensorFlow.cmcVRExecTensorFlowScript;
import org.tektonik.tekStraktor.featureExtraction.ThreadMonitorDTO;
import org.tektonik.tekStraktor.featureExtraction.ThreadMonitorDTO.INFOTRANSFERSTATUS;
import org.tektonik.tekStraktor.featureExtraction.ThreadMonitorDTO.MONITORSTATUS;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tekStraktor.thread.ConcurrencyController.SEMAPHORE_TYPE;
import org.tektonik.tools.logger.logLiason;

public class cmcVRTensorFlowScriptThread extends Thread {
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	
    ConcurrencyController locker = null;
    ThreadMonitorDTO moni = null;

private String ErrorMsg=null;

	
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
    	ErrorMsg=sIn + "\n";
    	do_log(0,sIn);
    }
    
    //------------------------------------------------------------
    public String getErrorMessage()
    //------------------------------------------------------------
    {
      return ErrorMsg;	
    }
    

    cmcVRTensorFlowScriptThread(cmcProcSettings iM, logLiason ilog ,  ThreadMonitorDTO mon , Semaphore s)
    {
    	xMSet = iM;
		logger = ilog;
		locker = new ConcurrencyController(xMSet , logger , SEMAPHORE_TYPE.FOLLOWER , s);
		moni = mon;
    }
    
    //----------------------------------------------------------------
   	public void run()
    //----------------------------------------------------------------
   	{
   		if( locker.getLock() == false ) return;
		moni.setStatus( MONITORSTATUS.BUSY );
		locker.unLock();
		//
		ThreadMonitorDTO resp = null;
	    cmcVRExecTensorFlowScript exc = new cmcVRExecTensorFlowScript( xMSet , logger );
		boolean ok = exc.execTensorFlowVR( moni.getImageFileName() , moni.getFieldIndex() );
		resp = exc.getTensorRespons();
		ok = (resp == null) ? false : resp.getExitStatus();
		// copy the contents of the resp
		if( locker.getLock() == false ) return;
	    if( resp == null ) {
	    	moni.setStatus( MONITORSTATUS.FAILED );
	        moni.setErrorMsg( "could not initialize resp");
	    }
	    else {
	    	int fieldIndex = moni.getFieldIndex();
	    	moni.shallowCopy( resp );
	    	moni.setFieldIndex(fieldIndex);
	    }
	    moni.setTxstat( INFOTRANSFERSTATUS.READY );
		//
	    if( ok == false ) {
			moni.setStatus( MONITORSTATUS.FAILED);
	    }
	    else {
			moni.setStatus( MONITORSTATUS.COMPLETED);
	    }
	    moni.setEndtime(System.currentTimeMillis());
	    locker.unLock();

   	}
}
