package org.tektonik.MachineLearning.NeuralNetwork;

import org.tektonik.MachineLearning.ARFF.ARFFCategoryCoreDTO;
import org.tektonik.MachineLearning.ARFF.ARFFCategoryLightDTO;
import org.tektonik.MachineLearning.ARFF.ARFFEnums;
import org.tektonik.MachineLearning.ARFF.ARFFEnums.ARFF_TYPE;
import org.tektonik.MachineLearning.ARFF.ARFFLightDTO;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.hiddenNeuralLayer;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.neuralNetwork;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.neuralNetworkSupportRoutines;
import org.tektonik.MachineLearning.NeuralNetwork.monitor.EpochMonitorDTO;
import org.tektonik.MachineLearning.NeuralNetwork.monitor.cmcEpochMonitor;
import org.tektonik.MachineLearning.dao.cmcARFFDAOLight;
import org.tektonik.MachineLearning.dao.cmcLargeMatrix;
import org.tektonik.MachineLearning.dao.cmcMLPDTODAO;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalpurpose.gpAppendStream;
import org.tektonik.tools.generalpurpose.gpInterrupt;
import org.tektonik.tools.generalpurpose.gpPrintStream;
import org.tektonik.tools.linearAlgebra.cmcMath;
import org.tektonik.tools.linearAlgebra.cmcMatrix;
import org.tektonik.tools.logger.logLiason;

public class cmcMLP {
	
	private int MOETANDERS = -1;
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	private gpPrintStream dumper = null;
	private cmcMath vmath = null;
	private neuralNetwork ntw = null;
	private cmcEpochMonitor monitor = null;
	private gpInterrupt interrupt = null;
	private cmcMLPSupport mlpsupp = null;
	private neuralNetworkSupportRoutines ntwrout=null;
	
	private String LastErrorMsg = null;
	private boolean DETAILED_DEBUG = true;
	private boolean applyNiceFactor = false;
	private boolean IsContinuation = false;
		
	
	//
	private int[][] miniBatchPointers = null;
	
	class NetworkMetricDTO
	{
		double accuracy = 0;  // must be 0 - do not change
		double cost = 0;
		int hits = 0;  
		int rows = 0;
		int[][] confusion = null;
		double[] learningSpeed = null;
		NetworkMetricDTO(int dims , int NbrOfHiddenLayers )
		{
			accuracy = 0;  // must be 0
			cost = 0;
			hits = 0; 
			rows = 0;
			confusion = new int[ dims ][ dims ];
			for(int i=0;i<dims;i++)
			{
				for(int j=0;j<dims;j++) confusion[i][j]=0;
			}
			learningSpeed = new double[NbrOfHiddenLayers+1];
		}
		
		public double getAccuracy() {
			return accuracy;
		}
		public void setAccuracy(double accuracy) {
			this.accuracy = accuracy;
		}
		public double getCost() {
			return cost;
		}
		public void setCost(double a) {
			this.cost = a;
		}
		public int getHits() {
			return hits;
		}
		public void setHits(int hits) {
			this.hits = hits;
		}
		public int getRows() {
			return rows;
		}
		public void setRows(int rows) {
			this.rows = rows;
		}
		public int[][] getConfusion() {
			return confusion;
		}
		public void copyConfusion(int[][] mt) {
			if( mt == null ) return;
			if( mt.length != confusion.length ) return;
			if( mt[0].length != confusion[0].length ) return;
			for(int i=0;i<mt.length;i++)
			{
				for(int j=0;j<mt[0].length;j++) confusion[i][j]=mt[i][j];
			}
		}
		public void setConfusionValue( int i , int j , int val)
		{
			confusion[i][j] = val;
		}
		public double[] getLearningSpeed()
		{
			return learningSpeed;
		}
		public void copyLearningSpeed(double[] speeds)
		{
			if( speeds == null ) return;
			if( speeds.length != learningSpeed.length ) {
				System.err.println( "System error - copyLearningSpeed " + speeds.length + " " + learningSpeed.length );
			}
			for(int i=0;i<speeds.length;i++) learningSpeed[i] = speeds[i];
			//String ss="";
			//for(int i=0;i<speeds.length;i++) ss+= speeds[i] + " ";
			//System.err.println( ss );
		}
	}
	
