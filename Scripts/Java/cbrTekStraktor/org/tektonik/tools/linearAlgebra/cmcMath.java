package org.tektonik.tools.linearAlgebra;

import java.util.Random;

public class cmcMath {

	Random glornd = null;
	
	private void do_error(String sin)
	{
		System.err.println(sin);
	}
	
	public cmcMath()
	{
		glornd = new Random();
	}
	
	//------------------------------------------------------------
	public boolean isEven(int i)
	//------------------------------------------------------------
	{
		double rest = i % 2;
		return (rest==0);
	}
	//------------------------------------------------------------
	public int getRandomNumberExcluding(int range , int excli )
	//------------------------------------------------------------
	{
		for(int i=0;i<10000;i++)
		{
			int p = (int)( Math.random() * (double)range);
		    if( p != excli ) return p;			
		}
		return -1;
	}
	//------------------------------------------------------------
	public int[] shuffleList( int len )
	//------------------------------------------------------------
	{
		if( len <= 0 ) { do_error("Empty list"); return null; }
		
		// make a list of LEN random and unique numbers
		int[] rlist = new int[ len ];
		for(int i=0 ; i< rlist.length ; i++ ) rlist[i] = -1;
	
		int UPPER = len * 1234;
		
		for(int col=0;col<len;col++)
		{
			int insert = -1;
		    for( int r=0 ; r<10000 ; r++)
		    {
		        int result = glornd.nextInt( UPPER );
		        boolean found = false;
	            for(int j=0;j<col;j++) 
	            {
	            	if( rlist[j] == result ) { found = true; break; }
	            }
	            if( found == false ) { insert = result; break; } 
		    }
		    if( insert < 0 ) { do_error("Could not create random list - increase attempts"); return null; }
		    rlist[col] = insert;
		}
	    // list of random number - replace each number by its rang in the range 0 .. len	
		int[] sorted = new int[ len ];
		for(int i=0;i<sorted.length;i++) sorted[i] = rlist[i];
		for(int i=0;i<len;i++)
		{
			boolean swap=false;
			for(int j=0;j<len-1;j++)
			{
				if( rlist[j] > rlist[j+1]) {
					swap=true;
					int z = rlist[j];
					rlist[j] = rlist[j+1];
					rlist[j+1]=z;
				}
			}
			if( swap == false ) break;
		} 
		// replace the value in the rlist by its rang
		for(int i=0;i<len;i++)
		{
			int idx = -1;
			for(int j=0;j<len;j++) {
				if( rlist[i] == sorted[j] ) { idx=j; break; }
			}
			if( idx < 0 ) { do_error("Could not determine rang " + rlist[i] ); return null; }
			rlist[i]=idx;
		}
		// check
		for(int i=0;i<len;i++)
		{
			boolean found = false;
			for(int j=0;j<len;j++)
			{
				if( rlist[j] == i ) { found = true; break; }
			}
			if( found == false ) { do_error("Could not find value in list " + i ); return null; }
		}
		return rlist;
	}
	
	//------------------------------------------------------------
	public double[] runningAverageAll(  double[] fxs , int spread )
	//------------------------------------------------------------
	{
		if( fxs == null ) { do_error( "NULL values"); return null; }	
		return runningAverageSelected( fxs , spread , fxs.length );
	}
	
	//------------------------------------------------------------
	public double[] runningAverageSelected(  double[] fxs , int spread , int effectiveNbrOfRecords)
	//------------------------------------------------------------
	{
		if( fxs == null ) { do_error( "NULL values"); return null; }
		if( fxs.length < 1) { do_error("Empty values"); return null; }
		if( spread < 1 ) { do_error( "Spread must be at least 1"); return null; }
		if( effectiveNbrOfRecords < 1 ){ do_error( "Nbr of records must be at least 1"); return null; }
		
		// uneven
		int fullspread = isEven(spread) ? spread+1 : spread;
	    int halfspread = fullspread / 2;
		double[] ret = new double[ fxs.length ];
		for(int i=0;i<fxs.length;i++)
		{
		  double sum =0;
		  int count = 0;
		  for(int k=0;k<=fullspread;k++)
		  {
			  int j = i - halfspread + k;
			  if( (j<0) || (j>=effectiveNbrOfRecords) ) continue;
			  count++;
			  sum += fxs[j];
		  }
		  ret[i] = sum / (double)count;
		}	
		
		/*
		for(int i=0;i<ret.length;i++) ret[i] = Double.NaN;
		double[] samples = new double[ spread ];
		for(int i=0;i<samples.length;i++) samples[i] = Double.NaN;
		//
		for(int i=0;i<fxs.length;i++)
		{
		   double runavg = 0;
		   for(int k=0;k<spread;k++)
		   {
			   int j = i - spread + k;
			   if( j < 0 ) { ret[i] = Double.NaN; continue; }
			   runavg += fxs[ j];
		   }
		   ret[i] = runavg / (double)spread;
		}
		*/
	   return ret;
	}
}
