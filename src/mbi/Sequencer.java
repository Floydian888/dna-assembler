package mbi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Sequencer {

	// nie wiem czy ma to ostatecznie byæ, ale do naszych testów niech na razie zostanie
	public static String generateSequence(int length) {
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

	public static List<String> shotgun(String dna, int k) {
		List<String> results = new LinkedList<String>();
		for (int i = 0; i <= dna.length() - k; ++i) {
			results.add(dna.substring(i, i + k));
		}
		Collections.shuffle(results);
		return results;
	}

	public static DeBruijnGraph getDeBruijnGraph(Collection<String> kmers,
			boolean allowRepeatedEdges) {
		DeBruijnGraph graph = new DeBruijnGraph();
		graph.setGraph(graph);
		for (String kmer : kmers) {
			int s = 0;
			int e = kmer.length();

			String lo = kmer.substring(s, e - 1);
			String ld = kmer.substring(s + 1, e);
			if (!graph.containsVertex(lo)) {
				graph.addVertex(lo);
			}
			if (!graph.containsVertex(ld)) {
				graph.addVertex(ld);
			}
			if (!graph.containsEdge(graph.findEdge(lo,ld)) || allowRepeatedEdges) {
				graph.addEdge(graph.createEdge(lo,ld),lo,ld);
			}
		}
		System.out.println(graph.toString()); // mo¿na sobie zerkn¹æ czy nie oszukuje ;)
		return graph;

	}

	public static String assemble(List<String> kmers)
			throws MbiException {
		DeBruijnGraph graph = getDeBruijnGraph(kmers, true);
		List<String> path = graph.findEulerPath();
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

	public static String readSequenceFromFile(String fileName) {
		String subStr = "";
		try {
			FileInputStream fstream = new FileInputStream(fileName);
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
		return subStr.trim();

	}
}