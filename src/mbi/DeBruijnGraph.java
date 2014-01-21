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
	//++++++++++++ straszna gimmnastyka, ale inaczej nie dzia³a +++++++++
	private DirectedSparseMultigraph<String, String> g;
	public void setGraph(DirectedSparseMultigraph<String, String> graph){
		g = graph;
	}
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public String createEdge(String v1, String v2) {
		if(v1.substring(1).equals(v2.substring(0, v2.length()-1))){	
			String suffix="";
			if(g.containsEdge(g.findEdge(v1,v2))){
				Collection<String> fromV1 = g.getOutEdges(v1);
				Collection<String> intoV2 = g.getInEdges(v2);
				int nextNo=1;
				for(String fv1:fromV1){
					if(intoV2.contains(fv1)){
						++nextNo;
					}
				}
				suffix="("+nextNo+")";
			}
			if(v1.length()>v2.length()){
				return v1+v2.substring(v2.length()-1, v2.length())+suffix;
			}else{
				return v1.substring(0,1)+v2+suffix;
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

	public synchronized List<String> findEulerPath() // na razie algorytm Fleury'ego
			throws MbiException {
		List<String> path = new LinkedList<String>();
		DeBruijnGraph gtmp = (DeBruijnGraph) g; // niezbyt eleganckie 
		Set<String> imbalanced = gtmp.getImbalancedVertices();
		if (imbalanced.size() != 2) {
			throw new MbiException("Imbalanced graph given");
		}
		while (gtmp.getVertices().size() != 0) {
			String start = null, end = null;
			int startIndex = -1;
			if (path.size() == 0) {
				for (String vert : imbalanced) {
					if (gtmp.inDegree(vert) < gtmp.outDegree(vert)) {
						start = vert;
					} else if (gtmp.inDegree(vert) > gtmp.outDegree(vert)) {
						end = vert;
					}
				}
				assert (start != null && end != null && !start.equals(end));
			} else {
				for (String vert : gtmp.getVertices()) {
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
				Collection<String> directions = gtmp.getOutgoing_internal(start);
				if (directions.size() > 0) {
					String direction = directions.toArray(new String[directions.size()])[0];
					start = gtmp.getDest(direction);
					gtmp.removeEdge(direction);
				} else {
					start = null;
				}
			}
			Set<String> vertsToRemove = new HashSet<String>();
			for (String vert : gtmp.getVertices()) {
				if (gtmp.inDegree(vert) == 0 && gtmp.outDegree(vert) == 0) {
					vertsToRemove.add(vert);
				}
			}
			for (String vert : vertsToRemove) {
				gtmp.removeVertex(vert);
			}
		}
		return path;
	}
}
