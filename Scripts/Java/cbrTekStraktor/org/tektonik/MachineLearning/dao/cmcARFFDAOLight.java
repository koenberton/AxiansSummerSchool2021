package org.tektonik.MachineLearning.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.tektonik.MachineLearning.cmcMachineLearningConstants;
import org.tektonik.MachineLearning.ARFF.ARFFCategoryLightDTO;
import org.tektonik.MachineLearning.ARFF.ARFFEnums;
import org.tektonik.MachineLearning.ARFF.ARFFEnums.ARFF_TYPE;
import org.tektonik.MachineLearning.ARFF.ARFFLightDTO;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalpurpose.gpJulianDate;
import org.tektonik.tools.generalpurpose.gpPrintStream;
import org.tektonik.tools.logger.logLiason;

public class cmcARFFDAOLight {
	
	private static int MAX_NBR_ATTRIBUTES = 12500;
	private static int TICK = 250;
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
	private gpJulianDate julian = null;
	
	private int DAOnattribs=-1;
	private int DAOndatarows=-1;
	private int firstNonString = -1;
	private String RelationName=null;
	private ArrayList<String> featurelist = null;
	private int MaxNumberOfClassItems = -1;
	private String LastErrorMsg = null;
	private String relationComment=null;
	private ArrayList<String> fullcomment = null;
    private ARFFLightDTO gdto = null;
    private boolean HasQuestionMarks = false;
	private int[][] innerFrequencies = null;
	private int[] validfields = null;
	private int[] substitutes = null;
	private double[] meansbuffer = null;
	private double[] minsbuffer = null;
	private double[] maxsbuffer = null;
	private double[] stdevbuffer = null;
	private double[] linebuffer = null;
    private boolean[] skiplist = null;
   	private String ignoredFeatures = null;
   	private int ReadEvent = 0;
	
	
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
    public String getLastErrorMsg()
    {
    	return LastErrorMsg;
    }
    //------------------------------------------------------------
    public cmcARFFDAOLight(cmcProcSettings is,logLiason ilog )
    //------------------------------------------------------------
    {
    	xMSet = is;
		logger=ilog;
		julian = new gpJulianDate();
    }
    
    public int getNumberOfDataRows()
    {
     return DAOndatarows;
    }
    public int getNumberOfAttributes()
    {
     return DAOnattribs;
    }
    public ArrayList<String> getProbeList()
    {
     return featurelist;
    }
    public ArrayList<String> getFeatureList()
    {
     return featurelist;
    }
    public String getRelationName()
    {
     return RelationName;
    }
    public int getMaxNumberOfItemsOnClassNames()
    {
     return MaxNumberOfClassItems;
    }
    public String getRelationComment()
    {
    	return relationComment;
    }
    public int getFirstNonString()
    {
    	return ( firstNonString < 0) ? 0 : firstNonString;
    }
    public ArrayList<String> getFullComment()
    {
    	return fullcomment;
    }
    public String getIgnoredFeatures() {
		return ignoredFeatures;
	}
	public void setIgnoredFeatures(String ignoredFeatures) {
		this.ignoredFeatures = ignoredFeatures;
	}

	//------------------------------------------------------------
    public boolean RemoveFile(String TargetFileName )
    {
    	if( xMSet.xU.IsBestand( TargetFileName ) ) {
    		xMSet.xU.VerwijderBestand( TargetFileName );
    		if( xMSet.xU.IsBestand( TargetFileName ) ) {
    			do_error("Could not remove [" + TargetFileName + "]");
    			return false;
    		} 			
    	}
    	return true;
    }
	
    //------------------------------------------------------------
    public String getFullCommentHTMLString()
    {
    	String ret="<html>";
    	if( fullcomment == null ) return ret;
    	for(int i=0;i<fullcomment.size();i++) 
    	{
    		ret += fullcomment.get(i) + "<br>";
    	}
    	ret += "</html>";
    	return ret;
    }
    
   	//---------------------------------------------------------------------------------
   	 private String packit ( String sIn )
   	{   
   		if( sIn ==null ) return null;
   	    char[] buffer = sIn.toCharArray();
   	    String Ret="";
   	    boolean inquote=false;
   		for(int i=0;i<buffer.length;i++) 
   		{	
   		   if( buffer[i] == '\'') { inquote = !inquote; continue; }
   		   if( ((buffer[i] == ' ') || (buffer[i] == '\t')) && (inquote==true) ) { Ret += "_"; continue;}
   		   Ret += buffer[i];
   		}		
   		return Ret;
   	}
    
   	 
    //------------------------------------------------------------
 	private boolean processComment(String sLijn,boolean indata)
 	{
 	     String sl = sLijn.trim();	
 		 if( sl.startsWith("%") ) {
       	   if( !indata ) {
       		   String ss = xMSet.xU.Remplaceer(sLijn,"%","").trim();
      		   if( ss.length() > 0 ) {
      			   if( relationComment == null ) relationComment = ss; 
           		   fullcomment.add(xMSet.xU.Remplaceer(sLijn,"%",""));
      		   }
    		   return true;
       	   }
          }
          return false;	 
 	}  	 
   	 
 	//------------------------------------------------------------ 	
 	private boolean isAComment(String sLijn )
 	{
 	  if( sLijn.startsWith("%") || sLijn.startsWith("--") ) return true;
 	  return false;
 	}
 	
 	//------------------------------------------------------------ 
 	private boolean ticker()
 	{
 	   ReadEvent++;
 	   if( ReadEvent >= cmcMachineLearningConstants.MAX_ARFF_LINES ) {
   	     do_error( "Maximum number of records has been reached [" + cmcMachineLearningConstants.MAX_ARFF_LINES + "]");
   	     return false;
        }
       if( (ReadEvent % TICK) == (TICK-1)) {
    	   String ss = "";
    	   for(int i=0;i<=(ReadEvent/TICK)%32;i++) ss += ".";
    	   System.out.println(ss);
       }
       return true;
 	}
 	
