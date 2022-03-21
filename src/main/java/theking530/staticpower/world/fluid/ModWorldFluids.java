package theking530.staticpower.world.fluid;

import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.fluid.StaticPowerFluidBundle;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.world.ModFeatures;
import theking530.staticpower.world.features.StaticPowerLakeFeatureConfiguration;

public class ModWorldFluids {
	public static final PlacedFeature LAKE_OIL_SURFACE = PlacementUtils.register("lake_oil_surface", new WorldFluidConfigBuilder(ModFluids.CrudeOil).build().placed(RarityFilter.onAverageOnceEvery(10),
			InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

	public static void addFluidGenFeatures(BiomeLoadingEvent event) {
		event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, LAKE_OIL_SURFACE);
	}

	private static class WorldFluidConfigBuilder {
		private final StaticPowerFluidBundle fluid;

		public WorldFluidConfigBuilder(StaticPowerFluidBundle fluid) {
			this.fluid = fluid;
		}

		public ConfiguredFeature<?, ?> build() {
			StaticPowerLakeFeatureConfiguration configuration = new StaticPowerLakeFeatureConfiguration(BlockStateProvider.simple(fluid.FluidBlock.defaultBlockState()), UniformInt.of(2, 10),
					UniformInt.of(15, 50));
			return ModFeatures.STATIC_LAKE.configured(configuration);
		}
	}
}
