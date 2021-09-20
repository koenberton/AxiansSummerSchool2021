package org.tektonik.tools.generalpurpose;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class gpJulianDate {

	public static int JGREG= 15 + 31*(10+12*1582);
	//public static double HALFSECOND = 0.5;

	
	public double getDoubleDateTime(String dataInString , String Pattern)
	{
		try {
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Pattern);
		    LocalDate dateTime = LocalDate.parse(dataInString, formatter);
		    int jaar  = dateTime.getYear();
		    int maand = dateTime.getMonthValue();
		    int dag   = dateTime.getDayOfMonth();
		    double dd = JulianDate( jaar , maand , dag );
//System.err.println( "" + dateTime + " " + jaar + " " + maand + " " + dag + " " + dd);
		    return dd;
		}
	    catch (Exception e) {
	    	e.printStackTrace();
	        return Double.NaN;
	    }
	}

	/**
	 * Returns the Julian day number that begins at noon of
	 * this day, Positive year signifies A.D., negative year B.C.
	 * Remember that the year after 1 B.C. was 1 A.D.
	 *
	 * ref :
	 *  Numerical Recipes in C, 2nd ed., Cambridge University Press 1992
	 */
	public double JulianDate( int year , int month , int day )
	{
	 try { 	
	   // Gregorian Calendar adopted Oct. 15, 1582 (2299161)
	   int julianYear = year;
	   if (year < 0) julianYear++;
	   int julianMonth = month;
	   if (month > 2) {
	     julianMonth++;
	   }
	   else {
	     julianYear--;
	     julianMonth += 13;
	   }
	   double julian = (java.lang.Math.floor(365.25 * julianYear) + java.lang.Math.floor(30.6001*julianMonth) + day + 1720995.0);
	   if (day + 31 * (month + 12 * year) >= JGREG) {
	     // change over to Gregorian calendar
	     int ja = (int)(0.01 * julianYear);
	     julian += 2 - ja + (0.25 * ja);
	   }
	   return java.lang.Math.floor(julian);
	 }
	 catch(Exception e ) {
		 return Double.NaN;
	 }
	}
	
}
