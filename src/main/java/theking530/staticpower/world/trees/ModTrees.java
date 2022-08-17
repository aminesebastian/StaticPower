package theking530.staticpower.world.trees;

import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.StaticPowerRegistry;

public class ModTrees {
	public static RubberTree rubberTree;

	public static void init() {
		StaticPowerRegistry.preRegisterTree(rubberTree = new RubberTree());
	}

	public static void addTreeFeatures(BiomeLoadingEvent event) {
		for (AbstractStaticPowerTree tree : StaticPowerRegistry.TREES) {
			tree.growTree(event);
		}
	}
}
