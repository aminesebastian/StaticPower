package theking530.staticpower.world.trees.rubbertree;

import java.util.Random;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.world.ModConfiguredFeatures;
import theking530.staticpower.world.ModPlacedFeatures;
import theking530.staticpower.world.trees.AbstractStaticPowerTree;

@SuppressWarnings("deprecation")
public class RubberTreePlacer extends AbstractStaticPowerTree {
	public RubberTreePlacer() {
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
		return ModConfiguredFeatures.RUBBER_WOOD_TREE;
	}

	@Override
	public boolean shouldAddToBiome(BiomeLoadingEvent event, Set<BiomeDictionary.Type> biomeTypes) {
		if (!StaticPowerConfig.SERVER.generateRubberTrees.get()) {
			return false;
		}

		// Rubber trees cannot spawn in snowy biomes (if enabled).
		if (StaticPowerConfig.SERVER.disableRubberTreesInSnowyBiomes.get() && biomeTypes.contains(BiomeDictionary.Type.COLD)) {
			return false;
		}

		return true;
	}

	@Override
	public TreeGenerationBundle getGenerationBundle() {
		return new TreeGenerationBundle(ModConfiguredFeatures.RUBBER_WOOD_TREE, ModConfiguredFeatures.RUBBER_WOOD_TREE_CHECKED, ModConfiguredFeatures.RUBBER_WOOD_TREE_SPAWN,
				ModPlacedFeatures.RUBBER_WOOD_TREE_PLACED);
	}
}