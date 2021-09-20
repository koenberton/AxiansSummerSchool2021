package org.tektonik.tekStraktor.drawing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.Map;

import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalImagePurpose.color.gpColorRoutines;
import org.tektonik.tools.logger.logLiason;


public class cmcDrawPixelText {
	
	private static final Color INIT_COLOR = new Color( 1 , 23 , 33);
	private static final boolean USE_GLYPH = false;  // toggle and determine which fonts are rendered better
	//private static final int BACKGROUNDMARK = 0xabfefafb;
	
	cmcProcSettings xMSet=null;
	logLiason logger=null;
    gpColorRoutines colsupp = null;
    
	private FontMetrics metrics;
    private Color foregroundColor;
    private int txtBreedte=-1;
	private int txtHoogte=-1;
	private Color txtBackGround = INIT_COLOR;
	private Map<?, ?> desktopHints = null;
	 
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
    
    //---------------------------------------------------------------------------------
	public cmcDrawPixelText(cmcProcSettings is,logLiason ilog )
	//---------------------------------------------------------------------------------
	{
		xMSet = is;
		logger=ilog;
		colsupp = new gpColorRoutines();
		desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
	}
	
	//---------------------------------------------------------------------------------
	private void ApplyHints(Graphics g)
	//---------------------------------------------------------------------------------
	{
		if( g == null ) return;
		Graphics2D g2 = (Graphics2D) g;
		if (desktopHints != null) {
	           g2.setRenderingHints(desktopHints);
	    }
	    else {
	       	   g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	           g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	           //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	           g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	           do_error( "Overruling desktop hints");
	    }
	}
	
