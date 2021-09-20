package org.tektonik.tools.generalStatPurpose;

import java.util.Random;


public class gpRandom {
	
	private Random rnd = new Random();
	
	private int NBR_OF_SEEDS   = 512* 512;
	private int NBR_OF_SAMPLES = 512;
	private double generatedMean = Double.NaN;
	private double generatedVariance = Double.NaN;
	

	//
	public double getGeneratedMean() {
		return generatedMean;
	}
	public double getGeneratedVariance() {
		return generatedVariance;
	}

	// 
	public gpRandom()
	{
		
	}

/*
standard normal distribution already has mean 0 and variance 1.

If you want to change the mean, just "translate" the distribution, i.e., add your mean value to each generated number. 
Similarly, if you want to change the variance, just "scale" the distribution, i.e., multiply all your numbers by sqrt(v). 

For example,

v = 1.5; % variance
sigma = sqrt(v); % standard deviation
mu = 2; % mean
n = 1000
X = sigma .* randn(n, 1) + mu;
stats = [mean(X) std(X) var(X)]

See the following article: https://ch.mathworks.com/help/matlab/math/random-numbers-with-specific-mean-and-variance.html

Random r = new Random();
double mySample = r.nextGaussian() * desiredStandardDeviation + desiredMean;

And, stdev = SQRT ( variance )
So => r.nextGaussian() * math.sqrt( variance )
 	
 */
	// Provides an array of Normal disributed randdom values with specified mean and stdev
	//------------------------------------------------------------
	public double[] getGaussianWithMeanAndStandardDeviation( int NbrOfRandomValues , double targetMean , double targetStdDev )
	{
		return getGaussianWithMeanAndVariance( NbrOfRandomValues , targetMean , targetStdDev*targetStdDev );
	}
	
	// Provides an array of Normal disributed randdom values with specified mean and variance
	// by picking a best matching sample from a large pool of normal distributed random numbers
	//------------------------------------------------------------
	public double[] getGaussianWithMeanAndVariance( int NbrOfRandomValues , double targetMean , double targetVariance )
	{
	 try {
		generatedMean = Double.NaN;
	    generatedVariance = Double.NaN;
		double[] seed = new double[ NBR_OF_SEEDS ];
		double seedMean = 0;
		for(int i=0;i<seed.length;i++)
		{
			seed[i] = (rnd.nextGaussian() * Math.sqrt(targetVariance)) + targetMean;
			seedMean += seed[i];
		}
		seedMean = seedMean / seed.length;
		double seedVariance = 0;
		for(int i=0;i<seed.length;i++)
		{
			seedVariance +=  (seed[i] - seedMean) * (seed[i] - seedMean);
		}
		seedVariance = seedVariance / seed.length;
		//System.out.println( "RND [TargetMean=" + targetMean + "] [TargetVariance=" + targetVariance + "]");
		//System.out.println( "RND [SeedMean=" + seedMean + "] [SeedVariance=" + seedVariance + "]");
		//
		
		// Nu 100 samples nemen en neem dan die samples die het dichts bij MEAN/TARGT komt
		double[][] samples = new double[ NBR_OF_SAMPLES ][ NbrOfRandomValues ];
		for(int i=0;i< samples.length ; i++)
		{
			for(int j=0;j<samples[i].length;j++)
			{
			   samples[i][j] = seed[ rnd.nextInt( seed.length ) ];
			}
		}
		// bereken mean en varianten van de samples
		double[] samplemeans = new double[ samples.length ];
		double[] samplevariance = new double[ samples.length ];
		for(int i=0;i<samples.length;i++)
		{
			double smean = 0;
			for(int j=0;j<samples[i].length;j++)
			{
				smean += samples[i][j];
			}
			smean = smean / samples[i].length;
			double svari = 0;
			for(int j=0;j<samples[i].length;j++)
			{
				svari += (samples[i][j] - smean )*(samples[i][j] - smean );
			}
			svari = svari / samples[i].length;
			samplemeans[i] = smean;
			samplevariance[i] = svari;
		}
		//
		double[] distance = new double[ samples.length ];
		for(int i=0;i<samples.length;i++)
		{
			double d1 = targetMean - samplemeans[i];
			double d2 = targetVariance - samplevariance[i];
			distance[i] = Math.sqrt((d1 * d1) + (d2 * d2));
		}
		// minimal
		double min = 0;
		int idx = 0;
		for(int i=0;i<samples.length;i++)
		{
			if( i == 0 ) { min = distance[i]; idx=i; }
			if( min > distance[i]) {
				idx = i;
				min = distance[i];
			}
		}
		// debug
		/*
		for(int i=0;i<samples.length;i++)
		{
			System.out.println("" + samplemeans[i] + " " + samplevariance[i] + " " + " " + distance[i] + " " + samples[i][0] );
		}
		System.out.println( "");
		*/
		
		// copy
		// DO NOT display the weight, the STDOUT gets all jumbled up and program stops
		double[] ret = new double[ NbrOfRandomValues ];
		//String ss = "";
		for(int i=0;i<ret.length;i++)
		{
			ret[i] = samples[idx][i];
			//ss += "[" + ret[i] + "]";
		}
		generatedMean = samplemeans[idx];
		generatedVariance = samplevariance[idx];
		//System.out.println("RND [Mean=" + samplemeans[idx] + "] [Variance=" + samplevariance[idx] + "] "); // + ss );

		//
		samplemeans=null;
		samplevariance=null;
		seed = null;
		return ret;
	 }
	catch(Exception e ) {
	    System.err.println( "getGaussianWithMeanAndVariance [Nbr=" + NbrOfRandomValues + "][mean=" + targetMean + "][var=" + targetVariance + "]");
	    e.printStackTrace();
	    return null;
	}
  }

  //------------------------------------------------------------	
  public int[] getSimpleRandomList(int nsources , int ntargets )
  {
     int[] ret = new int[ ntargets ];
     for(int i=0;i<ret.length;i++) ret[i] = -1;
	 int count=0;
     boolean completed=false;
     Random rn = new Random();
     boolean found = false;
     for(int i=0;i< (nsources*10000) ;i++)
     {
   	   int idx = rn.nextInt(nsources);
   	   found = false;
       for(int j=0;j<ret.length;j++)
       {
    	   if( ret[j] == idx ) { found = true; continue; }
       }
       if( found ) continue;
       ret[ count ] = idx;
       count++;
       if( count == ntargets ) { completed = true; break; }
     }
     if( completed == false ) {
   	   System.err.println("Strange - Could not allocate enough random records");
   	   return null;
     }
     return ret;
  }
	
	
	
}
