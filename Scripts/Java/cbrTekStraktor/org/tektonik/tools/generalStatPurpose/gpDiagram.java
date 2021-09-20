package org.tektonik.tools.generalStatPurpose;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import org.tektonik.tekStraktor.drawing.cmcDrawPixelSupport;
import org.tektonik.tekStraktor.drawing.cmcDrawPixelText;
import org.tektonik.tekStraktor.model.cmcProcConstants;
import org.tektonik.tekStraktor.model.cmcProcEnums;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalImagePurpose.color.gpColor;
import org.tektonik.tools.linearAlgebra.cmcMath;
import org.tektonik.tools.logger.logLiason;

public class gpDiagram {

	cmcProcSettings xMSet=null;
	logLiason logger=null;
	private cmcDrawPixelText drwtxt = null;
	private gpColor colrout = null;
	private cmcDrawPixelSupport drw = null;
	private cmcMath matrout = null;
	
	private static int TILE_WIDTH     = 700;
	private static int TILE_HEIGTH    = 350;
	private static int RECTANGLE_SIDE = TILE_HEIGTH < TILE_WIDTH ? TILE_HEIGTH : TILE_WIDTH;
	private static int CORRELATION_LEVELS            = 16;
	private static double CORRELATION_SIDE_AMPLIFIER = 1.90;
	
	private int TileBackGround = cmcProcConstants.WIT;
	private Color TextColor = Color.BLACK;
	private int LineColor = cmcProcConstants.ZWART;
	private cmcProcEnums.SCATTER_DOT_TYPE ScatterDotType = cmcProcEnums.SCATTER_DOT_TYPE.RECTANGLE;
	private int ScatterDiameter = 3;
	private cmcProcEnums.ColorSentiment colorsentiment = cmcProcEnums.ColorSentiment.HARSH;
	private cmcProcEnums.ColorSentiment histocolorsentiment = cmcProcEnums.ColorSentiment.SOFT;
		
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
    public gpDiagram(cmcProcSettings is,logLiason ilog)
    //--------------------------
    {
      xMSet = is;
      logger = ilog;
  	  colrout = new gpColor();
  	  drw = new cmcDrawPixelSupport();
	  drwtxt = new cmcDrawPixelText(xMSet,logger);
	  matrout = new cmcMath();
	  //
	  TextColor = colrout.ColorLabel2Color("Dark Slate Gray");
	  LineColor = colrout.ColorLabel2Int("gainsboro");
    }
   
    public void setBackGroundColor(Color col)
    {
    	 TileBackGround = col.getRGB();
    	 drwtxt.setBackGroundColor( col );
    	 //do_log( 9 , "Setting [" + col + "]");
    }
    public Color getBackGroundColor()
    {
    	return new Color( TileBackGround );
    }
    public int getTileWidth()
    {
    	return TILE_WIDTH;
    }
    public int getTileHeigth()
    {
    	return TILE_HEIGTH;
    }
    public int getRectangleSide()
    {
    	return RECTANGLE_SIDE;
    }
    public int getCorrelationWidth()
    {
    	return (int)((double)RECTANGLE_SIDE * (double)CORRELATION_SIDE_AMPLIFIER);
    }
    public int getCorrelationHeigth()
    {
    	return (int)((double)getCorrelationWidth() * (double)0.6);
    }
    private int getTileHorizontalMargin()
    {
    	return TILE_WIDTH / 20;
    }
    private int getTileVerticalMargin()
    {
    	return TILE_HEIGTH / 20;
    }
    public cmcProcEnums.SCATTER_DOT_TYPE getScatterDotType()
    {
    	return ScatterDotType;
    }
    public void setScatterDotType( cmcProcEnums.SCATTER_DOT_TYPE tipe )
    {
    	ScatterDotType = tipe;
    }
    public void setScatterDotDiameter(int dia)
    {
    	int olddia = ScatterDiameter;
    	if( dia < 0 ) ScatterDiameter -=2;
    	else
    	if( dia > 100 ) ScatterDiameter += 2;
    	else ScatterDiameter = dia;
    	if( ScatterDiameter < 1 ) ScatterDiameter = olddia;
    	if( ScatterDiameter > 20) ScatterDiameter = olddia;
    	//do_log( 1 , "DIAMETER " + ScatterDiameter );
    }
    public cmcProcEnums.ColorSentiment getColorSentiment()
    {
    	return colorsentiment;
    }
    public void setColorSentiment(cmcProcEnums.ColorSentiment cos)
    {
    	colorsentiment = cos;
    }
    
    //------------------------------------------------------------
    private void doHorizontalGridLines(int[] canvas , int width , int dx , int dy , int dwidth , int dheight , int nlines)
    //------------------------------------------------------------
    {
         if ( nlines < 1 ) return;
         int gapSize = (int)Math.round((double)dheight / (double)(nlines + 1));
         for(int i=1;i<=nlines;i++)
         {
        	int curHeight = (i * gapSize) + dy;
        	drw.doLine( canvas ,  width ,  dx , curHeight , dx + dwidth , curHeight , LineColor );
         }
    }
    
