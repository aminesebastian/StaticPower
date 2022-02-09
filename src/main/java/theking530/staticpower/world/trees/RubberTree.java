package theking530.staticpower.world.trees;

import net.minecraft.data.worldgen.Features;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.FrequencyWithExtraChanceDecoratorConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.init.ModBlocks;

public class RubberTree extends AbstractStaticPowerTree {
	public static final TreeConfiguration TREE_CONFIG = new TreeConfiguration.TreeConfigurationBuilder(
			new SimpleStateProvider(ModBlocks.RubberTreeLog.defaultBlockState()), new StraightTrunkPlacer(4, 2, 2),
			new SimpleStateProvider(ModBlocks.RubberTreeLeaves.defaultBlockState()),
			new SimpleStateProvider(ModBlocks.RubberTreeSapling.defaultBlockState()),
			new BlobFoliagePlacer(UniformInt.of(2, 2), UniformInt.of(0, 0), 5), new TwoLayersFeatureSize(10, 0, 10))
					.build();

	@Override
	protected TreeConfiguration getTreeConfiguration() {
		return TREE_CONFIG;
	}

	@Override
	public boolean addToBiome(BiomeLoadingEvent event) {
		// Check if rubber tree spawning is disabled (we do it here instead of not
		// registering the tree at all to avoid breaking the Rubber Tree blocks that
		// depend on the tree defenition not being null.
		if (!StaticPowerConfig.SERVER.generateRubberTrees.get()) {
			return false;
		}

		// Rubber trees cannot spawn in snowy biomes (if enabled).
		if (StaticPowerConfig.SERVER.disableRubberTreesInSnowyBiomes.get()
				&& event.getClimate().precipitation == Precipitation.SNOW) {
			return false;
		}

		// Check to make sure we're in the correct biome.
		if (event.getCategory() == BiomeCategory.FOREST || event.getCategory() == BiomeCategory.EXTREME_HILLS
				|| event.getCategory() == BiomeCategory.JUNGLE || event.getCategory() == BiomeCategory.RIVER
				|| event.getCategory() == BiomeCategory.SAVANNA || event.getCategory() == BiomeCategory.SWAMP
				|| event.getCategory() == BiomeCategory.TAIGA) {

			// Calculate the bonus chance.
			int bonusCount = SDMath.getRandomIntInRange(StaticPowerConfig.SERVER.minRubberTreeCount.get(),
					StaticPowerConfig.SERVER.maxRubberTreeCount.get());

			// Add the trees.
			event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.TREE
					.configured(RubberTree.TREE_CONFIG).decorated(Features.Decorators.HEIGHTMAP_SQUARE).decorated(
							FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(
									StaticPowerConfig.SERVER.minRubberTreeCount.get(),
									StaticPowerConfig.SERVER.rubberTreeSpawnChance.get().floatValue(), bonusCount))));
			return true;
		}

		return false;
	}

}
