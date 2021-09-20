package org.tektonik.MachineLearning.NeuralNetwork.monitor;

public class EpochMonitorDTO {
	
	    private long UID=0;
		private long timeElapsed = 0L;
		private double epochCost = Double.NaN;
		private double epochAccuracy = Double.NaN;
		private double testAccuracy  = Double.NaN;
		private int[][] confusionMatrix = null;
		private double[] learningSpeed = null;
		

		public EpochMonitorDTO(int idx)
		{
			UID=idx;
			timeElapsed = 0L;
			epochCost = Double.NaN;
			epochAccuracy = Double.NaN;
			confusionMatrix = null;
			learningSpeed = null;
		}
		
		public long getUID() {
			return UID;
		}
		public long getTimeElapsed() {
			return timeElapsed;
		}
		public double getEpochCost() {
			return epochCost;
		}
		public double getEpochAccuracy() {
			return epochAccuracy;
		}
		public void setTimeElapsed(long timeElapsed) {
			this.timeElapsed = timeElapsed;
		}
		public void setEpochCost(double epochCost) {
			this.epochCost = epochCost;
		}
		public void setEpochAccuracy(double epochAccuracy) {
			this.epochAccuracy = epochAccuracy;
		}
		public int[][] getConfusionMatrix() {
			return confusionMatrix;
		}
		public void setConfusionMatrix(int[][] confusionMatrix) {
			this.confusionMatrix = confusionMatrix;
		}
		public double[] getLearningSpeed() {
			return learningSpeed;
		}
		public void setLearningSpeed(double[] learningSpeed) {
			this.learningSpeed = learningSpeed;
		}
		public double getTestAccuracy() {
			return testAccuracy;
		}
		public void setTestAccuracy(double testAccuracy) {
			this.testAccuracy = testAccuracy;
		}
		
}
