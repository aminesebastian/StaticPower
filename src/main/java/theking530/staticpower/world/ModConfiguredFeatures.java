package theking530.staticpower.world;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.RegistryEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.world.features.StaticPowerLakeFeature;
import theking530.staticpower.world.features.StaticPowerLakeFeatureConfiguration;
import theking530.staticpower.world.trees.AbstractStaticPowerTree;

public class ModConfiguredFeatures {
	public static final Feature<StaticPowerLakeFeatureConfiguration> STATIC_LAKE = new StaticPowerLakeFeature(new ResourceLocation(StaticPower.MOD_ID, "static_lake"),
			StaticPowerLakeFeatureConfiguration.CODEC);

	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> RUBBER_WOOD_TREE = FeatureUtils.register("rubber_tree", Feature.TREE,
			AbstractStaticPowerTree.createStraightBlobTree(ModBlocks.RubberTreeLog.get(), ModBlocks.RubberTreeLeaves.get(), 4, 2, 0, 2).ignoreVines().build());

	public static final Holder<PlacedFeature> RUBBER_WOOD_TREE_CHECKED = PlacementUtils.register("rubber_tree_checked", RUBBER_WOOD_TREE,
			PlacementUtils.filteredByBlockSurvival(ModBlocks.RubberTreeSapling.get()));

	public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> RUBBER_WOOD_TREE_SPAWN = FeatureUtils.register("rubber_tree_spawn", Feature.RANDOM_SELECTOR,
			new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(RUBBER_WOOD_TREE_CHECKED, 0.25f)), RUBBER_WOOD_TREE_CHECKED));

	public static void registerFeatures(final RegistryEvent.Register<Feature<?>> event) {
		event.getRegistry().register(STATIC_LAKE);
	}

}
