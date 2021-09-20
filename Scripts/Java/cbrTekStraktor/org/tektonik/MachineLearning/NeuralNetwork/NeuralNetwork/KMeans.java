package org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork;

import java.util.ArrayList;

import org.tektonik.MachineLearning.ARFF.ARFFCategoryCoreDTO;
import org.tektonik.MachineLearning.ARFF.ARFFCategoryLightDTO;
import org.tektonik.MachineLearning.ARFF.ARFFEnums;
import org.tektonik.MachineLearning.ARFF.ARFFLightDTO;
import org.tektonik.MachineLearning.ARFF.ARFFEnums.ARFF_TYPE;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTO;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTOCore;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPSupport;
import org.tektonik.MachineLearning.NeuralNetwork.monitor.cmcEpochMonitor;
import org.tektonik.MachineLearning.dao.cmcARFFDAOLight;
import org.tektonik.MachineLearning.dao.cmcLargeMatrix;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalStatPurpose.gpRandom;
import org.tektonik.tools.generalpurpose.gpInterrupt;
import org.tektonik.tools.generalpurpose.gpPrintStream;
import org.tektonik.tools.linearAlgebra.cmcMath;
import org.tektonik.tools.logger.logLiason;

public class KMeans {
	
	private int NTRIALS = 80;
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	
	private String LastErrorMsg = null;
	private int  K = 4;
	
	class Node {
		double DistanceToBase = Double.NaN;
		double[] DistanceToCentroid = null;
		int clusterIdx=-1;
		int prevClusterIdx=-2;
		double[] features = null;
		int NormalizedLabel = -1;
	}
	
	private Node[] nodes = null;
	private Node[] centroids = null;
	
	private Node[] prevCentroids = null;
	