    //------------------------------------------------------------
    public int[] makeHistogram( gpDiagramDTO dto , boolean squared)
    //------------------------------------------------------------
    {
    	int diagram_width = TILE_WIDTH;
    	int diagram_heigth = TILE_HEIGTH;
    	int bargap = 10;
    	cmcProcEnums.ColorSentiment barcolor = histocolorsentiment;
    	if( squared ) {
    		diagram_width = diagram_heigth = RECTANGLE_SIDE;
    		bargap = 0;
    		barcolor = colorsentiment;
    	}
    	//  
    	try  {
    	  if( dto == null ) return null;
    	  // SUBHISTO = [  number of bins ] [ number of classes ]
     	  if( dto.getHisto() == null ) return null;
     	  int nbins = dto.getHisto().length;
     	  if( nbins < 0 ) { do_error( "Empty"); return null; }
     	  int nclasses = dto.getClassNameList().length;
     	  int sampleTotal = 0;
     	  for( int i=0; i<dto.getHisto().length ; i++)
     	  {
     		 if( dto.getHisto()[i].length != nclasses ) { do_error("Number of classes differs"); return null; }
     		 for(int j=0;j<nclasses;j++) sampleTotal += dto.getHisto()[i][j];
     	  }
     	 
    	  //
    	  int[] tile = new int[ diagram_heigth * diagram_width ];
    	  for(int i=0;i<tile.length;i++) tile[i] = TileBackGround;
    	  int histoWidth = diagram_width - ( 2 * getTileHorizontalMargin());
    	  int histoHeigth = diagram_heigth - ( 2 * getTileVerticalMargin());
    	
    	  Font f = xMSet.getPreferredFont();
    	  drwtxt.drawPixelText(f, TextColor, 5 , 5 , tile, diagram_width, dto.getxLabel() );
    	  // Axis
    	  drw.doLine( tile , diagram_width , 0 , histoHeigth , histoWidth , histoHeigth , LineColor );
    	 
    	  // width of the histobar
    	  int bWidthWide =  histoWidth / nbins;
    	  int bWidthSmall = bWidthWide - bargap;   
    	  // 
          int categoryMax=0;
          for(int i=0;i<dto.getHisto().length;i++)
          {
        	  int subMax=0;
        	  for(int j=0;j<dto.getHisto()[i].length;j++) subMax += dto.getHisto()[i][j];
        	  if( subMax > categoryMax) categoryMax = subMax;
          }
          categoryMax = (int)((double)categoryMax * (double)1.25);   // take a bigger portion
          int quarter = (int)((double)histoHeigth/ (double)5);
          /*
          int divi=quarter;
          for(int i=1;i<5;i++)
          {
        	  drw.doLine( tile , diagram_width , 0 , histoHeigth-divi , histoWidth , histoHeigth-divi , LineColor ); 
        	  divi += quarter;
        	  if( quarter >= histoHeigth)break;
          }
          */
          doHorizontalGridLines( tile , diagram_width , 0 , 0 , histoWidth , histoHeigth , 4 );
          //
          int minX=-1;
          int minY=100000;
          int minVal=-1;
    	  for(int z=0 ; z<nbins ; z++)
    	  {
    		int barX = (z * bWidthWide ) + (bargap/2);
    	    drw.doVerticalLine( tile , diagram_width , barX + (bWidthSmall/2), histoHeigth , histoHeigth+5 ,  LineColor);	  
    	    int hoffset=0;
    	    for(int j=0;j<nclasses;j++)
    	    {
    	       double dh = ( (double)dto.getHisto()[z][j] / (double)categoryMax ) * histoHeigth;
    	       if( dh > (double)histoHeigth ) dh = histoHeigth;
    	       if( dh < 1 ) continue;
    	       drw.doVerticalBar( tile , diagram_width , barX , histoHeigth - hoffset , bWidthSmall , (int)dh , colrout.ColorSentiment2Int(j,barcolor) );
    	       hoffset += (int)dh;
    	       if( (histoHeigth - hoffset) < minY )  {
    	    	   minY = histoHeigth - hoffset;
    	    	   //minX = barX;
    	    	   minX = barX + ((bWidthSmall+bargap)/2);
    	    	   minVal = z;
    	       }
    	    }
    	  }
    	  int subtot=0;
    	  for(int j=0;j<nclasses;j++) subtot += dto.getHisto()[minVal][j];
    	  subtot =   (int)((double)subtot * 100 / (double)sampleTotal);
    	  minY -= 15;
    	  if( minY > 0) drwtxt.drawPixelTextCenter(f, TextColor, minX  , minY + 5 , tile , diagram_width , "%"+subtot );
    	  
    	  // move everything to the right and down
    	  return tile;
    	}
    	catch( Exception e) {
    		do_error("makeHistogram - Oops " + xMSet.xU.LogStackTrace(e));
    		return null;
    	}
    }
    
