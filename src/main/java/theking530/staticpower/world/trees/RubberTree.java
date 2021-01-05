package theking530.staticpower.world.trees;

import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.init.ModBlocks;

public class RubberTree extends AbstractStaticPowerTree {
	public static final BaseTreeFeatureConfig TREE_CONFIG = new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.RubberTreeLog.getDefaultState()),
			new SimpleBlockStateProvider(ModBlocks.RubberTreeLeaves.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3),
			new StraightTrunkPlacer(4, 2, 2), new TwoLayerFeature(10, 0, 10)).build();

	@Override
	protected BaseTreeFeatureConfig getTreeConfiguration() {
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
		if (StaticPowerConfig.SERVER.disableRubberTreesInSnowyBiomes.get() && event.getClimate().precipitation == RainType.SNOW) {
			return false;
		}

		// Check to make sure we're in the correct biome.
		if (event.getCategory() == Category.FOREST || event.getCategory() == Category.EXTREME_HILLS || event.getCategory() == Category.JUNGLE || event.getCategory() == Category.RIVER
				|| event.getCategory() == Category.SAVANNA || event.getCategory() == Category.SWAMP || event.getCategory() == Category.TAIGA) {

			// Calculate the bonus chance.
			int bonusCount = SDMath.getRandomIntInRange(StaticPowerConfig.SERVER.minRubberTreeCount.get(), StaticPowerConfig.SERVER.maxRubberTreeCount.get());

			// Add the trees.
			event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
					Feature.TREE.withConfiguration(RubberTree.TREE_CONFIG).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(
							new AtSurfaceWithExtraConfig(StaticPowerConfig.SERVER.minRubberTreeCount.get(), StaticPowerConfig.SERVER.rubberTreeSpawnChance.get().floatValue(), bonusCount))));
			return true;
		}

		return false;
	}

}
