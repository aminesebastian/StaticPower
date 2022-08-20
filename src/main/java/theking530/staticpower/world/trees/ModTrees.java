package theking530.staticpower.world.trees;

import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.world.trees.rubbertree.RubberTreePlacer;

public class ModTrees {
	public static RubberTreePlacer rubberTree;

	public static void init() {
		StaticPowerRegistry.preRegisterTree(rubberTree = new RubberTreePlacer());
	}

	public static void addTreeFeatures(BiomeLoadingEvent event) {
		for (AbstractStaticPowerTree tree : StaticPowerRegistry.TREES) {
			tree.addTreeToBiome(event);
		}
	}
}
