package mbi;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class Tests {
	
	private final String bigSeparator = "=================";
	private final String smallSeparator = "---------";
	
	private final String pathToGenomeData = "/genome-data/"; 
	
	private class AssembleFromFileCommand implements Command {

		private String pathToFile;
		private Sequencer sequencer = new Sequencer();
		private int oneKmerLength;
		private int kmersOverlapLength;
		private int graphDegree;

		public AssembleFromFileCommand(String organismName, int oneKmerLengthToLoad, int kmersOverlapLengthToLoad,
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
	
	private static int longestSubstring(String first, String second) {
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
			Logger.log("de Bruijn graph building", Double.toString(elapsedTimeInSeconds));
			
			resultSequence = TimeMeasureHandler.executeAndMeasure(new AssembleCommand(sequencer), "finding eulerian path");

//			Logger.log("RESULT: " + resultSequence);
			
			if(inputSequence.equals(resultSequence)) {
				Logger.log("INPUT equals RESULT");
			}
			else {
				Logger.log("INPUT differs from RESULT");
				
				startTime = System.nanoTime();
				int longestCommonSubstringLength = longestSubstring(inputSequence, resultSequence);
				Logger.log("longest: " + longestCommonSubstringLength);
				Logger.logLongestCommonSubstringLength(Integer.toString(longestCommonSubstringLength));
				elapsedTime = System.nanoTime() - startTime;
				elapsedTimeInSeconds = (double)elapsedTime / 1000000000.0;
				Logger.log("longest substring searching", Double.toString(elapsedTimeInSeconds));
			}
			
			
		} catch (MbiException e) {
			Logger.log("Exception: " + e.getMessage());
		} catch (Exception e) {
			Logger.log("Exception: " + e.getMessage());
		}

		return resultSequence;
	}
	
	//@Test
	public void simpleTest() throws IOException {

//		Sequencer sequencer = new Sequencer();
//		String sequence = "ACCGGGT";
//		
//		int oneKmerLength = 3;
//		int kmersOverlapLength = 2;
//
//		sequencer.loadSequence(sequence);
//
//		assemble(sequencer, oneKmerLength, kmersOverlapLength, oneKmerLength-1);
	}
	
	private int test_constOverlapVariableLength(String organismName, int kmerUpperLength, boolean logOnlyLongestCommonSubstringLength)
			throws Exception {
		
		int kmerLength;
		int kmersOverlapLength;
		int graphDegree;
		
		int smallestKmerLengthToProduceCorrectResult = 0;
		boolean smallestSaved = false;
		
		int kmerLowerLength = 2;
		
		Logger.InitializeFile(organismName, "constOverlapVarLength", Integer.toString(kmerUpperLength), "",
				logOnlyLongestCommonSubstringLength);

		int i = kmerLowerLength;
		
		String inputSequence = null;
		String resultSequence = null;

		do {
			kmerLength = i;
			kmersOverlapLength = kmerLength - 1;
			graphDegree = kmerLength;

			Logger.log(smallSeparator);

			AssembleFromFileCommand assembleFromFileCommand = new AssembleFromFileCommand(organismName, kmerLength,
					kmersOverlapLength, graphDegree);

			resultSequence = TimeMeasureHandler.executeAndMeasure(assembleFromFileCommand, "whole operation");
			inputSequence = assembleFromFileCommand.getInputSequence();

			Logger.log("k-mer length: " + kmerLength);
			Logger.log("overlap: " + kmersOverlapLength);
			Logger.log("graph degree: " + graphDegree);

			if(!smallestSaved) {
				if(inputSequence.equals(resultSequence)) {
					smallestKmerLengthToProduceCorrectResult = kmerLength;
					smallestSaved = true;
				}
			}
			
			++i;
		} while (i < kmerUpperLength);

		Logger.log("genome length: " + (inputSequence == null ? 0 : inputSequence.length()));
		
		return smallestKmerLengthToProduceCorrectResult;
	}
	
	//@Test
	public void porcine_constOverlapVariableLength() throws Exception {
		test_constOverlapVariableLength("porcine", 20, false);
	}
	
	private void test_variableOverlapConstLength(String organismName, int kmerLength, boolean customizeDegree, boolean logOnlyLongestCommonSubstringLength)
			throws Exception {
		
		int kmersOverlapLength;
		int graphDegree;
		
		Logger.InitializeFile(organismName, "varOverlapConstLength", Integer.toString(kmerLength), customizeDegree ? "degree" : "noDegree",
				logOnlyLongestCommonSubstringLength);

		String inputSequence = null;

		for (int i = 1; i <= kmerLength - 1; ++i) {
			kmersOverlapLength = i;
			
			if(customizeDegree)
				graphDegree = kmersOverlapLength + 1;
			else
				graphDegree = kmerLength;

			Logger.log(smallSeparator);

			AssembleFromFileCommand assembleFromFileCommand =
					new AssembleFromFileCommand(organismName, kmerLength, kmersOverlapLength, graphDegree);

			TimeMeasureHandler.executeAndMeasure(assembleFromFileCommand, "whole operation");
			inputSequence = assembleFromFileCommand.getInputSequence();

			Logger.log("k-mer length: " + kmerLength);
			Logger.log("overlap: " + kmersOverlapLength);
			Logger.log("graph degree: " + graphDegree);
		}

		Logger.log("genome length: " + (inputSequence == null ? 0 : inputSequence.length()));
	}
	
//	@Test
	public void porcine_variableOverlapConstLength() throws Exception {
		test_variableOverlapConstLength("porcine", 30, true, false);
	}
	
	//@Test
	public void phiX174_variableOverlapConstLength() throws Exception {
		test_variableOverlapConstLength("phiX174", 30, true, false);
	}
	
	private void testAll(String organismName) throws Exception {
//		test_constOverlapVariableLength(organismName, 20, true);
		int smallestKmerLengthToProduceCorrectResult = test_constOverlapVariableLength(organismName, 15, true);
		
		boolean[][] allPossibilities = { { true, true }, { true, false }, { false, true }, { false, false } };
//		boolean[][] allPossibilities = { { true, false }, { false, false } };
		
		for (boolean[] combination : allPossibilities) {
			test_variableOverlapConstLength(organismName, smallestKmerLengthToProduceCorrectResult, combination[0], combination[1]);
			test_variableOverlapConstLength(organismName, smallestKmerLengthToProduceCorrectResult + 20, combination[0], combination[1]);
		}
	}
	
	//@Test
	public void porcine_testAll() throws Exception {
		testAll("porcine");
	}
	
	//@Test
	public void phiX174_testAll() throws Exception {
		testAll("phiX174");
	}
	
	@Test
	public void humanMitochondrion_testAll() throws Exception {
//		int smallestKmerLengthToProduceCorrectResult = test_constOverlapVariableLength("mitoch", 4, false);
		testAll("mitoch");
	}
	
	//@Test
	public void ebv_testAll() throws Exception {
		testAll("ebv");
	}
	
	//@Test
	public void ecoli_testAll() throws Exception {
		testAll("ecoli");
	}
}
