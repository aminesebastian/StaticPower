package theking530.staticpower.init.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;

public class ModFluidTags {
	public static final TagKey<Fluid> CONCRETE = create("concrete");
	public static final TagKey<Fluid> DYE = create("dye");

	public static boolean matches(TagKey<Fluid> tag, Fluid biome) {
		return ForgeRegistries.FLUIDS.tags().getTag(tag).contains(biome);
	}

	public static TagKey<Fluid> create(String name) {
		return TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(StaticPower.MOD_ID, name));
	}
}
