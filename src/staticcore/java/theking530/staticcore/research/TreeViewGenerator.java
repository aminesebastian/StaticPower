package theking530.staticcore.research;

/**
 * Massive thanks to cwi-swat and his algorithm here! https://github.com/cwi-swat/timelytree/blob/master/src/treelayout/algorithms/buchheimwalker/Algorithm.java
 */

import java.util.Arrays;

public class TreeViewGenerator {
	private double[] m_depths = new double[10];
	private int m_maxDepth = 0;

	private <T> double spacing(NodeItem<T> l, NodeItem<T> r, boolean siblings) {
		return 0.5 * (l.width + r.width);
	}

	private <T> void updateDepths(int depth, NodeItem<T> item) {
		double d = (item.height);
		if (m_depths.length <= depth) {
			m_depths = resize(m_depths, 3 * depth / 2);
		}
		m_depths[depth] = Math.max(m_depths[depth], d);
		m_maxDepth = Math.max(m_maxDepth, depth);
	}

	private void determineDepths() {
		for (int i = 1; i < m_maxDepth; ++i)
			m_depths[i] += m_depths[i - 1];
	}

	// ------------------------------------------------------------------------

	/**
	 * @see prefuse.action.Action#run(double)
	 */
	public <T> void run(NodeItem<T> root) {

		Arrays.fill(m_depths, 0);
		m_maxDepth = 0;

		// do first pass - compute breadth information, collect depth info
		firstWalk(root, 0, 1);

		// sum up the depth info
		determineDepths();

		// do second pass - assign layout positions
		secondWalk(root, null, -root.prelim, 0);
	}

	private <T> void firstWalk(NodeItem<T> n, int num, int depth) {
		n.number = num;
		updateDepths(depth, n);

		if (n.children.length == 0) // is leaf
		{
			NodeItem<T> l = (NodeItem<T>) n.prevSibling;
			if (l == null) {
				n.prelim = 0;
			} else {
				n.prelim = l.prelim + spacing(l, n, true);
			}
		} else {
			NodeItem<T> leftMost = (NodeItem<T>) n.getFirstChild();
			NodeItem<T> rightMost = (NodeItem<T>) n.getLastChild();
			NodeItem<T> defaultAncestor = leftMost;
			NodeItem<T> c = leftMost;
			for (int i = 0; c != null; ++i, c = (NodeItem<T>) c.nextSibling) {
				firstWalk(c, i, depth + 1);
				defaultAncestor = apportion(c, defaultAncestor);
			}

			executeShifts(n);

			double midpoint = 0.5 * (leftMost.prelim + rightMost.prelim);

			NodeItem<T> left = (NodeItem<T>) n.prevSibling;
			if (left != null) {
				n.prelim = left.prelim + spacing(left, n, true);
				n.mod = n.prelim - midpoint;
			} else {
				n.prelim = midpoint;
			}
		}
	}

	private <T> NodeItem<T> apportion(NodeItem<T> v, NodeItem<T> a) {
		NodeItem<T> w = (NodeItem<T>) v.prevSibling;
		if (w != null) {
			NodeItem<T> vip, vim, vop, vom;
			double sip, sim, sop, som;

			vip = vop = v;
			vim = w;
			vom = (NodeItem<T>) vip.parent.getFirstChild();

			sip = vip.mod;
			sop = vop.mod;
			sim = vim.mod;
			som = vom.mod;

			NodeItem<T> nr = nextRight(vim);
			NodeItem<T> nl = nextLeft(vip);
			while (nr != null && nl != null) {
				vim = nr;
				vip = nl;
				vom = nextLeft(vom);
				vop = nextRight(vop);
				vop.ancestor = v;
				double shift = (vim.prelim + sim) - (vip.prelim + sip) + spacing(vim, vip, false);
				if (shift > 0) {
					moveSubtree(ancestor(vim, v, a), v, shift);
					sip += shift;
					sop += shift;
				}
				sim += vim.mod;
				sip += vip.mod;
				som += vom.mod;
				sop += vop.mod;

				nr = nextRight(vim);
				nl = nextLeft(vip);
			}
			if (nr != null && nextRight(vop) == null) {
				vop.thread = nr;
				vop.mod += sim - sop;
			}
			if (nl != null && nextLeft(vom) == null) {
				vom.thread = nl;
				vom.mod += sip - som;
				a = v;
			}
		}
		return a;
	}

	private <T> NodeItem<T> nextLeft(NodeItem<T> n) {
		NodeItem<T> c = null;
		c = (NodeItem<T>) n.getFirstChild();
		return (c != null ? c : n.thread);
	}

	private <T> NodeItem<T> nextRight(NodeItem<T> n) {
		NodeItem<T> c = null;
		c = (NodeItem<T>) n.getLastChild();
		return (c != null ? c : n.thread);
	}

	private <T> void moveSubtree(NodeItem<T> wm, NodeItem<T> wp, double shift) {
		double subtrees = wp.number - wm.number;
		wp.change -= shift / subtrees;
		wp.shift += shift;
		wm.change += shift / subtrees;
		wp.prelim += shift;
		wp.mod += shift;
	}

	private <T> void executeShifts(NodeItem<T> n) {
		double shift = 0, change = 0;
		for (NodeItem<T> c = (NodeItem<T>) n.getLastChild(); c != null; c = (NodeItem<T>) c.prevSibling) {
			c.prelim += shift;
			c.mod += shift;
			change += c.change;
			shift += c.shift + change;
		}
	}

	private <T> NodeItem<T> ancestor(NodeItem<T> vim, NodeItem<T> v, NodeItem<T> a) {
		NodeItem<T> p = (NodeItem<T>) v.parent;
		if (vim.ancestor.parent == p) {
			return vim.ancestor;
		} else {
			return a;
		}
	}

	private <T> void secondWalk(NodeItem<T> n, NodeItem<T> p, double m, int depth) {
		n.x = n.prelim + m;
		n.y = m_depths[depth];

		depth += 1;
		for (NodeItem<T> c = (NodeItem<T>) n.getFirstChild(); c != null; c = (NodeItem<T>) c.nextSibling) {
			secondWalk(c, n, m + n.mod, depth);
		}

		n.clear();
	}

	/**
	 * Resize the given array as needed to meet a target size.
	 * 
	 * @param a    the array to potentially resize
	 * @param size the minimum size of the target array
	 * @return the resized array, if the original array meets the size requirement,
	 *         it is simply return, otherwise a new array is allocated and the
	 *         contents of the original array are copied over.
	 */
	public static final double[] resize(double[] a, int size) {
		if (a.length >= size)
			return a;
		double[] b = new double[size];
		System.arraycopy(a, 0, b, 0, a.length);
		return b;
	}

	public static class NodeItem<T> {
		public final T payload;
		public int number = -2;
		public double prelim;
		public double mod;
		public NodeItem<T> ancestor = null;
		public NodeItem<T> thread = null;
		public double change;
		public double shift;

		double width, height;
		double x, y;
		NodeItem<T>[] children;
		NodeItem<T> parent;
		NodeItem<T> nextSibling;
		NodeItem<T> prevSibling;

		public NodeItem(T payload) {
			this.payload = payload;
			ancestor = this;
			number = -1;
		}

		public NodeItem<T> getFirstChild() {
			if (children.length != 0) {
				return children[0];
			}
			return null;
		}

		public NodeItem<T> getLastChild() {
			if (children.length != 0) {
				return children[children.length - 1];
			}
			return null;
		}

		public void clear() {
			number = -2;
			prelim = mod = shift = change = 0;
			ancestor = thread = null;
		}
	}
}
