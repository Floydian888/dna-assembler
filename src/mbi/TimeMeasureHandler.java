package mbi;

public class TimeMeasureHandler {

	private Logger logger = new Logger();
	
	public String executeAndMeasure(Command cmd, String description) throws Exception {
		long startTime = System.nanoTime();
		String resultSequence =  cmd.execute();
		long elapsedTime = System.nanoTime() - startTime;
		double elapsedTimeInSeconds = (double)elapsedTime / 1000000000.0;
		logger.log(description, Double.toString(elapsedTimeInSeconds));
		return resultSequence;
	}
	
}
