package mbi;

public class AssembleFromFileCommand implements Command {

	private String pathToFile;
	private Sequencer sequencer = new Sequencer();
	private int oneKmerLength;
	private int kmersOverlapLength;
	private int graphDegree;

	public AssembleFromFileCommand(String pathToFileToLoad, int oneKmerLengthToLoad, int kmersOverlapLengthToLoad,
			int graphDegreeToLoad) {
		pathToFile = pathToFileToLoad;
		oneKmerLength = oneKmerLengthToLoad;
		kmersOverlapLength = kmersOverlapLengthToLoad;
		graphDegree = graphDegreeToLoad;
	}
	
	public AssembleFromFileCommand(String organismName, String pathToGenomeData, int oneKmerLengthToLoad, int kmersOverlapLengthToLoad,
			int graphDegreeToLoad) {
		pathToFile = System.getProperty("user.dir") + pathToGenomeData + organismName + ".txt";
		oneKmerLength = oneKmerLengthToLoad;
		kmersOverlapLength = kmersOverlapLengthToLoad;
		graphDegree = graphDegreeToLoad;
	}
	
	public final String getInputSequence() {
		return sequencer.getInputSequence();
	}

	public String execute() throws Exception {
		long startTime = System.nanoTime();
		sequencer.loadSequenceFromOneLineFile(pathToFile);
		long elapsedTime = System.nanoTime() - startTime;
		double elapsedTimeInSeconds = (double)elapsedTime / 1000000000.0;
		Logger.log("loading from file", Double.toString(elapsedTimeInSeconds));

		String resultSequence = null;
//		String resultSequence = assemble(sequencer, oneKmerLength, kmersOverlapLength, graphDegree);
		return resultSequence;
	}

}