    //------------------------------------------------------------
    public int[] drawScatterPlot( gpDiagramDTO dto )
    //------------------------------------------------------------
    {
    	 if( dto.getXvals() == null ) return null;
    	 if( dto.getYvals() == null ) return null;
    	 if( dto.getResults() == null ) return null;
    	 if( dto.getXvals().length != dto.getYvals().length ) return null;
    	 if( dto.getXvals().length != dto.getResults().length ) return null;
    	 int nvals = dto.getXvals().length;
    	 //
    	 int[] tile = new int[ RECTANGLE_SIDE * RECTANGLE_SIDE ];
  	     for(int i=0;i<tile.length;i++) tile[i] = TileBackGround;
  	     int horzMargin = (int)((double)RECTANGLE_SIDE * 0.12);
  	     int vertMargin = (int)((double)RECTANGLE_SIDE * 0.12);
  	     int scatterWidth  = RECTANGLE_SIDE - (2 * horzMargin);
  	     int scatterHeigth = RECTANGLE_SIDE - (2 * vertMargin);
  	     if( scatterWidth > scatterHeigth ) scatterWidth = scatterHeigth; 
  	                                   else scatterHeigth = scatterWidth;
  	     //
  	     if( doRescaleAndSort( dto ) == false ) return null;
  	     // 
  	     for(int i=0;i<nvals;i++)
	     {
  	     	 int x = (int)Math.round( dto.getXvals()[i] * scatterWidth) + horzMargin;
	    	 int y = RECTANGLE_SIDE - ((int)Math.round( dto.getYvals()[i]  * scatterHeigth) + vertMargin);  // Y0 is top of page
	    	 try {
	    	  switch( ScatterDotType )
	    	  {
	    	  case FILLED_RECTANGLE : {  drw.drawFilledRectangleFromCenter( tile , RECTANGLE_SIDE , x , y , ScatterDiameter , colrout.ColorSentiment2Int( (int)dto.getResults()[i] , colorsentiment ) ); break; }
	 	      case RECTANGLE : { drw.drawRectangleFromCenter( tile , RECTANGLE_SIDE , x , y , ScatterDiameter , colrout.ColorSentiment2Int( (int)dto.getResults()[i] , colorsentiment) ); break;}
	    	  case CIRCLE : {  drw.drawCircleFromCenter( tile , RECTANGLE_SIDE , x , y , ScatterDiameter , colrout.ColorSentiment2Int( (int)dto.getResults()[i] , colorsentiment) ); break; }
	    	  case CROSS : {  drw.drawCrossFromCenter( tile , RECTANGLE_SIDE , x , y , ScatterDiameter , colrout.ColorSentiment2Int( (int)dto.getResults()[i] , colorsentiment) ); break; }
		      default : { do_error("Unsupported scatter type"); return null; }
	    	  }
	    	 }
	    	 catch(Exception e) {
	    	   do_log( 1 , "OOB xd" + dto.getXvals()[i] + " yd" + dto.getYvals()[i] + " x" + x + " y" + y );
	    	 }
	     }
    	 // 
	     drw.drawRectangleFromCenter( tile  , RECTANGLE_SIDE , horzMargin + (scatterWidth/2) , vertMargin + (scatterHeigth/2) , scatterWidth + vertMargin , LineColor );
	     //  
	     Font f = xMSet.getPreferredFont();
	     drwtxt.drawPixelTextCenter(f, TextColor, RECTANGLE_SIDE/2 , RECTANGLE_SIDE - 10 , tile, RECTANGLE_SIDE , dto.getxLabel() );
		 drwtxt.drawPixelTextCenterFlip(f, TextColor, 10 , RECTANGLE_SIDE/2 , tile, RECTANGLE_SIDE , dto.getyLabel() );
		 
	     // Ticks
	     // todo
		 
		 // curve requested
		 
		 double[][] curve = dto.getDoubleArray();
		 if( curve != null ) {
			 do_error( " SCTTER _ CURVES NOT LONGER SUPPORTED  " + curve.length );
			 /*
		     if( curve[0].length == 2 ) {
		       for(int i=0;i< curve.length; i++ )
		       {
		    	 try {
		   	   	   //int x = (int)Math.round( curve[i][0] * scatterWidth) + horzMargin;
		    	   //int y = RECTANGLE_SIDE - ((int)Math.round( curve[i][1] * scatterHeigth) + vertMargin);  // Y0 is top of page
		    	   //do_log( 1 , "" + curve[i][0] + " " + curve[i][1] + " " + x + " " + y);
		    	   
		    	   double xrescale = (curve[i][0] - minx) / spanx;
		  	       double yrescale = (curve[i][1] - miny) / spany;
		  	       int x = (int)Math.round( xrescale * scatterWidth) + horzMargin;
			       int y = RECTANGLE_SIDE - ((int)Math.round( yrescale * scatterHeigth) + vertMargin);  // Y0 is top of page
			     
		    	   doDot( tile , RECTANGLE_SIDE , x , y , cmcProcConstants.ZWART );
		    	 }
		    	 catch( Exception e) { ; } 
		       }
		      }
		      */
		 }
		 
	     //		 
    	 return tile;
    }
   
