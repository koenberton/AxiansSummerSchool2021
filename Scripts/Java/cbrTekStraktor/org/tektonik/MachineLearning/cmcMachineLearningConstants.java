package org.tektonik.MachineLearning;

public class cmcMachineLearningConstants {

	 public static int NBR_BINS = 32;
	 public static int TEST_SET_PERCENTAGE = 18;
	 public static double TOLERANCE_LIMIT = (double)0.00001;  // tolerance for the calculations using DOUBLE 
	 //
	 // Decision tree
	 public static int MAX_NBR_OF_BRANCHES = 1000;  
     public static int MAX_NBR_OF_LEVELS   = 25;
	 //
     // KNN
	 public static int NBR_OF_REPARTITIONS = 5;
	 public static int NBR_OF_PARTITIONS   = 12;
	 public static int MAX_K               = 30;
	 // Lenair regression
	 public static int MAX_NUMBER_OF_LINEAIR_REGRESSION_CYCLES = 10000;
	 public static double LINEAIR_REGRESSION_STEPSIZE = (double)0.01;
	 // Logistic regression
	 public static int MAX_NUMBER_OF_LOGISTIC_REGRESSION_CYCLES = 2500;
	 public static double LOGISTIC_REGRESSION_STEPSIZE = (double)0.001;
	 // SMO
	 public static int SMO_NUMBER_OF_CYCLES = 40;
	 public static double SMO_SOFTMARGIN = (double)0.6;
	 public static double SMO_TOLERANCE = (double)0.001;
	 public static double SMO_RBF_GAMMA = (double)1;
	 public static double SMO_GAUSSIAN_SIGMA = (double)5;   // larger is more general classifier  0.25 .. 5
	 //
	 public static long REFRESH_INTERVAL_IN_SEC = 5L;
     public static int MONITOR_DIAGRAM_WIDTH    = 1500;
     public static int MONITOR_DIAGRAM_HEIGTH   = (int)((double)MONITOR_DIAGRAM_WIDTH * (double)0.55);
     public static int DEFAULT_NBR_OF_EPOCHS    = 5000;
     //
     public static int MAX_ARFF_LINES           = 50000;
     public static int MAX_MINIBATCH_LINES      = 1000;  // MOET naar 1000
     
}