	//------------------------------------------------------------
    private void do_log(int logLevel , String sIn)
	//------------------------------------------------------------
    {
       if( logger != null ) {
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
    public KMeans( cmcProcSettings is , logLiason ilog )
	//------------------------------------------------------------
    {
    	xMSet = is;
		logger=ilog;
    }
    
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
    public boolean trainMLP( String LongFileName , int NbrOfClusters)
    //------------------------------------------------------------
    {
    	    K = NbrOfClusters;
    	    if( K<1 ) return false;
    	    //
    		String TrainingFileName = LongFileName;   // 
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
            cmcMLPDTO mlpdto = new cmcMLPDTO( LongFileName );
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
            
            
            
            // data is now in mem file
            int nrows = lidto.getNbrOfDataRows();
            int ncols = lidto.getAttributeList().length;
            String MemoryFileName = xMSet.getARFFMemoryMappedTrainFileName( LongFileName );
            cmcLargeMatrix large = new cmcLargeMatrix( xMSet , logger , MemoryFileName , nrows , ncols );
            if( large.openLargeMatrixForRead() == false ) {
            	do_error("Cannot open file for reead");
            	return false;
            }
           
            
            
            // later uitbreiden zodat je kolommen kan weglaten
            // remove lastfeature
            double[][] corpus = new double[ nrows ][ ncols - 1 ];
            for(int i=0;i<nrows;i++)
            {
                //String ss="";
            	double[] row = large.readRow(i);
            	for(int j=0;j<(ncols-1);j++)
            	{
            		corpus[i][j] = row[j];
            		//ss += "[" + row[j] + "]";
            	}
            	//do_log( 1 , ""+ i + " -> " + ss );
            }
            large.closeMemoryMappedFile();
 
            NTRIALS = nrows / 2;
            int DIEPTE=25;
            int[][] stats = new int[DIEPTE][ nrows ];
            for(int z=0;z<DIEPTE;z++)
            {
            
              int[][] trials = new int[ nrows ][ NTRIALS];
              for(int i=0;i<NTRIALS;i++)
              {
                boolean ib = performKmeans(nrows, ncols, corpus);
                if( ib == false ) { i--; continue; }
                for(int j=0;j<nrows;j++) trials[j][i] = nodes[j].NormalizedLabel;
              }
            
              int[] result = new int[ nrows ];
              for(int i=0;i<nrows;i++)
              {
            	int[] turf = new int[ K ];
            	for(int j=0;j<K;j++) turf[j]=0;
            	for(int j=0;j<NTRIALS;j++)
            	{
            		int cl = trials[i][j];
            		turf[ cl ] += 1;
            	}
            	int max = turf[0];
            	int idx = 0;
            	for(int j=0;j<K;j++)
            	{
            		if( turf[j] > max ) { max = turf[j]; idx = j; }
            	}
            	result[i] = idx;
            	stats[z][i] = result[i];
              }
            }
            
            
            /*
            for(int i=0;i<nrows;i++)
            {
            	String ss="";
            	for(int j=0;j<NTRIALS;j++) ss += "[" + trials[i][j] + "]";
            	do_log( 1 , ss +  " -> " + result[i] );
            }
            */
            /*
            for(int k=0;k<K;k++)
            {
            	int clusterSize = 0;
            	int[] lines = new int[ nrows ];
            	for(int i=0;i<lines.length;i++) lines[i] = -1;
            	int zz=0;
            	for(int j=0;j<nrows;j++)
            	{
            		if( result[j] != k ) continue;
            		clusterSize++;
            		lines[zz++] = j;
            	}
            	if( k == 0) do_log( 1 , "K-means clusters");
                do_log( 1, "[#=" + clusterSize + "]");
                ArrayList<String>keys = lid.getLines( lines );
                if( keys != null ) {
                 String ss="";
                 for(int i=0;i<keys.size();i++) ss += keys.get(i) + ",";
                 do_log( 1 , "-> [" + ss + "]");
                }
            }
            */
            
            int[] cluster = new int[ nrows ];
            for(int i=0;i<nrows;i++)
            {
            	int[] turf = new int[ K ];
            	for(int j=0;j<K;j++) turf[j]=0;
            	for(int j=0;j<stats.length;j++)
            	{
            		int cl = stats[j][i];
            		turf[ cl ] += 1;
            	}
            	int max = turf[0];
            	int idx = 0;
            	for(int j=0;j<K;j++)
            	{
            		if( turf[j] > max ) { max = turf[j]; idx = j; }
            	}
                cluster[ i ] = idx;
            }
            
            String ReportName = xMSet.getSandBoxDir() + xMSet.xU.ctSlash + "kmeans.txt";
            gpPrintStream out = new gpPrintStream( ReportName , "UTF8");
            
        	int[] lines = new int[ nrows ];
        	for(int i=0;i<lines.length;i++) lines[i] = i;
            ArrayList<String>keys = lid.getLines( lines );
            if( keys == null ) return false;
            for(int i=0;i<nrows;i++)
            {
            	String ss = "" + i + " " + keys.get(i) + " {" + cluster[i] + "} ";
            	for(int j=0;j<stats.length;j++) ss += " [" + stats[j][i] + "]";
            	do_log( 1 ,  ss );
            	out.println( keys.get(i) + "\t" + (char)(cluster[i]+'A') );
            }
            out.close();
            do_log( 1 , "Report written to [" + ReportName + "]" );
            
            return true;
    }

    
    private boolean performKmeans(int nrows , int ncols , double[][] corpus )
    {
        // init
        double[] baseline = new double[ ncols ];
        for(int i=0;i<baseline.length;i++) baseline[i] = 0;
        nodes = new Node[ nrows ];
        for(int i=0;i<nrows;i++)
        {
          nodes[i] = new Node();
          nodes[i].DistanceToBase = calculateDistance( corpus[i] , baseline , ncols-1 ); 
          nodes[i].DistanceToCentroid = new double[ K ];
        }
        
        // centroids
        centroids = new Node[ K ];
        for(int i=0;i<centroids.length;i++)
        {
          centroids[i] = new Node();
          centroids[i].DistanceToBase = -1; 
          centroids[i].DistanceToCentroid = null;
        }
        // pick K random rows from the population
        gpRandom ra = new gpRandom();
        int[] candidates = ra.getSimpleRandomList( corpus.length , K );
        for(int i=0;i<centroids.length;i++)
        {
        	double[] row = corpus[ candidates[i] ];
        	centroids[i].features = new double[ row.length ];
        	for(int j=0;j<row.length;j++) centroids[i].features[j] = row[j];
        }
        
        
        int hit=0;
        int[] EndCentroids = new int[ K ];
        for(int i=0;i<K;i++) EndCentroids[i] = -1;
        for(int iter=0;iter< 1000;iter++)
        {
        	//  cluster nodes to centroid            	
        	for(int j=0;j<nrows;j++)
        	{
        	  for(int k=0;k<K;k++)
        	  {
        		  nodes[j].DistanceToCentroid[ k ] = calculateDistance( corpus[j] ,  centroids[k].features , ncols-1 ); 
        	  }
        	  double min = 99999999;
        	  int idx=-1;
        	  for(int k=0;k<K;k++)
        	  {
        		  if( k == 0 ) { min = nodes[j].DistanceToCentroid[ k ] ; idx = k; }
        		  if( min > nodes[j].DistanceToCentroid[ k ] ) {
        			  min = nodes[j].DistanceToCentroid[ k ] ; idx = k;
        		  }
        	  }
         	  nodes[j].prevClusterIdx = nodes[j].clusterIdx;
              nodes[j].clusterIdx = idx;
        	}
        	
        	// recalculate the centroid values => just mean over elke feature
        	double[][] means = new double[ K ][ corpus[0].length ];
        	double[][] count = new double[ K ][ corpus[0].length ];
        	for(int k=0;k<K;k++)
        	{
        		for(int j=0;j<means[k].length;j++ )
        		{
        			means[k][j] = 0;
        			count[k][j] = 0;
        		}
        	}
        	for(int j=0;j<nrows;j++)
        	{
        		int idx = nodes[j].clusterIdx;
        		for(int z=0;z<corpus[j].length;z++)
        		{
        			means[idx][z] += corpus[j][z];
        			count[idx][z] += (double)1;
        		}
        	}
            for(int k=0;k<K;k++)
            {
            	for(int j=0;j<means[k].length;j++)
            	{
            		if( count[ k ][j] == 0 ) { do_error("ops div 0"); return false; }
            		centroids[k].features[j] = means[k][j] / count[k][j];
            	}
            }
          
            /*
            for(int k=0;k<K;k++)
            {
            	int clusterSize = 0;
            	for(int j=0;j<nrows;j++)
            	{
            		if( nodes[j].clusterIdx != k ) continue;
            		clusterSize++;
            	}
                do_log( 1, "[#=" + clusterSize + "]");
                if( k == 0) do_log( 1 , "---");
            }
            */
            
            int afwijkingen=0;
            for(int j=0;j<nrows;j++)
        	{
        		if( nodes[j].prevClusterIdx != nodes[j].clusterIdx ) afwijkingen++;
        	}
            if( afwijkingen == 0 ) {
            	hit++;
            	if( hit == 5 ) {
            		do_log( 1 , "Clusters are stable after " + iter + " runs.");
            		break;
            	}
            }
            
            //if( iter > 20 ) break;
        }
        
        // bepaal afstand met vorige cluster en zet zo de labels
        if( prevCentroids == null ) {  // zet de eerste set aan centroids als referentie
        	prevCentroids = new Node[ K ];
        	for(int k=0;k<K;k++)
        	{
        		prevCentroids[k] = new Node();
        		prevCentroids[k].NormalizedLabel = k;
        		prevCentroids[k].features = new double[ corpus[0].length ];
        		for(int i=0;i<prevCentroids[k].features.length;i++)
        		{
        			prevCentroids[k].features[i] = centroids[k].features[i];
        		}
        	}
        }
        for(int k=0;k<K;k++)
        {
        	centroids[k].DistanceToCentroid = new double[K];
        	prevCentroids[k].clusterIdx = -1;
        }
        // bepaal welke vand huidige centroid matched met vorige
        for(int k=0;k<K;k++)
        {
        	double[] prev = prevCentroids[k].features;
        	for(int i=0;i<K;i++)
        	{
        		//System.err.println( "" + prev + " " + centroids[i].features + " " + centroids[i].DistanceToCentroid + " " + k + " " + i);
        		centroids[i].DistanceToCentroid[k] = this.calculateDistance( prev , centroids[i].features , (ncols-1) );
        	}
        }
        // welek is dichts
        for(int k=0;k<K;k++)
        {
            double min = 9999;
            int idx =-1;
            int p = 0;
            for(int i=0;i<K;i++)
            {
            	if( prevCentroids[i].clusterIdx != -1 ) continue;
            	if( p==0 ) { p++; min = centroids[k].DistanceToCentroid[i]; idx= i; }
            	if( min > centroids[k].DistanceToCentroid[i] ) {
            		min = centroids[k].DistanceToCentroid[i]; idx= i;
            	}
            }
            if( prevCentroids[idx].clusterIdx != -1 ) {
            	do_error("Already taken");
            	return false;
            }
            centroids[k].NormalizedLabel = prevCentroids[idx].NormalizedLabel;
            prevCentroids[idx].clusterIdx = k;
            for(int i=0;i<prevCentroids[idx].features.length;i++) prevCentroids[idx].features[i] = centroids[k].features[i]; 
        }
        for(int i=0;i<nrows;i++)
        {
        	nodes[i].NormalizedLabel = centroids[ nodes[i].clusterIdx ].NormalizedLabel;
        }
        
       
        return true;
    }
    
    
    private double calculateDistance( double[] one , double[] two , int width )
    {
    	if( (one == null) || (two == null) ) { do_error("one/two is null") ; return Double.NaN; }
    	//if( one.length != two.length )  { do_error("length one is not lenght two") ; return Double.NaN; }
    	if( one.length < width ) { do_error("length one is too small " + one.length + " " + width) ; return Double.NaN; }
    	if( two.length < width ) { do_error("length two is too small " + one.length + " " + width) ; return Double.NaN; }
    	double ret = 0;
    	for(int i=0;i<width;i++) 
    	{
    		ret += (one[i] - two[i]) * (one[i] - two[i]);
    	}
    	return Math.sqrt( ret );
    }
    
    
}