    //------------------------------------------------------------
    public int[] drawCorrelationDiagram( gpDiagramDTO dto )
    //------------------------------------------------------------
    {
    	int CORRELATION_WIDTH = getCorrelationWidth();
    	int CORRELATION_HEIGTH = getCorrelationHeigth();
    	Color drawColor = Color.BLUE;
    	
    	double[][] dvals = dto.getDoubleArray();
    	if( dvals == null ) return null;
    	int ndims = dvals.length;
    	if( ndims < 1) return null;
    	if( ndims != dvals[0].length ) return null;
    	boolean showVerticalLabels = true;
   		String[] vertLabels = dto.getVerticalTickLabels();
   		if( vertLabels == null ) showVerticalLabels=false;
   		if( vertLabels.length != ndims ) showVerticalLabels=false;
   		
   	    //
    	double portion = 0.12;
   	    int[] tile = new int[ CORRELATION_WIDTH * CORRELATION_HEIGTH ];
 	    for(int i=0;i<tile.length;i++) tile[i] = TileBackGround;
 	    int horzMargin = (int)((double)CORRELATION_WIDTH * portion);
 	    int vertMargin = (int)((double)CORRELATION_HEIGTH * portion);
 	    int scatterWidth  = CORRELATION_WIDTH - (2 * horzMargin);
 	    int scatterHeigth = CORRELATION_HEIGTH - (2 * vertMargin);
 	    if( scatterWidth > scatterHeigth ) scatterWidth = scatterHeigth; 
 	                                  else scatterHeigth = scatterWidth;
 	   
 	    double dmin = Double.NaN;
 	    double dmax = Double.NaN;
 	    for(int i=0;i<ndims;i++)
 	    {
 	    	for(int j=0;j<ndims;j++)
 	    	{
 	    	  if( Double.isNaN( dvals[i][j]) ) continue;
 	    	  if( Double.isNaN(dmin)) dmin = dmax = dvals[i][j];
 	    	  if( dmin > dvals[i][j] ) dmin = dvals[i][j];
 	    	  if( dmax < dvals[i][j] ) dmax = dvals[i][j];
	    	}
 	    }
 	    do_log( 1 , "Min=" + dmin + " Max=" + dmax );  
 	    double spread = dmax - dmin;
 	    if( spread == 0 ) { do_error( "There is no difference between minimum and maximum - No numerics?") ; return  null; }
 	    double[][] dscore = new double[ ndims ][ ndims ];
 	    for(int i=0;i<ndims;i++)
	    {
	    	for(int j=0;j<ndims;j++)
	    	{
	    	  if( Double.isNaN( dvals[i][j]) ) { dscore[i][j] = Double.NaN; continue; }
	    	  dscore[i][j] = Math.round(  (( dvals[i][j] - dmin ) / spread) * CORRELATION_LEVELS );
	    	}
	    }
 	    //
 	    /*
 	    String sl="";
 	    for(int i=0;i<ndims;i++)
	    {
 	    	sl += "dim=" + i + "   -> ";
 	    	for(int j=0;j<ndims;j++) sl += "[" + dscore[i][j] + "]";
 	    	sl += "\n";
	    	
	    }
 	    do_log( 1 , sl );
 	    */
 	    //
 	    
 	    Font f = xMSet.getPreferredFont();
 	    int step = (int)(scatterWidth / ndims);
 	    int x = 0 - step;
 	    for(int i=0;i<ndims;i++)
	    {
	    	x += step;
	    	int y = 0 - step;
	    	for(int j=0;j<ndims;j++)
	    	{
	    		y += step;
	    		int shade = cmcProcConstants.ZWART;
	    		if( Double.isNaN(dscore[i][j]) ) shade = cmcProcConstants.WIT;
	    		else shade = colrout.getColorGradient( drawColor ,(int)dscore[i][j] , CORRELATION_LEVELS);
	    		
	    		try {
	    		  drw.doVerticalBar( tile , CORRELATION_WIDTH , x + horzMargin ,  y+step+vertMargin , (step-2) , (step-2) , shade );
	    		  if((i == 0) && (showVerticalLabels)) drwtxt.drawPixelText(f, TextColor, horzMargin + scatterWidth + 8  , y+(step/2)+vertMargin  , tile , CORRELATION_WIDTH , ""+j + " " + vertLabels[j]);
	    		}
	    		catch(Exception e ) {
	    			do_error( "oops" + e.getMessage() );
	    		}
	    	}
	 		drwtxt.drawPixelTextCenterFlip(f, TextColor, horzMargin + x + (step/2) , vertMargin + scatterHeigth + (step/2)  , tile , CORRELATION_WIDTH , ""+i );
	 		    
	    }
 	    //doHorizontalLine( tile , CORRELATION_WIDTH , 0 , 0 , CORRELATION_WIDTH-1 , cmcProcConstants.WIT);
	    //doHorizontalLine( tile , CORRELATION_WIDTH , CORRELATION_HEIGTH-1 , 0 , CORRELATION_WIDTH-1 , cmcProcConstants.WIT);
 	   
	    // shaded bar
	    step = (int)(Math.round((double)scatterHeigth / (double)(CORRELATION_LEVELS)));
	    int y = 0 - step;
	    for(int i=0;i<CORRELATION_LEVELS;i++)
	    {
	      y += step;
	      int shade = colrout.getColorGradient( drawColor , CORRELATION_LEVELS - i , CORRELATION_LEVELS);
  		  drw.doVerticalBar( tile , CORRELATION_WIDTH , 2 ,  y+step+vertMargin , 20 , step , shade );
  		  if( (i== 0) || (i==(CORRELATION_LEVELS-1))) {
  		   drwtxt.drawPixelText(f, TextColor, 25  ,  y+vertMargin+(step/2)  , tile , CORRELATION_WIDTH , (i==0)?"Hi":"Lo");
  		  }
  	    }
    	return tile;
    }
    
