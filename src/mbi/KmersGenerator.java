package mbi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class KmersGenerator {
	
	private String inputSequence;
	
	public final String getInputSequence() {
		return inputSequence;
	}
	
	public void loadSequence(String sequenceToLoad) {
		inputSequence = sequenceToLoad;
	}
	
	public void loadSequenceFromFile(String pathToFile) {
		String subStr = "";
		try {
			FileInputStream fstream = new FileInputStream(pathToFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				subStr += strLine;
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());

		}
		inputSequence = subStr.trim();
	}
	
	public void loadSequenceFromOneLineFile(String pathToFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(pathToFile));
		try {
		    StringBuilder sequenceBuilder = new StringBuilder();
		    sequenceBuilder.append(br.readLine());
		    inputSequence = sequenceBuilder.toString();
		   }
		finally {
		    br.close();
		}
	}
	
	public List<String> getKmers(int oneKmerLength, int kmersOverlapLength) throws IOException {
		List<String> results = new LinkedList<String>();
		for (int i = 0; i <= inputSequence.length() - oneKmerLength; i = i + (oneKmerLength-kmersOverlapLength)) {
			results.add(inputSequence.substring(i, i + oneKmerLength));
		}
		//Collections.shuffle(results);
		return results;
	}
	
	public List<String> getKmers(int oneKmerLength) throws IOException {
		return getKmers(oneKmerLength, oneKmerLength-1);
	}
	
	public String getRandomSequence(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; ++i) {
			double rand = Math.random();
			if (rand > 0.75) {
				builder.append("C");
			} else if (rand > 0.5) {
				builder.append("G");
			} else if (rand > 0.25) {
				builder.append("T");
			} else {
				builder.append("A");
			}
		}
		return builder.toString();
	}
}
