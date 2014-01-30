package mbi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Main {

	private static boolean doesFileExist(String fileName) {
		File inputFile = new File(fileName);
		if(!inputFile.exists())
		{
			System.out.println("There is no " + fileName + " file!");
			return false;
		}
		return true;
	}
	
	private static int parseIntArgument (String argument) throws Exception {
		int argumentAsInt;
		try {
	        argumentAsInt = Integer.parseInt(argument);
	    } catch (NumberFormatException e) {
	        throw new Exception();
	    }
		return argumentAsInt;
	}
	
	private static final String closingApplicationMessage = "Closing application...";
	
	private static void printClosingAppMessage() {
		System.out.println(closingApplicationMessage);
	}
	
	private static void createFileWithText(String fileName, String text) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print(text);
		writer.close();
	}
	
	private static void handleException(Exception exception) {
		System.out.println(exception.getMessage());
		printClosingAppMessage();
	}
	
	public static String oneLineFileToString(String pathToFile) throws IOException {
		String toReturn;
		BufferedReader br = new BufferedReader(new FileReader(pathToFile));
		try {
		    StringBuilder sequenceBuilder = new StringBuilder();
		    sequenceBuilder.append(br.readLine());
		    toReturn = sequenceBuilder.toString();
		   }
		finally {
		    br.close();
		}
		return toReturn;
	}
	
	public static void main(String[] args) {
		String inputGenomeFilePath;
		int kmerLength, kmerOverlapLength, deBruijnGraphDegree;
		if (args.length == 4) {
			inputGenomeFilePath = args[0];
			if(!doesFileExist(inputGenomeFilePath)) {
				System.out.println("Invalid input genome file path!");
				printClosingAppMessage();
				return;
			}
			try {
				kmerLength = parseIntArgument(args[1]);
			}
			catch (Exception e) {
		        System.out.println("Kmer length must be an integer!");
		        printClosingAppMessage();
		        return;
		    }
			try {
				kmerOverlapLength = parseIntArgument(args[2]);
			}
			catch (Exception e) {
		        System.out.println("Kmer overlap length must be an integer!");
		        printClosingAppMessage();
		        return;
		    }
			try {
				deBruijnGraphDegree = parseIntArgument(args[3]);
			}
			catch (Exception e) {
		        System.out.println("Kmer length must be an integer!");
		        printClosingAppMessage();
		        return;
		    }
			
			String inputGenomeSequence = null; 
			String assembledSequence = null;
			try {
				inputGenomeSequence = oneLineFileToString(inputGenomeFilePath);
				assembledSequence = DnaAssembler.assemble(inputGenomeFilePath, kmerLength, kmerOverlapLength, deBruijnGraphDegree);
			} catch (IOException e) {
				handleException(e);
			} catch (MbiException e) {
				handleException(e);
			}
			
			createFileWithText("output.txt", assembledSequence);
			
			StringBuilder logBuilder = new StringBuilder();
			if(inputGenomeSequence.equals(assembledSequence)) {
				logBuilder.append("INPUT equals RESULT");
			}
			else {
				logBuilder.append("INPUT differs from RESULT\n");			
				logBuilder.append("Longest common subsequence length: "
						+ Helpers.longestSubstringLength(inputGenomeSequence, assembledSequence));
				
//				startTime = System.nanoTime();
//				int longestCommonSubstringLength = longestSubstring(inputSequence, resultSequence);
//				Logger.log("longest: " + longestCommonSubstringLength);
//				Logger.logLongestCommonSubstringLength(Integer.toString(longestCommonSubstringLength));
//				elapsedTime = System.nanoTime() - startTime;
//				elapsedTimeInSeconds = (double)elapsedTime / 1000000000.0;
//				Logger.log("longest substring searching", Double.toString(elapsedTimeInSeconds));
			}
			createFileWithText("log.txt", logBuilder.toString());
		}
		else {
			System.out.println("Incorrect number of arguments!");
		}
	}
}
