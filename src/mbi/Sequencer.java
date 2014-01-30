package mbi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sun.org.mozilla.javascript.ast.LetNode;

public class Sequencer {

	private String inputSequence;
	private DeBruijnGraph deBruijnGraph;
	
	public final String getInputSequence() {
		return inputSequence;
	}
	
	public void loadSequence(String sequenceToLoad) {
		inputSequence = sequenceToLoad;
	}
	
	// nie wiem czy ma to ostatecznie by�, ale do naszych test�w niech na razie zostanie
	public void generateSequence(int length) {
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
		inputSequence = builder.toString();
	}

	public List<String> shotgun(int oneKmerLength, int kmersOverlapLength) throws IOException {
		List<String> results = new LinkedList<String>();
		for (int i = 0; i <= inputSequence.length() - oneKmerLength; i = i + (oneKmerLength-kmersOverlapLength)) {
			results.add(inputSequence.substring(i, i + oneKmerLength));
		}
		//Collections.shuffle(results);
		return results;
	}
	
	public List<String> shotgun(int oneKmerLength) throws IOException {
		return shotgun(oneKmerLength, oneKmerLength-1);
	}
	
//	public List<String> shotgun(int oneKmerLength, int kmersOverlapLength) throws IOException {
//		return Arrays.asList("AAABA","ABAAB","BAABA","AABAB");
//	}

	private static int countOccurrences(String string, String subString) {

		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){

		       lastIndex = string.indexOf(subString,lastIndex);

		       if( lastIndex != -1){
		             count++;
		             lastIndex++;
		      }
		}
		return count;
	}
	
	private static String getVertex(DeBruijnGraph graph, int index) {
		return graph.getVertices().toArray()[index].toString();
	}
	
	private String [] letters = {"A", "G", "T", "C"};
//	private String [] letters = {"A", "B"};
	
	public DeBruijnGraph getDeBruijnGraph(String inputSequence, Collection<String> kmers, boolean allowRepeatedEdges,
			int vertexStringLength ) throws IOException, MbiException {
		DeBruijnGraph graph = new DeBruijnGraph();
		graph.setGraph(graph);
		try {
			for (String kmer : kmers) {
//				Logger.log("kmer: " + kmer);
				for (int i = 0; i <= (kmer.length() - vertexStringLength); i++) {
					String vertexString = kmer.substring(i, i + vertexStringLength);
					if (!graph.containsVertex(vertexString)) {
						graph.addVertex(vertexString);
					}
//					Logger.log(vertexString);
				}
			}
			
			for (int i = 0; i < graph.getVertexCount(); ++i) {
			
				String firstVertex = getVertex(graph, i);
				String subString = firstVertex.substring(1, firstVertex.length());
				List<String> possibleStrings = new LinkedList<String>();
				
//				logger.log("subString " + subString);
				
//				LinkedList<char[]> items = new LinkedList<char[]>();
//		        char[] item = new char[vertexStringLength];
//		        char[] input = {'A', 'B'};
				
				for (int j = 0; j < letters.length; j++) {
					
					possibleStrings.add(subString + letters[j]);
				}
				
//				logger.log(possibleStrings.toString());
				
				for (String string : possibleStrings) {
					if(graph.containsVertex(string)) {
						String edge = graph.createEdgeLabel(firstVertex, string);
						if(edge != null) {
							int occurrencesNumber = countOccurrences(inputSequence, edge);
//							logger.log("edge: " + edge);
//							logger.log("occ: " + occurrencesNumber);
							if(occurrencesNumber > 0) {
								for (int k = 0; k < occurrencesNumber; k++) {
									graph.addEdge(edge + k, firstVertex, string);
								}
							}
						}

					}
				}
			}
		} finally {
			Logger.log("edges: " + Integer.toString(graph.getEdgeCount()));
			Logger.log("vertices: " + Integer.toString(graph.getVertexCount()));
		}
//		Logger.log(graph.toString()); // mo�na sobie zerkn�� czy nie oszukuje ;)
		return graph;

	}

	public void buildDeBruijnGraph (List<String> kmers, int graphDegree) throws IOException, MbiException {
		deBruijnGraph = getDeBruijnGraph(inputSequence, kmers, true, graphDegree-1);
	}
	
	public String assemble()
			throws MbiException {
		List<String> path = deBruijnGraph.findEulerPath_Fleury();
		if (path != null && path.size() > 0) {
			return pathToGenome(path);
		} else {
			throw new MbiException("Returned empty or null Euler path!");
		}
	}

	public static String pathToGenome(List<String> path) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < path.size() - 1; ++i) {
			sb.append(path.get(i).substring(0, 1));
		}
		sb.append(path.get(path.size() - 1));
		return sb.toString();
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
}