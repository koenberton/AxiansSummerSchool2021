package org.tektonik.tools.linearAlgebra;

public class cmcVector {
	
	private double[] values=null;
	
	public cmcVector( double[] ival)
	{
		values = new double[ ival.length ];
		for(int i=0;i<ival.length;i++ ) values[i]=ival[i];
	}
	
	public void setValue( int i , double dd)
	{
	     this.values[i] = dd;	
	}
	
    public double[] getVectorValues()
    {
    	return values;
    }
	public double getMagnitude()
	{
		double sum=0;
		for(int i=0;i<values.length;i++) sum += (values[i] * values[i]);
		return Math.sqrt(sum);
	}
	public double getNorm()
	{
		return getMagnitude();
	}
	public int getDimension()
	{
		return values.length;
	}
	public String show()
	{
		String sl="";
		for(int i=0;i<values.length;i++) sl += "[" + values[i] + "]";
		return sl;
	}
	public String showRound()
	{
		String sl="";
		for(int i=0;i<values.length;i++) sl += ((i==0)?"":", ") + String.format("%1.4f",values[i]);
		return "(" + sl + ")";
	}
}