    //------------------------------------------------------------
    public int[] dumpArrayToImage(gpDiagramDTO dto , int width , int height , Color background , int ActualRecordsCreated )
    {
    	 int CAP = 30;
    	 int START_GRADIENTS = 4;
    //boolean SHOW_TEST = true;
    	 //
    	 drwtxt.setBackGroundColor( background );
         //
     	 if( dto.getXvals() == null ) return null;
       	 if( dto.getYvals() == null ) return null;
       	 if( dto.getResults() == null ) return null;
         if( dto.getXvals().length != dto.getYvals().length ) return null;
    	 if( dto.getXvals().length != dto.getResults().length ) return null;
       	 int nvals = dto.getXvals().length;
       	 //
       	 if( dto.getDoubleArray() == null ) { do_error("no double array"); return null; }
       	 if( dto.getDoubleArray().length != nvals ) { do_error("double array nvals"); return null; } // [ N ][ vrij aantal ]
         //
         int iBackground = colrout.Color2Int(background);
         int curvecolor  = colrout.ColorLabel2Int("Dark gray");
         int AccurColor2 = colrout.ColorLabel2Int("Salmon");
       	 int AccurColor1 = colrout.ColorLabel2Int("Tomato");
       	 int TestColor2  = colrout.ColorLabel2Int("Plum");
       	 int TestColor1  = colrout.ColorLabel2Int("Purple");
         int CostColor2  = colrout.ColorLabel2Int("LightSkyBlue");
         int CostColor1  = colrout.ColorLabel2Int("SteelBlue");
         int SpeedColor1 = colrout.ColorLabel2Int("DarkSeaGreen");
         int TextColor   = colrout.ColorLabel2Int("SlateGray");
       	 //
         int[] tile = new int[ width * height ];
         for(int i=0;i<tile.length;i++) tile[i] = iBackground;
         //
         int horzMargin = (int)((double)width * 0.07);
         int vertMargin = (int)((double)height * 0.07);
         int drawAreaWidth = width - (2*horzMargin);
         int drawAreaHeigth = height - (2*vertMargin);
         //
         if( doRescaleAndSort( dto ) == false ) return null;
         //
         int NbrOfCurves = dto.getDoubleArray()[0].length;  // ==>  X - ACCURACY - COST - TEST - Gradients(#=>#hidden layers)
         // Running averages
         double[][] running = new double[ nvals ][ NbrOfCurves ];
	     for(int curve=1;curve<NbrOfCurves;curve++)
    	 {
	    	 double[] rtemp = new double[nvals];
	         for(int i=0;i<nvals;i++) rtemp[i] = dto.getDoubleArray()[i][curve];
	         //if( (ActualRecordsCreated > CAP) && (curve>=3) ) for(int i=0;i<CAP;i++) rtemp[i] = rtemp[CAP];
	         
	         // Logaritmische schaal
	         if( curve >= START_GRADIENTS) for(int i=0;i<CAP;i++) rtemp[i] = Math.log10( rtemp[i] );
	         //
	         //lastSpeed = rtemp[ActualRecordsCreated-1];
	         double[] ruav = matrout.runningAverageSelected( rtemp , 32 , ActualRecordsCreated );
	         if( ruav == null ) {
	        	 do_error( "cannot calculate running average");
	        	 return null;
	         }
	         running[curve] = new double[ nvals ];
	         for(int i=0;i<nvals;i++) running[curve][i] = 1;
	         for(int i=0;i<ActualRecordsCreated;i++) running[curve][i] = ruav[i];
	     }
	     //
	     int prevX=-1;
	     int[] prevY = new int[ NbrOfCurves ];
	     for(int tipe=0;tipe<2;tipe++)   // 1=actual - 2=running averages van de actuals
	     {
	 	   for(int curve=1;curve<NbrOfCurves;curve++)
    	   {
//if( (curve == 3) && (SHOW_TEST==false) ) continue;
	 		 if( curve < START_GRADIENTS) {
	 		       if( (dto.isShowCurve() == false) && (tipe == 0) ) break;
	 		       if( (dto.isShowRunningAverage() == false) && (tipe ==1 ) ) break;
	 		 }
	 		 else { // Speed => only show the running averages
	 			if( tipe == 0) break; 
	 			if( dto.isShowSpeed() == false ) break;
	 		 }
	 		   
	 		 // colors
	 		 if( tipe == 0 ) {
	 			 curvecolor = colrout.ColorLabel2Int("dark gray");
	 		 	 if( curve == 1 ) curvecolor =  AccurColor1;
		 		 if( curve == 2 ) curvecolor =  CostColor1;
		 		 if( curve == 3 ) curvecolor =  TestColor1;  // test accuracy
		    }
	 		 else  {
	 			curvecolor = colrout.ColorLabel2Int("dark gray");
	 			if( curve == 1 ) curvecolor =  AccurColor2;
		 		if( curve == 2 ) curvecolor =  CostColor2;
		 		if( curve == 3 ) curvecolor =  TestColor2;  // test accuracy
		 		if( curve == (NbrOfCurves-1) )  curvecolor =  SpeedColor1; 
		   	 }
	 		
	 		 // values
	 		 double[] xvals = new double[ nvals ];
	 		 double[] yvals = new double[ nvals ];
	 		 double minx = 0;
	 		 double maxx = 0;
	 		 for(int i=0;i<nvals;i++)
	 		 {
	 			 xvals[i] = dto.getDoubleArray()[i][0];
	 			 if( tipe == 0 ) yvals[i] = dto.getDoubleArray()[i][curve];
	 			 if( tipe == 1 ) yvals[i] = running[curve][i];
	 			 if( i == 0 ) {
	 				 minx = xvals[i];
	 		 		 maxx = xvals[i];
	 		 		 continue;		 
	 			 }
	 			 if( minx > xvals[i] ) minx = xvals[i];
	 			 if( maxx < xvals[i] ) maxx = xvals[i];
	 		 }	 
	 		 
	 		 
	 		 // ranges
	 	 	 double[] ranges = null;
		 	 if( (curve == 1) || (curve==2) || (curve==3) ) {  //   Accuracy and average accuracy  OR cost and average cost
		 		 ranges = new double[ nvals ];
		 		 for(int i=0;i<nvals;i++) ranges[i] = dto.getDoubleArray()[i][curve];
		 	 }
		 	 else  {
		         ranges = new double[ nvals * (NbrOfCurves-START_GRADIENTS) ];
		         int count=0;
		         for( int z=START_GRADIENTS;z<NbrOfCurves;z++)
		         {
		        	 for(int j=0;j<nvals;j++) 
		        	 {
		        		 ranges[count] = (tipe==0) ? dto.getDoubleArray()[j][z] : running[z][j];
		        		 count++;
		        	 }
		         }
		         if( count != ranges.length ) {
		        	 System.err.println("(gpDiagram) System Error XIV"); System.exit(1);
		         }
		 	 }
		 	 //
			 double miny = 0;
	 		 double maxy = 0;
	 		 for(int i=0;i<nvals;i++)
	 		 {
	 			 if( miny > ranges[i] ) miny = ranges[i];
	 			 if( maxy < ranges[i] ) maxy = ranges[i];
	 		 }
	 		 // OVERRULE 
	 		 if( (curve == 1) || (curve==3) ) {  // percentage dus : tussen 0 en 1
	 			 miny = 0;   
	 			 maxy = 1;   	 		 
	 		 }
	 	     double xrange = maxx - minx;
	         double yrange = maxy - miny;
	 	     //
	         prevX=-1;
			 prevY[curve]=-1;
			 double ratio = 1;
			 if( curve >= START_GRADIENTS ) ratio = (double)0.50;   // zorgt ervoor dat de speed info onderaan staat
		     for(int i=0;i<nvals;i++)
		     {
  		    	 int xabscis = (int)Math.round( (xvals[i] - minx) / xrange * drawAreaWidth) + horzMargin;
	  	     	 int yabscis = height - ((int)Math.round( (yvals[i] - miny) / yrange * drawAreaHeigth * ratio) + vertMargin);  // Y0 is top of page
	  	     	 if(i != 0) drw.doLine( tile , width , prevX , prevY[curve] , xabscis , yabscis , curvecolor ); 
	  	     	 prevX = xabscis;
	  	     	 prevY[curve] = yabscis;
	  	     	 if( i >= (ActualRecordsCreated - 1) ) break;
		     }
		   }	 
	     }
	     
	     // Gridlines and borders
	     doHorizontalGridLines( tile , width , horzMargin , vertMargin , drawAreaWidth , drawAreaHeigth , 3 );
	     drw.doRectangle( tile , width , horzMargin-1 , vertMargin-1, drawAreaWidth+2 , drawAreaHeigth+2 , LineColor );
	     doFreeText( dto, tile , width , colrout.Int2Color(TextColor) );
	     // de last values
	  	 double lastAccuracy = dto.isShowCurve() ? dto.getDoubleArray()[ActualRecordsCreated-1][1] : running[1][ActualRecordsCreated-1];
	  	 double lastCost = dto.isShowCurve() ? dto.getDoubleArray()[ActualRecordsCreated-1][2] : running[2][ActualRecordsCreated-1];
	  	 double lastTest = dto.isShowCurve() ? dto.getDoubleArray()[ActualRecordsCreated-1][3] : running[3][ActualRecordsCreated-1];
	  	 double lastSpeed = running[NbrOfCurves-1][ActualRecordsCreated-1];
	  	 // kleuren
	  	 int AccurColor = dto.isShowCurve() ? AccurColor1 : AccurColor2;
	  	 int TestColor = dto.isShowSpeed() ? TestColor1 : TestColor2;
	  	 int CostColor = dto.isShowCurve() ? CostColor1 : CostColor2;
	  	 int SpeedColor = dto.isShowSpeed() ? SpeedColor1 : iBackground;
	  	 //
	     doPutTextInClearing( tile , width , background, colrout.Int2Color(AccurColor) , horzMargin , vertMargin , prevX , prevY[1] , (String.format("%3.0f", (lastAccuracy*100))).trim()+"%" , false , 60 );
	     doPutTextInClearing( tile , width , background, colrout.Int2Color(CostColor) , horzMargin , vertMargin , prevX , prevY[2] , (String.format("%8.0f", lastCost)).trim() , false , 60 );
	     doPutTextInClearing( tile , width , background, colrout.Int2Color(TestColor) , horzMargin , vertMargin , prevX/2 , prevY[3] , (String.format("%3.0f", (lastTest*100))).trim()+"%", false , 40 );
	     doPutTextInClearing( tile , width , background, colrout.Int2Color(SpeedColor) , horzMargin , vertMargin , prevX , prevY[NbrOfCurves-1] , (String.format("%3.2f", lastSpeed)).trim()  ,true , 30 );
	     //	     
  	     return tile;
    }
    
