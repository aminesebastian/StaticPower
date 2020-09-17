package theking530.staticpower.init;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.trees.rubbertree.RubberTree;

public class ModTrees {

	public static void init() {
		for (Biome biome : ForgeRegistries.BIOMES) {
			biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
					Feature.NORMAL_TREE.withConfiguration(RubberTree.TREE_CONFIG).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(1, 0.05f, 1))));
		}
	}
}
