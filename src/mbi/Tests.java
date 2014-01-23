package mbi;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class Tests {
	
	private boolean logAlgorithmData = false;
	TimeMeasureHandler timeMeasureHandler = new TimeMeasureHandler();
	
	private final String bigSeparator = "=================";
	private final String smallSeparator = "---------";
	
	private final String pathToGenomeData = "/genome-data/"; 
	
	private class AssembleFromFileCommand implements Command {

		private String pathToFile;
		private Sequencer sequencer = new Sequencer();
		private int oneKmerLength;
		private int kmersOverlapLength;

		public AssembleFromFileCommand(String pathToFileToLoad, int oneKmerLengthToLoad, int kmersOverlapLengthToLoad) {
			pathToFile = pathToFileToLoad;
			oneKmerLength = oneKmerLengthToLoad;
			kmersOverlapLength = kmersOverlapLengthToLoad; 
		}
		
		public final String getInputSequence() {
			return sequencer.getInputSequence();
		}

		public String execute() throws IOException {
			sequencer.loadSequenceFromOneLineFile(pathToFile);
			String resultSequence = assemble(sequencer, oneKmerLength, kmersOverlapLength);
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
	
	private String assemble(Sequencer sequencer, int oneKmerLength, int kmersOverlapLength) throws IOException {
		
		List<String> kmers = sequencer.shotgun(oneKmerLength, kmersOverlapLength);
		String inputSequence = sequencer.getInputSequence();
		String resultSequence = null;

		if (logAlgorithmData) {
			//Helpers.log("K-MERS: " + kmers.toString());
			//Helpers.log("INPUT:  " + inputSequence);
		}

		try {
			
			long startTime = System.nanoTime();
			
			sequencer.buildDeBruijnGraph(kmers);
			
			long elapsedTime = System.nanoTime() - startTime;
			double elapsedTimeInSeconds = (double)elapsedTime / 1000000000.0;
			//Helpers.log("de Bruijn graph building", Double.toString(elapsedTimeInSeconds));
			
			resultSequence = timeMeasureHandler.executeAndMeasure(new AssembleCommand(sequencer), "finding eulerian path");

			if (logAlgorithmData) {
				//Helpers.log("RESULT: " + resultSequence);
			}
			
			if(inputSequence.equals(resultSequence))
			{}//Helpers.log("INPUT equals RESULT");
			else {
				//Helpers.log("INPUT differs from RESULT");
				Helpers.log("" + longestSubstr(inputSequence, resultSequence));
			}
			
			
		} catch (MbiException e) {
			//Helpers.log("Exception: " + e.getMessage());
		} catch (Exception e) {
			//Helpers.log("Exception: " + e.getMessage());
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

		assemble(sequencer, oneKmerLength, kmersOverlapLength);
	}
	
	@Test
	public void phiX174() throws Exception {
		int kmerLength = 0;
		String fileName = "phiX174-1line.txt";
		String pathToFile = pathToGenomeData + fileName;
		//Helpers.log(bigSeparator);
		//Helpers.log(getCurrentDate());
		//Helpers.log("file name: " + fileName);
		
		int kmersOverlapLength;
		int kmersOverlapDifference;
		
		int i = 2;
		String inputSequence = null;
		
		do {
			kmerLength = i;
			for (int j = 1; j < kmerLength; ++j) {
				kmersOverlapDifference = j;
				kmersOverlapLength = i - kmersOverlapDifference;
				
				//Helpers.log(smallSeparator);
				
				AssembleFromFileCommand assembleFromFileCommand = new AssembleFromFileCommand(
						System.getProperty("user.dir") + pathToFile,
						kmerLength, kmersOverlapLength);
				
				timeMeasureHandler.executeAndMeasure(assembleFromFileCommand, "whole operation");
				inputSequence = assembleFromFileCommand.getInputSequence();

				//Helpers.log("one k-mer length: " + kmerLength);
				//Helpers.log("kmersOverlapLength: " + kmersOverlapLength);
				//Helpers.log("kmersOverlapDifference: " + kmersOverlapDifference);

				//Helpers.log("genome length: " + inputSequence.length());

				i = i + 1;
			}
		} while (i < 22);

		
		//assertTrue(inputSequence.equals(resultSequence));
	}
	
	//@Test
	public void ecoli() {
		//assembleFromFile("ecoli-1line.txt");
	}
}
