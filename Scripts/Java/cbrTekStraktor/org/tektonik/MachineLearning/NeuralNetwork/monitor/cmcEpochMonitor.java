package org.tektonik.MachineLearning.NeuralNetwork.monitor;

import org.tektonik.MachineLearning.cmcMachineLearningConstants;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTO;
import org.tektonik.MachineLearning.dao.cmcEpochMonitorDAO;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.logger.logLiason;

public class cmcEpochMonitor {
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	
	private cmcEpochMonitorDAO dao = null;
	cmcMLPDTO mlpdto = null;
	
	private EpochMonitorDTO[] mondata = null;
	private int dataptr=0;
	private long lastRefresh = 0L;
	private String[] extendedClassList=null;
	private boolean IsContinuation = false;
	
	
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
    	do_log(0,sIn);
    }

    //------------------------------------------------------------
    public cmcEpochMonitor(cmcProcSettings is,logLiason ilog )
    //--------------------------
    {
      xMSet = is;
      logger = ilog;
    }
    
    //------------------------------------------------------------
    public boolean initializeMonitor(cmcMLPDTO dto , String[] elst , boolean ic)
    //------------------------------------------------------------
    {
    	if( dto == null )  { do_error( "Null dto"); return false; }
        if( dto.getMaximumNumberOfEpochs() < 0) { do_error("Maximum number epochs not set"); return false;}
        if( elst == null ) { do_error( "Null extended ClassNamelist"); return false; }
        if( elst.length < 2) { do_error("Class list should have at least 2 classes"); return false; }
        //    	
        IsContinuation = ic;
        mlpdto = dto;
        extendedClassList = elst;
        mondata = new EpochMonitorDTO[ dto.getMaximumNumberOfEpochs() ];
        for(int i=0;i<mondata.length;i++) mondata[i] = null;
        dataptr=-1;
        dao = new cmcEpochMonitorDAO( xMSet , logger );
        lastRefresh = 0L;
        if( IsContinuation )  {
        	boolean ib = reloadStats();
        	if( ib == false ) return false;
        }
        do_log( 1 , "Monitor intialized [" + IsContinuation + "]");
    	return true;
    }
    
    //------------------------------------------------------------
    private boolean reloadStats()
    //------------------------------------------------------------
    {
     	if( mondata ==  null ) { do_error("monitor not initialized"); return false;}
     	if( dao ==  null ) { do_error("DAO not initialized"); return false;}
        String OldStatFile = xMSet.getPreviousEpochStatXMLFileName();
        if( xMSet.xU.IsBestand( OldStatFile ) == false ) {
        	do_error("Cannot locate old stat file [" + OldStatFile + "]"); return false; 
        }
        EpochMonitorDTO[] list = dao.readEpochMonitorStats( OldStatFile );
        int naant = dao.getreportedNbrOfEntries();
        if( list == null ) {
        	if( naant <= 0 )  return true;
        	do_error("Reading stats from old epoch file");
        	return false;
        }
        if( naant >= mondata.length ) { do_error("Previous nbr of log entries will not fit in array"); return true; } // negeer
        for(int i=0;i<naant;i++)
        {
        	mondata[i] = list[i];
        	dataptr=i;
        }
     	return true;
    }
    
    //------------------------------------------------------------
    public boolean pushToMonitor( EpochMonitorDTO mdto , boolean forced)
    //------------------------------------------------------------
    {
    //do_error( "monitor pushed " + forced );
    	if( mondata ==  null ) { do_error("monitor not initialized"); return false;}
    	if( forced == false ) {
    	  if( mdto == null ) { do_error("Null mdto"); return false; }
    	  dataptr++;
    	  if( dataptr >= mondata.length ) { do_error("Too many monitor entries"); return false; }
    	  mondata[ dataptr ] = mdto;
    	  //
    	  long passed = System.currentTimeMillis() - lastRefresh;
    	  if( passed < (cmcMachineLearningConstants.REFRESH_INTERVAL_IN_SEC*1000L) ) return true;
    	   // wait until right on the 1 sec interval or a period has passed; i.e. refresh + 1
    	   if( passed < (cmcMachineLearningConstants.REFRESH_INTERVAL_IN_SEC+1)*1000L) return true;
    	   lastRefresh = System.currentTimeMillis();
    	}
     //do_error( "monitor written " + forced );
    	//
    	if( dao.writeEpochMonitorStats( xMSet.getEpochStatXMLFileName() , mlpdto, mondata , dataptr , extendedClassList) == false ) return false;
    	//
    	return true;
    }
    
   
}
