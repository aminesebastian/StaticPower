package theking530.staticpower.world.trees;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.init.ModFeatures;

public abstract class ModTreeRegistration<T extends ModTreeRegistration<T>> {
	public final String name;
	public RegistryObject<ConfiguredFeature<?, ?>> configuredFeature;
	public RegistryObject<PlacedFeature> saplingPlacedFeature;
	public RegistryObject<ConfiguredFeature<?, ?>> saplingSpawnConfiguredFeature;
	public RegistryObject<PlacedFeature> placedFeature;

	public ModTreeRegistration(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public T register() {
		configuredFeature = ModFeatures.CONFIGURED_FEATURES.register(name, () -> getConfiguredFeature());
		saplingPlacedFeature = ModFeatures.PLACED_FEATURES.register(name + "_checked", () -> getCheckedFeature());

		saplingSpawnConfiguredFeature = ModFeatures.CONFIGURED_FEATURES.register(name + "_spawn", () -> getConfiguredSpawnFeature());
		placedFeature = ModFeatures.PLACED_FEATURES.register(name + "_placed", () -> getPlacedFeature());
		return (T) this;
	}

	protected abstract ConfiguredFeature<?, ?> getConfiguredFeature();

	protected abstract PlacedFeature getCheckedFeature();

	protected abstract ConfiguredFeature<?, ?> getConfiguredSpawnFeature();

	protected abstract PlacedFeature getPlacedFeature();

	public AbstractTreeGrower getTreeGrower() {
		return new AbstractTreeGrower() {
			@Override
			protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222910_, boolean p_222911_) {
				return saplingSpawnConfiguredFeature.getHolder().get();
			}
		};
	}
}
