package theking530.staticpower.world.fluid;

import com.google.common.collect.ImmutableList;

import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.fluid.StaticPowerFluidBundle;
import theking530.staticpower.init.ModFeatures;
import theking530.staticpower.world.features.StaticPowerLakeFeatureConfiguration;

public abstract class ModLakePlacement<T extends ModLakePlacement<T>> {
	private final String name;
	public RegistryObject<ConfiguredFeature<?, ?>> configuredFeature;
	public RegistryObject<PlacedFeature> placedFeature;

	public ModLakePlacement(String name) {
		this.name = name;
	}

	public abstract StaticPowerFluidBundle getFluidBundle();

	public abstract BlockStateProvider getBarrier();

	public abstract IntProvider getRadius();

	public abstract IntProvider getDepth();

	public abstract int getRarity();

	@SuppressWarnings("unchecked")
	public T register() {
		configuredFeature = ModFeatures.CONFIGURED_FEATURES.register(name, () -> getConfiguredFeature());
		placedFeature = ModFeatures.PLACED_FEATURES.register(name + "_placed", () -> getCheckedFeature());
		return (T) this;
	}

	protected ConfiguredFeature<?, ?> getConfiguredFeature() {
		return new ConfiguredFeature<>(ModFeatures.STATIC_LAKE.get(),
				new StaticPowerLakeFeatureConfiguration(BlockStateProvider.simple(getFluidBundle().getBlock().get().defaultBlockState()), getBarrier(), getRadius(), getDepth()));
	}

	protected PlacedFeature getCheckedFeature() {
		return new PlacedFeature(configuredFeature.getHolder().get(), treePlacementBase(getRarity()).build());
	}

	private static ImmutableList.Builder<PlacementModifier> treePlacementBase(int rarity) {
		return ImmutableList.<PlacementModifier>builder().add(RarityFilter.onAverageOnceEvery(rarity)).add(HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE))
				.add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE).add(BiomeFilter.biome());
	}
}