    //------------------------------------------------------------
    private void doPutTextInClearing( int[] pix , int width , Color background , Color foreground , int horzMargin , int vertMargin , int xspot , int yspot , String msg , boolean left , int size)
    {
    	int heigth = pix.length / width;
    	Font f = new Font("Comic", Font.PLAIN, size);
    	//
    	Rectangle2D bounds = drwtxt.getBounds( f , msg , foreground );
    	if( bounds == null ) return;
    	int msgWidth = (int)bounds.getWidth();
    	int msgHeigth = (int)bounds.getHeight();
    	//
    	if( left ) xspot -= msgWidth;
    	if( (xspot + msgWidth + horzMargin) > width )  xspot = width - horzMargin - msgWidth;
       	if( (yspot + msgHeigth + vertMargin) > heigth ) yspot = heigth - vertMargin - msgHeigth;
    	try {
   		 drwtxt.drawPixelText(  f , foreground ,   xspot ,  yspot , pix , width, msg );
   		}
   		catch(Exception e) { return; }
    }
    
    //------------------------------------------------------------
    private boolean doFreeText( gpDiagramDTO dto , int[] pix , int width , Color tcolor)
    {
    	String[] ftext = dto.getFreeText();
    	if( ftext == null ) return true;
    	if( pix == null ) return true;
    	int heigth = pix.length / width;
    	int FontHeigth = 14;
    	Font f = new Font("Helvetica", Font.PLAIN, FontHeigth);
    	int z=-1; 
    	for(int i=0;i<ftext.length;i++)
    	{
    		String ft = ftext[i];
    		if( ft == null ) continue;
    		z++;
    		try {
    		 drwtxt.drawPixelText(f, tcolor ,   (width*75)/100 ,  (heigth/3) + (z*(FontHeigth+3)) - ((ftext.length * 15)/2), pix , width, ft );
    		}
    		catch(Exception e) { break; }
    	}
    	
    	return true;
    }
    //------------------------------------------------------------
    public int[] createBlankDiagram( int w ,  int h , Color background)
    //------------------------------------------------------------
    {
    	int iBackground = colrout.Color2Int(background);
    	int[] diagram = new int[ w * h];
    	for(int i=0;i< diagram.length ; i++) diagram[i] = iBackground;
    	return diagram;
    }
    //------------------------------------------------------------
    private boolean doRescaleAndSort( gpDiagramDTO dto)
    //------------------------------------------------------------
    {
        double minx = Double.NaN;
        double maxx = Double.NaN;
        double miny = Double.NaN;
        double maxy = Double.NaN;
        for(int i=0;i<dto.getXvals().length;i++)
        {
       	 if( i == 0 )  {
       		 minx = maxx = dto.getXvals()[i];
       		 miny = maxy = dto.getYvals()[i];
       	 }
       	 if( minx > dto.getXvals()[i] ) minx = dto.getXvals()[i];
       	 if( maxx < dto.getXvals()[i] ) maxx = dto.getXvals()[i];
       	 if( miny > dto.getYvals()[i] ) miny = dto.getYvals()[i];
       	 if( maxy < dto.getYvals()[i] ) maxy = dto.getYvals()[i];
        }
        double spanx = maxx - minx;
        double spany = maxy - miny;
        // Rescale
        for(int i=0;i<dto.getXvals().length;i++)
        {
         if( dto.setXval( i , (dto.getXvals()[i] - minx) / spanx) == false ) { do_error("set X val"); return false; }
         if( dto.setYval( i , (dto.getYvals()[i] - miny) / spany) == false ) { do_error("set Y val"); return false; }
        }
        //
     	for(int k=0;k<dto.getXvals().length;k++)
    	 {
    		 boolean swap = false;
    		 for(int i=0;i<dto.getXvals().length-1;i++)
    		 {
    			 if(  dto.getXvals()[i] < dto.getXvals()[i+1] ) continue;
    			 if( (dto.getXvals()[i] == dto.getXvals()[i+1] ) && ( dto.getYvals()[i] <= dto.getYvals()[i+1] ) ) continue;
    			 double x = dto.getXvals()[i];
    			 double y = dto.getYvals()[i];
    			 dto.setXval( i   , dto.getXvals()[i+1] );
    			 dto.setYval( i   , dto.getYvals()[i+1] );
    			 dto.setXval( i+1 , x );
    			 dto.setYval( i+1 , y );
    			 swap=true;
    		 }
    		 if( swap == false ) break;
    	}
    	
    	return true;
    }
    
