package mbi;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DeBruijnGraphBuilder {
	
	private static String [] letters = {"A", "G", "T", "C"};
	
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
//		graph.setGraph(graph);
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
}
