package mbi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.junit.Test;

public class Tests {
	
	private boolean logAlgorithmData = false;
	TimeMeasureHandler timeMeasureHandler = new TimeMeasureHandler();
	private final String separator = "========";
	private final String pathToGenomeData = "/genome-data/"; 
	
	private class AssembleFromFileCommand implements Command {

		private String pathToFile;
		private Sequencer sequencer = new Sequencer();
		private int oneKmerLength;

		public AssembleFromFileCommand(String pathToFileToLoad, int oneKmerLengthToLoad) {
			pathToFile = pathToFileToLoad;
			oneKmerLength = oneKmerLengthToLoad;
		}
		
		public final String getInputSequence() {
			return sequencer.getInputSequence();
		}

		public String execute() throws IOException {
			sequencer.loadSequenceFromOneLineFile(pathToFile);
			String resultSequence = assemble(sequencer, oneKmerLength);
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
	
	private String assemble(Sequencer sequencer, int oneKmerLength) throws IOException {
		
		List<String> kmers = sequencer.shotgun(oneKmerLength);
		String inputSequence = sequencer.getInputSequence();
		String resultSequence = null;

		if (logAlgorithmData) {
			Helpers.log("K-MERS: " + kmers.toString());
			Helpers.log("input:  " + inputSequence);
		}

		try {
			sequencer.buildDeBruijnGraph(kmers);
			resultSequence = timeMeasureHandler.executeAndMeasure(new AssembleCommand(sequencer), "finding eulerian path");

			if (logAlgorithmData) {
				Helpers.log("result: " + resultSequence);
				Helpers.log("INPUT "
						+ (inputSequence.equals(resultSequence) ? "equals"
								: "differs from") + " RESULT");
			}
		} catch (MbiException e) {
			System.out.println("Exception: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

		return resultSequence;
	}
	
	private String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	@Test
	public void simpleTest() throws IOException {

		Sequencer sequencer = new Sequencer();
		String sequence = "ACCGGGT";
		int oneKmerLength = 3;

		sequencer.loadSequence(sequence);

		assemble(sequencer, oneKmerLength);
	}
	
	@Test
	public void phiX174() throws Exception {
		int oneKmerLength = 3;
		String fileName = "phiX174-1line.txt";
		String pathToFile = pathToGenomeData + fileName;
		Helpers.log(separator);
		Helpers.log(getCurrentDate());
		Helpers.log("file name: " + fileName);
		
		AssembleFromFileCommand assembleFromFileCommand = new AssembleFromFileCommand(
				System.getProperty("user.dir") + pathToFile, oneKmerLength);
		String resultSequence = null;
		resultSequence = timeMeasureHandler.executeAndMeasure(assembleFromFileCommand, "whole operation");
		String inputSequence = assembleFromFileCommand.getInputSequence();
		
		Helpers.log("one k-mer length: " + oneKmerLength);
		Helpers.log("genome length: " + inputSequence.length());
		
		assertTrue(inputSequence.equals(resultSequence));
	}
	
	//@Test
	public void ecoli() {
		//assembleFromFile("ecoli-1line.txt");
	}
}
