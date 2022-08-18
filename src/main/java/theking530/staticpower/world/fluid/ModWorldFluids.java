package theking530.staticpower.world.fluid;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import theking530.staticpower.fluid.StaticPowerFluidBundle;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.world.ModFeatures;
import theking530.staticpower.world.features.StaticPowerLakeFeatureConfiguration;

public class ModWorldFluids {
	public static final Holder<PlacedFeature> LAKE_OIL_SURFACE = PlacementUtils.register("lake_oil_surface",
			new WorldFluidConfigBuilder("lake_oil", ModFluids.CrudeOil, BlockStateProvider.simple(Blocks.MOSSY_COBBLESTONE)).build());

	public static void addFluidGenFeatures(BiomeLoadingEvent event) {
		event.getGeneration().addFeature(GenerationStep.Decoration.LAKES, LAKE_OIL_SURFACE);
	}

	private static class WorldFluidConfigBuilder {
		private final String name;
		private final StaticPowerFluidBundle fluid;
		private final BlockStateProvider barrier;

		public WorldFluidConfigBuilder(String name, StaticPowerFluidBundle fluid, BlockStateProvider barrier) {
			this.name = name;
			this.fluid = fluid;
			this.barrier = barrier;
		}

		public Holder<ConfiguredFeature<StaticPowerLakeFeatureConfiguration, ?>> build() {
			return FeatureUtils.register(name, ModFeatures.STATIC_LAKE, new StaticPowerLakeFeatureConfiguration(BlockStateProvider.simple(fluid.block.get().defaultBlockState()),
					barrier, UniformInt.of(2, 10), UniformInt.of(30, 100)));
		}
	}
}
