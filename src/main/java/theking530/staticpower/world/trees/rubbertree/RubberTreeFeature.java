package theking530.staticpower.world.trees.rubbertree;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.world.trees.AbstractStaticPowerTree;

public class RubberTreeFeature {
	public static final String NAME = new ResourceLocation(StaticPower.MOD_ID, "rubber_wood").toString();

	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> RUBBER_WOOD_TREE = FeatureUtils.register(NAME, Feature.TREE,
			AbstractStaticPowerTree.createStraightBlobTree(ModBlocks.RubberTreeLog.get(), ModBlocks.RubberTreeLeaves.get(), 4, 2, 0, 2).ignoreVines().build());

	public static final Holder<PlacedFeature> RUBBER_WOOD_TREE_CHECKED = PlacementUtils.register(NAME, RUBBER_WOOD_TREE,
			PlacementUtils.filteredByBlockSurvival(ModBlocks.RubberTreeSapling.get()));

	public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> RUBBER_WOOD_TREE_SPAWN = FeatureUtils.register(NAME, Feature.RANDOM_SELECTOR,
			new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(RUBBER_WOOD_TREE_CHECKED, 0.5f)), RUBBER_WOOD_TREE_CHECKED));

	public static final Holder<PlacedFeature> RUBBER_WOOD_TREE_PLACED = PlacementUtils.register(NAME, RUBBER_WOOD_TREE_SPAWN,
			VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2)));

}
