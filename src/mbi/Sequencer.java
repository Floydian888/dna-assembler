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

public class Sequencer {

	private static Logger logger = new Logger();
	private String sequence;
	private DeBruijnGraph deBruijnGraph; 

	public final String getInputSequence() {
		return sequence;
	}
	
	public void loadSequence(String sequenceToLoad) {
		sequence = sequenceToLoad;
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
		sequence = builder.toString();
	}

	public List<String> shotgun(int oneKmerLength, int kmersOverlapLength) throws IOException {
		logger.log("oneKmerLength: " + oneKmerLength);
		logger.log("kmersOverlapLength: " + kmersOverlapLength);
		List<String> results = new LinkedList<String>();
		for (int i = 0; i <= sequence.length() - oneKmerLength; i = i + (oneKmerLength-kmersOverlapLength)) {
			results.add(sequence.substring(i, i + oneKmerLength));
		}
		//Collections.shuffle(results);
		return results;
	}
	
	public List<String> shotgun(int oneKmerLength) throws IOException {
		return shotgun(oneKmerLength, oneKmerLength-1);
	}
	
/*	public List<String> shotgun(int oneKmerLength, int kmersOverlapLength) throws IOException {
		return Arrays.asList("AAABA","ABAAB","BAABA","AABAB");
	}*/

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
	
	public static DeBruijnGraph getDeBruijnGraph(String inputSequence, Collection<String> kmers, boolean allowRepeatedEdges,
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
			
				
				
				for (int j = 0; j < graph.getVertexCount(); ++j) {
					
					String firstVertex = getVertex(graph, i);
					String secondVertex = getVertex(graph, j);

					if(firstVertex.length() != secondVertex.length())
						throw new MbiException("Vertices with strings of different length!");
					
					int length = firstVertex.length();
					
					if(firstVertex.substring(1, length).equals(secondVertex.substring(0, length-1))) {
						String lastLetterOfSecondVertex = secondVertex.substring(secondVertex.length()-1);
						String string =  firstVertex + lastLetterOfSecondVertex;
						int occurrencesNumber = countOccurrences(inputSequence, string);
						
//						Logger.log("---");
//						Logger.log("firstVertex: " + firstVertex);
//						Logger.log("secondVertex: " + secondVertex);
//						Logger.log("lastLetterOfSecondVertex: " + lastLetterOfSecondVertex);
//						Logger.log("string: " + string);
//						Logger.log("occurrencesNumber: " + occurrencesNumber);
						
						for (int k = 0; k < occurrencesNumber; k++) {
							String edge = graph.createEdge(firstVertex, secondVertex);
							if(edge != null)
								graph.addEdge(edge, firstVertex, secondVertex);
						}
					}
				}
			}
		} finally {
			logger.log("edges: " + Integer.toString(graph.getEdgeCount()));
			logger.log("vertices: " + Integer.toString(graph.getVertexCount()));
		}
//		Logger.log(graph.toString()); // mo�na sobie zerkn�� czy nie oszukuje ;)
		return graph;

	}

	public void buildDeBruijnGraph (String inputSequence, List<String> kmers, int graphDegree) throws IOException, MbiException {
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
		sequence = subStr.trim();
	}
	
	public void loadSequenceFromOneLineFile(String pathToFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(pathToFile));
		try {
		    StringBuilder sequenceBuilder = new StringBuilder();
		    sequenceBuilder.append(br.readLine());
		    sequence = sequenceBuilder.toString();
		   }
		finally {
		    br.close();
		}
	}
}