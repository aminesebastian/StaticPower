package theking530.staticpower.tileentities.network.pathfinding;

import java.util.Objects;

public class Node<T> {
    private final T ID;

    public Node(T id) {
        this.ID = id;
    }

    public T getId() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return ID.equals(node.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    @Override
    public String toString() {
		return ID.toString();
    }
}