package org.tektonik.MachineLearning.NeuralNetwork.monitor;

import java.awt.Color;

import org.tektonik.MachineLearning.Evaluation.cmcModelEvaluation;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTO;
import org.tektonik.MachineLearning.dao.cmcEpochMonitorDAO;
import org.tektonik.tekStraktor.drawing.cmcDrawPixelSupport;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalImagePurpose.cmcImageRoutines;
import org.tektonik.tools.generalImagePurpose.color.gpColor;
import org.tektonik.tools.generalStatPurpose.gpDiagram;
import org.tektonik.tools.generalStatPurpose.gpDiagramDTO;
import org.tektonik.tools.logger.logLiason;

public class cmcEpochMonitorInfoAndDiagrams {

	private int START_GRADIENTS = 4;  // voor de epoch stat buffers
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	private gpDiagram diarout = null;
	private gpColor colrout = null;
	private cmcImageRoutines irout=null;
	private cmcEpochMonitorDAO dao = null;
	private cmcModelEvaluation eval = null;
	private EpochMonitorDTO[] epochdata = null;
	private cmcDrawPixelSupport drw = null;
	private cmcMLPDTO mlpdto = null;
		
	private String[] extendedClassNameList = null;
	private long lastTimeStampFile = -1L;
	private int actualNbrOfEntries = -1;
	private int[][] lastconfusion = null;
	private String lastError = null;
	
	private double monitLastCost = Double.NaN;
	private double monitLastAccuracy = Double.NaN;
	private double monitLastTestAccuracy = Double.NaN;
	
