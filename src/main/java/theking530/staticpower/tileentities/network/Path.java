package theking530.staticpower.tileentities.network;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Path<T> {
	private final List<Node<T>> Path;

	public Path(List<Node<T>> path) {
		this.Path = path;
	}

	public Node<T> at(int i) {
		return Path.get(i);
	}

	public int length() {
		return Path.size();
	}

	public Deque<T> toQueue() {
		Deque<T> path = new ArrayDeque<>();

		for (int i = length() - 1; i >= 0; --i) {
			path.push(at(i).getId());
		}

		return path;
	}
}