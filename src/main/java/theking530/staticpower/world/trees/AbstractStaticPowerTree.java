package theking530.staticpower.world.trees;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;

@SuppressWarnings("deprecation")
public abstract class AbstractStaticPowerTree extends AbstractTreeGrower {
	protected final HashSet<BiomeDictionary.Type> biomeRules;

	public AbstractStaticPowerTree() {
		biomeRules = new HashSet<>();
	}

	public boolean addTreeToBiome(BiomeLoadingEvent event) {
		// Check to make sure we're in the correct biome.
		ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, event.getName());
		Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(key);
		if (!shouldAddToBiome(event, types)) {
			return false;
		}

		// Check to make sure this is a valid biome.
		boolean biomeFound = false;
		for (BiomeDictionary.Type type : biomeRules) {
			if (types.contains(type)) {
				biomeFound = true;
				break;
			}
		}
		if (!biomeFound) {
			return false;
		}

		List<Holder<PlacedFeature>> base = event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION);
		base.add(getGenerationBundle().treePlacedFeature);
		return true;
	}

	public abstract boolean shouldAddToBiome(BiomeLoadingEvent event, Set<BiomeDictionary.Type> biomeTypes);

	public abstract TreeGenerationBundle getGenerationBundle();

	public static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block trunk, Block leaves, int p_195149_, int p_195150_, int p_195151_, int p_195152_) {
		return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(trunk), new StraightTrunkPlacer(p_195149_, p_195150_, p_195151_),
				BlockStateProvider.simple(leaves), new BlobFoliagePlacer(ConstantInt.of(p_195152_), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
	}

	public class TreeGenerationBundle {
		public Holder<ConfiguredFeature<TreeConfiguration, ?>> treeFeature;
		public Holder<PlacedFeature> treePlacedFeatureChecked;
		public Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> treeSpawnFeature;
		public Holder<PlacedFeature> treePlacedFeature;

		public TreeGenerationBundle(Holder<ConfiguredFeature<TreeConfiguration, ?>> treeFeature, Holder<PlacedFeature> treePlacedFeatureChecked,
				Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> treeSpawnFeature, Holder<PlacedFeature> treePlacedFeature) {
			super();
			this.treeFeature = treeFeature;
			this.treePlacedFeatureChecked = treePlacedFeatureChecked;
			this.treeSpawnFeature = treeSpawnFeature;
			this.treePlacedFeature = treePlacedFeature;
		}
	}
}
