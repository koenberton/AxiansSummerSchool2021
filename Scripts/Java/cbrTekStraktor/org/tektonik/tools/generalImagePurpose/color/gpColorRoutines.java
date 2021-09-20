package org.tektonik.tools.generalImagePurpose.color;

import java.awt.Color;

public class gpColorRoutines {

	
	public gpColorRoutines() 
	{
		
	}
	
	public Color Int2Color(int color)
    {
     	float r = ((color >> 16) & 0xff) / 255.0f;
        float g = ((color >>  8) & 0xff) / 255.0f;
        float b = ((color      ) & 0xff) / 255.0f;
        float a = ((color >> 24) & 0xff) / 255.0f;
        return new Color(r, g, b, a);
    }
	
	public int Color2Int(Color c)
    {
    	int red   = c.getRed();
    	int green = c.getGreen();
    	int blue  = c.getBlue();
    	int trans = c.getTransparency();
        return (trans & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
    }
	
}
