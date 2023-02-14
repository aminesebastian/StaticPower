package theking530.staticpower.init.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;

public class ModBlockTags {

	public static final TagKey<Block> CABLES = create("cables");
	public static final TagKey<Block> PUMPS = create("pumps");
	public static final TagKey<Block> MACHINES = create("machines");
	public static final TagKey<Block> BATTERY_BLOCKS = create("battery_blocks");
	public static final TagKey<Block> HEATSINKS = create("heatsinks");
	public static final TagKey<Block> MACHINE_BLOCKS = create("machine_blocks");
	public static final TagKey<Block> SOLAR_PANELS = create("solar_panels");
	public static final TagKey<Block> TANKS = create("tanks");
	public static final TagKey<Block> TRANSFORMERS = create("transformers");
	public static final TagKey<Block> POWER_MANAGEMENT = create("power_management");
	public static final TagKey<Block> CIRCUIT_BREAKERS = create("circuit_breakers");
	public static final TagKey<Block> RESISTORS = create("resistors");

	public static final TagKey<Block> FARMLAND = create("farmland");
	public static final TagKey<Block> PUMP_TUBE = create("pump_tube");
	public static final TagKey<Block> TILLABLE = create("tillable");
	public static final TagKey<Block> REFINERY_BLOCK = create("valid_refinery_block");
	public static final TagKey<Block> CAULDRONS = create("cauldrons");
	public static final TagKey<Block> CHESTS = create("chests");
	public static final TagKey<Block> CONVEYORS = create("conveyors");

	public static boolean matches(TagKey<Block> tag, Block block) {
		return ForgeRegistries.BLOCKS.tags().getTag(tag).contains(block);
	}

	public static TagKey<Block> create(String name) {
		return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation(StaticPower.MOD_ID, name));
	}

	public static TagKey<Block> createForgeTag(String name) {
		return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("forge", name));
	}
}
