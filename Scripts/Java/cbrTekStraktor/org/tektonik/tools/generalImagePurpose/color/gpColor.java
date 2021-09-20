package org.tektonik.tools.generalImagePurpose.color;

/**
 * based on code by @author  Shannon Hickey
 */

import java.awt.Color;

import org.tektonik.tekStraktor.model.cmcProcConstants;
import org.tektonik.tekStraktor.model.cmcProcEnums;

public class gpColor {

	    static Color defaultColor = Color.WHITE;
	    static int defaultColorInt =  defaultColor.getRGB();

	    private gpColorRoutines colsupp = null;
	    
        public gpColor()
        {
        	colsupp = new gpColorRoutines();
        }	

        public Color getDefaultColor()
        {
        	return defaultColor;
        }
        public int getDefaultColorInt()
        {
        	return defaultColorInt;
        }
        public Color Int2Color(int color)
        {
         	return colsupp.Int2Color(color);
        }
    	
    	public int Color2Int(Color c)
        {
        	return colsupp.Color2Int(c);
        } 
    	
        public Color ColorLabel2Color(String s)
        {
            for(int i=0;i<ar_colors.length;i++)
            {
            	if( ar_colors[i].name.compareToIgnoreCase(s) == 0) {
            		Color c = new Color(ar_colors[i].red , ar_colors[i].green , ar_colors[i].blue );
            		return c;
            	}
            }
        	return defaultColor;
        }
        public int ColorLabel2Int(String s)
        {
            for(int i=0;i<ar_colors.length;i++)
            {
            	if( ar_colors[i].name.compareToIgnoreCase(s) == 0) {
            		Color c = new Color(ar_colors[i].red , ar_colors[i].green , ar_colors[i].blue );
            		return c.getRGB();
            	}
            }
        	return defaultColorInt;
        }
        //------------------------------------------------------------
        public int ColorSentiment2Int( int j , cmcProcEnums.ColorSentiment sentiment  )
        //------------------------------------------------------------
        {
        	   int color = cmcProcConstants.WIT;
        	   if( sentiment == cmcProcEnums.ColorSentiment.SOFT )
        	   {
    	        switch( j % 5)
    	        {
    	         case 0 :   { color = ColorLabel2Int("Dim Gray"); break; }
    	         case 1 :   { color = ColorLabel2Int("Dark Sea Green"); break; }
    	         case 2 :   { color = ColorLabel2Int("Cadet Blue"); break; }
    	         case 3 :   { color = ColorLabel2Int("Slate Gray"); break; }
    	         default :  { color = ColorLabel2Int("Maroon"); break; }
    	        }
        	   }
        	   else
        	   if( sentiment == cmcProcEnums.ColorSentiment.HARSH )
               {
        	     switch( j % 5)
        	     {
        	      case 0 :   { color = ColorLabel2Int("Red"); break; }
        	      case 1 :   { color = ColorLabel2Int("Orange"); break; }
        	      case 2 :   { color = ColorLabel2Int("Blue"); break; }
        	      case 3 :   { color = ColorLabel2Int("Pink"); break; }
        	      default :  { color = ColorLabel2Int("FireBrick"); break; }
        	     }
               }   
        	   else
               if( sentiment == cmcProcEnums.ColorSentiment.RED )
               {
            	 switch( j % 10)
            	 {
            	  case 0 :   { color = ColorLabel2Int("deep pink"); break; }
            	  case 8 :   { color = ColorLabel2Int("medium orchid"); break; }
            	  case 2 :   { color = ColorLabel2Int("orchid"); break; }
            	  case 7 :   { color = ColorLabel2Int("violet"); break; }
            	  case 4 :   { color = ColorLabel2Int("plum"); break; }
            	  case 5 :   { color = ColorLabel2Int("thistle"); break; }
            	  case 6 :   { color = ColorLabel2Int("pink"); break; }
            	  case 3 :   { color = ColorLabel2Int("rosy brown"); break; }
            	  case 1 :   { color = ColorLabel2Int("medium violet red"); break; }
            	  default :  { color = ColorLabel2Int("hot pink"); break; }
            	 }
               }   
               else
               if( sentiment == cmcProcEnums.ColorSentiment.BLUE )
               {
                 switch( j % 10)
                 {
                  case 0 :   { color = ColorLabel2Int("powder blue"); break; }
                  case 8 :   { color = ColorLabel2Int("light steel blue"); break; }
                  case 2 :   { color = ColorLabel2Int("light sky blue"); break; }
                  case 7 :   { color = ColorLabel2Int("dodger blue"); break; }
                  case 4 :   { color = ColorLabel2Int("royal blue"); break; }
                  case 5 :   { color = ColorLabel2Int("aquamarine"); break; }
                  case 6 :   { color = ColorLabel2Int("dark turquoise"); break; }
                  case 3 :   { color = ColorLabel2Int("aqua"); break; }
                  case 1 :   { color = ColorLabel2Int("steel blue"); break; }
                  default :  { color = ColorLabel2Int("blue violet"); break; }
                 }
               }   
        	   else
        	   if( sentiment == cmcProcEnums.ColorSentiment.GREEN )
               {
                 switch( j % 10)
                 {
                  case 0 :   { color = ColorLabel2Int("forest green"); break; }
                  case 8 :   { color = ColorLabel2Int("lime green"); break; }
                  case 2 :   { color = ColorLabel2Int("gold"); break; }
                  case 7 :   { color = ColorLabel2Int("olive drab"); break; }
                  case 4 :   { color = ColorLabel2Int("sea green"); break; }
                  case 5 :   { color = ColorLabel2Int("dark sea green"); break; }
                  case 6 :   { color = ColorLabel2Int("pale green"); break; }
                  case 3 :   { color = ColorLabel2Int("light green"); break; }
                  case 1 :   { color = ColorLabel2Int("dark green"); break; }
                  default :  { color = ColorLabel2Int("yellow green"); break; }
                 }
               }   
        	   else { System.err.println("unsupported color sentiment " + sentiment ); }
        	   if( color == getDefaultColorInt() ) color = ColorLabel2Int("yellow");
    	       return color;
        }
        
        //------------------------------------------------------------
        public int getColorGradient( Color basecolor , int step , int totalNumberOfLevels)
        //------------------------------------------------------------
        {
        	int rgb       = basecolor.getRGB();
        	int red       =  (rgb>>16) & 0xff;
        	int green     =  (rgb>>8) & 0xff;
        	int blue      =  rgb & 0xff;
        	float[] hsb = Color.RGBtoHSB( red , green , blue , null);
        	float hue = hsb[0];
        	float saturation = hsb[1];
        	float brightness = hsb[2];
        	//
        	saturation =  (saturation / (float)(totalNumberOfLevels+1)) * (float)(step+1);
        	//hue =  (hue / (float)(totalNumberOfLevels+1)) * (float)(step+1);
        	//brightness =  (brightness / (float)(totalNumberOfLevels+1)) * (float)(step+1);
            rgb = Color.HSBtoRGB(hue, saturation, brightness);
        	return rgb;
        	
        }
        