 	//------------------------------------------------------------	
    public boolean performFirstPass(String FullARFFFileName , String caller )
    {
    	gdto = null;
        relationComment = null;
    	DAOnattribs=-1;
    	DAOndatarows=-1;
    	MaxNumberOfClassItems =-1;
    	fullcomment=new ArrayList<String>();
    	DAOnattribs=0;
    	DAOndatarows=0;
    	firstNonString = -1;
    	featurelist = new ArrayList<String>();
    	boolean indata=false;
        HasQuestionMarks = false;
        meansbuffer = maxsbuffer = minsbuffer = stdevbuffer =  null;
        validfields = substitutes = null;
        innerFrequencies = null;
        linebuffer=null;
        skiplist = new boolean[MAX_NBR_ATTRIBUTES];
        ignoredFeatures = "";
        ReadEvent=0;
     	BufferedReader reader=null;
        //
    	if( xMSet.xU.IsBestand( FullARFFFileName ) == false ) {
    		do_error("Cannot locate ARFF file [" + FullARFFFileName + "] 1st pass [" + caller + "]");
    		return false;
    	}
    	try {
    	 	int tmpattrib=-1;
    	    File inFile  = new File(FullARFFFileName);  // File to read from.
	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), xMSet.getCodePageString()));
	       	//
	       	String sLijn=null;
	      	while ((sLijn=packit(reader.readLine())) != null) {
	      	   if( !ticker() ) return false;
	           String sl = sLijn.toUpperCase().trim();
	           if( sl.length() == 0 ) continue;
	           //
	           if( !indata ) {
	           //
	           if( sl.startsWith("@") ) {
	        	   boolean supp=false;
	        	   if( sl.startsWith("@RELATION") ) supp=true;
	        	   if( sl.startsWith("@ATTRIBUTE") ) supp=true;
	        	   if( sl.startsWith("@DATA") ) supp=true;
	        	   if( supp == false ) {
	        		   do_error( "Unsupported command [" + sl + "]");
	        		   return false;
	        	   }
	           }
	           if( sl.startsWith("@RELATION ") ) {
	        	   RelationName = xMSet.xU.getField( sLijn.trim(), 2 , " \t");
	           }
	           // 
	           if( (sl.startsWith("@ATTRIBUTE ")) || (sl.startsWith("@ATTRIBUTE\t")) ) {
	        	   String s = xMSet.xU.getField( sLijn.trim(), 2 , " \t");
	        	   if( s != null ) {
	        		   tmpattrib++;
	        		   if( tmpattrib >= MAX_NBR_ATTRIBUTES ) { do_error("Maximum number of attributes reached [" + tmpattrib + "] - Please increase"); return false; }
		        	   skiplist[ tmpattrib ] = false; 
	        		   if( (sl.indexOf("STRING") >= 0)||(sl.indexOf("IGNORE") >= 0)) {
	        			    do_error("Feature[" + s + "][Pos=" + tmpattrib + "] IGNORED");
	        			    skiplist[ tmpattrib ] = true;
	        			    ignoredFeatures += "[" + s + "]";
	        			    continue;
	        		   }
	        		   DAOnattribs++;
	  	        	   //
	        		   featurelist.add( s );
	        		   //
	        		   if( (sl.indexOf("STRING") < 0) && (firstNonString == -1) ) firstNonString = (DAOnattribs-1);
	        		   // if a ClassNameList count the classes - will work because class must be last feature
	       	           if( sl.indexOf("{") < 0 ) continue;
	        		   if( sl.indexOf("}") < 0 ) continue;
	        		   int zz = xMSet.xU.TelDelims( sl , ',' );
	        		   if (zz > 0) zz++;
	        		   if( zz > MaxNumberOfClassItems ) MaxNumberOfClassItems = zz;	   
	        	   }
	        	   else {
	        		   do_error("Cannot extract attributename [" + sl + "]");
	        		   return false;
	        	   }
	           }
	           //
	           if( sl.startsWith("@DATA") ) { indata=true; continue; }
	           //
	           if( processComment(sLijn,indata) ) continue;   // skip if comment
	           }  // !indata
	           else {
	        	   if( isAComment(sLijn) ) continue;
	        	   DAOndatarows++;
	        	   if( (sLijn.indexOf(",?") >= 0) || (sLijn.indexOf("?,") >= 0) ) HasQuestionMarks=true;
	           }
	       	} // while
    	}
    	catch (Exception e) {
    		do_error("Could not read ARFF [" + FullARFFFileName + "]" + xMSet.xU.LogStackTrace(e));
    		return false;
    	}
    	finally {
    		try {
    		    reader.close();
    		}
    		catch(Exception e ) {
    			do_error("Could not close ARFF [" + FullARFFFileName + "]" + xMSet.xU.LogStackTrace(e));
        		return false;
    		}
    	}
    	do_log( 1 , "First pass completed [" + FullARFFFileName + "] [#Features=" + DAOnattribs + "][#Rows=" + DAOndatarows + "]" );
    	return true;
    }

    //------------------------------------------------------------
    public ARFFLightDTO performSecondPass(String FullARFFFileName)
    {
    	gdto = null;
    	meansbuffer = maxsbuffer = minsbuffer = stdevbuffer = null;
    	validfields = substitutes = null;
    	linebuffer = new double[  DAOnattribs  ];
     	try {
    	  gdto = new ARFFLightDTO( FullARFFFileName , DAOnattribs , DAOndatarows);
    	  gdto.setLongFileName(FullARFFFileName);
    	  gdto.setNbrOfLines(DAOndatarows);
    	}
    	catch(Exception e ) {
    		do_error("Initializing ARFF buffer");
    		e.printStackTrace();
    		return null;
    	}
    	int attribCount=-1;
    	int rowIndex=-1;
    	boolean indata=false;
    	ReadEvent=0;
        BufferedReader reader=null;
    	try {
    	 	File inFile  = new File(FullARFFFileName);  // File to read from.
	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), xMSet.getCodePageString()));
	       	String sLijn=null;
	       	while ((sLijn=packit(reader.readLine())) != null) {
	       	   if( !ticker() ) return null;
		       String sl = sLijn.toUpperCase().trim();
	           if( sl.length() == 0 ) continue;
	           //
	           if( !indata ) {
	           //
	           if( sl.startsWith("@RELATION") ) {
	        	   try {
	        	     gdto.setARFFName (sLijn.substring("@RELATION".length()).trim());
	        	   }
	        	   catch(Exception e ) {
	        	  		 do_error("oeps3");
		        		 e.printStackTrace();
		        		 return null;
		    	   }
	           }
	           //
	        //do_error("" + attribCount + " " + sl + " -> " + sl.startsWith("@ATTRIBUTE ") );
	           if( sl.startsWith("@ATTRIBUTE ") || (sl.startsWith("@ATTRIBUTE\t")) ) {
		        	 try {   
		        	   attribCount++;    // OK unless you want to ignore the last feature which is improbable
		    //do_error("" + attribCount + " + " + sLijn );	
		        	   if( attribCount >= gdto.getAttributeList().length ) {
		        		   boolean continueparsing = false;
		        		   if( (sl.indexOf("STRING")>=0) || (sl.indexOf("IGNORE")>=0) ) {
		        			   // STRIGN and IGNORE could be between brackets
		        			   int px = sl.indexOf("{");
		        			   if( px >= 0 ) {
		        				   if( sl.indexOf("STRING") > px ) { 
		        					   do_error("CAUTION - There is a STRING category defined in the nominal list");
		        					   continueparsing=true;
		        				   }
		        				   if( sl.indexOf("IGNORE") > px ) {
		        					   do_error("CAUTION - There is an IGNORE category defined in the nominal list");
		        					   continueparsing=true;
		        				   }
				        	   }
		        			   if( !continueparsing) continue;
		        		   }
		        		   if( continueparsing == false ) {
		        		     do_error("Too many attributes [" + attribCount + "] expected [" + gdto.getAttributeList().length + "]");
		        		     return null;		
		        		   }
		        	   }
       	    //do_error("" + attribCount + " * " + sLijn );	
		        	   StringTokenizer st = new StringTokenizer(sLijn," \t");
		        	   int idx=0;
		        	   while(st.hasMoreTokens()){
       		    		 String sElem = st.nextToken().trim();
       		//do_error("" + attribCount + " " + sElem + " " + sLijn );		   
       		    		 if( sElem.compareToIgnoreCase("@attribute") == 0) { idx =1; continue; }
		        		 if( idx == 1 ) { gdto.getAttributeList()[attribCount].setCategoryName( sElem ); idx++; continue; }
		        		 if( idx == 2 ) {
		        		   idx++;
		        		   if( sElem.compareToIgnoreCase("NUMERIC") == 0 )  gdto.getAttributeList()[attribCount].setTipe(ARFF_TYPE.NUMERIC);
		        		   else
		        		   if( sElem.compareToIgnoreCase("REAL") == 0 )  gdto.getAttributeList()[attribCount].setTipe(ARFF_TYPE.NUMERIC);
		        		   else
		        		   if( sElem.compareToIgnoreCase("INTEGER") == 0 )  gdto.getAttributeList()[attribCount].setTipe(ARFF_TYPE.NUMERIC);
			        	   else
		        		   if( (sElem.compareToIgnoreCase("STRING") == 0)||(sElem.compareToIgnoreCase("IGNORE") == 0) )  {
		        			   attribCount--;
		        			   continue;
		        		   }
		        		   else
		        		   if( sElem.compareToIgnoreCase("DATE") == 0 )  {
		        			   gdto.getAttributeList()[attribCount].setTipe(ARFF_TYPE.DATE);
		        			   gdto.getAttributeList()[attribCount].setDateFormat("yyyy-MM-dd'T'HH:mm:ss");  // default
		        		   }
		        		   else // class or nominal
		        		   if( (sElem.startsWith("{")) && (sl.indexOf("}")>0) ) {
		        			  int zdx = sLijn.indexOf('}');
		        			  sElem = sLijn.substring( sLijn.indexOf('{') , (zdx+1) );
		        			  // TRICK - CONVENTION :  only the last CLASS is a CLASS else a NOMINAL type
		        			  if( attribCount != (gdto.getAttributeList().length-1)) gdto.getAttributeList()[attribCount].setTipe( ARFF_TYPE.NOMINAL );
		        			                                                    else gdto.getAttributeList()[attribCount].setTipe( ARFF_TYPE.CLASS );
		        			  String sval = sElem.toUpperCase().trim();
		        			  if( sval.length() <= 2) {
		        				  do_error("No nominal values specified");
		        				  return null;
		        			  }
		        			  sval = sval.substring( 1 , sval.length() - 1 );
		        			  if( HasQuestionMarks ) {
		        				  if( sval.indexOf("?") < 0 ) sval += ",?";   // add the unknown nominal value
		        			  }
		        			  gdto.getAttributeList()[attribCount].setClassesDefinition(sval);
		        			  int nvals = xMSet.xU.TelDelims( sval , ',') + 1;
		        			  if( nvals < 1 ) { // 7 AUG - allowed to have 1 class
		        				  do_error("There are no commas between the classvals or there is only 1 class [" + sval + "]");
		        				  return null;
		        			  }
		        			  String[] cats = new String[nvals];
		        			  StringTokenizer sx = new StringTokenizer(sval,", \t");
		        			  int pdx=-1;
		   	        	      while(sx.hasMoreTokens()){
		   	        	    	pdx++;
		   	        	    	cats[pdx] = sx.nextToken().trim().toUpperCase();
		   	        	      }
		   	        	      gdto.getAttributeList()[attribCount].setNominalValueList(cats);
		        			  break;
		        		   }
		        		   else {
		        		    do_error("Unknown type [" + sElem + "] [" + sLijn +"]");
		        		    return null;
		        		   }
		        		   continue;
		        		  }// idx = 2
		        		  if( idx == 3 ) { // only for dates   @attribute name DATE format
		        			 if( gdto.getAttributeList()[attribCount].getTipe() == ARFF_TYPE.DATE ) {
		        				 gdto.getAttributeList()[attribCount].setDateFormat(sElem);
		        			 }
		        			 else {
		        				  if (sElem.startsWith("%") == false) {
		        					 do_error("Found a fourth item on line " + sLijn);
		        					 return null;
		        			      }
		        				  // OK
		        			 }
		        		  }
		        	   }
		        	 }
		        	 catch(Exception e ) {
		        		 do_error("2nd pass - parsing attributes");
		        		 e.printStackTrace();
		        		 return null;
		        	 }
		           }
	           
	               // start of data
	               if( sl.startsWith("@DATA") ) {
	        	     if( attribCount != (DAOnattribs-1) ) {
	        		   //do_error( dumpStructure(xa) );
	        		   do_error("2ND pass - Number of attributes does not match [#=" + attribCount + "] [Req=" + DAOnattribs + "]");
	        		   return null;
	        	     }
	        	     indata=true; 
	        	     
	        	     // zet de counters op nul
	        	     meansbuffer = new double[ DAOnattribs ];
	        	     validfields = new int[ DAOnattribs ];
	        	     substitutes = new int[ DAOnattribs ];
	        	     maxsbuffer  = new double[ DAOnattribs ];
	        	     minsbuffer  = new double[ DAOnattribs ];
	        	     stdevbuffer = new double[ DAOnattribs ];
	        	     innerFrequencies = new int[ DAOnattribs ][1];
	        	     for(int i=0;i<DAOnattribs;i++) 
	        	     {
	        	      meansbuffer[i] = (double)0;
	        	      validfields[i] = 0;
	        	      maxsbuffer[i]  = (double)0;
	        	      minsbuffer[i]  = (double)0;
	        	      stdevbuffer[i] = (double)0;
	        	      substitutes[i] = 0;
	        	      if( gdto.getAttributeList()[i].getNominalValueList() == null ) innerFrequencies[i] =  null;
	        	      else  {
	        	    	  innerFrequencies[i] =  null;
	        	    	  int nn = gdto.getAttributeList()[i].getNominalValueList().length;
	        	    	  if( nn > 0 ) {
	        	    		  innerFrequencies[i] = new int[ nn ];
	        	    		  for(int j=0;j<innerFrequencies[i].length;j++ ) innerFrequencies[i][j] = 0;
	        	    	  }
	        	      }
	        	     }
	        	     continue; 
	              }
	           } // !indata
	           else {
	            	  if( isAComment(sLijn) ) continue;
	            	  int z = parseDataLine( sLijn , rowIndex , 2);
	                  if( z < 0 ) return null;
	                  rowIndex += z;
               }
	       	} // while
	  	    if( this.DAOndatarows != (rowIndex+1) ) {
	  	    	do_error("Second pass rowcount mismatch " + DAOndatarows + " " + rowIndex );
	  	    	return null;
	  	    }
    	}
    	catch (Exception e) {
    		do_error("Could not read ARFF [" + FullARFFFileName + "]" + xMSet.xU.LogStackTrace(e));
    		e.printStackTrace();
    		return null;
    	}
    	finally {
    		try {
    		    reader.close();
    		}
    		catch(Exception e ) {
    			do_error("Could not close ARFF [" + FullARFFFileName + "]" + xMSet.xU.LogStackTrace(e));
        		return null;
    		}
    	}
    	//
    	if( gdto.getAttributeList()[ DAOnattribs - 1].getTipe() != ARFF_TYPE.CLASS ) {
    	  do_error("The concluding attribute is not a nominal [Cat=" + gdto.getAttributeList()[ DAOnattribs - 1].getCategoryName() + "][Type=" + gdto.getAttributeList()[ DAOnattribs - 1].getTipe() + "]");
    	  return null;
    	}
    	//
        // Calculate and Store MEANS, MIN, Max and FREQ
  	    for(int i=0;i<DAOnattribs;i++)
  	    {
  	      //meansbuffer[i] = meansbuffer[i] / (double)DAOndatarows;   JAN 3 - take the number of actual populated values
  	      meansbuffer[i] = meansbuffer[i] / (double)validfields[ i ];
  	      gdto.getAttributeList()[i].setMean( meansbuffer[i] );
  	      gdto.getAttributeList()[i].setMax( maxsbuffer[i] );
  	      gdto.getAttributeList()[i].setMin( minsbuffer[i] );
  	      if( innerFrequencies[i] != null ) {
  	    	  int[] list = new int[ innerFrequencies[i].length ];
  	    	  for(int j=0;j<list.length;j++ ) list[j] = innerFrequencies[i][j];
  	    	  gdto.getAttributeList()[i].setFrequencies(list);
  	      }
  	      else  gdto.getAttributeList()[i].setFrequencies(null);
  	      // report
  	      if( (double)DAOndatarows != validfields[i] ) {
  	    	do_log( 1 , "WARNING missing values on feature [" + gdto.getAttributeList()[i].getCategoryName() + "] [#Tot=" + DAOndatarows + "] [#Valid=" + (int)validfields[i] + "] will be replaced by [" + meansbuffer[i]  + "]");
  	    	if( (double)DAOndatarows < validfields[i] ) {
  	    	   	do_error("[" + gdto.getAttributeList()[i].getCategoryName() + "] [#" + DAOndatarows + "] LESS THAN [#" + (int)validfields[i] + "]" );
  	    	   	return null; // error
  	    	}
  	      }
  	    }
        //
  	    do_log( 1 , "Second pass completed");
     	return gdto;
    }

    //------------------------------------------------------------
    public ARFFLightDTO performThirdPass()
    {
    	if(gdto == null ) { do_error("NULL dto at start of third pass"); return null; }
    	int rowIndex = -1;
    	boolean indata=false;
        linebuffer = new double[  DAOnattribs ];
        BufferedReader reader=null;
        ReadEvent=0;
    	try {
    	 	File inFile  = new File(gdto.getLongFileName());  // File to read from.
	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), xMSet.getCodePageString()));
	       	String sLijn=null;
	       	while ((sLijn=packit(reader.readLine())) != null) {
	       	   if( !ticker() ) return null;
	           String sl = sLijn.toUpperCase().trim();
	           if( sl.length() == 0 ) continue;
	           if( !indata ) {
                 if( sl.startsWith("@DATA") ) {
        	       indata=true; 
        	       continue; 
                 }
	           }
	           else {
            	  if( isAComment(sLijn) ) continue;
            	  int z = parseDataLine( sLijn , rowIndex , 3);
                  if( z < 0 ) return null;
                  rowIndex += z;
               }
	       	}
	        if( this.DAOndatarows != (rowIndex+1) ) {
	  	    	do_error("Third pass rowcount mismatch " + DAOndatarows + " " + rowIndex );
	  	    	return null;
	  	    }
    	}
    	catch (Exception e) {
    		do_error("Could not read ARFF [" + gdto.getLongFileName() + "]" + xMSet.xU.LogStackTrace(e));
    		return null;
    	}
    	finally {
    		try {
    		    reader.close();
    		}
    		catch(Exception e ) {
    			do_error("Could not close ARFF [" + gdto.getLongFileName() + "]" + xMSet.xU.LogStackTrace(e));
        		return null;
    		}
    	}
    	// calculate STDEV 
        for(int i=0;i<DAOnattribs;i++)
  	    {
  	       stdevbuffer[ i ] =  Math.sqrt( stdevbuffer[ i ] / (double)validfields[ i ] );
  	       gdto.getAttributeList()[i].setStDev( stdevbuffer[i] );
  	    }
    	do_log( 1 , "Third pass completed");
    	//  
    	return gdto;
    }
    
    //------------------------------------------------------------
    public boolean assessCorrectness()
    {
    	if(gdto == null ) { do_error("NULL gdto var"); return false; }
    	if( gdto.getAttributeList() == null ) { do_error("Categories should not be null"); return false; }
    	String pattern = "%10.4f";
    	for(int i=0;i<gdto.getAttributeList().length;i++)
    	{
    	   ARFFCategoryLightDTO cat = gdto.getAttributeList()[i];
    	   if( cat == null ) { do_error("category is NULL"); return false; }
    	   //
           String ss =  "[" +
        		      cat.getCategoryName()                       + "][" + 
		  	          cat.getTipe()                               + "][Min=" +
		  	          String.format(pattern,cat.getMin()).trim()  + "][Max=" +
		  	          String.format(pattern,cat.getMax()).trim()  + "][Mean=" +
		  	          String.format(pattern,cat.getMean()).trim() + "][Stdv=" +
		  	          String.format(pattern,cat.getStDev()).trim()+ "][Subs=" + 
			          (int)substitutes[i] + "]";
           if( cat.getFrequencies() != null) {
    	     int[] list = cat.getFrequencies();
    	     for(int j=0;j<list.length;j++) {
    		   ss += "[" + cat.getNominalValueList()[j] + "=" + list[j] + "]"; 
    	     }
           }
           if( cat.getDateFormat() != null ) ss += "[DateFormat=" + cat.getDateFormat() + "]";
           do_log( 1 , ss );
           //
    	   if( cat.getCategoryName() == null ) { do_error("Category name should not be null"); return false; }
           switch( cat.getTipe() )
           {
           case NUMERIC : { 
        	   if( cat.getClassesDefinition() != null ) { do_error("Classes should be null"); return false; }
        	   if( cat.getNominalValueList() != null ) { do_error("Nominalvalues should NOT be null"); return false; }
        	   break;
           }
           case CLASS : ;
           case NOMINAL : {
        	   if( cat.getClassesDefinition() == null ) { do_error("Classes should NOT be null"); return false; }
        	   if( cat.getNominalValueList() == null ) { do_error("Nominalvalues should NOT be null"); return false; }
        	   if( cat.getFrequencies() == null ) { do_error("Frequencies should NOT be null"); return false; }
        	   if( Double.isNaN( cat.getMean()) ) { do_error("Mean is NAN"); return false; }
        	   if( Double.isNaN( cat.getMin()) ) { do_error("Min is NAN"); return false; }
        	   if( Double.isNaN( cat.getMax()) ) { do_error("Max is NAN"); return false; }
        	   if( Double.isNaN( cat.getStDev()) ) { do_error("Stdev is NAN"); return false; }
        	   //
        	   if( cat.getStDev() == 1 ) { do_log( 1, "  WARNING : Standard Deviation is 1"); }
        	   if( cat.getFrequencies() != null) {
          	     int[] list = cat.getFrequencies();
          	     ss=""; 
          	     for(int j=0;j<list.length;j++) 
          	     {
          	    	 if( list[j] == 0 ) {
          	    		  if( cat.getNominalValueList()[j].trim().compareToIgnoreCase("?") != 0) ss += "[" + cat.getNominalValueList()[j] + "]"; 
          	    	 }
          	     }
          	     if( ss.length() > 0 ) do_log(1, "  WARNING : following classes do not appear in data : {" + ss + "}");
               }
        	   break;
           }
           case DATE : {
        	   String sf = cat.getDateFormat();
        	   if( sf == null ) sf = "";
        	   if( sf.trim().length() < 4) { do_error("probably wrong DATE format [" + sf + "]"); return false; }
        	   break;
           }
           default : { do_error("ARFF type " + cat.getTipe() + " not supported"); return false; }
           }
    	}
    	//
    	{
    	String ss = getIgnoredFeatures();
    	if( ss == null ) ss = "";
    	if( ss.trim().length() > 0 ) do_log( 1 , "IGNORED : " + ss );
    	}
    	//
    	return true;
    }
    
    //------------------------------------------------------------
    private void performSubstitution(int idx, int pass ,  int rowIndex , String sElem , String sLijn )
    {
    	 if( pass == 2 ) {
    		 linebuffer[idx] = 0;
    		 validfields[idx] = validfields[idx] - 1;
    	 }
    	 //
    	 else if( pass == 3 ) {
    		 if( Double.isNaN(meansbuffer[idx]) ) {
	             do_log(1,"Something went wrong [" + sElem + "] on [" + sLijn + "] - could not transform to double [" +  gdto.getAttributeList()[ idx ].getCategoryName() + "] [Row=" + rowIndex + "] - NO VALID FIELDS");
    		 }
    		 else {
    			 linebuffer[idx] = meansbuffer[idx];
    			 substitutes[idx] = substitutes[idx] + 1;
    			 //do_log(1,"[Row=" + rowIndex +"] [" + gdto.getAttributeList()[ idx ].getCategoryName() + "] [value=" + sElem + "] -> [subst=" + linebuffer[idx] + "]" );
    		 }
    	 } 
    	 //
    	 else if( pass == 4 ) {
    		 if( Double.isNaN(meansbuffer[idx])  ) {
  			     do_log(1,"[Row=" + rowIndex +"] [" + gdto.getAttributeList()[ idx ].getCategoryName() + "] [value=" + sElem + "] -> [subst=" + linebuffer[idx] + "]");
  		     }
    		 else {
    			 linebuffer[idx] = meansbuffer[idx];
     			 //do_log(1,"[Row=" + rowIndex +"] [value=" + sElem + "] -> [subst=" + linebuffer[idx] + "]");
      	     }
         } 
    	 else {
    	   do_log(1,"Something went wrong [" + sElem + "] on [" + sLijn + "] - could not transform to double [" +  gdto.getAttributeList()[ idx ].getCategoryName() + "] [Row=" + rowIndex + "]");
	    }
    }
    
    
    // -1 is fout, 0 = ok doch niet verwerkt , 1 is verwerkt
    //------------------------------------------------------------
    private int parseDataLine( String sLijn , int rowIndex , int pass)
    {
      if( isAComment(sLijn) ) return 0;
	  try {
    	   if( rowIndex >= DAOndatarows ) {
    		   do_error("Too many rows. Expect [" + DAOndatarows + "] got [" + rowIndex + "]");
    		   return -1;	
    	   }
    	   // read each value - there must be exactly nattribs
    	   StringTokenizer st = new StringTokenizer(sLijn,",");
    	   int idx=-1;
    	   int fieldcounter=-1;
    	   double value = Double.NaN;
    	   for(int i=0;i<linebuffer.length;i++) linebuffer[i] = Double.NaN;
    	   while(st.hasMoreTokens()) {
    		   String sElem = st.nextToken().trim();
    		   fieldcounter++;
    		   if( skiplist[fieldcounter] ) continue;
    		   idx++;
    		   if( pass == 2 ) validfields[idx] = validfields[idx] + 1;
    	       if( idx >= DAOnattribs ) {
    	    	   do_error("Too many values on row [ " + rowIndex + "] [" + sLijn + "]");
    	    	   return -1;
    	       }
    	       value = Double.NaN;
    	       // store the value
    	       if( gdto.getAttributeList()[ idx ].getTipe() == ARFF_TYPE.STRING ) {
    	    	 value = Double.NaN;
    	    	 do_error( "Got a STRING type - which should have been removed from set of features");
    	    	 return -1;
    		   } // STRING
    	       else if( gdto.getAttributeList()[ idx ].getTipe() == ARFF_TYPE.NUMERIC ) {
    	    	 linebuffer[idx] = value =  xMSet.xU.NaarDoubleNAN(sElem);
    	         if( Double.isNaN(value)) {
    	    	    performSubstitution(idx, pass, rowIndex, sElem, sLijn);
    	    	    value = linebuffer[idx];
    		     }
    	       } // NUMERIC
    	       // find and store the index of the class/category
    	       else if( (gdto.getAttributeList()[ idx].getTipe() == ARFF_TYPE.CLASS) ||  
    	    		    (gdto.getAttributeList()[ idx].getTipe() == ARFF_TYPE.NOMINAL ) ) {
    	    	     int pdx=-1;
    	    	     for(int j=0;j<gdto.getAttributeList()[idx].getNominalValueList().length;j++)
    	    	     {
    	    	    	 if( sElem.compareToIgnoreCase( gdto.getAttributeList()[idx].getNominalValueList()[j] ) == 0 ) { pdx=j; break; }
    	    	     }
    	    	     if( pdx < 0 ) {
    	    	    	 do_error("Could not map [" + sElem + "] to the values in class {" + gdto.getAttributeList()[idx].getClassesDefinition() + "}  [Row=" + rowIndex + "]");
    	    	    	 return -1;
    	    	     }
    	    	     linebuffer[idx] = value = (double)pdx;
    	       } // NOMINAL
    	       else if( gdto.getAttributeList()[ idx ].getTipe() == ARFF_TYPE.DATE ) {
        	       double dd = julian.getDoubleDateTime( sElem , gdto.getAttributeList()[ idx ].getDateFormat() );
        	    	if( Double.isNaN( dd ) ) {
        	    		do_error("Cannot convert date " + sElem + " " + gdto.getAttributeList()[ idx ].getDateFormat() );
        	    		this.performSubstitution(idx, pass, rowIndex, sElem, sLijn);
        	    	    value = linebuffer[idx];
        	    	}
        	    	else {
        	    		linebuffer[idx] = value = dd;
        	    	}
        	   } // DATE
    	       else {
    	    	   do_error("SYSTEM ERROR - parsing data line - unsupported type " + gdto.getAttributeList()[ idx ].getTipe() );
    	    	   return -1;
    	       }
    	       // store value
    	       if( pass == 2 ) {
    	           meansbuffer[ idx ] += value;
    	           if( rowIndex == 0 ) {
    	        	   maxsbuffer[ idx ] = value;
    	        	   minsbuffer[ idx ] = value;
    	           }
    	           if( maxsbuffer[ idx ] < value ) maxsbuffer[ idx ] = value;
    	           if( minsbuffer[ idx ] > value ) minsbuffer[ idx ] = value;
    	           //
    	           if( innerFrequencies[ idx ] != null ) {
    	        	   int pdx = (int)value;
    	        	   int zdx = innerFrequencies[ idx ][ pdx ];
    	        	   innerFrequencies[ idx ][ pdx ] = zdx+1;
    	           }
    	       }
    	       else if( pass == 3 ){
    	           double dd =  value - meansbuffer[idx];
    	           stdevbuffer[ idx ] +=  (dd*dd);
    	       }
    	         
    	   }
    	   idx++;
    	   if( idx != DAOnattribs ) {
    		   do_error("Too few values on row [ " + rowIndex + "] [Txt=" + sLijn + "] [idx=" + idx +"] [attr=" + DAOnattribs +"]");
	    	   return -1;
    	   }
    	 }
    	 catch(Exception e ) {
      		 do_error("Parsedataline" + xMSet.xU.LogStackTrace(e));
    		 return -1;
         }
  	  
  	  return 1;
    }
    
    //------------------------------------------------------------
    public ARFFLightDTO  prepareMemoryMappedFile(ARFFEnums.DATA_FILE_TYPE dftipe)
    {
    	if(gdto == null ) { do_error("NULL dto at prepareMemoryMappedFile"); return null; }
    	// check
    	if( dftipe == ARFFEnums.DATA_FILE_TYPE.TRAINING ) {  // when TESTING mean/stde buffers are overwritten and checked separatedly
          for(int i=0;i<DAOnattribs;i++)
          {
        	if( Double.isNaN(meansbuffer[i]) ) { do_error("(prepareMemoryMappedFile) NAN on meansbuffer"); return null; }
        	if( gdto.getAttributeList()[i].getMean() != meansbuffer[i] ) { do_error("(prepareMemoryMappedFile) System error X"); return null; }
        	if( Double.isNaN(stdevbuffer[i]) ) { do_error("(prepareMemoryMappedFile) NAN on stdevbuffer"); return null; }
        	if( gdto.getAttributeList()[i].getStDev() != stdevbuffer[i] ) { do_error("(prepareMemoryMappedFile) System error XI"); return null; }
          }
    	}
        // create a new LargeMatrix / Memory Mapped File
        String MemoryFileName = null;
        switch( dftipe )
        {
         case TRAINING : { MemoryFileName = xMSet.getARFFMemoryMappedTrainFileName( gdto.getLongFileName() ); break; }
         case TESTING : { MemoryFileName = xMSet.getARFFMemoryMappedTestFileName( gdto.getLongFileName() ); break; }
         default : { do_error("unsupported data file type [" + dftipe + "]"); return null; }
        }
        if( MemoryFileName == null ) { do_error("NULL memory mapped filename"); return null; }
        //
        cmcLargeMatrix large = new cmcLargeMatrix( xMSet , logger , MemoryFileName , gdto.getNbrOfDataRows() , gdto.getAttributeList().length);
        if( large.initializeLargeMatrix() == false ) { do_error("Could not initialize memory Mapped Filebuffer"); return null;  }
        //
    	int rowIndex = -1;
    	boolean indata=false;
        BufferedReader reader=null;
        ReadEvent=0;
        linebuffer = new double[ DAOnattribs ];
    	try {
    	 	File inFile  = new File(gdto.getLongFileName());  // File to read from.
	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), xMSet.getCodePageString()));
	       	String sLijn=null;
	       	while ((sLijn=packit(reader.readLine())) != null) {
	       	   if( !ticker() ) return null;
	           String sl = sLijn.toUpperCase().trim();
	           if( sl.length() == 0 ) continue;
	           if( !indata ) {
	             if( sl.startsWith("@DATA") ) {
        	       indata=true; 
        	       continue; 
                 }
	           }       
	           else {
            	  if( isAComment(sLijn) ) continue;
            	  int z = parseDataLine( sLijn , rowIndex , 4);
                  if( z < 0 ) return null;
                  rowIndex += z;
                  //
                  for( int i=0;i<linebuffer.length;i++) 
                  {
                	  if( Double.isNaN( linebuffer[i] ) ) {
                		  do_error("NAN value on line " + rowIndex );
                		  return null;
                	  }
                	  // Normalize except last column which is the CLASS
                	  if( i != (linebuffer.length-1) ) {
                	    linebuffer[i] = (stdevbuffer[i] != 0) ? (linebuffer[i] - meansbuffer[i]) / stdevbuffer[i] : (double)0;
                	  }
                  }
                  for( int i=0;i<linebuffer.length;i++) {
                  	  if( Double.isNaN( linebuffer[i] ) ) {
                  		  do_error("NAN value on linebuffer - after normalization");
                  		  return null;
                  	  }
                   }
                   if( large.writeSequentiallyToMemoryFile(linebuffer , rowIndex ) == false ) return null;
               }
	       	} // while
	        if( this.DAOndatarows != (rowIndex+1) ) {
	  	    	do_error("Fourth pass rowcount mismatch " + DAOndatarows + " " + rowIndex );
	  	    	return null;
	  	    }
	       	//
	       	if( large.closeMemoryMappedFile() == false ) return null;
    	}
    	catch (Exception e) {
    		do_error("Could not read ARFF [" + gdto.getLongFileName() + "]" + xMSet.xU.LogStackTrace(e));
    		return null;
    	}
    	finally {
    		try {
    		    reader.close();
    		}
    		catch(Exception e ) {
    			do_error("Could not close ARFF [" + gdto.getLongFileName() + "]" + xMSet.xU.LogStackTrace(e));
        		return null;
    		}
    	}
    	
    	return gdto;
    }
    
    
    public ArrayList<String> getLines( int[] list )
    {
    	if(list == null ) { do_error("NULL list at getKeys"); return null; }
        if(gdto == null ) { do_error("NULL dto at getKeys"); return null; }
    	int rowIndex = -1;
    	boolean indata=false;
        BufferedReader reader=null;
        ReadEvent=0;
        linebuffer = new double[ DAOnattribs ];
        ArrayList<String> ret = new ArrayList<String>();
    	try {
    	 	File inFile  = new File(gdto.getLongFileName());  // File to read from.
	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), xMSet.getCodePageString()));
	       	String sLijn=null;
	       	while ((sLijn=packit(reader.readLine())) != null) {
	       	   if( !ticker() ) return null;
	           String sl = sLijn.toUpperCase().trim();
	           if( sl.length() == 0 ) continue;
	           if( !indata ) {
	             if( sl.startsWith("@DATA") ) {
        	       indata=true; 
        	       continue; 
                 }
	           }       
	           else {
            	  if( isAComment(sLijn) ) continue;
            	  int z = parseDataLine( sLijn , rowIndex , 4);
                  if( z < 0 ) return null;
                  rowIndex += z;
                  boolean found = false;
                  for(int i=0;i<list.length;i++)
                  {
                	  if( list[i] == rowIndex ) found = true;
                  }
                  if( found == true ) {
                	  int zz = sLijn.indexOf(",");
                	  String pp = sLijn.substring(0,zz);
                	  ret.add( pp );
                  }
               }
	       	} // while
	        if( this.DAOndatarows != (rowIndex+1) ) {
	  	    	do_error("Fourth pass rowcount mismatch " + DAOndatarows + " " + rowIndex );
	  	    	return null;
	  	    }
	       	//
	 	}
    	catch (Exception e) {
    		do_error("Could not read ARFF [" + gdto.getLongFileName() + "]" + xMSet.xU.LogStackTrace(e));
    		return null;
    	}
    	finally {
    		try {
    		    reader.close();
    		}
    		catch(Exception e ) {
    			do_error("Could not close ARFF [" + gdto.getLongFileName() + "]" + xMSet.xU.LogStackTrace(e));
        		return null;
    		}
    	}
    	
    	return ret;
    	
    }
    
    
  //------------------------------------------------------------
    public boolean overwriteMeanBuffer( double[] vals)
    {
    	if( vals == null ) return false;
    	if( meansbuffer == null ) return false;
    	if( meansbuffer.length != vals.length ) { do_error("overwrite buffer lenght mismatch"); return false; }
    	for(int i=0;i<vals.length;i++)
    	{
    		if( Double.isNaN( vals[i] ) ) { do_error("overwrite by NAN request"); return false; }
    		meansbuffer[i] = vals[i];
    	}
    	return true;
    }
    
    //------------------------------------------------------------
    public boolean overwriteStdevBuffer( double[] vals)
    {
    	if( vals == null ) return false;
    	if( stdevbuffer == null ) return false;
    	if( stdevbuffer.length != vals.length ) { do_error("overwrite buffer lenght mismatch"); return false; }
    	for(int i=0;i<vals.length;i++)
    	{
    		if( Double.isNaN( vals[i] ) ) { do_error("overwrite by NAN request"); return false; }
    		stdevbuffer[i] = vals[i];
    	}
    	return true;
    }
    
    //------------------------------------------------------------
    public boolean performARFFExtract( String SourceFileName , int NbrOfDataRows , String TargetFileName , int[] testrows , ARFFEnums.DATA_FILE_TYPE tipe , String comment)
    {
    	if( RemoveFile( TargetFileName ) == false ) return false;
    	BufferedReader reader=null;
    	ReadEvent=0;
    	try {
    		boolean indata = false;
    		int rowindex=-1;
    	  	String sLijn=null;
   	        File inFile  = new File(SourceFileName);  // File to read from.
	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), xMSet.getCodePageString()));
	        gpPrintStream tgt = new gpPrintStream( TargetFileName , "ASCII");
	        int expected = ( tipe == ARFFEnums.DATA_FILE_TYPE.TRAINING ) ? NbrOfDataRows - testrows.length : testrows.length;
	        int counter =0;
	        boolean[] validation = new boolean[ testrows.length ];
	        for(int i=0;i<validation.length;i++) validation[i]=false;
	       	while ((sLijn=reader.readLine()) != null) {
	       		if( !ticker() ) return false;
	       		String sl = sLijn.trim().toUpperCase();
	       		if( sl.length() == 0 ) continue;
	       		if( !indata ) {
	       	         if( sl.startsWith("@DATA") ) {
	       	        	 indata=true;
	       	        	 tgt.println("%");
	       	        	 tgt.println("% ARFF Splitted [Type=" + tipe + "] "  +xMSet.xU.prntStandardDateTime(System.currentTimeMillis()));
	       	        	 tgt.println("% " + comment );
	       	        	 tgt.println("%");
	       	         }
	       			 tgt.println(sLijn);
	     	       }
		           else {
		        	   if( isAComment(sl) ) continue;
		        	   rowindex++;
		        	   boolean found=false;
		        	   for(int j=0;j<testrows.length;j++) 
		        	   {
		        		   if( rowindex == testrows[j] ) {
		        			   validation[j] = true;
		        			   found = true; 
		        			   break; 
		        		   }
		        	   }
		        	   if(  (tipe == ARFFEnums.DATA_FILE_TYPE.TRAINING  ) && (found==true) ) continue;
		        	   if(  (tipe == ARFFEnums.DATA_FILE_TYPE.TESTING  ) && (found==false) ) continue; 
		        	   counter++;
		        	   tgt.println(sLijn);
          	       }    
	       	}
	       	tgt.close();
	       	do_log( 1 , "Created [" + TargetFileName + "] [#Rows=" + counter + "][#tst=" + testrows.length + "]");
	       	// Check whether all requested rows have been found and written
	       	for(int i=0;i<testrows.length;i++)
	       	{
	       		if( !validation[i]  ) {
	       			do_error("Could not find Row [" + testrows[i] + "] " + tipe + " [#Requested=" + expected + "][#Tot=" + NbrOfDataRows + "][#Tst=" + testrows.length + "]"); 
	       			return false;
	       		}
	       	}
	       	if( counter != expected ) {
	       		do_error("Mismatch " + tipe + " [#Counted=" + counter + "] [#Requested=" + expected + "][#Tot=" + NbrOfDataRows + "][#Tst=" + testrows.length + "]");
	       		return false;
	       	}
	  }
    	catch (Exception e) {
    		do_error("Could not read ARFF [" + SourceFileName + "]" + xMSet.xU.LogStackTrace(e));
    		return false;
    	}
    	finally {
    		try {
    		    reader.close();
    		}
    		catch(Exception e ) {
    			do_error("Could not close ARFF [" + SourceFileName + "]" + xMSet.xU.LogStackTrace(e));
        		return false;
    		}
    	}       	  
    	return true;
    }    
    
    
    
    /*
    // DEBUG
    //------------------------------------------------------------
    public cmcMatrix getDataMatrix()
    {
    	  String MemoryFileName = xMSet.getARFFMemoryMappedFileName( gdto.getLongFileName() );
          if( MemoryFileName == null ) { do_error("NULL memory filename"); return null; }
          cmcLargeMatrix large = new cmcLargeMatrix( xMSet , logger , MemoryFileName , gdto.getNbrOfDataRows() , gdto.getAttributeList().length);
          if( large.openLargeMatrix() == false ) { do_error("cannot open"); return null; }
          double[][] val = new double[ gdto.getNbrOfDataRows() ][ gdto.getAttributeList().length-1 ];
      	  for(int i=0;i<gdto.getNbrOfDataRows();i++)
      	  {
      		double[] ret = large.readRow(i);
      		if( ret ==  null ) { do_error("reading " + i); return null; }
      		for(int j=0;j<ret.length-1;j++) val[i][j] = ret[j];
      	  }
      	  large.closeMemoryMappedFile();
          cmcMatrix z = new cmcMatrix( val );      
          return z;
    }
    //-- DEBUG ----------------------------------------------------------
    public cmcMatrix getResultMatrix()
    {
    	  String MemoryFileName = xMSet.getARFFMemoryMappedFileName( gdto.getLongFileName() );
          if( MemoryFileName == null ) { do_error("NULL memory filename"); return null; }
          cmcLargeMatrix large = new cmcLargeMatrix( xMSet , logger , MemoryFileName , gdto.getNbrOfDataRows() , gdto.getAttributeList().length);
          if( large.openLargeMatrix() == false ) { do_error("cannot open"); return null; }
          double[][] val = new double[ gdto.getNbrOfDataRows() ][ 1 ];
      	  for(int i=0;i<gdto.getNbrOfDataRows();i++)
      	  {
      		double[] ret = large.readRow(i);
      		if( ret ==  null ) { do_error("reading " + i); return null; }
      		val[i][0] = ret[ret.length-1];
      	  }
      	  large.closeMemoryMappedFile();
          cmcMatrix z = new cmcMatrix( val );      
          return z;
    }
    */
    	
   
}