	private double[] xvals = null;
	private double[] yvals = null;
	private double[] rvals = null;
	private double[][] accur = null;
	private double[][][] fprtpr = null;   // classes - FPR/TPR
	private boolean showRunningAverage = false;
	private boolean showSpeed = false;
	private boolean showCurve = true;

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
    	lastError = sIn;
    }

    public String getLastError()
    {
       return lastError;	
    }
    
    //------------------------------------------------------------
    public cmcEpochMonitorInfoAndDiagrams(cmcProcSettings is,logLiason ilog )
    //--------------------------
    {
      xMSet = is;
      logger = ilog;
      doReset();
      colrout = new gpColor();
      irout = new cmcImageRoutines(logger);
      diarout = new gpDiagram(xMSet,logger);
      drw = new cmcDrawPixelSupport();
      dao = new cmcEpochMonitorDAO( xMSet , logger );
    }
    
    public void doReset()
    {
    	extendedClassNameList = null;
    	lastTimeStampFile = -1L;
    	actualNbrOfEntries = -1;
    	lastconfusion = null;
    	lastError = null;
    	
    	monitLastCost = Double.NaN;
    	monitLastAccuracy = Double.NaN;
    	monitLastTestAccuracy = Double.NaN;
    	
    	xvals = null;
    	yvals = null;
    	rvals = null;
    	accur = null;
    	fprtpr = null;   // classes - FPR/TPR	
    }
    
    //------------------------------------------------------------
    public boolean createBlankDiagram( int w ,  int h , Color background , String ImageFileName )
    //------------------------------------------------------------
    {
    	int[] diagram = diarout.createBlankDiagram( w, h, background);
        if( diagram == null ) return false;
    	irout.writePixelsToFile( diagram , w, h ,  ImageFileName , cmcImageRoutines.ImageType.RGB );
    	return true;
    }
    
    //------------------------------------------------------------
    public cmcMLPDTO getMLPDTO()
    //------------------------------------------------------------
    {
    	return this.mlpdto;
    }
    //------------------------------------------------------------
    public int getActualNbrOfentries()
    //------------------------------------------------------------
    {
    	return this.actualNbrOfEntries;
    }
    public boolean isShowRunningAverage() {
		return showRunningAverage;
	}
	public void setShowRunningAverage(boolean showRunningAverage) {
		this.showRunningAverage = showRunningAverage;
	}
	public boolean isShowSpeed() {
		return showSpeed;
	}
	public void setShowSpeed(boolean show) {
		this.showSpeed = show;
	}
	public boolean isShowCurve() {
		return showCurve;
	}
	public void setShowCurve(boolean showCurve) {
		this.showCurve = showCurve;
	}

    //------------------------------------------------------------
    public String getLastConfusionInfo()
    //------------------------------------------------------------
    {
    	if( lastconfusion == null ) return null;
    	if( lastconfusion.length != extendedClassNameList.length ) { 
    		do_error("ExtendedClassNameList lengt does not match confusion matrix " + lastconfusion.length +" " + extendedClassNameList.length); 
    		return null; }
		eval.injectMatrix( lastconfusion );
		eval.calculate();
		return eval.showConfusionMatrix();
    }
    
    public double getLastCost()
    {
    	return monitLastCost;
    }
    public double getLastAccuracy()
    {
    	return monitLastAccuracy;
    }
    //------------------------------------------------------------
    public boolean getEpochStats()
    //------------------------------------------------------------
    {
    	//if( xMSet.xU.IsBestand( xMSet.getEpochStatXMLFileName() ) == false ) return true;
    	if( readXMLFile() == false ) { do_error("Reading XML file") ; return false; }
    	if( mlpdto == null ) { do_error("Extracting mlpdto"); return false; }
    	if( extendedClassNameList == null ) { do_error("Extracting extendedClassNameList"); return false; }
    	if( epochdata == null ) { do_error("NULL epoch data"); return false; }
    	return true;
    }
    //------------------------------------------------------------
    public void forceReRead()
    //------------------------------------------------------------
    {
    	lastTimeStampFile = lastTimeStampFile - 1L;
    }
    //------------------------------------------------------------
    private boolean readXMLFile()
    //------------------------------------------------------------
    {
    	if( dao == null ) return false;
    	if( xMSet.xU.IsBestand(xMSet.getEpochStatXMLFileName())==false) {
    		do_log( 1 , "Stats file missing " + xMSet.getEpochStatXMLFileName() );
    		return false;
    	}
        long currentTS = xMSet.xU.getModificationTime( xMSet.getEpochStatXMLFileName());
        if( currentTS == lastTimeStampFile ) return true;
        //	
        epochdata = null;
    	mlpdto=null;
    	extendedClassNameList = null;	
    	lastconfusion = null;
       //
    	epochdata = dao.readEpochMonitorStats( xMSet.getEpochStatXMLFileName() );
    	if( epochdata == null ) { 
    		if( dao.getreportedNbrOfEntries() != 0 ) {
    			do_error("Could not read XML file");
    		    return false; 
    		}
    		else return true; // nothing to do
    	}
    	//do_log( 1 , "reading epoch data [" + dao.getreportedNbrOfEntries() + "] [" + epochdata.length + "]");
    	//
    	mlpdto = dao.getEmbeddedMLPDTO();
    	if( mlpdto == null ) { do_error("Could not read MLDTO from stat file"); return false; }
    	extendedClassNameList = dao.getExtendedClassNameList();
    	if( extendedClassNameList == null ) { do_error("Could not read ExtendedClassNameList from stat file"); return false; }
    	if( extendedClassNameList.length < 1) { do_error("No entries in ExtendedClassNameList from stat file"); return false; }
    	//
    	eval = new cmcModelEvaluation(extendedClassNameList);
    	//
    	int bufferLength = epochdata.length;
    	actualNbrOfEntries = dao.getreportedNbrOfEntries();
        int adjustedSize = ((bufferLength + 300) / 100) * 100;
    	bufferLength = adjustedSize < mlpdto.getMaximumNumberOfEpochs() ? adjustedSize : mlpdto.getMaximumNumberOfEpochs();  
    	
    	int NbrOfCurves = mlpdto.getNbrOfHiddenLayers() + 5;  // X - ACCURACY - COST - TEST - Gradients(#=>#hidden layers)
    	xvals = new double[ bufferLength ];
    	yvals = new double[ xvals.length ];
    	rvals = new double[ xvals.length  ];
    	accur = new double[ xvals.length  ][ NbrOfCurves ]; //  (x,y) en y is tussen 0 en 1
    	fprtpr = new double[ extendedClassNameList.length ][ xvals.length ][2];  //  class - fpr, tpr
    	
    	for(int i=0;i<xvals.length ;i++)
    	{
    		xvals[i] = i;
    		yvals[i] = 0;
    		rvals[i] = 1;
    		accur[ i ][ 0 ]= i;
    		accur[ i ][ 1 ]= 1;
    		for(int k=0;k<extendedClassNameList.length;k++)
    		{
    			fprtpr[k][i][0] = 0;
    			fprtpr[k][i][1] = 0;
    		}
    		//
    		if( i >= epochdata.length ) continue;
    		if( epochdata[i] == null ) continue;
    		//elapsed += epochdata[i].getTimeElapsed();
    		yvals[i] =  monitLastCost = epochdata[i].getEpochCost();
    		accur[ i ][ 1 ] = monitLastAccuracy = epochdata[i].getEpochAccuracy();
    		accur[ i ][ 2 ] = monitLastCost = epochdata[i].getEpochCost();
    		accur[ i ][ 3 ] = monitLastTestAccuracy = epochdata[i].getTestAccuracy();
    		for(int k=START_GRADIENTS;k<NbrOfCurves;k++)
    		{
    		  accur[ i ][ k ] = epochdata[i].getLearningSpeed()[ k-START_GRADIENTS ];
    		}
    		//
    		lastconfusion = epochdata[i].getConfusionMatrix();
    		//
      	    if( lastconfusion.length != extendedClassNameList.length ) { do_error("ExtendedClassNameList lengt does not match confusion matrix " + lastconfusion.length +" " + extendedClassNameList.length); return false; }
    		eval.injectMatrix( lastconfusion );
    		eval.calculate();
    		for(int k=0;k<extendedClassNameList.length;k++)
    		{
    			fprtpr[k][i][0] = eval.getFPRPerClass( extendedClassNameList[k] );
    			fprtpr[k][i][1] = eval.getTPRPerClass( extendedClassNameList[k] );
    		}
    	}
    	// OK
    	//do_log( 1 , "Read [#=" + this.getActualNbrOfentries() + "] epoch stats");
    	lastTimeStampFile = currentTS;
    	return true;
    }
    
    //------------------------------------------------------------
    public boolean createProgressDiagram( int w ,  int h , Color background )
    //------------------------------------------------------------
    {
        //do_log( 1, "Request to create diagram");
    	if( getEpochStats() == false) return false;
    	//
    	long elapsed=0L;
    	int idx = -1;
    	for(int i=0;i<xvals.length ;i++)
    	{
    		if( i >= epochdata.length ) continue;
    		if( epochdata[i] == null ) continue;
    		idx=i;
    		elapsed += epochdata[i].getTimeElapsed();
    	}
    	if( idx < 0 ) { do_error("No last"); return false; }
		int[][] lastconfusion = epochdata[idx].getConfusionMatrix();
		double lastAccuracy = epochdata[idx].getEpochAccuracy();
		double lastCost = epochdata[idx].getEpochCost();
	    //
    	gpDiagramDTO gdto =	new gpDiagramDTO( xvals , yvals , rvals );
    	gdto.setDoubleArray( accur );
    	gdto.setShowCurve(showCurve);
    	gdto.setShowRunningAverage(showRunningAverage);
    	gdto.setShowSpeed(showSpeed);
    	int diagram_width  = w; 
    	int diagram_heigth = h; 
    	//
    	String[] freetext = new String[20];
    	for(int i=0;i<freetext.length;i++) freetext[i] = null;
    	freetext[0] = "[LearningRate=" + mlpdto.getLearningRate() + "]";
    	freetext[1] = "[Epoch=" + epochdata.length + "]";
    	freetext[2] = "[Elapsed=" + (elapsed / 1000000000L) + " sec]";
    	freetext[3] = "[Loss=" + String.format("%8.0f",lastCost).trim() + "]";
    	freetext[4] = "[Accuracy=" + String.format("%3.0f",lastAccuracy*100).trim() + "%]";
    	freetext[5] = "[Cost=" + (""+mlpdto.getCostFunctionType()).toLowerCase() + "]";
    	freetext[6] = "[Activation=" + (""+mlpdto.getActivationFunction()).toLowerCase() + "]";
    	freetext[7] = "[OutputActivation=" +(""+ mlpdto.getOutputActivationFunction()).toLowerCase() + "]";
    	freetext[8] = "[Strategy=" + (""+mlpdto.getWeightStrategy()).toLowerCase() + "]";
    	freetext[9] = "[Optimization=" + (""+mlpdto.getOptimizationType()).toLowerCase() + "]";
    	//  move to ROC
    	if( lastconfusion != null ) {
    	  if( lastconfusion.length != extendedClassNameList.length ) { do_error("ExtendedClassNameList lengt does not match confusion matrix " + lastconfusion.length +" " + extendedClassNameList.length); return false; }
    	  eval.injectMatrix( lastconfusion );
    	  for(int i=0;i<extendedClassNameList.length;i++)
    	  {
    		  double d1 = eval.getPrecisionPerClass( extendedClassNameList[i] );
    		  double d2 = eval.getRecallPerClass( extendedClassNameList[i] );
    		  double d3 = eval.getTPRPerClass( extendedClassNameList[i] );
    		  double d4 = eval.getFPRPerClass( extendedClassNameList[i] );
    		  if( (10 + i) >= freetext.length ) continue;
    		  freetext[10+i] = extendedClassNameList[i] + String.format("%10.5s",d1) + " " +  " " +  String.format("%10.5s",d3) + " " +  String.format("%10.5s",d4);
    	  }
    	  /*
    	  String ss = eval.showConfusionMatrix();
    	  StringTokenizer sx = new StringTokenizer(ss,"\n");
          int k=0;
    	  while(sx.hasMoreTokens()) {
          	String sl = sx.nextToken();
          	if( sl.trim().length() < 1) continue;
  		    //freetext[k] = sl;
  		    k++;
          }    	
          */
    	}
    	gdto.setFreeText(freetext);
    	//
    	double[] freedoubles = new double[2];
    	freedoubles[0] = lastCost;
    	freedoubles[1] = lastAccuracy;
    	gdto.setFreeDoubles(freedoubles);
    	//
    	int[] diagram = diarout.dumpArrayToImage( gdto , diagram_width , diagram_heigth , background , actualNbrOfEntries);
    	if( diagram == null) { do_error("something went wrong"); return false; }
    	//
    	String ImageFileName = xMSet.getEpochPictureFileName();
    	irout.writePixelsToFile( diagram , diagram_width , diagram_heigth ,  ImageFileName , cmcImageRoutines.ImageType.RGB );
        do_log( 1 , "(Epochmonitor) [#Cycls=" + this.getActualNbrOfentries() + "][Img=" + xMSet.xU.getFolderOrFileName(ImageFileName)  + "][W=" + diagram_width + "][Sz=" + diagram.length  + "]");
    	//
    	return true;
    }
    
   
    
    //------------------------------------------------------------
    public boolean createROCDiagram( int w ,  int h , Color background , boolean showNAClass)
    //------------------------------------------------------------
    {
    	if( getEpochStats() == false) return false;
    	//
    	int idx = -1;
    	for(int i=0;i<xvals.length ;i++)
    	{
    		if( i >= epochdata.length ) continue;
    		if( epochdata[i] == null ) continue;
    		idx=i;
    	}
    	if( idx < 0 ) { do_error("No last"); return false; }
    	int[][] lastconfusion = epochdata[idx].getConfusionMatrix();
    
    	try {
    	 // Loop through classes and exclude the N/A
    	 int nDiagrams = showNAClass ? fprtpr.length : fprtpr.length - 1;
    	 int ROC_width = w / nDiagrams;
    	 int ROC_height = h;
    	 if( ROC_width < ROC_height ) ROC_height = ROC_width;
    	                         else ROC_width = ROC_height;
    	 int[] canvas = new int[ w * h];
    	 for(int i=0;i<canvas.length;i++) canvas[i] = colrout.Color2Int(background);
    	 for(int k=0;k<nDiagrams;k++)
    	 {
    		// get ROC data for this class
    		double[] FPR = new double[ fprtpr[k].length + 2];
    		double[] TPR = new double[ fprtpr[k].length + 2];
    		for(int i=0;i<fprtpr[k].length;i++)
    		{
    			double fpr = fprtpr[k][i][0];
    			double tpr = fprtpr[k][i][1];
    			FPR[i] = Double.isNaN(fpr) ? 0 : fpr;
    			TPR[i] = Double.isNaN(tpr) ? 0 : tpr;
    		}
    		FPR[ FPR.length - 2 ] = 0;  // force  to have 0,0 and 1,1 in the diagram
			TPR[ TPR.length - 2 ] = 0;
    		FPR[ FPR.length - 1 ] = 1;
			TPR[ TPR.length - 1 ] = 1;
			//
    		gpDiagramDTO gdto =	new gpDiagramDTO( FPR , TPR , null );
    		gdto.setShowCurve(showCurve);
        	gdto.setShowRunningAverage(showRunningAverage);
        	gdto.setShowSpeed(showSpeed);
        	int[] diagram = diarout.createPlainDiagram( gdto , ROC_width , ROC_height , background );
    		gdto=null;
    		if( diagram == null ) { do_error( "Got a NULL ROC diagram"); return false; }
    		int tx = ((w-(ROC_width * nDiagrams)) / 2 ) + (ROC_width * k);
    		int ty = (h - ROC_height) / 2;
    		drw.pasteSnippet( canvas , tx , ty , w , diagram , ROC_width );
    	    
    	 }
 		String ImageFileName = xMSet.getEpochROCImageFileName();
    	irout.writePixelsToFile( canvas , w , h ,  ImageFileName , cmcImageRoutines.ImageType.RGB );
    	 
    	}
    	catch(Exception e ) {
    		do_error( "ROC" + xMSet.xU.LogStackTrace(e));
    		return false;
    	}
    	
    	return true;
    }
    
    
}
