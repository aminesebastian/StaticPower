package theking530.staticpower.world.trees;

import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.init.ModBlocks;

public class RubberTree extends AbstractStaticPowerTree {
	public static final TreeConfiguration TREE_CONFIG = createStraightBlobTree(ModBlocks.RubberTreeLog, ModBlocks.RubberTreeLeaves, 4, 2, 0, 2).ignoreVines().build();

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
		if (StaticPowerConfig.SERVER.disableRubberTreesInSnowyBiomes.get() && event.getClimate().precipitation == Precipitation.SNOW) {
			return false;
		}

		// Check to make sure we're in the correct biome.
		if (event.getCategory() == BiomeCategory.FOREST || event.getCategory() == BiomeCategory.EXTREME_HILLS || event.getCategory() == BiomeCategory.JUNGLE
				|| event.getCategory() == BiomeCategory.RIVER || event.getCategory() == BiomeCategory.SAVANNA || event.getCategory() == BiomeCategory.SWAMP
				|| event.getCategory() == BiomeCategory.TAIGA) {

			// Calculate the bonus chance. // Add the trees with bonus.
			int difference = StaticPowerConfig.SERVER.maxRubberTreeCount.get() - StaticPowerConfig.SERVER.minRubberTreeCount.get();
			int maxBonus = SDMath.getRandomIntInRange(0, difference);
			int actualBonus = SDMath.diceRoll(StaticPowerConfig.SERVER.rubberTreeSpawnChance.get()) ? Math.max(0, maxBonus) : 0;
			event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.TREE.configured(RubberTree.TREE_CONFIG)
					.placed(CountPlacement.of(UniformInt.of(StaticPowerConfig.SERVER.minRubberTreeCount.get(), StaticPowerConfig.SERVER.minRubberTreeCount.get() + actualBonus))));

			return true;
		}

		return false;
	}

	private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block trunk, Block leaves, int p_195149_, int p_195150_, int p_195151_, int p_195152_) {
		return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(trunk), new StraightTrunkPlacer(p_195149_, p_195150_, p_195151_), BlockStateProvider.simple(leaves),
				new BlobFoliagePlacer(ConstantInt.of(p_195152_), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
	}

	private static TreeConfiguration.TreeConfigurationBuilder createOak() {
		return createStraightBlobTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 4, 2, 0, 2).ignoreVines();
	}

}
