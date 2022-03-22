package theking530.staticpower.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import theking530.staticpower.StaticPower;

public class ModTags {
	public static final Tag<Item> LOG = createItemWrapper(new ResourceLocation("minecraft", "logs"));
	public static final Tag<Item> LEAVES = createItemWrapper(new ResourceLocation("minecraft", "leaves"));
	public static final Tag<Item> SAPLING = createItemWrapper(new ResourceLocation("minecraft", "saplings"));
	public static final Tag<Item> INGOT = createItemWrapper(new ResourceLocation("forge", "ingots"));
	public static final Tag<Item> COVER_SAW = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "saw"));
	public static final Tag<Item> FARMING_AXE = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "farming_axe"));
	public static final Tag<Item> FARMING_HOE = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "farming_hoe"));
	public static final Tag<Item> SOLDERING_IRON = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "soldering_iron"));
	public static final Tag<Item> RESEARCH = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "research"));
	public static final Tag<Item> TILLABLE = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "tillable"));

	public static final Tag<Fluid> OIL = createFluidWrapper(new ResourceLocation("minecraft", "oil_crude"));

	public static Tag<Item> createItemWrapper(ResourceLocation name) {
		return ItemTags.getAllTags().getTagOrEmpty(name);
	}

	public static Tag<Fluid> createFluidWrapper(ResourceLocation name) {
		return FluidTags.getAllTags().getTagOrEmpty(name);
	}
}