	//------------------------------------------------------------
    private void do_log(int logLevel , String sIn)
	//------------------------------------------------------------
    {
       if( logger != null ) {
    	   dumpit(  sIn );
    	   if( logLevel <= 1 )logger.write( this.getClass().getName() , logLevel , sIn);
       }
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
    public void setDetailedDebug(boolean ib)
    //------------------------------------------------------------
    {
    	DETAILED_DEBUG = ib;
    	if( dumper != null ) dumper.close();
    	if( DETAILED_DEBUG ) {
   		dumper = new gpPrintStream( xMSet.getEpochDebugFileName() , "ASCII");
		}
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
    	IsContinuation = ib;
    }
    //------------------------------------------------------------
    private void dumpit( String sIn )
    {
    	if( (DETAILED_DEBUG==false) || (dumper==null) )  return;
    	if( sIn == null ) return;
    	sIn += "\n";
    	String[] result = sIn.split("\n");
        for (int x=0; x<result.length; x++)
        {
        	dumper.println( xMSet.xU.prntDateTime( System.currentTimeMillis(), "hh:mm:ss.SSS") +" " + result[x] );
        }
        //System.out.println( sIn );
    }
    //------------------------------------------------------------
    public void closeLogger()
    {
    	if( dumper != null ) dumper.close();
    }
    //------------------------------------------------------------
    public cmcMLP( cmcProcSettings is , logLiason ilog )
	//------------------------------------------------------------
    {
    	xMSet = is;
		logger=ilog;
		vmath = new cmcMath();
		monitor = new cmcEpochMonitor( xMSet , logger );
		mlpsupp = new cmcMLPSupport( xMSet , logger );
	    ntwrout = new neuralNetworkSupportRoutines(xMSet,logger);
		interrupt = new gpInterrupt( xMSet.getEpochInterruptFileName() );
		interrupt.resetInterrupt();
		if( DETAILED_DEBUG ) {
			dumper = new gpPrintStream( xMSet.getEpochDebugFileName() , "ASCII");
		}
    }
    
    /*
    //------------------------------------------------------------
    public boolean makeTestSet(String TestFileName )
    //------------------------------------------------------------
    {
      if( xMSet.xU.IsBestand( TestFileName ) ) {
    	  xMSet.xU.VerwijderBestand( TestFileName );
      }
      if( xMSet.xU.IsBestand( TestFileName ) ) {
    	  do_error( "Could not delete test file [" + TestFileName + "]");
    	  return false;
      }
      gpPrintStream tout = new gpPrintStream( TestFileName , "ASCII" );
      tout.println( "% ARFF file to test multi layer perceptron");
      tout.println( "% Created " + xMSet.xU.prntStandardDateTime(System.currentTimeMillis()));
      tout.println( "@RELATION MultiLayerPerceptronTestData");
      tout.println( "@ATTRIBUTE width NUMERIC");
      tout.println( "@ATTRIBUTE height NUMERIC");
      tout.println( "@ATTRIBUTE depth NUMERIC");
      tout.println( "@ATTRIBUTE shape {FLAT,HIGH,CUBE}");
      tout.println( "@DATA");
      tout.println( "10,2,1,FLAT");
      tout.println( "100,100,100,CUBE");
      tout.println( "1,2,1,HIGH");
      tout.println( "40,23,1000,FLAT");
      tout.println( "50,2,1,HIGH");
      tout.println( "0.2,0.23,1.00,HIGH");
      tout.println( "0.23,0.2,3.00,FLAT");
      tout.println( "0.2,0.23,0.45,HIGH");
      tout.println( "34.2,23.5,230,FLAT");
      tout.println( "0.23,0.2,3.00,FLAT");
      tout.println( "0.23,0.23,3.00,CUBE");
      tout.println( "39,45,88,HIGH");
      tout.println( "45,23,18,FLAT");
      //
      tout.close();
      return true;	
    }
    */
    
    //------------------------------------------------------------
    private boolean checkARFFCategoriesLight( ARFFCategoryLightDTO[] categories )
    //------------------------------------------------------------
    {
    	if( categories == null ) { do_error("Null category list"); return false; }
        if( categories.length <= 0 ) { do_error("Empty category list"); return false; }
    	// 
        for(int i=0 ; i<categories.length ; i++)
        {
        	ARFFCategoryLightDTO cat = categories[i];
        	if( cat == null ) continue;
        	if( cat.getTipe() == ARFF_TYPE.CLASS ) { // class
        		if( i != (categories.length-1) ) {
        			do_error( "CLASS is not on last column" + cat.getCategoryName());
        			return false;
        		}
        		String[] ClassNames = cat.getNominalValueList();
        		if( ClassNames == null ) { do_error("System Error - NULL ClassNames"); return false; }
        	}	
        }
        return true;
    }
    
    //------------------------------------------------------------
    public boolean trainMLP( cmcMLPDTOCore core )
    //------------------------------------------------------------
    {
    	    interrupt.resetInterrupt();
    	    //
    		String TrainingFileName = core.getLongARFFFileName();   // 
    		cmcARFFDAOLight lid = new cmcARFFDAOLight( xMSet , logger );
    		if( lid.performFirstPass(TrainingFileName , "trainMLP" ) == false ) {
    			do_error("Cannot perform first pass [" + TrainingFileName );
    			return false;
    		}
    		// 2nd pass => means and categories
    		ARFFLightDTO lidto = lid.performSecondPass(TrainingFileName);
    		if(  lidto == null ) {
    			do_error("Cannot perform second pass [" + TrainingFileName );
    			return false;
    		}
    		ARFFCategoryLightDTO[] categories = lidto.getAttributeList();
        	if( checkARFFCategoriesLight(categories) == false ) return false;
        	// CLASS must be defined as the last feature
        	String[] ClassNameList = categories[ categories.length - 1].getNominalValueList();
        	if( ClassNameList == null ) { do_error("Null class name list"); return false; }
        	if( ClassNameList.length <= 0) { do_error("Empty class name list"); return false; }
        	
        	// perform 3rd pass => provides the STDEVs
        	lidto = lid.performThirdPass();
    		if(  lidto == null ) {
    			do_error("Cannot perform third pass [" + TrainingFileName );
    			return false;
    		}
    		// assess the correctness and display
    		if( lid.assessCorrectness() == false ) return false;
    		
        	// 4th pass - write to memory mapped file after normalizing
    		lidto = lid.prepareMemoryMappedFile( ARFFEnums.DATA_FILE_TYPE.TRAINING );
    		if(  lidto == null ) {
    			do_error("Could no write to MemoryMappedFile [" + TrainingFileName + "]");
    			return false;
    		}
    		
    		//
            cmcMLPDTO mlpdto = new cmcMLPDTO( core.getLongARFFFileName() );
            mlpdto.kloonCore( core );
            // Categories to Core
            ARFFCategoryCoreDTO[] cclist = new ARFFCategoryCoreDTO[ categories.length ];
            for(int i=0 ; i<categories.length ; i++)
            {
            	ARFFCategoryLightDTO cat = categories[i];
            	if( cat == null ) continue;
            	cclist[i] = new ARFFCategoryCoreDTO(cat.getCategoryName());
            	cclist[i].setTipe( cat.getTipe() );
            	cclist[i].setNominalValueList(cat.getNominalValueList());
            }
            mlpdto.setNbrOfFeatures( cclist.length );
            mlpdto.setFeatures(cclist);
            mlpdto.setNbrOfRows( lidto.getNbrOfDataRows() );
            mlpdto.setNormalizerMean( lidto.getNormalizerMean());
            mlpdto.setNormalizerStdDev( lidto.getNormalizerStdDev());
            mlpdto.setIgnoredFeatures( lid.getIgnoredFeatures() );
    		//
    	    if( mlpdto.getNormalizerMean() == null ) { do_error("Cannot fetch normalizer mean values"); return false; }
            if( mlpdto.getNormalizerMean().length != mlpdto.getNbrOfFeatures() ) { do_error("Normalizer Mean number of items mismatch "); return false;}
            if( mlpdto.getNormalizerStdDev() == null ) { do_error("Cannot fetch normalizer STDDEV values"); return false; }
            if( mlpdto.getNormalizerStdDev().length != mlpdto.getNbrOfFeatures() ) { do_error("Normalizer STDDEV number of items mismatch "); return false;}
            
            // store TEST data into Memory Mapped Buffer
            String TestDataFileName = xMSet.getARFFFileNameTestSet(TrainingFileName);
            if( xMSet.xU.IsBestand(TestDataFileName) == false )  { do_error("Cannot locate TEST data [" + TestDataFileName + "]"); return false; }
            if( lid.performFirstPass( TestDataFileName , "trainMLP (testdata)" ) == false ) {do_error("Cannot perform first pass [" + TestDataFileName ); return false; }
            ARFFLightDTO junkdto = lid.performSecondPass( TestDataFileName);
    		if(  junkdto == null ) { do_error("Cannot perform second pass [" + TestDataFileName + "]"); return false; }
    		junkdto = lid.performThirdPass();
    		if(  junkdto == null ) { do_error("Cannot perform third pass [" + TestDataFileName + "]"); return false; }
    	    // inject mean and stdev of TRAIN onto TEST
    		if( lid.overwriteMeanBuffer(mlpdto.getNormalizerMean()) == false ) { do_error("cannot overwrite meansbuffer"); return false; }
    		if( lid.overwriteStdevBuffer(mlpdto.getNormalizerStdDev()) == false ) { do_error("cannot overwrite stdevbuffer"); return false; }
    		// 4de pass
    		junkdto = lid.prepareMemoryMappedFile( ARFFEnums.DATA_FILE_TYPE.TESTING );
    		if(  junkdto == null ) { do_error("Cannot perform fourth pass [" + TestDataFileName + "]"); return false; }
    		MOETANDERS = lid.getNumberOfDataRows();
    	    
    		
            // make network
            if( makeNetwork( mlpdto , ClassNameList ) == false ) return false;
            if( ntw == null ) return false;
            mlpdto.setNtw(ntw);
            //
            if( IsContinuation ) {
        	   if( ntwrout.mergeModels( mlpdto ) == false ) { do_error("Models could not be merged " + ntwrout.getLastErrorMessage()); return false; }
            }
            if( dumpWeightsToFile(true,0,mlpdto) == false ) return false;
            // Training loop
            if( performMiniBatch(mlpdto,ClassNameList) == false ) return false;
            //
            if( writeXMLModel( mlpdto ) == false ) return false;
            //
    	    return true;
    }
    
   
    // Maakt n lijsten van lengte M (= minibatchsize) met willekeurige pointers naar de data
    //-----------------------------------------------------------------------
    private int[][] createMiniBatches( cmcMLPDTO dto )
    {
    	//do_error( "NbrOfDataReorcs=" + NbrOfDataRecords + " Mini=" + dto.getSizeOfMiniBatch() );
    	try {
    	  int NbrOfDataRecords = dto.getNbrOfRows();
    	  if( NbrOfDataRecords <= dto.getSizeOfMiniBatch() ) dto.setSizeOfMiniBatch( NbrOfDataRecords );
    	  int TotalSize = 0;
    	  int GapSize = 0;
    	  if( (NbrOfDataRecords %  dto.getSizeOfMiniBatch()) != 0 ) { 
    	    TotalSize =  ((NbrOfDataRecords /  dto.getSizeOfMiniBatch()) + 1) * dto.getSizeOfMiniBatch();
    	    GapSize = TotalSize - NbrOfDataRecords;
    	  }
    	  else {
    		  TotalSize = NbrOfDataRecords;
    		  GapSize = 0;
    	  }
    	  // 
    	  int[] list1 = vmath.shuffleList( NbrOfDataRecords );
    	  int[] list2 = vmath.shuffleList( NbrOfDataRecords );
          //do_error( "#Data=" + NbrOfDataRecords + " #Total=" + TotalSize + " #Gap=" + GapSize + " #L1=" + list1.length + " #MBSz=" + dto.getSizeOfMiniBatch() );
    	  //
    	  int[] list = new int[ TotalSize ];  // make a list of randows and fillit so that it aligns with N x Minibatchsize
    	  for(int i=0;i<list1.length;i++) list[i] = list1[i];
    	  for(int i=0;i<GapSize;i++) list[i + NbrOfDataRecords ] = list2[i];
    	  //
    	  int NbrOfMiniBatches = TotalSize /  dto.getSizeOfMiniBatch();
    	  int[][] miniBatchPointers = new int[ NbrOfMiniBatches ][  dto.getSizeOfMiniBatch() ];
    	  for(int i=0;i<list.length;i++)
    	  {
    	    miniBatchPointers[ i /  dto.getSizeOfMiniBatch() ][ i %  dto.getSizeOfMiniBatch()] = list[i];
    	  }
    	  return miniBatchPointers;
    	}
    	catch(Exception e) {
    		do_error( xMSet.xU.LogStackTrace(e) );
    		return null;
    	}
    }
    // is called at the start of each epoch - THEREFORE data is reshuffled each time
    //-----------------------------------------------------------------------
    private boolean runMiniBatchCycle(cmcMLPDTO dto )
    {
    	if( dto.getSizeOfMiniBatch() < 5 ) {do_error( "Size of minibatches in less than 5");return false; }
    	miniBatchPointers = createMiniBatches( dto );
        if( miniBatchPointers == null ) { do_error( "Mini Batch pointer is NULL"); return false; }
        if( miniBatchPointers[0].length != dto.getSizeOfMiniBatch() ) { do_error( "Mini Batch pointer size is wrong"); return false; }
    	if( miniBatchPointers.length < 1 ) { do_error("Number of mini batches is less than 1"); return false; }
    	return true;
    }
    //-----------------------------------------------------------------------
    private boolean makeNetwork( cmcMLPDTO dto , String[] ClassNameList )
    {
    	int NbrOfLayers        = dto.getNbrOfHiddenLayers() + 3;
    	int NbrOfResultNeurons = ClassNameList.length;
    	int NbrOfInputNeurons  = dto.getNbrOfFeatures();  // aanal feaures in input - 1 voor class + 1 voor Bias node
    	int NbrOfHiddenNeurons = dto.getNbrOfNeuronsPerHiddenLayer() + 1; // dummy neuron to attach bias
    	// this is a test run - it will also set the actual minibatch size correctly on the global var miniBatchPointers
    	if( runMiniBatchCycle( dto ) == false) { do_error( "Error cycling minibatch"); return false; }
    	//
    	ntw = new neuralNetwork( NbrOfLayers , NbrOfInputNeurons , NbrOfResultNeurons , NbrOfHiddenNeurons , dto.getSizeOfMiniBatch() );
        if(  ntw.initializeNetwork(dto,ClassNameList) == false ) {
        	do_error( "Something went wrong initializing network " + ntw.getErrorMsg() );
        	return false;
        }
     	return true;
    }
    //-----------------------------------------------------------------------
    private NetworkMetricDTO consolidate( NetworkMetricDTO[] buffer , int len)
    {
    	if( buffer == null ) { do_error("Consolidated - null buffer"); return null; }
    	if( (len<0) || (len>buffer.length)) { do_error("Consolidated - lenght issues"); return null; }
        //   
    	int ndims = buffer[0].getConfusion().length;
    	NetworkMetricDTO conso = new NetworkMetricDTO( ndims , ntw.getNbrOfLayers()-3  );
    	for(int k=0;k<len;k++)
        {
    		NetworkMetricDTO curr = buffer[k];
    		if( curr == null ) { do_error("Consolidate - system error 1"); return null; }
    		if( Double.isNaN( curr.getCost()) ) { do_error("Consolidate - System error getCost"); return null; }
    		if( Double.isNaN( curr.getAccuracy()) ) { do_error("Consolidate - System error getAccuracy"); return null; }
    		if( curr.getAccuracy() > 1 ) { do_error("Consolidate - system error 3"); return null; }
    		//
    		conso.setHits( conso.getHits() + curr.getHits() );
    		conso.setRows( conso.getRows() + curr.getRows() );
            double average = (len==0) ? curr.getCost() : ((conso.getCost() * (len - 1)) +  curr.getCost())/len ; // gemiddelde in flux berekenen
    	    conso.setCost( average );
    		double dd = (double)conso.getHits() / (double)conso.getRows();
    		conso.setAccuracy( dd );
    		conso.copyLearningSpeed( buffer[k].getLearningSpeed() );
    		//
    		for(int i=0;i<ndims;i++)
    		{
    			for(int j=0 ; j<ndims ; j++)
    			{
    				conso.setConfusionValue( i , j, conso.getConfusion()[i][j] + curr.getConfusion()[i][j] );
    			}
    		}
    		//
    		int hits = 0;
    		int rows = 0;
    		for(int i=0;i<ndims;i++)
    		{
    			for(int j=0 ; j<ndims ; j++)
    			{
    				rows += conso.getConfusion()[i][j];
    				if( i == j ) hits += conso.getConfusion()[i][j];
    			}
    		}
    		//
    		double calc = (double)hits / (double)rows;
    		if( conso.getAccuracy() != calc ) {
    			do_error( "ConfH=" + hits + " ConfRows=" + rows + " ConfCalc=" + calc );
    			do_error( "bhits=" + conso.getHits() + " brows=" + conso.getRows() + " bfracc=" + conso.getAccuracy() );
       		    return null;
    		}
        }
    	//
    	return conso;
    }
    
    //-----------------------------------------------------------------------
    private cmcMatrix[] extractMiniBatchDataFromMemory( int index , cmcLargeMatrix DataAndResultsMatrix )
    {
    	if( miniBatchPointers == null ) { do_error("minibatch pointers are null"); return null; }
    	int SizeOfMiniBatch = this.miniBatchPointers[0].length;
    	if( SizeOfMiniBatch < 0) { do_error("minibatch pointer first entry has too few lines"); return null; }
    	for(int i=0;i<miniBatchPointers.length;i++)
    	{
    		if( miniBatchPointers[i].length != SizeOfMiniBatch ) { do_error("Size of minibatch not consistent" + SizeOfMiniBatch); return null; }
    	}
    	if( index < 0 ) { do_error("minibatch index less than zero"); return null; }
    	if( index >= this.miniBatchPointers.length ) { do_error("minibatch index exceeds " + this.miniBatchPointers.length ); return null; }
    	if( DataAndResultsMatrix == null ) { do_error("DataAndResultMatrix is null"); return null; }
    	// buffers
    	double[][] ddata   = new double[ SizeOfMiniBatch ][ DataAndResultsMatrix.getNbrOfCols() - 1 ]; // laaste feature (CLASS) uitsluiten
    	double[][] dresult = new double[ SizeOfMiniBatch ][ 1 ];    	
     	
    	// 
    	double[] vals = new double[ DataAndResultsMatrix.getNbrOfCols() ];
    	for(int i=0;i< ddata.length ; i++)
    	{
    	  int ptr = miniBatchPointers[  index ][ i ];    // random pointer to line in dataset
    	  vals = DataAndResultsMatrix.readRow( ptr );
    	  if( vals == null ) { do_error( "cannot read row [" + index + "] from MMF" + DataAndResultsMatrix.getLongMemoryFileName() ); return null; }
    	  for(int j=0;j<vals.length;j++)
    	  {
    	    if( Double.isNaN( vals[j] ) ) { do_error( "Row[" + index + "][" + j + "] is NAN in MMF" + DataAndResultsMatrix.getLongMemoryFileName() ); return null; }
    	    if( j != (vals.length-1) )  ddata[ i ][ j ] = vals[j];
    	                        else  dresult[ i ][ 0 ] = vals[j];
    	  }
    	}
    	cmcMatrix[] ret = new cmcMatrix[2];
     	ret[0] = new cmcMatrix( ddata );
    	ret[1] = new cmcMatrix( dresult );
        return ret;
    }
    
    
    //-----------------------------------------------------------------------
    private cmcMatrix[] extractTestDataFromMemory( cmcLargeMatrix TestAndResultsMatrix )
    {
    	if( TestAndResultsMatrix == null ) { do_error("TestAndResultMatrix is null"); return null; }
    	int SizeOfTestData = TestAndResultsMatrix.getNbrOfRows();
    	double[][] ddata   = new double[ SizeOfTestData ][ TestAndResultsMatrix.getNbrOfCols() - 1 ]; // laaste feature (CLASS) uitsluiten
    	double[][] dresult = new double[ SizeOfTestData ][ 1 ];    	
    	     	
    	// NIEUWE VERSIE
    	double[] vals = new double[ TestAndResultsMatrix.getNbrOfCols() ];
    	for(int i=0;i< ddata.length ; i++)
    	{
    	  vals = TestAndResultsMatrix.readRow( i );
    	  if( vals == null ) { do_error( "cannot read row [" + i + "] from MMF" + TestAndResultsMatrix.getLongMemoryFileName() ); return null; }
    	  for(int j=0;j<vals.length;j++)
    	  {
    	    if( Double.isNaN( vals[j] ) ) { do_error( "Row[" + i + "][" + j + "] is NAN in MMF" + TestAndResultsMatrix.getLongMemoryFileName() ); return null; }
    	    if( j != (vals.length-1) )  ddata[ i ][ j ] = vals[j];
    	                        else  dresult[ i ][ 0 ] = vals[j];
    	  }
    	}
    	cmcMatrix[] ret = new cmcMatrix[2];
     	ret[0] = new cmcMatrix( ddata );
    	ret[1] = new cmcMatrix( dresult );
        return ret;
    }
    
    
    // implements a Stochastic Gradient Descent in which the minibatches are reshuffled
    // both Batch as Minibatch supported
    // My terminology is confusing; i.e. cycles and epochs
    // the Test data is also submitted to the forward pass during each cyle
    //-----------------------------------------------------------------------
    private boolean performMiniBatch( cmcMLPDTO dto , String[] ClassNameList)
    {
      cmcLargeMatrix DataAndResultsMatrix = null;
      cmcLargeMatrix TestAndResultsMatrix = null;
      double TestedAccuracy = 0;
      try {
    	//
    	String TrainingMemoryFileName = xMSet.getARFFMemoryMappedTrainFileName( dto.getLongARFFFileName() );
        if( TrainingMemoryFileName == null ) { do_error("NULL training memory filename"); return false; }
        DataAndResultsMatrix = new cmcLargeMatrix( xMSet , logger , TrainingMemoryFileName , dto.getNbrOfRows() , dto.getNbrOfFeatures() );
        if( DataAndResultsMatrix.openLargeMatrixForRead() == false ) { do_error("Cannot open training MMF"); return false; }
        //
        String TestingMemoryFileName = xMSet.getARFFMemoryMappedTestFileName( dto.getLongARFFFileName() );
        if( TestingMemoryFileName == null ) { do_error("NULL testing memory filename"); return false; }
        TestAndResultsMatrix = new cmcLargeMatrix( xMSet , logger , TestingMemoryFileName , MOETANDERS , dto.getNbrOfFeatures() );
        if( TestAndResultsMatrix.openLargeMatrixForRead() == false ) { do_error("Cannot open test MMF"); return false; }
        //
        
    	// split data in minibatches
    	if( runMiniBatchCycle( dto ) == false) { do_error( "Error cycling minibatch II"); return false; }
    	int NbrOfMiniBatches = miniBatchPointers.length;
    	double miniBatchProportion = (double)dto.getSizeOfMiniBatch() / (double)dto.getNbrOfRows();
        //
    	if( monitor.initializeMonitor(dto, ntw.getExtendedClassNameList() , IsContinuation) == false ) return false;
    	//
    	NetworkMetricDTO[] metricBuffer = new NetworkMetricDTO[ NbrOfMiniBatches ]; 
    	int NbrOfTargetClasses = ntw.getOutputlayer().getExtendedClassNameList().length;
    	for(int i=0;i<metricBuffer.length;i++) metricBuffer[i] = new NetworkMetricDTO( NbrOfTargetClasses , dto.getNbrOfHiddenLayers() ); // +1 to accommodate for undetermined class
    	NetworkMetricDTO consolidatedMetrics = null;
    	//
    	double dropoutpercentage = 0;
    	hiddenNeuralLayer[] hl = ntw.getHiddenlayers();
    	if( hl != null ) {
    		if( hl.length >= 1 ) {
    			dropoutpercentage = hl[0].getDropOutRatio();
    		}
    	}
    	//
    	int minicounter=-1;
        int epoch = -1;
        int cycleCounter = 0;
        long cycleStart = System.nanoTime();
        long displayPeriod = System.currentTimeMillis();
        boolean forceAFirstLog = true;
        startTiming ( dto );
        while(true)
        {
        	minicounter++;
        	epoch++;
        	if( epoch >= dto.getMaximumNumberOfEpochs() ) {
                 do_error("Maximum number of epochs [" + dto.getMaximumNumberOfEpochs()  + " has been reached"); break;     
            }
            // interrupt
        	if( interrupt.gotInterrupt() ) break;
        	// MINIBATCH part
        	// when all samples have been processed (and there is more than 1 minibatch) then reshuffle the samples in the minibatch
        	if( (minicounter == NbrOfMiniBatches) && (epoch > 0) ) {
        	   cycleCounter++;
        	   minicounter=0;
        	   //
        	   long cycleElapsed = System.nanoTime() - cycleStart;
               cycleStart = System.nanoTime();
               consolidatedMetrics = consolidate( metricBuffer , NbrOfMiniBatches );
               if( consolidatedMetrics == null ) { do_error( ntw.getErrorMsg() ); return false; }
        	   //
               EpochMonitorDTO cm = new EpochMonitorDTO( epoch / NbrOfMiniBatches );
               cm.setEpochAccuracy( consolidatedMetrics.getAccuracy() );
               cm.setEpochCost( consolidatedMetrics.getCost() );
               cm.setTimeElapsed( cycleElapsed );
               cm.setConfusionMatrix( consolidatedMetrics.getConfusion() );
               cm.setLearningSpeed( consolidatedMetrics.getLearningSpeed() );
               cm.setTestAccuracy( TestedAccuracy );  // dummy
                //
               if( monitor.pushToMonitor(cm,forceAFirstLog) == false ) { do_error("Pushing to monitor"); return false; }
               forceAFirstLog = false;
               // reshuffle 
               if( NbrOfMiniBatches >= 1 ) {
        		if( runMiniBatchCycle( dto ) == false) { do_error( "Error cycling minibatch III"); return false; }
        	    if( miniBatchPointers.length != NbrOfMiniBatches ) { do_error("Error recycling minibatch"); return false; }
        	   }
        	}
        	
            //  populate the data matrix with the data for the current minibatch
        	int minipointer =  epoch % NbrOfMiniBatches;
        	cmcMatrix[] dare = extractMiniBatchDataFromMemory( minipointer , DataAndResultsMatrix );
        	if( dare == null ) { do_error("Cannot extract minibatch data"); return false; }
        	if( dare[0] == null ) { do_error("Cannot extract minibatch data for DATA"); return false; }
           	if( dare[1] == null ) { do_error("Cannot extract minibatch data for RESULT"); return false; }
            cmcMatrix datamatrix = dare[0];
        	cmcMatrix resultmatrix = dare[1];
        	//
        	       	
        	// Inject the values of the current minibatch data set in the network
        	if( ntw.initializeInputDataFast( datamatrix.getValues() ) == false ) {
             	do_error( "Initialize Input Data " + ntw.getErrorMsg() );
             	return false;
            }
            if( ntw.initializeTargetDataFast( resultmatrix.getValues() , ClassNameList ) == false ) {
             	do_error( "Initialize Target Data " + ntw.getErrorMsg() );
             	return false;
            }
            
            // DROP-OUT
            if( ntw.initializeDropOut( dropoutpercentage ) == false ) return false;
            
            // FORWARD
            if( ntw.FastForwardPropagation(true) == false ) {
            	do_error("Fast Forward propagation ->" + ntw.getErrorMsg() );
            	return false;
            }
            if( ntw.FastEvaluateOutput() == false ) {
            	do_error("Fast Forward propagation ->" + ntw.getErrorMsg() );
            	return false;
            }
           
            // Cache results
            NetworkMetricDTO currentMetric = metricBuffer[ minicounter ];
            currentMetric.setCost( ntw.getLastTotalCost() );
            currentMetric.setAccuracy( ntw.getAccuracy() );
            currentMetric.setHits( ntw.getHits() );
            currentMetric.setRows( ntw.getRowsAssessed() );
            currentMetric.copyConfusion( ntw.getConfusionMatrix() );
            currentMetric.copyLearningSpeed( ntw.getLearningRates() );
            // Update consolidated 
            consolidatedMetrics = consolidate( metricBuffer , minicounter+1 );
            if( consolidatedMetrics == null ) { do_error( ntw.getErrorMsg() ); return false; }
      
     if( System.currentTimeMillis() - displayPeriod > 2500L ) {
    	 String sl =  "[Epoch=" + cycleCounter + "][Cycles=" + epoch +  
         		"][Cost=" + String.format("%10.5f", currentMetric.getCost()).trim() + 
         		//"][CnsCst=" + String.format("%10.5f", consolidatedMetrics.getCost()).trim() +
         		"][TrainAccur=" + String.format("%10.5f", currentMetric.getAccuracy()).trim() + 
         		"][TestAcc=" + String.format("%10.5f", TestedAccuracy ).trim() + 
         		//"][Hits=" + consolidatedMetrics.getHits() + 
         		//"] [Tot=" + consolidatedMetrics.getRows() + "]" +
         		"][Dropouts=" + ntw.getDropOutInfo().length() + "]" +
                "{Grdnts=" + ntw.getLearningRateInfo() + "}";
         do_log( 1 , sl );
         displayPeriod  = System.currentTimeMillis();
     }
            // evaluate accuracy - may need to be revisited => when to stop
            //if( cycleAccuracy >= 0.995 ) break;
     
     
     
            // BACKWARD
            if( ntw.backPropagationFast( dto.getLearningRate() , miniBatchProportion , epoch ) == false ) {
            	do_error("Back propagation" + ntw.getErrorMsg() );
            	return false;
            }

            // run the test set after, after backprop (otherwize we had to swap in the minibatch data again)
            // the train accuracy and test accuracy are therefore calculated on (slighlty) differing weights, however
            // this is however deemed to be insignificant, so acceptable 
            if( minipointer == (NbrOfMiniBatches - 1) ) {
                //	
            	dare = extractTestDataFromMemory( TestAndResultsMatrix );
            	if( dare == null ) { do_error("Cannot extract TEST-DATA"); return false; }
            	if( dare[0] == null ) { do_error("Cannot extract data for TEST-DATA"); return false; }
               	if( dare[1] == null ) { do_error("Cannot extract data for TEST-RESULT"); return false; }
                datamatrix = dare[0];
            	resultmatrix = dare[1];
            	
            	// reset the data stores
            	ntw.resetInputOutputTargets( TestAndResultsMatrix.getNbrOfRows() );
            	
            	// Inject the values of the current minibatch in the network
            	if( ntw.initializeInputDataFast( datamatrix.getValues() ) == false ) {
                 	do_error( "Initialize Input Test Data " + ntw.getErrorMsg() );
                 	return false;
                }
                if( ntw.initializeTargetDataFast( resultmatrix.getValues() , ClassNameList ) == false ) {
                 	do_error( "Initialize Target Test Data " + ntw.getErrorMsg() );
                 	return false;
                }
             
                // switch all DROP-OUT nodes back on
                if( ntw.restoreDropOut() == false ) return false;
                
                // FORWARD
                if( ntw.FastForwardPropagation(false) == false ) {
                	do_error("Fast Forward propagation Test ->" + ntw.getErrorMsg() );
                	return false;
                }
                if( ntw.FastEvaluateOutput() == false ) {
                	do_error("Fast Evaluate propagation Test ->" + ntw.getErrorMsg() );
                	return false;
                }
                //
                TestedAccuracy = ntw.getAccuracy();
                
                // restore the data stores
                ntw.resetInputOutputTargets( dto.getSizeOfMiniBatch() );
            }
            dare=null;
            datamatrix=null;
            resultmatrix=null;
            
            //
            if( applyNiceFactor ) doNice( cycleStart );
           
  //System.out.println("epoch=" + epoch + "\n" + ntw.getInfo( true ));
  //if( epoch >= 1 ) break;
  
        }
        // aftercare
        dto.setEpochsPerformed(epoch + dto.getEpochsPerformed());
        
    	// Dump the stat XML
        if( monitor.pushToMonitor(null,true) == false ) { 
        	do_error("Pushing final monitor entry to monitor"); 
        	return false; 
        }
        // Dump weights
        if( dumpWeightsToFile(false,epoch,dto) == false )  {
        	do_error("Dump weights to file ->" + ntw.getErrorMsg() );
        	return false;
        }
        // update stats
        updateTiming(dto,epoch);
    	return true;
      }
      catch( Exception e ) {
    	  do_error( "Train Main Routine " + xMSet.xU.LogStackTrace(e) );
    	  return false;
      }
      finally {
    	  if( DataAndResultsMatrix != null ) {
    		  boolean ib = DataAndResultsMatrix.closeMemoryMappedFile();
    		  if( ib == false ) {
    			  do_error("Could not close Memory Mapped Matrix ");
    			  return false;
    		  }
    	  }
      }
      
    }

    //-----------------------------------------------------------------------
    private void startTiming (cmcMLPDTO mlpdto )
    {
    	  if( mlpdto == null ) return;
          int idx = -1;
          for(int i=0;i<mlpdto.getRuntimes().length;i++)
          {
        	  if( mlpdto.getRuntimes()[i][0] < 0L ) { idx = i; break; }
          }
          if( idx < 0 ) return;
          mlpdto.getRuntimes()[idx][0] =  System.currentTimeMillis();
          mlpdto.getRuntimes()[idx][1] =  -1L;
          mlpdto.getRuntimes()[idx][2] =  -1L;
    }

    //-----------------------------------------------------------------------
    private void updateTiming(cmcMLPDTO mlpdto , int nEpochs)
    {
      if( mlpdto == null ) return;
      int idx = -1;
      for(int i=0;i<mlpdto.getRuntimes().length;i++)
      {
    	  if( mlpdto.getRuntimes()[i][0] < 0L ) { idx = i; break; }
      }
      idx--;
      if( idx < 0 ) return;
      mlpdto.getRuntimes()[idx][1] = System.currentTimeMillis();
      mlpdto.getRuntimes()[idx][2] = nEpochs;
    }
    
    //-----------------------------------------------------------------------
    private void doNice(long nanostartt)
    {
    	try {
    		long elapsed = System.nanoTime() - nanostartt;
    	    Thread.sleep(100);
    	}
    	catch(Exception e ) { return; }
    }
    
    //-----------------------------------------------------------------------
    private boolean writeXMLModel(cmcMLPDTO mlpdto)
    {
     	String XMLFileName = mlpsupp.getXMLModelName( mlpdto.getLongARFFFileName() );
  		if( XMLFileName == null ) return false;
  		cmcMLPDTODAO ntwdao = new cmcMLPDTODAO( xMSet , logger );
  		boolean ib = ntwdao.writeMLPDTO( XMLFileName , mlpdto );
  		if( ib == false ) return false;
  		do_log( 1 , "Created XML Model [" + XMLFileName + "]");
  		
  		// DEBUG
  		//cmcMLPDTO junk = ntwdao.readFullMLPDTO( XMLFileName );
  		//do_error( "===> READING MLPDTO" + (!(junk==null)) );
  	
  		//
  		ntwdao=null;
  		return true;
    }
    
    //-----------------------------------------------------------------------
  	public boolean dumpWeightsToFile(boolean init , int epoch , cmcMLPDTO dto)
  	{
  		String DumpFileName = xMSet.getEpochWeightDumpFileName();
  	   if( init ) {
  			gpPrintStream dmp = new gpPrintStream( DumpFileName , xMSet.getCodePageString() );
  			dmp.println( "[File=" + dto.getLongARFFFileName()  + "]");
  			dmp.println( "[Time=" + xMSet.xU.prntStandardDateTime(System.currentTimeMillis()).toUpperCase() + "]");
  			dmp.println( ntw.getAbbreviatedInfo() );
  			
  			dmp.close();
  			return true;
  		}
  		gpAppendStream dmp = new gpAppendStream( DumpFileName , xMSet.getCodePageString() );
  		dmp.AppendIt( "[Start WeightDump]" );
  		dmp.AppendIt( "[Time=" +  xMSet.xU.prntStandardDateTime(System.currentTimeMillis()).toUpperCase() + "]");
  		dmp.AppendIt( "[epoch=" + epoch + "]" );
  		dmp.AppendIt( ntw.getAbbreviatedWeightInfo() );
  		dmp.AppendIt( "[End WeightDump]" );
  		dmp.CloseAppendFile();
  		//
  		return true;
  	}
   
  	
  	/*
  	private boolean DataIsCorrectlyPopulated( cmcMatrix m)
  	{
  		boolean ib = vrout.MatrixIsPopulated(m);
  		if( ib == false ) do_error( "DATA contains NaN values");
  		return ib;
  	}
  	*/
  	
}
