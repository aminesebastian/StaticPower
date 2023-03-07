package theking530.staticpower.init.tags;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;

public class ModItemTags {
	public static final TagKey<Item> MATERIALS = create("materials");

	public static final TagKey<Item> GLASS_BOTTLES = create("glass_bottles");

	public static final TagKey<Item> CABLES = create("cables");
	public static final TagKey<Item> PUMPS = create("pumps");
	public static final TagKey<Item> MACHINES = create("machines");
	public static final TagKey<Item> HEATSINKS = create("heatsinks");
	public static final TagKey<Item> BATTERY_BLOCKS = create("battery_blocks");
	public static final TagKey<Item> MACHINE_BLOCKS = create("machine_blocks");
	public static final TagKey<Item> SOLAR_PANELS = create("solar_panels");
	public static final TagKey<Item> TANKS = create("tanks");
	public static final TagKey<Item> TRANSFORMERS = create("transformers");
	public static final TagKey<Item> POWER_MANAGEMENT = create("power_management");
	public static final TagKey<Item> CIRCUIT_BREAKERS = create("circuit_breakers");
	public static final TagKey<Item> RESISTORS = create("resistors");
	public static final TagKey<Item> PUMP_TUBE = create("pump_tube");
	public static final TagKey<Item> TILLABLE = create("tillable");
	public static final TagKey<Item> REFINERY_BLOCK = create("refinery_block");
	public static final TagKey<Item> FARMLAND = create("farmland");
	public static final TagKey<Item> CAULDRONS = create("cauldrons");
	public static final TagKey<Item> CHESTS = create("chests");
	public static final TagKey<Item> CONVEYORS = create("conveyors");

	public static final TagKey<Item> BACKPACKS = create("backpacks");
	public static final TagKey<Item> COVER_SAW = create("tools/cover_saw");
	public static final TagKey<Item> SOLDERING_IRON = create("tools/soldering_iron");
	public static final TagKey<Item> MAGNET = create("tools/magnet");

	public static final TagKey<Item> MINING_DRILL = create("tools/mining_drill");
	public static final TagKey<Item> MINING_DRILL_BIT = create("tools/mining_drill_bit");

	public static final TagKey<Item> BLADE = create("tools/blade");

	public static final TagKey<Item> CHAINSAW = create("tools/chainsaw");
	public static final TagKey<Item> CHAINSAW_BLADE = create("tools/chainsaw_blade");

	public static final TagKey<Item> HAMMER = create("tools/hammer");
	public static final TagKey<Item> WIRE_CUTTER = create("tools/wire_cutter");
	public static final TagKey<Item> WRENCH = create("tools/wrench");
	public static final TagKey<Item> PORTABLE_BATTERY = create("tools/batteries");
	public static final TagKey<Item> PORTABLE_BATTERY_PACK = create("tools/battery_packs");
	public static final TagKey<Item> FLUID_CAPSULES = create("tools/fluid_capsules");

	public static final TagKey<Item> TOOLS = create("tools");

	public static final TagKey<Item> FARMING_AXE = create("farming_axe");
	public static final TagKey<Item> FARMING_HOE = create("farming_hoe");
	public static final TagKey<Item> FARMING_SEEDS = create("farming_seeds");

	public static final TagKey<Item> RESEARCH = create("research");
	public static final TagKey<Item> LIGHTBULB = create("lightbulbs");

	public static final TagKey<Item> DIGGER_BACKPACK = create("backpacks/digger");
	public static final TagKey<Item> LUMBERJACK_BACKPACK = create("backpacks/lumberjack");
	public static final TagKey<Item> HUNTER_BACKPACK = create("backpacks/hunter");
	public static final TagKey<Item> BUILDER_BACKPACK = create("backpacks/builder");
	public static final TagKey<Item> MINER_BACKPACK = create("backpacks/miner");
	public static final TagKey<Item> FARMER_BACKPACK = create("backpacks/farmer");
	public static final TagKey<Item> ENGINEER_BACKPACK = create("backpacks/engineer");
	public static final TagKey<Item> TOOL_BACKPACK = create("backpacks/tool");

	public static final TagKey<Item> HEATED_INGOTS = create("heated_ingots");
	public static final TagKey<Item> WIRE_COILS = create("wire_coils");
	public static final TagKey<Item> WIRES = create("wires");
	public static final TagKey<Item> PLATES = createForgeTag("plates");
	public static final TagKey<Item> GEARS = createForgeTag("gear");

	public static final TagKey<Item> RUBBER = createForgeTag("rubber");
	public static final TagKey<Item> RUBBER_SHEET = createForgeTag("rubber_sheet");
	public static final TagKey<Item> RUBBER_WOOD_LOGS = createForgeTag("rubber_wood_logs");

	public static final TagKey<Item> COAL_DUST = createForgeTag("dusts/coal");
	public static final TagKey<Item> CHARCOAL_DUST = createForgeTag("dusts/charcoal");
	
	public static final TagKey<Item> OBSIDIAN_DUST = create("dusts/obsidian");
	public static final TagKey<Item> WOOD_DUST = create("dusts/woood");

	public static boolean matches(TagKey<Item> tag, Item item) {
		return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
	}

	public static TagKey<Item> create(String name) {
		return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(StaticPower.MOD_ID, name));
	}

	public static TagKey<Item> createForgeTag(String name) {
		return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", name));
	}

	public static TagKey<Item> createTagOnDomain(String domain, String name) {
		return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(domain, name));
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
