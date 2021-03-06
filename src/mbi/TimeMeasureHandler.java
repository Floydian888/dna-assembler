package mbi;

public class TimeMeasureHandler {
	
	public static String executeAndMeasure(Command cmd, String description) throws Exception {
		long startTime = System.nanoTime();
		String resultSequence =  cmd.execute();
		long elapsedTime = System.nanoTime() - startTime;
		double elapsedTimeInSeconds = (double)elapsedTime / 1000000000.0;
		Logger.log(description, Double.toString(elapsedTimeInSeconds));
		return resultSequence;
	}
	
}