	    private static final cpColorDAO[] ar_colors = {

	        new cpColorDAO("alice blue", 240, 248, 255),
	        new cpColorDAO("aliceblue", 240, 248, 255),
	        new cpColorDAO("antique white", 250, 235, 215),
	        new cpColorDAO("antiquewhite", 250, 235, 215),
	        new cpColorDAO("antiquewhite1", 255, 239, 219),
	        new cpColorDAO("antiquewhite2", 238, 223, 204),
	        new cpColorDAO("antiquewhite3", 205, 192, 176),
	        new cpColorDAO("antiquewhite4", 139, 131, 120),
	        new cpColorDAO("aquamarine", 127, 255, 212),
	        new cpColorDAO("aquamarine1", 127, 255, 212),
	        new cpColorDAO("aquamarine2", 118, 238, 198),
	        new cpColorDAO("aquamarine3", 102, 205, 170),
	        new cpColorDAO("aquamarine4", 69, 139, 116),
	        new cpColorDAO("azure", 240, 255, 255),
	        new cpColorDAO("azure1", 240, 255, 255),
	        new cpColorDAO("azure2", 224, 238, 238),
	        new cpColorDAO("azure3", 193, 205, 205),
	        new cpColorDAO("azure4", 131, 139, 139),
	        new cpColorDAO("beige", 245, 245, 220),
	        new cpColorDAO("bisque", 255, 228, 196),
	        new cpColorDAO("bisque1", 255, 228, 196),
	        new cpColorDAO("bisque2", 238, 213, 183),
	        new cpColorDAO("bisque3", 205, 183, 158),
	        new cpColorDAO("bisque4", 139, 125, 107),
	        new cpColorDAO("black", 0, 0, 0),
	        new cpColorDAO("blanched almond", 255, 235, 205),
	        new cpColorDAO("blanchedalmond", 255, 235, 205),
	        new cpColorDAO("blue", 0, 0, 255),
	        new cpColorDAO("blue violet", 138, 43, 226),
	        new cpColorDAO("blue1", 0, 0, 255),
	        new cpColorDAO("blue2", 0, 0, 238),
	        new cpColorDAO("blue3", 0, 0, 205),
	        new cpColorDAO("blue4", 0, 0, 139),
	        new cpColorDAO("blueviolet", 138, 43, 226),
	        new cpColorDAO("brown", 165, 42, 42),
	        new cpColorDAO("brown1", 255, 64, 64),
	        new cpColorDAO("brown2", 238, 59, 59),
	        new cpColorDAO("brown3", 205, 51, 51),
	        new cpColorDAO("brown4", 139, 35, 35),
	        new cpColorDAO("burlywood", 222, 184, 135),
	        new cpColorDAO("burlywood1", 255, 211, 155),
	        new cpColorDAO("burlywood2", 238, 197, 145),
	        new cpColorDAO("burlywood3", 205, 170, 125),
	        new cpColorDAO("burlywood4", 139, 115, 85),
	        new cpColorDAO("cadet blue", 95, 158, 160),
	        new cpColorDAO("cadetblue", 95, 158, 160),
	        new cpColorDAO("cadetblue1", 152, 245, 255),
	        new cpColorDAO("cadetblue2", 142, 229, 238),
	        new cpColorDAO("cadetblue3", 122, 197, 205),
	        new cpColorDAO("cadetblue4", 83, 134, 139),
	        new cpColorDAO("chartreuse", 127, 255, 0),
	        new cpColorDAO("chartreuse1", 127, 255, 0),
	        new cpColorDAO("chartreuse2", 118, 238, 0),
	        new cpColorDAO("chartreuse3", 102, 205, 0),
	        new cpColorDAO("chartreuse4", 69, 139, 0),
	        new cpColorDAO("chocolate", 210, 105, 30),
	        new cpColorDAO("chocolate1", 255, 127, 36),
	        new cpColorDAO("chocolate2", 238, 118, 33),
	        new cpColorDAO("chocolate3", 205, 102, 29),
	        new cpColorDAO("chocolate4", 139, 69, 19),
	        new cpColorDAO("coral", 255, 127, 80),
	        new cpColorDAO("coral1", 255, 114, 86),
	        new cpColorDAO("coral2", 238, 106, 80),
	        new cpColorDAO("coral3", 205, 91, 69),
	        new cpColorDAO("coral4", 139, 62, 47),
	        new cpColorDAO("cornflower blue", 100, 149, 237),
	        new cpColorDAO("cornflowerblue", 100, 149, 237),
	        new cpColorDAO("cornsilk", 255, 248, 220),
	        new cpColorDAO("cornsilk1", 255, 248, 220),
	        new cpColorDAO("cornsilk2", 238, 232, 205),
	        new cpColorDAO("cornsilk3", 205, 200, 177),
	        new cpColorDAO("cornsilk4", 139, 136, 120),
	        new cpColorDAO("cyan", 0, 255, 255),
	        new cpColorDAO("cyan1", 0, 255, 255),
	        new cpColorDAO("cyan2", 0, 238, 238),
	        new cpColorDAO("cyan3", 0, 205, 205),
	        new cpColorDAO("cyan4", 0, 139, 139),
	        new cpColorDAO("dark blue", 0, 0, 139),
	        new cpColorDAO("dark cyan", 0, 139, 139),
	        new cpColorDAO("dark goldenrod", 184, 134, 11),
	        new cpColorDAO("dark gray", 169, 169, 169),
	        new cpColorDAO("dark green", 0, 100, 0),
	        new cpColorDAO("dark grey", 169, 169, 169),
	        new cpColorDAO("dark khaki", 189, 183, 107),
	        new cpColorDAO("dark magenta", 139, 0, 139),
	        new cpColorDAO("dark olive green", 85, 107, 47),
	        new cpColorDAO("dark orange", 255, 140, 0),
	        new cpColorDAO("dark orchid", 153, 50, 204),
	        new cpColorDAO("dark red", 139, 0, 0),
	        new cpColorDAO("dark salmon", 233, 150, 122),
	        new cpColorDAO("dark sea green", 143, 188, 143),
	        new cpColorDAO("dark slate blue", 72, 61, 139),
	        new cpColorDAO("dark slate gray", 47, 79, 79),
	        new cpColorDAO("dark slate grey", 47, 79, 79),
	        new cpColorDAO("dark turquoise", 0, 206, 209),
	        new cpColorDAO("dark violet", 148, 0, 211),
	        new cpColorDAO("darkblue", 0, 0, 139),
	        new cpColorDAO("darkcyan", 0, 139, 139),
	        new cpColorDAO("darkgoldenrod", 184, 134, 11),
	        new cpColorDAO("darkgoldenrod1", 255, 185, 15),
	        new cpColorDAO("darkgoldenrod2", 238, 173, 14),
	        new cpColorDAO("darkgoldenrod3", 205, 149, 12),
	        new cpColorDAO("darkgoldenrod4", 139, 101, 8),
	        new cpColorDAO("darkgray", 169, 169, 169),
	        new cpColorDAO("darkgreen", 0, 100, 0),
	        new cpColorDAO("darkgrey", 169, 169, 169),
	        new cpColorDAO("darkkhaki", 189, 183, 107),
	        new cpColorDAO("darkmagenta", 139, 0, 139),
	        new cpColorDAO("darkolivegreen", 85, 107, 47),
	        new cpColorDAO("darkolivegreen1", 202, 255, 112),
	        new cpColorDAO("darkolivegreen2", 188, 238, 104),
	        new cpColorDAO("darkolivegreen3", 162, 205, 90),
	        new cpColorDAO("darkolivegreen4", 110, 139, 61),
	        new cpColorDAO("darkorange", 255, 140, 0),
	        new cpColorDAO("darkorange1", 255, 127, 0),
	        new cpColorDAO("darkorange2", 238, 118, 0),
	        new cpColorDAO("darkorange3", 205, 102, 0),
	        new cpColorDAO("darkorange4", 139, 69, 0),
	        new cpColorDAO("darkorchid", 153, 50, 204),
	        new cpColorDAO("darkorchid1", 191, 62, 255),
	        new cpColorDAO("darkorchid2", 178, 58, 238),
	        new cpColorDAO("darkorchid3", 154, 50, 205),
	        new cpColorDAO("darkorchid4", 104, 34, 139),
	        new cpColorDAO("darkred", 139, 0, 0),
	        new cpColorDAO("darksalmon", 233, 150, 122),
	        new cpColorDAO("darkseagreen", 143, 188, 143),
	        new cpColorDAO("darkseagreen1", 193, 255, 193),
	        new cpColorDAO("darkseagreen2", 180, 238, 180),
	        new cpColorDAO("darkseagreen3", 155, 205, 155),
	        new cpColorDAO("darkseagreen4", 105, 139, 105),
	        new cpColorDAO("darkslateblue", 72, 61, 139),
	        new cpColorDAO("darkslategray", 47, 79, 79),
	        new cpColorDAO("darkslategray1", 151, 255, 255),
	        new cpColorDAO("darkslategray2", 141, 238, 238),
	        new cpColorDAO("darkslategray3", 121, 205, 205),
	        new cpColorDAO("darkslategray4", 82, 139, 139),
	        new cpColorDAO("darkslategrey", 47, 79, 79),
	        new cpColorDAO("darkturquoise", 0, 206, 209),
	        new cpColorDAO("darkviolet", 148, 0, 211),
	        new cpColorDAO("deep pink", 255, 20, 147),
	        new cpColorDAO("deep sky blue", 0, 191, 255),
	        new cpColorDAO("deeppink", 255, 20, 147),
	        new cpColorDAO("deeppink1", 255, 20, 147),
	        new cpColorDAO("deeppink2", 238, 18, 137),
	        new cpColorDAO("deeppink3", 205, 16, 118),
	        new cpColorDAO("deeppink4", 139, 10, 80),
	        new cpColorDAO("deepskyblue", 0, 191, 255),
	        new cpColorDAO("deepskyblue1", 0, 191, 255),
	        new cpColorDAO("deepskyblue2", 0, 178, 238),
	        new cpColorDAO("deepskyblue3", 0, 154, 205),
	        new cpColorDAO("deepskyblue4", 0, 104, 139),
	        new cpColorDAO("dim gray", 105, 105, 105),
	        new cpColorDAO("dim grey", 105, 105, 105),
	        new cpColorDAO("dimgray", 105, 105, 105),
	        new cpColorDAO("dimgrey", 105, 105, 105),
	        new cpColorDAO("dodger blue", 30, 144, 255),
	        new cpColorDAO("dodgerblue", 30, 144, 255),
	        new cpColorDAO("dodgerblue1", 30, 144, 255),
	        new cpColorDAO("dodgerblue2", 28, 134, 238),
	        new cpColorDAO("dodgerblue3", 24, 116, 205),
	        new cpColorDAO("dodgerblue4", 16, 78, 139),
	        new cpColorDAO("firebrick", 178, 34, 34),
	        new cpColorDAO("firebrick1", 255, 48, 48),
	        new cpColorDAO("firebrick2", 238, 44, 44),
	        new cpColorDAO("firebrick3", 205, 38, 38),
	        new cpColorDAO("firebrick4", 139, 26, 26),
	        new cpColorDAO("floral white", 255, 250, 240),
	        new cpColorDAO("floralwhite", 255, 250, 240),
	        new cpColorDAO("forest green", 34, 139, 34),
	        new cpColorDAO("forestgreen", 34, 139, 34),
	        new cpColorDAO("gainsboro", 220, 220, 220),
	        new cpColorDAO("ghost white", 248, 248, 255),
	        new cpColorDAO("ghostwhite", 248, 248, 255),
	        new cpColorDAO("gold", 255, 215, 0),
	        new cpColorDAO("gold1", 255, 215, 0),
	        new cpColorDAO("gold2", 238, 201, 0),
	        new cpColorDAO("gold3", 205, 173, 0),
	        new cpColorDAO("gold4", 139, 117, 0),
	        new cpColorDAO("goldenrod", 218, 165, 32),
	        new cpColorDAO("goldenrod1", 255, 193, 37),
	        new cpColorDAO("goldenrod2", 238, 180, 34),
	        new cpColorDAO("goldenrod3", 205, 155, 29),
	        new cpColorDAO("goldenrod4", 139, 105, 20),
	        new cpColorDAO("gray", 190, 190, 190),
	        new cpColorDAO("gray0", 0, 0, 0),
	        new cpColorDAO("gray1", 3, 3, 3),
	        new cpColorDAO("gray10", 26, 26, 26),
	        new cpColorDAO("gray100", 255, 255, 255),
	        new cpColorDAO("gray11", 28, 28, 28),
	        new cpColorDAO("gray12", 31, 31, 31),
	        new cpColorDAO("gray13", 33, 33, 33),
	        new cpColorDAO("gray14", 36, 36, 36),
	        new cpColorDAO("gray15", 38, 38, 38),
	        new cpColorDAO("gray16", 41, 41, 41),
	        new cpColorDAO("gray17", 43, 43, 43),
	        new cpColorDAO("gray18", 46, 46, 46),
	        new cpColorDAO("gray19", 48, 48, 48),
	        new cpColorDAO("gray2", 5, 5, 5),
	        new cpColorDAO("gray20", 51, 51, 51),
	        new cpColorDAO("gray21", 54, 54, 54),
	        new cpColorDAO("gray22", 56, 56, 56),
	        new cpColorDAO("gray23", 59, 59, 59),
	        new cpColorDAO("gray24", 61, 61, 61),
	        new cpColorDAO("gray25", 64, 64, 64),
	        new cpColorDAO("gray26", 66, 66, 66),
	        new cpColorDAO("gray27", 69, 69, 69),
	        new cpColorDAO("gray28", 71, 71, 71),
	        new cpColorDAO("gray29", 74, 74, 74),
	        new cpColorDAO("gray3", 8, 8, 8),
	        new cpColorDAO("gray30", 77, 77, 77),
	        new cpColorDAO("gray31", 79, 79, 79),
	        new cpColorDAO("gray32", 82, 82, 82),
	        new cpColorDAO("gray33", 84, 84, 84),
	        new cpColorDAO("gray34", 87, 87, 87),
	        new cpColorDAO("gray35", 89, 89, 89),
	        new cpColorDAO("gray36", 92, 92, 92),
	        new cpColorDAO("gray37", 94, 94, 94),
	        new cpColorDAO("gray38", 97, 97, 97),
	        new cpColorDAO("gray39", 99, 99, 99),
	        new cpColorDAO("gray4", 10, 10, 10),
	        new cpColorDAO("gray40", 102, 102, 102),
	        new cpColorDAO("gray41", 105, 105, 105),
	        new cpColorDAO("gray42", 107, 107, 107),
	        new cpColorDAO("gray43", 110, 110, 110),
	        new cpColorDAO("gray44", 112, 112, 112),
	        new cpColorDAO("gray45", 115, 115, 115),
	        new cpColorDAO("gray46", 117, 117, 117),
	        new cpColorDAO("gray47", 120, 120, 120),
	        new cpColorDAO("gray48", 122, 122, 122),
	        new cpColorDAO("gray49", 125, 125, 125),
	        new cpColorDAO("gray5", 13, 13, 13),
	        new cpColorDAO("gray50", 127, 127, 127),
	        new cpColorDAO("gray51", 130, 130, 130),
	        new cpColorDAO("gray52", 133, 133, 133),
	        new cpColorDAO("gray53", 135, 135, 135),
	        new cpColorDAO("gray54", 138, 138, 138),
	        new cpColorDAO("gray55", 140, 140, 140),
	        new cpColorDAO("gray56", 143, 143, 143),
	        new cpColorDAO("gray57", 145, 145, 145),
	        new cpColorDAO("gray58", 148, 148, 148),
	        new cpColorDAO("gray59", 150, 150, 150),
	        new cpColorDAO("gray6", 15, 15, 15),
	        new cpColorDAO("gray60", 153, 153, 153),
	        new cpColorDAO("gray61", 156, 156, 156),
	        new cpColorDAO("gray62", 158, 158, 158),
	        new cpColorDAO("gray63", 161, 161, 161),
	        new cpColorDAO("gray64", 163, 163, 163),
	        new cpColorDAO("gray65", 166, 166, 166),
	        new cpColorDAO("gray66", 168, 168, 168),
	        new cpColorDAO("gray67", 171, 171, 171),
	        new cpColorDAO("gray68", 173, 173, 173),
	        new cpColorDAO("gray69", 176, 176, 176),
	        new cpColorDAO("gray7", 18, 18, 18),
	        new cpColorDAO("gray70", 179, 179, 179),
	        new cpColorDAO("gray71", 181, 181, 181),
	        new cpColorDAO("gray72", 184, 184, 184),
	        new cpColorDAO("gray73", 186, 186, 186),
	        new cpColorDAO("gray74", 189, 189, 189),
	        new cpColorDAO("gray75", 191, 191, 191),
	        new cpColorDAO("gray76", 194, 194, 194),
	        new cpColorDAO("gray77", 196, 196, 196),
	        new cpColorDAO("gray78", 199, 199, 199),
	        new cpColorDAO("gray79", 201, 201, 201),
	        new cpColorDAO("gray8", 20, 20, 20),
	        new cpColorDAO("gray80", 204, 204, 204),
	        new cpColorDAO("gray81", 207, 207, 207),
	        new cpColorDAO("gray82", 209, 209, 209),
	        new cpColorDAO("gray83", 212, 212, 212),
	        new cpColorDAO("gray84", 214, 214, 214),
	        new cpColorDAO("gray85", 217, 217, 217),
	        new cpColorDAO("gray86", 219, 219, 219),
	        new cpColorDAO("gray87", 222, 222, 222),
	        new cpColorDAO("gray88", 224, 224, 224),
	        new cpColorDAO("gray89", 227, 227, 227),
	        new cpColorDAO("gray9", 23, 23, 23),
	        new cpColorDAO("gray90", 229, 229, 229),
	        new cpColorDAO("gray91", 232, 232, 232),
	        new cpColorDAO("gray92", 235, 235, 235),
	        new cpColorDAO("gray93", 237, 237, 237),
	        new cpColorDAO("gray94", 240, 240, 240),
	        new cpColorDAO("gray95", 242, 242, 242),
	        new cpColorDAO("gray96", 245, 245, 245),
	        new cpColorDAO("gray97", 247, 247, 247),
	        new cpColorDAO("gray98", 250, 250, 250),
	        new cpColorDAO("gray99", 252, 252, 252),
	        new cpColorDAO("green", 0, 255, 0),
	        new cpColorDAO("green yellow", 173, 255, 47),
	        new cpColorDAO("green1", 0, 255, 0),
	        new cpColorDAO("green2", 0, 238, 0),
	        new cpColorDAO("green3", 0, 205, 0),
	        new cpColorDAO("green4", 0, 139, 0),
	        new cpColorDAO("greenyellow", 173, 255, 47),
	        new cpColorDAO("grey", 190, 190, 190),
	        new cpColorDAO("grey0", 0, 0, 0),
	        new cpColorDAO("grey1", 3, 3, 3),
	        new cpColorDAO("grey10", 26, 26, 26),
	        new cpColorDAO("grey100", 255, 255, 255),
	        new cpColorDAO("grey11", 28, 28, 28),
	        new cpColorDAO("grey12", 31, 31, 31),
	        new cpColorDAO("grey13", 33, 33, 33),
	        new cpColorDAO("grey14", 36, 36, 36),
	        new cpColorDAO("grey15", 38, 38, 38),
	        new cpColorDAO("grey16", 41, 41, 41),
	        new cpColorDAO("grey17", 43, 43, 43),
	        new cpColorDAO("grey18", 46, 46, 46),
	        new cpColorDAO("grey19", 48, 48, 48),
	        new cpColorDAO("grey2", 5, 5, 5),
	        new cpColorDAO("grey20", 51, 51, 51),
	        new cpColorDAO("grey21", 54, 54, 54),
	        new cpColorDAO("grey22", 56, 56, 56),
	        new cpColorDAO("grey23", 59, 59, 59),
	        new cpColorDAO("grey24", 61, 61, 61),
	        new cpColorDAO("grey25", 64, 64, 64),
	        new cpColorDAO("grey26", 66, 66, 66),
	        new cpColorDAO("grey27", 69, 69, 69),
	        new cpColorDAO("grey28", 71, 71, 71),
	        new cpColorDAO("grey29", 74, 74, 74),
	        new cpColorDAO("grey3", 8, 8, 8),
	        new cpColorDAO("grey30", 77, 77, 77),
	        new cpColorDAO("grey31", 79, 79, 79),
	        new cpColorDAO("grey32", 82, 82, 82),
	        new cpColorDAO("grey33", 84, 84, 84),
	        new cpColorDAO("grey34", 87, 87, 87),
	        new cpColorDAO("grey35", 89, 89, 89),
	        new cpColorDAO("grey36", 92, 92, 92),
	        new cpColorDAO("grey37", 94, 94, 94),
	        new cpColorDAO("grey38", 97, 97, 97),
	        new cpColorDAO("grey39", 99, 99, 99),
	        new cpColorDAO("grey4", 10, 10, 10),
	        new cpColorDAO("grey40", 102, 102, 102),
	        new cpColorDAO("grey41", 105, 105, 105),
	        new cpColorDAO("grey42", 107, 107, 107),
	        new cpColorDAO("grey43", 110, 110, 110),
	        new cpColorDAO("grey44", 112, 112, 112),
	        new cpColorDAO("grey45", 115, 115, 115),
	        new cpColorDAO("grey46", 117, 117, 117),
	        new cpColorDAO("grey47", 120, 120, 120),
	        new cpColorDAO("grey48", 122, 122, 122),
	        new cpColorDAO("grey49", 125, 125, 125),
	        new cpColorDAO("grey5", 13, 13, 13),
	        new cpColorDAO("grey50", 127, 127, 127),
	        new cpColorDAO("grey51", 130, 130, 130),
	        new cpColorDAO("grey52", 133, 133, 133),
	        new cpColorDAO("grey53", 135, 135, 135),
	        new cpColorDAO("grey54", 138, 138, 138),
	        new cpColorDAO("grey55", 140, 140, 140),
	        new cpColorDAO("grey56", 143, 143, 143),
	        new cpColorDAO("grey57", 145, 145, 145),
	        new cpColorDAO("grey58", 148, 148, 148),
	        new cpColorDAO("grey59", 150, 150, 150),
	        new cpColorDAO("grey6", 15, 15, 15),
	        new cpColorDAO("grey60", 153, 153, 153),
	        new cpColorDAO("grey61", 156, 156, 156),
	        new cpColorDAO("grey62", 158, 158, 158),
	        new cpColorDAO("grey63", 161, 161, 161),
	        new cpColorDAO("grey64", 163, 163, 163),
	        new cpColorDAO("grey65", 166, 166, 166),
	        new cpColorDAO("grey66", 168, 168, 168),
	        new cpColorDAO("grey67", 171, 171, 171),
	        new cpColorDAO("grey68", 173, 173, 173),
	        new cpColorDAO("grey69", 176, 176, 176),
	        new cpColorDAO("grey7", 18, 18, 18),
	        new cpColorDAO("grey70", 179, 179, 179),
	        new cpColorDAO("grey71", 181, 181, 181),
	        new cpColorDAO("grey72", 184, 184, 184),
	        new cpColorDAO("grey73", 186, 186, 186),
	        new cpColorDAO("grey74", 189, 189, 189),
	        new cpColorDAO("grey75", 191, 191, 191),
	        new cpColorDAO("grey76", 194, 194, 194),
	        new cpColorDAO("grey77", 196, 196, 196),
	        new cpColorDAO("grey78", 199, 199, 199),
	        new cpColorDAO("grey79", 201, 201, 201),
	        new cpColorDAO("grey8", 20, 20, 20),
	        new cpColorDAO("grey80", 204, 204, 204),
	        new cpColorDAO("grey81", 207, 207, 207),
	        new cpColorDAO("grey82", 209, 209, 209),
	        new cpColorDAO("grey83", 212, 212, 212),
	        new cpColorDAO("grey84", 214, 214, 214),
	        new cpColorDAO("grey85", 217, 217, 217),
	        new cpColorDAO("grey86", 219, 219, 219),
	        new cpColorDAO("grey87", 222, 222, 222),
	        new cpColorDAO("grey88", 224, 224, 224),
	        new cpColorDAO("grey89", 227, 227, 227),
	        new cpColorDAO("grey9", 23, 23, 23),
	        new cpColorDAO("grey90", 229, 229, 229),
	        new cpColorDAO("grey91", 232, 232, 232),
	        new cpColorDAO("grey92", 235, 235, 235),
	        new cpColorDAO("grey93", 237, 237, 237),
	        new cpColorDAO("grey94", 240, 240, 240),
	        new cpColorDAO("grey95", 242, 242, 242),
	        new cpColorDAO("grey96", 245, 245, 245),
	        new cpColorDAO("grey97", 247, 247, 247),
	        new cpColorDAO("grey98", 250, 250, 250),
	        new cpColorDAO("grey99", 252, 252, 252),
	        new cpColorDAO("honeydew", 240, 255, 240),
	        new cpColorDAO("honeydew1", 240, 255, 240),
	        new cpColorDAO("honeydew2", 224, 238, 224),
	        new cpColorDAO("honeydew3", 193, 205, 193),
	        new cpColorDAO("honeydew4", 131, 139, 131),
	        new cpColorDAO("hot pink", 255, 105, 180),
	        new cpColorDAO("hotpink", 255, 105, 180),
	        new cpColorDAO("hotpink1", 255, 110, 180),
	        new cpColorDAO("hotpink2", 238, 106, 167),
	        new cpColorDAO("hotpink3", 205, 96, 144),
	        new cpColorDAO("hotpink4", 139, 58, 98),
	        new cpColorDAO("indian red", 205, 92, 92),
	        new cpColorDAO("indianred", 205, 92, 92),
	        new cpColorDAO("indianred1", 255, 106, 106),
	        new cpColorDAO("indianred2", 238, 99, 99),
	        new cpColorDAO("indianred3", 205, 85, 85),
	        new cpColorDAO("indianred4", 139, 58, 58),
	        new cpColorDAO("ivory", 255, 255, 240),
	        new cpColorDAO("ivory1", 255, 255, 240),
	        new cpColorDAO("ivory2", 238, 238, 224),
	        new cpColorDAO("ivory3", 205, 205, 193),
	        new cpColorDAO("ivory4", 139, 139, 131),
	        new cpColorDAO("khaki", 240, 230, 140),
	        new cpColorDAO("khaki1", 255, 246, 143),
	        new cpColorDAO("khaki2", 238, 230, 133),
	        new cpColorDAO("khaki3", 205, 198, 115),
	        new cpColorDAO("khaki4", 139, 134, 78),
	        new cpColorDAO("lavender", 230, 230, 250),
	        new cpColorDAO("lavender blush", 255, 240, 245),
	        new cpColorDAO("lavenderblush", 255, 240, 245),
	        new cpColorDAO("lavenderblush1", 255, 240, 245),
	        new cpColorDAO("lavenderblush2", 238, 224, 229),
	        new cpColorDAO("lavenderblush3", 205, 193, 197),
	        new cpColorDAO("lavenderblush4", 139, 131, 134),
	        new cpColorDAO("lawn green", 124, 252, 0),
	        new cpColorDAO("lawngreen", 124, 252, 0),
	        new cpColorDAO("lemon chiffon", 255, 250, 205),
	        new cpColorDAO("lemonchiffon", 255, 250, 205),
	        new cpColorDAO("lemonchiffon1", 255, 250, 205),
	        new cpColorDAO("lemonchiffon2", 238, 233, 191),
	        new cpColorDAO("lemonchiffon3", 205, 201, 165),
	        new cpColorDAO("lemonchiffon4", 139, 137, 112),
	        new cpColorDAO("light blue", 173, 216, 230),
	        new cpColorDAO("light coral", 240, 128, 128),
	        new cpColorDAO("light cyan", 224, 255, 255),
	        new cpColorDAO("light goldenrod", 238, 221, 130),
	        new cpColorDAO("light goldenrod yellow", 250, 250, 210),
	        new cpColorDAO("light gray", 211, 211, 211),
	        new cpColorDAO("light green", 144, 238, 144),
	        new cpColorDAO("light grey", 211, 211, 211),
	        new cpColorDAO("light pink", 255, 182, 193),
	        new cpColorDAO("light salmon", 255, 160, 122),
	        new cpColorDAO("light sea green", 32, 178, 170),
	        new cpColorDAO("light sky blue", 135, 206, 250),
	        new cpColorDAO("light slate blue", 132, 112, 255),
	        new cpColorDAO("light slate gray", 119, 136, 153),
	        new cpColorDAO("light slate grey", 119, 136, 153),
	        new cpColorDAO("light steel blue", 176, 196, 222),
	        new cpColorDAO("light yellow", 255, 255, 224),
	        new cpColorDAO("lightblue", 173, 216, 230),
	        new cpColorDAO("lightblue1", 191, 239, 255),
	        new cpColorDAO("lightblue2", 178, 223, 238),
	        new cpColorDAO("lightblue3", 154, 192, 205),
	        new cpColorDAO("lightblue4", 104, 131, 139),
	        new cpColorDAO("lightcoral", 240, 128, 128),
	        new cpColorDAO("lightcyan", 224, 255, 255),
	        new cpColorDAO("lightcyan1", 224, 255, 255),
	        new cpColorDAO("lightcyan2", 209, 238, 238),
	        new cpColorDAO("lightcyan3", 180, 205, 205),
	        new cpColorDAO("lightcyan4", 122, 139, 139),
	        new cpColorDAO("lightgoldenrod", 238, 221, 130),
	        new cpColorDAO("lightgoldenrod1", 255, 236, 139),
	        new cpColorDAO("lightgoldenrod2", 238, 220, 130),
	        new cpColorDAO("lightgoldenrod3", 205, 190, 112),
	        new cpColorDAO("lightgoldenrod4", 139, 129, 76),
	        new cpColorDAO("lightgoldenrodyellow", 250, 250, 210),
	        new cpColorDAO("lightgray", 211, 211, 211),
	        new cpColorDAO("lightgreen", 144, 238, 144),
	        new cpColorDAO("lightgrey", 211, 211, 211),
	        new cpColorDAO("lightpink", 255, 182, 193),
	        new cpColorDAO("lightpink1", 255, 174, 185),
	        new cpColorDAO("lightpink2", 238, 162, 173),
	        new cpColorDAO("lightpink3", 205, 140, 149),
	        new cpColorDAO("lightpink4", 139, 95, 101),
	        new cpColorDAO("lightsalmon", 255, 160, 122),
	        new cpColorDAO("lightsalmon1", 255, 160, 122),
	        new cpColorDAO("lightsalmon2", 238, 149, 114),
	        new cpColorDAO("lightsalmon3", 205, 129, 98),
	        new cpColorDAO("lightsalmon4", 139, 87, 66),
	        new cpColorDAO("lightseagreen", 32, 178, 170),
	        new cpColorDAO("lightskyblue", 135, 206, 250),
	        new cpColorDAO("lightskyblue1", 176, 226, 255),
	        new cpColorDAO("lightskyblue2", 164, 211, 238),
	        new cpColorDAO("lightskyblue3", 141, 182, 205),
	        new cpColorDAO("lightskyblue4", 96, 123, 139),
	        new cpColorDAO("lightslateblue", 132, 112, 255),
	        new cpColorDAO("lightslategray", 119, 136, 153),
	        new cpColorDAO("lightslategrey", 119, 136, 153),
	        new cpColorDAO("lightsteelblue", 176, 196, 222),
	        new cpColorDAO("lightsteelblue1", 202, 225, 255),
	        new cpColorDAO("lightsteelblue2", 188, 210, 238),
	        new cpColorDAO("lightsteelblue3", 162, 181, 205),
	        new cpColorDAO("lightsteelblue4", 110, 123, 139),
	        new cpColorDAO("lightyellow", 255, 255, 224),
	        new cpColorDAO("lightyellow1", 255, 255, 224),
	        new cpColorDAO("lightyellow2", 238, 238, 209),
	        new cpColorDAO("lightyellow3", 205, 205, 180),
	        new cpColorDAO("lightyellow4", 139, 139, 122),
	        new cpColorDAO("lime green", 50, 205, 50),
	        new cpColorDAO("limegreen", 50, 205, 50),
	        new cpColorDAO("linen", 250, 240, 230),
	        new cpColorDAO("magenta", 255, 0, 255),
	        new cpColorDAO("magenta1", 255, 0, 255),
	        new cpColorDAO("magenta2", 238, 0, 238),
	        new cpColorDAO("magenta3", 205, 0, 205),
	        new cpColorDAO("magenta4", 139, 0, 139),
	        new cpColorDAO("maroon", 176, 48, 96),
	        new cpColorDAO("maroon1", 255, 52, 179),
	        new cpColorDAO("maroon2", 238, 48, 167),
	        new cpColorDAO("maroon3", 205, 41, 144),
	        new cpColorDAO("maroon4", 139, 28, 98),
	        new cpColorDAO("medium aquamarine", 102, 205, 170),
	        new cpColorDAO("medium blue", 0, 0, 205),
	        new cpColorDAO("medium orchid", 186, 85, 211),
	        new cpColorDAO("medium purple", 147, 112, 219),
	        new cpColorDAO("medium sea green", 60, 179, 113),
	        new cpColorDAO("medium slate blue", 123, 104, 238),
	        new cpColorDAO("medium spring green", 0, 250, 154),
	        new cpColorDAO("medium turquoise", 72, 209, 204),
	        new cpColorDAO("medium violet red", 199, 21, 133),
	        new cpColorDAO("mediumaquamarine", 102, 205, 170),
	        new cpColorDAO("mediumblue", 0, 0, 205),
	        new cpColorDAO("mediumorchid", 186, 85, 211),
	        new cpColorDAO("mediumorchid1", 224, 102, 255),
	        new cpColorDAO("mediumorchid2", 209, 95, 238),
	        new cpColorDAO("mediumorchid3", 180, 82, 205),
	        new cpColorDAO("mediumorchid4", 122, 55, 139),
	        new cpColorDAO("mediumpurple", 147, 112, 219),
	        new cpColorDAO("mediumpurple1", 171, 130, 255),
	        new cpColorDAO("mediumpurple2", 159, 121, 238),
	        new cpColorDAO("mediumpurple3", 137, 104, 205),
	        new cpColorDAO("mediumpurple4", 93, 71, 139),
	        new cpColorDAO("mediumseagreen", 60, 179, 113),
	        new cpColorDAO("mediumslateblue", 123, 104, 238),
	        new cpColorDAO("mediumspringgreen", 0, 250, 154),
	        new cpColorDAO("mediumturquoise", 72, 209, 204),
	        new cpColorDAO("mediumvioletred", 199, 21, 133),
	        new cpColorDAO("midnight blue", 25, 25, 112),
	        new cpColorDAO("midnightblue", 25, 25, 112),
	        new cpColorDAO("mint cream", 245, 255, 250),
	        new cpColorDAO("mintcream", 245, 255, 250),
	        new cpColorDAO("misty rose", 255, 228, 225),
	        new cpColorDAO("mistyrose", 255, 228, 225),
	        new cpColorDAO("mistyrose1", 255, 228, 225),
	        new cpColorDAO("mistyrose2", 238, 213, 210),
	        new cpColorDAO("mistyrose3", 205, 183, 181),
	        new cpColorDAO("mistyrose4", 139, 125, 123),
	        new cpColorDAO("moccasin", 255, 228, 181),
	        new cpColorDAO("navajo white", 255, 222, 173),
	        new cpColorDAO("navajowhite", 255, 222, 173),
	        new cpColorDAO("navajowhite1", 255, 222, 173),
	        new cpColorDAO("navajowhite2", 238, 207, 161),
	        new cpColorDAO("navajowhite3", 205, 179, 139),
	        new cpColorDAO("navajowhite4", 139, 121, 94),
	        new cpColorDAO("navy", 0, 0, 128),
	        new cpColorDAO("navy blue", 0, 0, 128),
	        new cpColorDAO("navyblue", 0, 0, 128),
	        new cpColorDAO("old lace", 253, 245, 230),
	        new cpColorDAO("oldlace", 253, 245, 230),
	        new cpColorDAO("olive drab", 107, 142, 35),
	        new cpColorDAO("olivedrab", 107, 142, 35),
	        new cpColorDAO("olivedrab1", 192, 255, 62),
	        new cpColorDAO("olivedrab2", 179, 238, 58),
	        new cpColorDAO("olivedrab3", 154, 205, 50),
	        new cpColorDAO("olivedrab4", 105, 139, 34),
	        new cpColorDAO("orange", 255, 165, 0),
	        new cpColorDAO("orange red", 255, 69, 0),
	        new cpColorDAO("orange1", 255, 165, 0),
	        new cpColorDAO("orange2", 238, 154, 0),
	        new cpColorDAO("orange3", 205, 133, 0),
	        new cpColorDAO("orange4", 139, 90, 0),
	        new cpColorDAO("orangered", 255, 69, 0),
	        new cpColorDAO("orangered1", 255, 69, 0),
	        new cpColorDAO("orangered2", 238, 64, 0),
	        new cpColorDAO("orangered3", 205, 55, 0),
	        new cpColorDAO("orangered4", 139, 37, 0),
	        new cpColorDAO("orchid", 218, 112, 214),
	        new cpColorDAO("orchid1", 255, 131, 250),
	        new cpColorDAO("orchid2", 238, 122, 233),
	        new cpColorDAO("orchid3", 205, 105, 201),
	        new cpColorDAO("orchid4", 139, 71, 137),
	        new cpColorDAO("pale goldenrod", 238, 232, 170),
	        new cpColorDAO("pale green", 152, 251, 152),
	        new cpColorDAO("pale turquoise", 175, 238, 238),
	        new cpColorDAO("pale violet red", 219, 112, 147),
	        new cpColorDAO("palegoldenrod", 238, 232, 170),
	        new cpColorDAO("palegreen", 152, 251, 152),
	        new cpColorDAO("palegreen1", 154, 255, 154),
	        new cpColorDAO("palegreen2", 144, 238, 144),
	        new cpColorDAO("palegreen3", 124, 205, 124),
	        new cpColorDAO("palegreen4", 84, 139, 84),
	        new cpColorDAO("paleturquoise", 175, 238, 238),
	        new cpColorDAO("paleturquoise1", 187, 255, 255),
	        new cpColorDAO("paleturquoise2", 174, 238, 238),
	        new cpColorDAO("paleturquoise3", 150, 205, 205),
	        new cpColorDAO("paleturquoise4", 102, 139, 139),
	        new cpColorDAO("palevioletred", 219, 112, 147),
	        new cpColorDAO("palevioletred1", 255, 130, 171),
	        new cpColorDAO("palevioletred2", 238, 121, 159),
	        new cpColorDAO("palevioletred3", 205, 104, 137),
	        new cpColorDAO("palevioletred4", 139, 71, 93),
	        new cpColorDAO("papaya whip", 255, 239, 213),
	        new cpColorDAO("papayawhip", 255, 239, 213),
	        new cpColorDAO("peach puff", 255, 218, 185),
	        new cpColorDAO("peachpuff", 255, 218, 185),
	        new cpColorDAO("peachpuff1", 255, 218, 185),
	        new cpColorDAO("peachpuff2", 238, 203, 173),
	        new cpColorDAO("peachpuff3", 205, 175, 149),
	        new cpColorDAO("peachpuff4", 139, 119, 101),
	        new cpColorDAO("peru", 205, 133, 63),
	        new cpColorDAO("pink", 255, 192, 203),
	        new cpColorDAO("pink1", 255, 181, 197),
	        new cpColorDAO("pink2", 238, 169, 184),
	        new cpColorDAO("pink3", 205, 145, 158),
	        new cpColorDAO("pink4", 139, 99, 108),
	        new cpColorDAO("plum", 221, 160, 221),
	        new cpColorDAO("plum1", 255, 187, 255),
	        new cpColorDAO("plum2", 238, 174, 238),
	        new cpColorDAO("plum3", 205, 150, 205),
	        new cpColorDAO("plum4", 139, 102, 139),
	        new cpColorDAO("powder blue", 176, 224, 230),
	        new cpColorDAO("powderblue", 176, 224, 230),
	        new cpColorDAO("purple", 160, 32, 240),
	        new cpColorDAO("purple1", 155, 48, 255),
	        new cpColorDAO("purple2", 145, 44, 238),
	        new cpColorDAO("purple3", 125, 38, 205),
	        new cpColorDAO("purple4", 85, 26, 139),
	        new cpColorDAO("red", 255, 0, 0),
	        new cpColorDAO("red1", 255, 0, 0),
	        new cpColorDAO("red2", 238, 0, 0),
	        new cpColorDAO("red3", 205, 0, 0),
	        new cpColorDAO("red4", 139, 0, 0),
	        new cpColorDAO("rosy brown", 188, 143, 143),
	        new cpColorDAO("rosybrown", 188, 143, 143),
	        new cpColorDAO("rosybrown1", 255, 193, 193),
	        new cpColorDAO("rosybrown2", 238, 180, 180),
	        new cpColorDAO("rosybrown3", 205, 155, 155),
	        new cpColorDAO("rosybrown4", 139, 105, 105),
	        new cpColorDAO("royal blue", 65, 105, 225),
	        new cpColorDAO("royalblue", 65, 105, 225),
	        new cpColorDAO("royalblue1", 72, 118, 255),
	        new cpColorDAO("royalblue2", 67, 110, 238),
	        new cpColorDAO("royalblue3", 58, 95, 205),
	        new cpColorDAO("royalblue4", 39, 64, 139),
	        new cpColorDAO("saddle brown", 139, 69, 19),
	        new cpColorDAO("saddlebrown", 139, 69, 19),
	        new cpColorDAO("salmon", 250, 128, 114),
	        new cpColorDAO("salmon1", 255, 140, 105),
	        new cpColorDAO("salmon2", 238, 130, 98),
	        new cpColorDAO("salmon3", 205, 112, 84),
	        new cpColorDAO("salmon4", 139, 76, 57),
	        new cpColorDAO("sandy brown", 244, 164, 96),
	        new cpColorDAO("sandybrown", 244, 164, 96),
	        new cpColorDAO("sea green", 46, 139, 87),
	        new cpColorDAO("seagreen", 46, 139, 87),
	        new cpColorDAO("seagreen1", 84, 255, 159),
	        new cpColorDAO("seagreen2", 78, 238, 148),
	        new cpColorDAO("seagreen3", 67, 205, 128),
	        new cpColorDAO("seagreen4", 46, 139, 87),
	        new cpColorDAO("seashell", 255, 245, 238),
	        new cpColorDAO("seashell1", 255, 245, 238),
	        new cpColorDAO("seashell2", 238, 229, 222),
	        new cpColorDAO("seashell3", 205, 197, 191),
	        new cpColorDAO("seashell4", 139, 134, 130),
	        new cpColorDAO("sienna", 160, 82, 45),
	        new cpColorDAO("sienna1", 255, 130, 71),
	        new cpColorDAO("sienna2", 238, 121, 66),
	        new cpColorDAO("sienna3", 205, 104, 57),
	        new cpColorDAO("sienna4", 139, 71, 38),
	        new cpColorDAO("sky blue", 135, 206, 235),
	        new cpColorDAO("skyblue", 135, 206, 235),
	        new cpColorDAO("skyblue1", 135, 206, 255),
	        new cpColorDAO("skyblue2", 126, 192, 238),
	        new cpColorDAO("skyblue3", 108, 166, 205),
	        new cpColorDAO("skyblue4", 74, 112, 139),
	        new cpColorDAO("slate blue", 106, 90, 205),
	        new cpColorDAO("slate gray", 112, 128, 144),
	        new cpColorDAO("slate grey", 112, 128, 144),
	        new cpColorDAO("slateblue", 106, 90, 205),
	        new cpColorDAO("slateblue1", 131, 111, 255),
	        new cpColorDAO("slateblue2", 122, 103, 238),
	        new cpColorDAO("slateblue3", 105, 89, 205),
	        new cpColorDAO("slateblue4", 71, 60, 139),
	        new cpColorDAO("slategray", 112, 128, 144),
	        new cpColorDAO("slategray1", 198, 226, 255),
	        new cpColorDAO("slategray2", 185, 211, 238),
	        new cpColorDAO("slategray3", 159, 182, 205),
	        new cpColorDAO("slategray4", 108, 123, 139),
	        new cpColorDAO("slategrey", 112, 128, 144),
	        new cpColorDAO("snow", 255, 250, 250),
	        new cpColorDAO("snow1", 255, 250, 250),
	        new cpColorDAO("snow2", 238, 233, 233),
	        new cpColorDAO("snow3", 205, 201, 201),
	        new cpColorDAO("snow4", 139, 137, 137),
	        new cpColorDAO("spring green", 0, 255, 127),
	        new cpColorDAO("springgreen", 0, 255, 127),
	        new cpColorDAO("springgreen1", 0, 255, 127),
	        new cpColorDAO("springgreen2", 0, 238, 118),
	        new cpColorDAO("springgreen3", 0, 205, 102),
	        new cpColorDAO("springgreen4", 0, 139, 69),
	        new cpColorDAO("steel blue", 70, 130, 180),
	        new cpColorDAO("steelblue", 70, 130, 180),
	        new cpColorDAO("steelblue1", 99, 184, 255),
	        new cpColorDAO("steelblue2", 92, 172, 238),
	        new cpColorDAO("steelblue3", 79, 148, 205),
	        new cpColorDAO("steelblue4", 54, 100, 139),
	        new cpColorDAO("tan", 210, 180, 140),
	        new cpColorDAO("tan1", 255, 165, 79),
	        new cpColorDAO("tan2", 238, 154, 73),
	        new cpColorDAO("tan3", 205, 133, 63),
	        new cpColorDAO("tan4", 139, 90, 43),
	        new cpColorDAO("thistle", 216, 191, 216),
	        new cpColorDAO("thistle1", 255, 225, 255),
	        new cpColorDAO("thistle2", 238, 210, 238),
	        new cpColorDAO("thistle3", 205, 181, 205),
	        new cpColorDAO("thistle4", 139, 123, 139),
	        new cpColorDAO("tomato", 255, 99, 71),
	        new cpColorDAO("tomato1", 255, 99, 71),
	        new cpColorDAO("tomato2", 238, 92, 66),
	        new cpColorDAO("tomato3", 205, 79, 57),
	        new cpColorDAO("tomato4", 139, 54, 38),
	        new cpColorDAO("turquoise", 64, 224, 208),
	        new cpColorDAO("turquoise1", 0, 245, 255),
	        new cpColorDAO("turquoise2", 0, 229, 238),
	        new cpColorDAO("turquoise3", 0, 197, 205),
	        new cpColorDAO("turquoise4", 0, 134, 139),
	        new cpColorDAO("violet", 238, 130, 238),
	        new cpColorDAO("violet red", 208, 32, 144),
	        new cpColorDAO("violetred", 208, 32, 144),
	        new cpColorDAO("violetred1", 255, 62, 150),
	        new cpColorDAO("violetred2", 238, 58, 140),
	        new cpColorDAO("violetred3", 205, 50, 120),
	        new cpColorDAO("violetred4", 139, 34, 82),
	        new cpColorDAO("wheat", 245, 222, 179),
	        new cpColorDAO("wheat1", 255, 231, 186),
	        new cpColorDAO("wheat2", 238, 216, 174),
	        new cpColorDAO("wheat3", 205, 186, 150),
	        new cpColorDAO("wheat4", 139, 126, 102),
	        new cpColorDAO("white", 255, 255, 255),
	        new cpColorDAO("white smoke", 245, 245, 245),
	        new cpColorDAO("whitesmoke", 245, 245, 245),
	        new cpColorDAO("yellow", 255, 255, 0),
	        new cpColorDAO("yellow green", 154, 205, 50),
	        new cpColorDAO("yellow1", 255, 255, 0),
	        new cpColorDAO("yellow2", 238, 238, 0),
	        new cpColorDAO("yellow3", 205, 205, 0),
	        new cpColorDAO("yellow4", 139, 139, 0),
	        new cpColorDAO("yellowgreen", 154, 205, 5)

	    };

