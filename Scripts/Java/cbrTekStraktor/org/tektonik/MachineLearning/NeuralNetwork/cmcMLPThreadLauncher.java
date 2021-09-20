package org.tektonik.MachineLearning.NeuralNetwork;

import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.logger.logLiason;

public class cmcMLPThreadLauncher extends Thread {
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	private  cmcMLPDTOCore core = null;
	private String LastErrorMsg = null;
	private boolean completed=false;
	private boolean DETAILED_DEBUG = true;
	private boolean applyNiceFactor = false;
	private boolean isContinuation = false;
	
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
    public cmcMLPThreadLauncher( cmcProcSettings is,logLiason ilog , cmcMLPDTOCore ic)
	//------------------------------------------------------------
    {
    	xMSet = is;
		logger=ilog;
		core = ic;
    }
    //------------------------------------------------------------
    public void setDetailedDebug(boolean ib)
    //------------------------------------------------------------
    {
    	DETAILED_DEBUG = ib;
    }
    //------------------------------------------------------------
    public void setNiceFactor(boolean ib)
    //------------------------------------------------------------
    {
    	applyNiceFactor = ib;
    }
    //------------------------------------------------------------
    public void setIsContinuation(boolean ib)
    //------------------------------------------------------------
    {
    	isContinuation = ib;
    }
    //------------------------------------------------------------
    public boolean hasCompleted()
    //------------------------------------------------------------
    {
    	return this.completed;
    }
    //------------------------------------------------------------
    public void run()
    //------------------------------------------------------------
    {
    	completed=false;
    	cmcMLP mpl = new cmcMLP( xMSet , logger );
    	mpl.setDetailedDebug( DETAILED_DEBUG );
    	mpl.setNiceFactor( applyNiceFactor );
    	mpl.setIsContinuation(isContinuation);
  		boolean ib = mpl.trainMLP( core );
  		mpl.closeLogger();
  		if( ib == false ) do_error( mpl.getLastErrorMsg() );
  		mpl = null;
  		core = null;
  		completed=true;
  	}
}
