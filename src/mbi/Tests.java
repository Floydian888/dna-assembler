package mbi;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class Tests {
	
	TimeMeasureHandler timeMeasureHandler = new TimeMeasureHandler();
	
	private final String bigSeparator = "=================";
	private final String smallSeparator = "---------";
	
	private final String pathToGenomeData = "/genome-data/"; 
	
	private class AssembleFromFileCommand implements Command {

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
		
		public final String getInputSequence() {
			return sequencer.getInputSequence();
		}

		public String execute() throws IOException {
			sequencer.loadSequenceFromOneLineFile(pathToFile);
			String resultSequence = assemble(sequencer, oneKmerLength, kmersOverlapLength, graphDegree);
			return resultSequence;
		}

	}
	
	private class AssembleCommand implements Command {

		private Sequencer sequencer;

		public AssembleCommand(Sequencer sequencerToLoad) {
			sequencer = sequencerToLoad;
		}

		public String execute() throws MbiException {
			return sequencer.assemble();
		}

	}
	
	private static int longestSubstr(String first, String second) {
	    if (first == null || second == null || first.length() == 0 || second.length() == 0) {
	        return 0;
	    }
	 
	    int maxLen = 0;
	    int fl = first.length();
	    int sl = second.length();
	    int[][] table = new int[fl+1][sl+1];
	 
	    for(int s=0; s <= sl; s++)
	      table[0][s] = 0;
	    for(int f=0; f <= fl; f++)
	      table[f][0] = 0;
	 
	    for (int i = 1; i <= fl; i++) {
	        for (int j = 1; j <= sl; j++) {
	            if (first.charAt(i-1) == second.charAt(j-1)) {
	                if (i == 1 || j == 1) {
	                    table[i][j] = 1;
	                }
	                else {
	                    table[i][j] = table[i - 1][j - 1] + 1;
	                }
	                if (table[i][j] > maxLen) {
	                    maxLen = table[i][j];
	                }
	            }
	        }
	    }
	    return maxLen;
	}
	
	private String assemble(Sequencer sequencer, int oneKmerLength, int kmersOverlapLength, int graphDegree) throws IOException {
		
		List<String> kmers = sequencer.shotgun(oneKmerLength, kmersOverlapLength);
		String inputSequence = sequencer.getInputSequence();
		String resultSequence = null;


//			Logger.log("K-MERS: " + kmers.toString());
//			Logger.log("INPUT:  " + inputSequence);

		try {
			
			long startTime = System.nanoTime();
			
			sequencer.buildDeBruijnGraph(inputSequence, kmers, graphDegree);
			
			long elapsedTime = System.nanoTime() - startTime;
			double elapsedTimeInSeconds = (double)elapsedTime / 1000000000.0;
			//Logger.log("de Bruijn graph building", Double.toString(elapsedTimeInSeconds));
			
			resultSequence = timeMeasureHandler.executeAndMeasure(new AssembleCommand(sequencer), "finding eulerian path");

//			Logger.log("RESULT: " + resultSequence);
			
			if(inputSequence.equals(resultSequence)) {
				Logger.log("INPUT equals RESULT");
			}
			else {
				Logger.log("INPUT differs from RESULT");
				Logger.log("longest: " + longestSubstr(inputSequence, resultSequence));
			}
			
			
		} catch (MbiException e) {
			Logger.log("Exception: " + e.getMessage());
		} catch (Exception e) {
			Logger.log("Exception: " + e.getMessage());
		}

		return resultSequence;
	}
	
	private String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	//@Test
	public void simpleTest() throws IOException {

		Sequencer sequencer = new Sequencer();
		String sequence = "ACCGGGT";
		
		int oneKmerLength = 3;
		int kmersOverlapLength = 2;

		sequencer.loadSequence(sequence);

		assemble(sequencer, oneKmerLength, kmersOverlapLength, oneKmerLength-1);
	}
	
	private void test_constOverlapVariableLength(String fileName) throws Exception {
		String pathToFile = pathToGenomeData + fileName;
		
		// Logger.log(bigSeparator);
		// Logger.log(getCurrentDate());
		// Logger.log("file name: " + fileName);

		int kmerLength = 0;
		int kmersOverlapLength;

		int start = 8;
		int end = 10;
		
		int i = start;
		String inputSequence = null;

		do {
			kmerLength = i;
			kmersOverlapLength = kmerLength - 1;

//			 Logger.log(smallSeparator);

			AssembleFromFileCommand assembleFromFileCommand = new AssembleFromFileCommand(
					System.getProperty("user.dir") + pathToFile, kmerLength,
					kmersOverlapLength, kmerLength-1);

			timeMeasureHandler.executeAndMeasure(assembleFromFileCommand, "whole operation");
			inputSequence = assembleFromFileCommand.getInputSequence();

			// Logger.log("one k-mer length: " + kmerLength);
			// Logger.log("kmersOverlapLength: " + kmersOverlapLength);
			// Logger.log("kmersOverlapDifference: " + kmersOverlapDifference);

			// Logger.log("genome length: " + inputSequence.length());

			i = i + 1;

		}
		while(i < end);
	}
	
	//@Test
	public void phiX174_constOverlapVariableLength() throws Exception {
		test_constOverlapVariableLength("phiX174-1line.txt");
	}
	
	private void test_variableOverlapConstLength(String fileName) throws Exception {
		String pathToFile = pathToGenomeData + fileName;
		
		Logger.log("");
		
		// Logger.log(bigSeparator);
		// Logger.log(getCurrentDate());
		// Logger.log("file name: " + fileName);

		int kmerLength = 13;
		int kmersOverlapLength;
		
		String inputSequence = null;

		//for (int i = 1; i < kmerLength; ++i) {
			kmersOverlapLength = 12;

			 Logger.log(smallSeparator);

			AssembleFromFileCommand assembleFromFileCommand = new AssembleFromFileCommand(
					System.getProperty("user.dir") + pathToFile, kmerLength,
					kmersOverlapLength, kmerLength-1);

			timeMeasureHandler.executeAndMeasure(assembleFromFileCommand, "whole operation");
			inputSequence = assembleFromFileCommand.getInputSequence();

			 Logger.log("one k-mer length: " + kmerLength);
			 Logger.log("kmersOverlapLength: " + kmersOverlapLength);

			 Logger.log("genome length: " + inputSequence.length());
//		}
	}
	
	@Test
	public void phiX174_variableOverlapConstLength() throws Exception {
		test_variableOverlapConstLength("phiX174-1line.txt");
	}
	
	//@Test
	public void ecoli() {
		//assembleFromFile("ecoli-1line.txt");
	}
}