    //---------------------------------------------------------------------------------
	private void setFont (Font f, Color col) 
    //---------------------------------------------------------------------------------
	{
		int size = 100;
		BufferedImage bi = new BufferedImage( size , size , BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		ApplyHints(g);
	    g.setFont(f);    
		metrics = g.getFontMetrics();
        foregroundColor = col;
        if( foregroundColor == txtBackGround) txtBackGround = Color.BLACK; // Safety
        g=null;
        bi=null;
    }
	
	//---------------------------------------------------------------------------------
	public Rectangle2D getBounds( Font f , String sIn , Color col)
	//---------------------------------------------------------------------------------
	{
		try {
			 if( f != null) setFont(f,col);
	    	 Rectangle2D bounds = new TextLayout( sIn , metrics.getFont(), metrics.getFontRenderContext()).getOutline(null).getBounds();
	         if(bounds.getWidth() == 0 || bounds.getHeight() == 0)  return null;
	         return bounds;
	     }
	     catch(Exception e) { return null; }    
	}
	
    //---------------------------------------------------------------------------------
    private int[] getPixelText(String sIn)
    //---------------------------------------------------------------------------------
    {
      try {
    	Rectangle2D bounds = getBounds(null,sIn,foregroundColor);
        Image img = new BufferedImage((int)((double)bounds.getWidth()*(double)1.25), (int)bounds.getHeight()+2, BufferedImage.TYPE_INT_ARGB);
        txtBreedte = img.getWidth(null);
 	    txtHoogte  = img.getHeight(null);
 	    //
 	    if( (txtBreedte < 0) || (txtHoogte <0) ) {
 		   do_error("Height or width cannot be determined of string [" + sIn + "]");
 		   return null;
 	    }
 	    //
        Graphics g = img.getGraphics();
        g.setColor(txtBackGround);
        g.fillRect(0,0,txtBreedte,txtHoogte);
        g.setColor(foregroundColor);
        //
        ApplyHints(g);
        //
       	if( USE_GLYPH ) {
       	    Graphics2D g2 = (Graphics2D) g;
       	    Font f = metrics.getFont();  
       	    FontRenderContext frc = new FontRenderContext(null, true, true);
       	    GlyphVector gv = f.createGlyphVector(frc, sIn);
       	    g2.drawGlyphVector(gv, 0, (int)(bounds.getHeight() - bounds.getMaxY()) );
       	}
       	else {
            g.setFont( metrics.getFont() );
            g.drawString(sIn, 0, (int)(bounds.getHeight() - bounds.getMaxY()) + 1);
       	}
        //
 	    int[] srcpixels = new int[ txtHoogte * txtBreedte ];
 	    try {
 		   PixelGrabber pg = new PixelGrabber( img , 0 , 0 , txtBreedte , txtHoogte , srcpixels , 0 , txtBreedte);
 		   pg.grabPixels();
 	    }
 	    catch (Exception e ) {
 		   do_error("Pixelgrabber on [" + sIn + "] " + e.getMessage());
 		   return null;
 	    }
 	    img=null;
 	    //do_log( 9 , "[text=" + sIn + "] [BCK=" + txtBackGround + "] [FORE=" + foregroundColor + "] [w=" + txtBreedte + "]");
        return srcpixels;
      }
      catch(Exception e) {
    		 do_error("getPixelText on [" + sIn + "] " + e.getMessage());
    		 return null;
      }
    }
    
    //---------------------------------------------------------------------------------
  	private void mergeSnippet( int x, int y , int canvasbreedte , int[] snippet , int[] canvas)
  	//---------------------------------------------------------------------------------
  	{
  		int rgb = colsupp.Color2Int(txtBackGround) & 0x00ffffff;
  		try {
  		  for(int i=0;i<txtHoogte;i++)
  		  {
  			for(int j=0;j<txtBreedte;j++)
  			{
  				if( ( x + j) >= canvasbreedte ) continue;
  				int q = snippet[(txtBreedte*i)+j];
  			    if( (q & 0x00ffffff) == rgb ) continue;
  				int p = ((y + i)*canvasbreedte) + x + j;
  				if( p < canvas.length ) canvas[p] = q;
  			}
  		  }
  		  snippet=null;
  		}
  		catch(Exception e ) {
  			do_error("(mergeSnippet)" + e.getMessage());
  		}
  	}
  	
    //---------------------------------------------------------------------------------
  	private int[] flipImage( int[] pix )
    //---------------------------------------------------------------------------------
  	{
  		try {
          int[] pox = new int[ pix.length ];
          int p = -1;
          int k = -1;
          for(int y=0;y<txtHoogte;y++)
          {
        	  for(int x=0;x<txtBreedte;x++)
        	  {
        		  p = pix[ x + (txtBreedte*y)];
        		  //k =  ((x + 1) * txtHoogte) - y - 1;   // + 90 graden of naar rechts
        		  k =  ((txtBreedte - 1 - x) * txtHoogte) + y;  // -90 graden of naar links
        		  pox[ k ] = p;
        	  }
          }
          k = txtBreedte;
          txtBreedte=txtHoogte;
          txtHoogte=k;
          return pox;
  		}
  		catch(Exception e ) {
  			do_error( "flip " + e.getMessage()) ;
  			return null;
  		}
  	}
  	
    //---------------------------------------------------------------------------------
  	public void drawPixelText(Font f , Color col , int x , int y , int[]pixelcanvas , int canvasWidth , String sIn , boolean flip)
    //---------------------------------------------------------------------------------
  	{
  		if( this.txtBackGround == INIT_COLOR ) {
  			do_error( "Draw Pixel Text - System Error - you need to set background - application will stop");
  			System.exit(1);
  		}
  		txtBreedte=-1;
  		txtHoogte=-1;
  	    setFont(f,col);
  	    int[] txtpixels = getPixelText(sIn);
  	    if( txtpixels == null )  return;
  	    if( flip ) {
  	    	txtpixels = flipImage( txtpixels );
  	    	if( txtpixels == null ) return;
  	    }
  	    mergeSnippet( x , y , canvasWidth , txtpixels , pixelcanvas );    
  	    txtpixels=null;
  	    return;
  	}
  	
    //---------------------------------------------------------------------------------
	public void drawPixelText(Font f , Color col , int x , int y , int[]pixelcanvas , int canvasWidth , String sIn)
    //---------------------------------------------------------------------------------
	{
		drawPixelText( f , col , x , y , pixelcanvas , canvasWidth , sIn , false);
	}
	//---------------------------------------------------------------------------------
	public void drawPixelTextFlip(Font f , Color col , int x , int y , int[]pixelcanvas , int canvasWidth , String sIn)
	//---------------------------------------------------------------------------------
	{
		drawPixelText( f , col , x , y , pixelcanvas , canvasWidth , sIn , true);
	}
	//---------------------------------------------------------------------------------
	public void drawPixelTextCenter(Font f , Color col , int x , int y , int[]pixelcanvas , int canvasWidth , String sIn)
	//---------------------------------------------------------------------------------
	{
			txtBreedte=-1;
			txtHoogte=-1;
		    setFont(f,col);
		    int[] txtpixels = getPixelText(sIn);
		    if( txtpixels == null )  return;
		    int x1 = (int)((double)x - ((double)txtBreedte / 2));
		    int y1 = (int)((double)y - ((double)txtHoogte / 2));
		    txtpixels=null;
		    drawPixelText( f , col , x1 , y1 , pixelcanvas , canvasWidth , sIn );
	}
	//---------------------------------------------------------------------------------
	public void drawPixelTextCenterFlip(Font f , Color col , int x , int y , int[]pixelcanvas , int canvasWidth , String sIn)
	//---------------------------------------------------------------------------------
	{
				txtBreedte=-1;
				txtHoogte=-1;
			    setFont(f,col);
			    int[] txtpixels = getPixelText(sIn);
			    if( txtpixels == null )  return;
			    int x1 = (int)((double)x - ((double)txtHoogte / 2));
			    int y1 = (int)((double)y - ((double)txtBreedte / 2));
			    txtpixels=null;
			    drawPixelTextFlip( f , col , x1 , y1 , pixelcanvas , canvasWidth , sIn );
	}
	
	//---------------------------------------------------------------------------------
	public void setBackGroundColor(Color col)
	//---------------------------------------------------------------------------------
	{
	  txtBackGround = col;
	}
	
}