  //------------------------------------------------------------
  public int[] createPlainDiagram( gpDiagramDTO dto ,int width , int height , Color background )
  //------------------------------------------------------------
  {
	 drwtxt.setBackGroundColor( background );
	 //
  	 if( dto.getXvals() == null ) { do_error( "NULL xvals"); return null; }
     if( dto.getYvals() == null ) { do_error( "NULL yvals"); return null; }
     if( dto.getXvals().length != dto.getYvals().length ) { do_error( "xvals - yvals size"); return null; }
 	 int nvals = dto.getXvals().length;
     //
 	 if( doRescaleAndSort( dto ) == false ) return null;
     //
     int horzMargin = (int)((double)width * 0.07);
     int vertMargin = (int)((double)height * 0.07);
     int drawAreaWidth = width - (2*horzMargin);
     int drawAreaHeigth = height - (2*vertMargin);
	 //
     int iBackground = colrout.Color2Int(background);
	 int CurveColor  = colrout.ColorLabel2Int("Indian Red");
     
	 //
     int[] pix = new int[ width * height ];
     for(int i=0;i<pix.length;i++) pix[i] = iBackground;
     
     int prevx = 0;
     int prevy = 0;
     for(int i=0;i<nvals;i++)
	 {
    	 int x =  (int)(Math.round( dto.getXvals()[i] * (double)drawAreaWidth));
    	 int y =  (int)(Math.round( dto.getYvals()[i] * (double)drawAreaHeigth));
    	 try {
    		int xx = x + horzMargin;
    		int px = prevx + horzMargin;
    		int yy = height - y - vertMargin;
    		int py = height - prevy - vertMargin;
    	    if( i > 0 ) drw.doLine( pix , width , px , py , xx , yy , CurveColor );
    	 }
    	 catch( Exception e) { do_error( "line"); }
    	 prevx = x;
    	 prevy = y;
	 }
     
     //
     drw.doRectangle( pix , width , horzMargin-1 , vertMargin-1 , drawAreaWidth+2 , drawAreaHeigth+2, LineColor );
	
     return pix;
  }
  
  
}
