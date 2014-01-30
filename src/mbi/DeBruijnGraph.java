package mbi;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class DeBruijnGraph extends DirectedSparseMultigraph<String, String> {
	private static final long serialVersionUID = 1L;
	
	//+++++++++++++++++++++++ magic happens +++++++++++++++++++++++++++++
	//++++++++++++ straszna gimmnastyka, ale inaczej nie dzia�a +++++++++
//	private DirectedSparseMultigraph<String, String> g;
//	public void setGraph(DirectedSparseMultigraph<String, String> graph){
//		g = graph;
//	}
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public String createEdgeLabel(String vertex1, String vertex2) {
		if(vertex1.substring(1).equals(vertex2.substring(0, vertex2.length()-1))){	
			String suffix="";
			if(containsEdge(findEdge(vertex1,vertex2))){
				Collection<String> fromV1 = getOutEdges(vertex1);
				Collection<String> intoV2 = getInEdges(vertex2);
				int nextNo=1;
				for(String fv1:fromV1){
					if(intoV2.contains(fv1)){
						++nextNo;
					}
				}
				suffix="("+nextNo+")";
			}
			if(vertex1.length()>vertex2.length()){
				return vertex1+vertex2.substring(vertex2.length()-1, vertex2.length())+suffix;
			}else{
				return vertex1.substring(0,1)+vertex2+suffix;
			}
		}else{
			return null;
		}
	}

	public Set<String> getImbalancedVertices() {
		Set<String> vers = new HashSet<String>();
		for (String vert : this.getVertices()) {
			if (this.inDegree(vert) != this.outDegree(vert)) {
				vers.add(vert);
			}
		}
		return vers;
	}

	public synchronized List<String> findEulerPath_FleuryAlg() // na razie algorytm Fleury'ego
			throws MbiException {
		List<String> path = new LinkedList<String>();
//		DeBruijnGraph gtmp = (DeBruijnGraph) g; // niezbyt eleganckie 
		Set<String> imbalanced = getImbalancedVertices();
		if (imbalanced.size() != 2) {
			throw new MbiException("Imbalanced graph given");
		}
		while (getVertices().size() != 0) {
			String start = null, end = null;
			int startIndex = -1;
			if (path.size() == 0) {
				for (String vert : imbalanced) {
					if (inDegree(vert) < outDegree(vert)) {
						start = vert;
					} else if (inDegree(vert) > outDegree(vert)) {
						end = vert;
					}
				}
				assert (start != null && end != null && !start.equals(end));
			} else {
				for (String vert : getVertices()) {
					startIndex = path.lastIndexOf(vert);
					if (startIndex >= 0) {
						path.remove(startIndex);
						start = vert;
						break;
					}
				}
			}
			while (start != null) {
				if (startIndex == -1) {
					path.add(start);
				} else {
					path.add(startIndex++, start);
				}
				Collection<String> directions = getOutgoing_internal(start);
				if (directions.size() > 0) {
					String direction = directions.toArray(new String[directions.size()])[0];
					start = getDest(direction);
					removeEdge(direction);
				} else {
					start = null;
				}
			}
			Set<String> vertsToRemove = new HashSet<String>();
			for (String vert : getVertices()) {
				if (inDegree(vert) == 0 && outDegree(vert) == 0) {
					vertsToRemove.add(vert);
				}
			}
			for (String vert : vertsToRemove) {
				removeVertex(vert);
			}
		}
		return path;
	}
}
