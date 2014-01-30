package mbi;

import java.io.IOException;
import java.util.List;

public class Assembler {

	private String inputSequence;
	List<String> kmers;
	private DeBruijnGraph deBruijnGraph;
	private String assembledSequence;
	
//	public List<String> shotgun(int oneKmerLength, int kmersOverlapLength) throws IOException {
//		return Arrays.asList("AAABA","ABAAB","BAABA","AABAB");
//	}

//	private String [] letters = {"A", "B"};
	
	public void loadSequence(String sequenceToLoad) {
		inputSequence = sequenceToLoad;
	}
	
	public void loadKmers(List<String> kmers) {
		this.kmers = kmers;
	}

	public void buildDeBruijnGraph (int graphDegree) throws IOException, MbiException {
		deBruijnGraph = DeBruijnGraphBuilder.getDeBruijnGraph(inputSequence, kmers, true, graphDegree-1);
	}
	
	public void assemble()
			throws MbiException, IOException {
		if(deBruijnGraph == null)
			System.out.println("graph NULL");
//		System.out.println("edges: " + deBruijnGraph.getEdgeCount());
//		System.out.println("vertices: " + deBruijnGraph.getVertexCount());
		List<String> path = deBruijnGraph.findEulerPath_FleuryAlg();
		if (path != null && path.size() > 0) {
			assembledSequence = pathToGenome(path);
		} else {
			throw new MbiException("Returned empty or null Euler path!");
		}
	}
	
	public String getAssembledSequence() {
		//System.out.println(assembledSequence);
		return assembledSequence;
	}

	public static String pathToGenome(List<String> path) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < path.size() - 1; ++i) {
			sb.append(path.get(i).substring(0, 1));
		}
		sb.append(path.get(path.size() - 1));
		return sb.toString();
	}

}