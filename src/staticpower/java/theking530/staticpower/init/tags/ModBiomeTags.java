package theking530.staticpower.init.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;

public class ModBiomeTags {

	public static final TagKey<Biome> OIL_DEPOSIT_SUITABLE = create("oil_deposit_suitable");
	public static final TagKey<Biome> RUBBER_TREE_SUITABLE = create("rubber_tree_suitable");

	public static boolean matches(TagKey<Biome> tag, Biome biome) {
		return ForgeRegistries.BIOMES.tags().getTag(tag).contains(biome);
	}

	private static TagKey<Biome> create(String name) {
		return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(StaticPower.MOD_ID, name));
	}
}
