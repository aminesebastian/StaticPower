package theking530.staticpower.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;
import theking530.staticpower.trees.rubbertree.RubberTree;

public class ModTrees {
	public static void init() {
//		for (Biome biome : ForgeRegistries.BIOMES) {
//			biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
//					Feature.TREE.withConfiguration(RubberTree.TREE_CONFIG)
//							.withPlacement(Placement.field_242904_h.configure(new NoPlacementConfig)));
//		}
	}

	@SuppressWarnings("deprecation")
	private static <C extends IFeatureConfig, F extends Feature<C>> F register(String key, F value) {
		return Registry.register(Registry.FEATURE, new ResourceLocation(StaticPower.MOD_ID, key), value);
	}
}
