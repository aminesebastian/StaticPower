package theking530.staticpower.cables.network.pathfinding;

public class Edge<T> {
	private final String ID;
	private final Node<T> From;
	private final Node<T> To;
	private final float EdgeWeight;

	public Edge(String id, Node<T> from, Node<T> to, float weight) {
		ID = id;
		From = from;
		To = to;
		EdgeWeight = weight;
	}

	public String getId() {
		return ID;
	}

	public Node<T> getTo() {
		return To;
	}

	public Node<T> getFrom() {
		return From;
	}

	public float getEdgeWeight() {
		return EdgeWeight;
	}

	@Override
	public String toString() {
		return String.format("[ID]: %1$s   [From]: %2$s   [To]: %3$s   [Weight]:  %4$s", ID, From, To, EdgeWeight);
	}
}