	    /*
		# Sun Public License Notice
		#
		# The contents of this file are subject to the Sun Public License
		# Version 1.0 (the "License"). You may not use this file except in
		# compliance with the License. A copy of the License is available at
		# http://www.sun.com/
		#
		# The Original Code is NetBeans. The Initial Developer of the Original
		# Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
		# Microsystems, Inc. All Rights Reserved.
		#Palette names
		basic=Primary colors
		svg=SVG/X11 color constants
		swing=Swing constants
		system=System colors
		recent=Recent colors
		#SVG/X11 Color constants
		# 240,248,255
		aliceblue=Alice blue
		# 250,235,215
		antiquewhite=Antique white
		# 0,255,255
		aqua=Aqua
		# 127,255,212
		aquamarine=Aquamarine
		# 240,255,255
		azure=Azure
		# 245,245,220
		beige=Beige
		# 255,228,196
		bisque=Bisque
		# 0,0,0
		black=Black
		# 255,235,205
		blanchedalmond=Blanched almond
		# 0,0,255
		blue=Blue
		# 138,43,226
		blueviolet=Blue violet
		# 165,42,42
		brown=Brown
		# 222,184,135
		burlywood=Burly wood
		# 95,158,160
		cadetblue=Cadet blue
		# 127,255,0
		chartreuse=Chartreuse
		# 210,105,30
		chocolate=Chocolate
		# 255,127,80
		coral=Coral
		# 100,149,237
		cornflowerblue=Cornflower blue
		# 255,248,220
		cornsilk=Corn silk
		# 220,20,60
		crimson=Crimson
		# 0,255,255
		cyan=Cyan
		# 0,0,139
		darkblue=Dark blue
		# 0,139,139
		darkcyan=Dark cyan
		# 184,134,11
		darkgoldenrod=Dark goldenrod
		# 169,169,169
		darkgray=Dark gray
		# 0,100,0
		darkgreen=Dark green
		# 169,169,169
		darkgrey=Dark grey
		# 189,183,107
		darkkhaki=Dark khaki
		# 139,0,139
		darkmagenta=Dark magenta
		# 85,107,47
		darkolivegreen=Dark olive green
		# 255,140,0
		darkorange=Dark orange
		# 153,50,204
		darkorchid=Dark orchid
		# 139,0,0
		darkred=Dark red
		# 233,150,122
		darksalmon=Dark salmon
		# 143,188,143
		darkseagreen=Dark sea green
		# 72,61,139
		darkslateblue=Dark slate blue
		# 47,79,79
		darkslategray=Dark slate gray
		# 47,79,79
		darkslategrey=Dark slate grey
		# 0,206,209
		darkturquoise=Dark turquoise
		# 148,0,211
		darkviolet=Dark violet
		# 255,20,147
		deeppink=Deep pink
		# 0,191,255
		deepskyblue=Deep sky blue
		# 105,105,105
		dimgray=Dim gray
		# 105,105,105
		dimgrey=Dim grey
		# 30,144,255
		dodgerblue=Dodger blue
		# 178,34,34
		firebrick=Fire brick
		# 255,250,240
		floralwhite=Floral white
		# 34,139,34
		forestgreen=Forest green
		# 255,0,255
		fuchsia=Fuchsia
		# 220,220,220
		gainsboro=Gainsboro
		# 248,248,255
		ghostwhite=Ghost white
		# 255,215,0
		gold=Gold
		# 218,165,32
		goldenrod=Goldenrod
		# 128,128,128
		gray=Gray
		# 128,128,128
		grey=Grey
		# 0,128,0
		green=Green
		# 173,255,47
		greenyellow=Green yellow
		# 240,255,240
		honeydew=Honeydew
		# 255,105,180
		hotpink=Hot pink
		# 205,92,92
		indianred=Indian red
		# 75,0,130
		indigo=Indigo
		# 255,255,240
		ivory=Ivory
		# 240,230,140
		khaki=Khaki
		# 230,230,250
		lavender=Lavender
		# 255,240,245
		lavenderblush=Lavender blush
		# 124,252,0
		lawngreen=Lawn green
		# 255,250,205
		lemonchiffon=Lemon chiffon
		# 173,216,230
		lightblue=Light blue
		# 240,128,128
		lightcoral=Light coral
		# 224,255,255
		lightcyan=Light cyan
		# 250,250,210
		lightgoldenrodyellow=Light goldenrod yellow
		# 211,211,211
		lightgray=Light gray
		# 144,238,144
		lightgreen=Light green
		# 211,211,211
		lightgrey=Light grey
		# 255,182,193
		lightpink=Light pink
		# 255,160,122
		lightsalmon=Light salmon
		# 32,178,170
		lightseagreen=Light sea green
		# 135,206,250
		lightskyblue=Light sky blue
		# 119,136,153
		lightslategray=Light slate gray
		# 119,136,153
		lightslategrey=Light slate grey
		# 176,196,222
		lightsteelblue=Light steel blue
		# 255,255,224
		lightyellow=Light yellow
		# 0,255,0
		lime=Lime
		# 50,205,50
		limegreen=Lime green
		# 250,240,230
		linen=Linen
		# 255,0,255
		magenta=Magenta
		# 128,0,0
		maroon=Maroon
		# 102,205,170
		mediumaquamarine=Medium aquamarine
		# 0,0,205
		mediumblue=Medium blue
		# 186,85,211
		mediumorchid=Medium orchid
		# 147,112,219
		mediumpurple=Medium purple
		# 60,179,113
		mediumseagreen=Medium sea green
		# 123,104,238
		mediumslateblue=Medium slate blue
		# 0,250,154
		mediumspringgreen=Medium spring green
		# 72,209,204
		mediumturquoise=Medium turquoise
		# 199,21,133
		mediumvioletred=Medium violet red
		# 25,25,112
		midnightblue=Midnight blue
		# 245,255,250
		mintcream=Mint cream
		# 255,228,225
		mistyrose=Misty rose
		# 255,228,181
		moccasin=Moccasin
		# 255,222,173
		navajowhite=Navajo white
		# 0,0,128
		navy=Navy
		# 253,245,230
		oldlace=Old lace
		# 128,128,0
		olive=Olive
		# 107,142,35
		olivedrab=Olivedrab
		# 255,165,0
		orange=Orange
		# 255,69,0
		orangered=Orange red
		# 218,112,214
		orchid=Orchid
		# 238,232,170
		palegoldenrod=Pale goldenrod
		# 152,251,152
		palegreen=Pale green
		# 175,238,238
		paleturquoise=Pale turquoise
		# 219,112,147
		palevioletred=Pale violet red
		# 255,239,213
		papayawhip=Papaya whip
		# 255,218,185
		peachpuff=Peach puff
		# 205,133,63
		peru=Peru
		# 255,192,203
		pink=Pink
		# 221,160,221
		plum=Plum
		# 176,224,230
		powderblue=Powder blue
		# 128,0,128
		purple=Purple
		# 255,0,0
		red=Red
		# 188,143,143
		rosybrown=Rosy brown
		# 65,105,225
		royalblue=Royal blue
		# 139,69,19
		saddlebrown=Saddle brown
		# 250,128,114
		salmon=Salmon
		# 244,164,96
		sandybrown=Sandy brown
		# 46,139,87
		seagreen=Sea green
		# 255,245,238
		seashell=Seashell
		# 160,82,45
		sienna=Sienna
		# 192,192,192
		silver=Silver
		# 135,206,235
		skyblue=Sky blue
		# 106,90,205
		slateblue=Slate blue
		# 112,128,144
		slategray=Slate gray
		# 112,128,144
		slategrey=Slate grey
		# 255,250,250
		snow=Snow
		# 0,255,127
		springgreen=Spring green
		# 70,130,180
		steelblue=Steel blue
		# 210,180,140
		tan=Tan
		# 0,128,128
		teal=Teal
		# 216,191,216
		thistle=Thistle
		# 255,99,71
		tomato=Tomato
		# 64,224,208
		turquoise=Turquoise
		# 238,130,238
		violet=Violet
		# 245,222,179
		wheat=Wheat
		# 255,255,255
		white=White
		# 245,245,245
		whitesmoke=White smoke
		# 255,255,0
		yellow=Yellow
		# 154,205,50
		yellowgreen=Yellow green
		*/

	
}
