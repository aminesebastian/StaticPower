package theking530.staticpower.world.trees;

import java.util.List;
import java.util.Random;
import java.util.Set;

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
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.init.ModBlocks;

@SuppressWarnings("deprecation")
public class RubberTree extends AbstractStaticPowerTree {
	public static final String NAME = new ResourceLocation(StaticPower.MOD_ID, "rubber_wood").toString();

	public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> RUBBER_WOOD = FeatureUtils.register(NAME,
			Feature.TREE, createStraightBlobTree(ModBlocks.RubberTreeLog, ModBlocks.RubberTreeLeaves, 4, 2, 0, 2)
					.ignoreVines().build());

	public static final Holder<PlacedFeature> RUBBER_WOOD_CHECKED = PlacementUtils.register(NAME, RUBBER_WOOD,
			PlacementUtils.filteredByBlockSurvival(ModBlocks.RubberTreeSapling));

	public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> RUBBER_WOOD_SPAWN = FeatureUtils
			.register(NAME, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
					List.of(new WeightedPlacedFeature(RUBBER_WOOD_CHECKED, 0.5f)), RUBBER_WOOD_CHECKED));

	public static final Holder<PlacedFeature> RUBBER_WOOD_PLACED = PlacementUtils.register(NAME, RUBBER_WOOD_SPAWN,
			VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2)));

	public RubberTree() {
		super();
		biomeRules.add(BiomeDictionary.Type.PLAINS);
		biomeRules.add(BiomeDictionary.Type.FOREST);
		biomeRules.add(BiomeDictionary.Type.HILLS);
		biomeRules.add(BiomeDictionary.Type.JUNGLE);
		biomeRules.add(BiomeDictionary.Type.RIVER);
		biomeRules.add(BiomeDictionary.Type.SAVANNA);
		biomeRules.add(BiomeDictionary.Type.SWAMP);
	}

	@Override
	protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random random, boolean p_204330_) {
		return RUBBER_WOOD;
	}

	@Override
	public boolean isSpawningEnabled(BiomeLoadingEvent event, Set<BiomeDictionary.Type> biomeTypes) {
		if (!StaticPowerConfig.SERVER.generateRubberTrees.get()) {
			return false;
		}

		// Rubber trees cannot spawn in snowy biomes (if enabled).
		if (StaticPowerConfig.SERVER.disableRubberTreesInSnowyBiomes.get()
				&& biomeTypes.contains(BiomeDictionary.Type.COLD)) {
			return false;
		}

		return false;
	}

	@Override
	public TreeGenerationBundle getGenerationBundle() {
		return new TreeGenerationBundle(RUBBER_WOOD, RUBBER_WOOD_CHECKED, RUBBER_WOOD_SPAWN, RUBBER_WOOD_PLACED);
	}
}
