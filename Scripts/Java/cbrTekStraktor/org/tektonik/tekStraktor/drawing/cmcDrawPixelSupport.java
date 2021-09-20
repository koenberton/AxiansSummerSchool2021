package org.tektonik.tekStraktor.drawing;

import org.tektonik.tools.linearAlgebra.cmcMath;

public class cmcDrawPixelSupport {
	
	private cmcMath mrout = null;
	
	
	public cmcDrawPixelSupport()
	{
		  mrout = new cmcMath();
	}

	//------------------------------------------------------------
    public boolean doDot( int[]pix , int width , int x , int y , int color)
    //------------------------------------------------------------
    {
    	try {
    	   pix[ x + (y * width)] = color;
        } catch(Exception e) { return false; } return true;
    }
    //------------------------------------------------------------
    public boolean doVerticalBar( int[] pix , int width , int x , int y , int w , int h , int color)
    //------------------------------------------------------------
    {
    	for(int i=0;i<=w;i++)
    	{
    		if( doVerticalLine( pix , width  , (x+i) , y , (y - h) , color) == false ) return false;
    	}
    	return true;
    }
    //------------------------------------------------------------
    public boolean doLine( int[]pix , int width , int x0 , int y0 , int x1 , int y1 , int color)
    //------------------------------------------------------------
    {
    	if( x0==x1 ) return doVerticalLine( pix , width , x0 , y0 , y1 , color);
    	else
        if( y0==y1 ) return doHorizontalLine( pix , width , y0 , x0 , x1 , color);
        else 
        return doSlantedLine( pix , width , x0 , y0 , x1 , y1 , color);
    }
    //------------------------------------------------------------
    public boolean doSlantedLine( int[]pix , int width , int x0 , int y0 , int x1 , int y1 , int color)
    //------------------------------------------------------------
    {
    	try {
    	int xa = x0;
    	int ya = y0;
    	int xb = x1;
    	int yb = y1;
    	if( x0 > x1 ) { 
    		xa = x1; ya = y1 ; xb = x0; yb= y0;
    	}
    	int ideltax = xb - xa;
    	if( ideltax == 0 ) return true;
    	double ddeltay = ((double)yb - (double)ya) / (double)ideltax;
    	double factor =  Math.abs( (yb- ya) * 2 );
    	for(int dx=0;dx<ideltax*factor;dx++)
    	{
    		int y = (int)Math.round((double)dx * ddeltay / (double)factor);
    		int i = (int)Math.round( ((double)dx / (double)factor) );
    		int z =  xa + i + ( (y+ya) * width);
    		if( (z<0) || (z>=pix.length)) continue;
    		pix[z] = color;
    	}
    	} catch(Exception e) { return false; } return true;
    }
    //------------------------------------------------------------
    public boolean doVerticalLine( int[] pix , int width , int x0 , int y1 , int y2 , int color)
    //------------------------------------------------------------
    {
      try {
       int Y1 = ( y1 < y2 ) ? y1 : y2;
       int Y2 = ( y1 < y2 ) ? y2 : y1;
       for(int y=Y1 ; y<=Y2 ; y++) pix[ x0 + (y * width)] = color;
      } catch(Exception e) { return false; } return true;
    }
    //------------------------------------------------------------
    public boolean doHorizontalLine( int[] pix , int width , int y0 , int x1 , int x2 , int color)
    //------------------------------------------------------------
    {
    	try  {
    	 int X1 = ( x1 < x2 ) ? x1 : x2;
    	 int X2 = ( x1 < x2 ) ? x2 : x1;
    	 int y = y0 * width;
    	 for(int x=X1; x<=X2 ; x++) pix[ y + x ] = color;
        } catch(Exception e) { return false; } return true;
    }
    //------------------------------------------------------------
    public boolean doRectangle( int[] pix , int pixwidth , int x0 , int y0 , int width , int heigth , int color )
    //------------------------------------------------------------
    {
    	 if( doLine( pix , pixwidth , x0 , y0 , x0+width , y0 , color) == false ) return false;
    	 if( doLine( pix , pixwidth , x0 , y0+heigth , x0+width , y0+heigth , color )== false ) return false;
    	 if( doLine( pix , pixwidth , x0 , y0 , x0 , y0+heigth , color)== false ) return false;
  	     if( doLine( pix , pixwidth , x0+width , y0 , x0+width, y0+heigth , color )== false ) return false;
  	     return true;
    }
    //------------------------------------------------------------
    public boolean drawFilledRectangleFromCenter( int[] pix , int width , int x1 , int y1 , int size , int color)
    //------------------------------------------------------------
    {
      try {
        int vsize = mrout.isEven( size ) ? size + 1 : size;
        for(int y=0 ; y<vsize ; y++)
        {
        	int k = ( ( y1 - (vsize/2) + y ) * width ) + ( x1 - (vsize/2) ) - 1;
        	for(int x=0;x<vsize;x++)
        	{
        		k++;
        		if( (k<0) || (k>=pix.length)) continue;
        		pix[ k ] = color;
        	}
        } 	
       } catch(Exception e) { return false; } return true;
    }
    //------------------------------------------------------------
    public boolean drawRectangleFromCenter( int[] pix , int width , int x1 , int y1 , int size , int color)
    //------------------------------------------------------------
    {
    	try {
            int vsize = mrout.isEven( size ) ? size + 1 : size;
            int h = pix.length / width;
        	int x = x1 - (vsize/2); if( x < 0) x=0;
        	int y = y1 - (vsize/2); if( y < 0) y=0;
        	int z = x + vsize; if( z >= width) z=width-1;
        	doHorizontalLine( pix , width , y , x , z , color);
        	y += vsize;  if( y >= h ) y = h-1;
        	doHorizontalLine( pix , width , y , x , z , color);
        
        	x = x1 - (vsize/2); if( x < 0) x=0;
        	y = y1 - (vsize/2); if( y < 0) y=0;
            z = y + vsize; if( z >= h ) z = h-1;
            doVerticalLine( pix , width , x , y , z , color);
            x += vsize; if( x >= width) x=width-1;
            doVerticalLine( pix , width , x , y , z , color);
        } catch(Exception e) { return false; } return true;
    }
    //------------------------------------------------------------
    public boolean drawCircleFromCenter( int[] pix , int width , int x1 , int y1 , int diameter , int color)
    //------------------------------------------------------------
    {
    	try {
        int h = pix.length / width;
    	double ddia  =   mrout.isEven( diameter ) ? diameter+1: diameter;
    	double radius = (double)ddia / 2;
    	int steps = (int)(radius * 2);
    	double stepsize = radius / (double)steps;
    	double dx=0;
    	int[] xprev = new int[4];  // track previous x an y in order to prevent different y for same x => single edge circle
    	int[] yprev = new int[4];
    	for(int i=0;i<steps+1;i++)
    	{
    	    double dy = Math.sqrt((radius * radius) - (dx*dx));
    	    int idy = (int)Math.round(dy);
    	    int idx = (int)Math.round(dx);
    	 
    		int xco = x1 - idx; if( xco < 0) xco=0;
    		int yco = y1 - idy; if( yco < 0) yco=0;
    		if( (i != 0) && (xco == xprev[0]) && (yco != yprev[0]) ) yco = yprev[0];
    		pix[ (yco * width) + xco ] = color;
    		xprev[0] = xco;
    		yprev[0] = yco;
    		//
    		yco = y1 + idy; if( yco > h) yco=h;
    		if( (i != 0) && (xco == xprev[1]) && (yco != yprev[1]) ) yco = yprev[1];
    	    pix[ (yco * width) + xco ] = color;
    		xprev[1] = xco;
    		yprev[1] = yco;
    		//
    	    xco = x1 + idx; if (xco > width) xco = width;
    	    if( (i != 0) && (xco == xprev[2]) && (yco != yprev[2]) ) yco = yprev[2];
    	    pix[ (yco * width) + xco ] = color;
    		xprev[2] = xco;
    		yprev[2] = yco;
    	    //
    	    yco = y1 - idy; if( yco < 0) yco=0;
    	    if( (i != 0) && (xco == xprev[3]) && (yco != yprev[3]) ) yco = yprev[3];
    	    pix[ (yco * width) + xco ] = color;
    		xprev[3] = xco;
    		yprev[3] = yco;
    	    //
    		dx += stepsize;
    		if( stepsize > radius) break;
    	}
        } catch(Exception e) { return false; } return true;
    }
    //------------------------------------------------------------
    public boolean drawCrossFromCenter( int[] pix , int width , int x1 , int y1 , int diameter , int color)
    //------------------------------------------------------------
    {
    	try {
        int h = pix.length / width;
    	double ddia  =   mrout.isEven( diameter ) ? diameter+1: diameter;
    	double radius = (double)ddia / 2;
    	
    	int x = x1 - (int)radius; if( x < 0) x=0;
    	int y = y1 - (int)radius; if( y < 0) y=0;
    	int x3 = x+diameter; if( x >= width) x=width-1;
    	int y3 = y+diameter; if( y>=h) y=h-1;
    	doLine( pix , width , x , y1 , x3 , y1 , color);
    	doLine( pix , width , x1 , y , x1 , y3 , color);
        } catch(Exception e) { return false; } return true;
    }
    
    
    //------------------------------------------------------------
    public boolean pasteSnippet( int[] target , int tx , int ty , int target_width , int[] src , int src_width )
    //------------------------------------------------------------
    {
    	try {
    	//int target_heigth = target.length / target_width;
    	int src_heigth = src.length / src_width;
    	for(int y=0;y<src_heigth;y++)
    	{
    		int z = ((ty + y) * target_width) + tx;
    		int k = y * src_width;
    		for(int x=0;x<src_width;x++)
    		{
    			target[z] = src[k];
    			z++;
    			k++;
    		}
    	}
    	} catch(Exception e) { return false; } return true;
    }
    
}
