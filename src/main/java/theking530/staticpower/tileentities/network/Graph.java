package theking530.staticpower.tileentities.network;

import java.util.List;

public class Graph<T> {
	private final List<Node<T>> Nodes;
	private final List<Edge<T>> Edges;

	public Graph(List<Node<T>> nodes, List<Edge<T>> edges) {
		Nodes = nodes;
		Edges = edges;
	}

	public List<Node<T>> getNodes() {
		return Nodes;
	}

	public int getNodeCount() {
		return Nodes.size();
	}

	public List<Edge<T>> getEdges() {
		return Edges;
	}

	public int getEdgeCount() {
		return Edges.size();
	}
}