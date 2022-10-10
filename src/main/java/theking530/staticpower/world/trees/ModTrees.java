package theking530.staticpower.world.trees;

import java.util.HashSet;
import java.util.LinkedHashSet;

import theking530.staticpower.world.trees.rubbertree.RubberTreePlacer;

public class ModTrees {
	public static final HashSet<AbstractStaticPowerTree> TREES = new LinkedHashSet<>();
	public static RubberTreePlacer rubberTree;

	public static void init() {
		preRegisterTree(rubberTree = new RubberTreePlacer());
	}

	/**
	 * Pre-registers a tree for registration through the init method.
	 * 
	 * @param tree The tree to pre-register.
	 * @return The tree that was passed.
	 */
	public static AbstractStaticPowerTree preRegisterTree(AbstractStaticPowerTree tree) {
		TREES.add(tree);
		return tree;
	}

}
