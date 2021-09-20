package org.tektonik.tools.linearAlgebra;

public class cmcVectorDTO {

    private double[] values=null;

	public cmcVectorDTO( double[] ival)
	{
		values = new double[ ival.length ];
		for(int i=0;i<ival.length;i++ ) values[i]=ival[i];
	}
	
	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}
}
