package theking530.staticpower.init;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;

public class ModTags {
	public static final TagKey<Item> INGOT = createItemWrapper(new ResourceLocation("forge", "ingots"));
	public static final TagKey<Item> SAPLING = createItemWrapper(new ResourceLocation("minecraft", "saplings"));

	public static final TagKey<Item> COVER_SAW = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "saw"));
	public static final TagKey<Item> FARMING_AXE = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "farming_axe"));
	public static final TagKey<Item> FARMING_HOE = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "farming_hoe"));
	public static final TagKey<Item> SOLDERING_IRON = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "soldering_iron"));
	public static final TagKey<Item> RESEARCH = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "research"));
	public static final TagKey<Item> FARMING_SEEDS = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "farming_seeds"));

	public static final TagKey<Item> LIGHTBULB = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "lightbulbs"));

	public static final TagKey<Item> DIGGER_BACKPACK = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "backpacks/digger"));
	public static final TagKey<Item> LUMBERJACK_BACKPACK = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "backpacks/lumberjack"));
	public static final TagKey<Item> HUNTER_BACKPACK = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "backpacks/hunter"));
	public static final TagKey<Item> BUILDER_BACKPACK = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "backpacks/builder"));
	public static final TagKey<Item> MINER_BACKPACK = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "backpacks/miner"));
	public static final TagKey<Item> FARMER_BACKPACK = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "backpacks/farmer"));
	public static final TagKey<Item> ENGINEER_BACKPACK = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "backpacks/engineer"));
	public static final TagKey<Item> TOOL_BACKPACK = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "backpacks/tool"));

	public static final TagKey<Block> LOG = createBlockWrapper(new ResourceLocation("minecraft", "logs"));
	public static final TagKey<Block> LEAVES = createBlockWrapper(new ResourceLocation("minecraft", "leaves"));

	public static final TagKey<Block> TILLABLE = createBlockWrapper(new ResourceLocation(StaticPower.MOD_ID, "tillable"));
	public static final TagKey<Block> REFINERY_BLOCK = createBlockWrapper(new ResourceLocation(StaticPower.MOD_ID, "valid_refinery_block"));

	public static final TagKey<Fluid> OIL = createFluidWrapper(new ResourceLocation("minecraft", "oil_crude"));
	public static final TagKey<Fluid> WATER = createFluidWrapper(new ResourceLocation("minecraft", "water"));

	public static boolean tagContainsItem(TagKey<Item> tag, Item item) {
		return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
	}

	public static boolean tagContainsItemStack(TagKey<Item> tag, ItemStack itemStack) {
		return tagContainsItem(tag, itemStack.getItem());
	}

	public static boolean tagContainsBlock(TagKey<Block> tag, Block block) {
		return ForgeRegistries.BLOCKS.tags().getTag(tag).contains(block);
	}

	public static boolean tagContainsFluid(TagKey<Fluid> tag, Fluid fluid) {
		return ForgeRegistries.FLUIDS.tags().getTag(tag).contains(fluid);
	}

	public static TagKey<Item> createItemWrapper(ResourceLocation name) {
		return TagKey.create(Registry.ITEM_REGISTRY, name);
	}

	public static TagKey<Fluid> createFluidWrapper(ResourceLocation name) {
		return TagKey.create(Registry.FLUID_REGISTRY, name);
	}

	public static TagKey<Block> createBlockWrapper(ResourceLocation name) {
		return TagKey.create(Registry.BLOCK_REGISTRY, name);
	}

	public static List<ResourceLocation> getTags(Item item) {
		Holder<Item> holder = ForgeRegistries.ITEMS.getHolder(item).orElse(null);
		if (holder != null) {
			holder.tags().toList();
		}
		return Collections.emptyList();
	}

	public static List<TagKey<Item>> getTags(ItemStack itemStack) {
		Holder<Item> holder = ForgeRegistries.ITEMS.getHolder(itemStack.getItem()).orElse(null);
		if (holder != null) {
			return holder.tags().toList();
		}
		return Collections.emptyList();
	}
}
