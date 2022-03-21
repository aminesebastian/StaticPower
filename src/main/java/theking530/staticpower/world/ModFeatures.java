package theking530.staticpower.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.world.features.StaticPowerLakeFeature;
import theking530.staticpower.world.features.StaticPowerLakeFeatureConfiguration;

public class ModFeatures {
	public static final Feature<StaticPowerLakeFeatureConfiguration> STATIC_LAKE = new StaticPowerLakeFeature(new ResourceLocation(StaticPower.MOD_ID, "static_lake"),
			StaticPowerLakeFeatureConfiguration.CODEC);

	public static void registerFeatures(final RegistryEvent.Register<Feature<?>> event) {
		event.getRegistry().register(STATIC_LAKE);
	}

}
