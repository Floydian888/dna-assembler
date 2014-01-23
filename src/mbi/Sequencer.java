package mbi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Sequencer {

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

	public List<String> shotgun(int oneKmerLength, int kmersOverlapLength) {
		List<String> results = new LinkedList<String>();
		for (int i = 0; i <= sequence.length() - oneKmerLength; i = i + (oneKmerLength-kmersOverlapLength)) {
			results.add(sequence.substring(i, i + oneKmerLength));
		}
		//Collections.shuffle(results);
		return results;
	}
	
	public List<String> shotgun(int oneKmerLength) {
		return shotgun(oneKmerLength, oneKmerLength-1);
	}

	public static DeBruijnGraph getDeBruijnGraph(Collection<String> kmers,
			boolean allowRepeatedEdges) throws IOException {
		DeBruijnGraph graph = new DeBruijnGraph();
		graph.setGraph(graph);
		for (String kmer : kmers) {
			int s = 0;
			int e = kmer.length();

			// for kmer AGTA beggining is AGT end is GTA
			String beggining = kmer.substring(s, e - 1);
			String end = kmer.substring(s + 1, e);
			if (!graph.containsVertex(beggining)) {
				graph.addVertex(beggining);
			}
			if (!graph.containsVertex(end)) {
				graph.addVertex(end);
			}
			if (!graph.containsEdge(graph.findEdge(beggining,end)) || allowRepeatedEdges) {
				graph.addEdge(graph.createEdge(beggining,end),beggining,end);
			}
		}
		//Helpers.log("edges: " + Integer.toString(graph.getEdgeCount()));
		//Helpers.log("vertices: " + Integer.toString(graph.getVertexCount()));
		//System.out.println(graph.toString()); // mo�na sobie zerkn�� czy nie oszukuje ;)
		return graph;

	}

	public void buildDeBruijnGraph (List<String> kmers) throws IOException {
		deBruijnGraph = getDeBruijnGraph(kmers, true);
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