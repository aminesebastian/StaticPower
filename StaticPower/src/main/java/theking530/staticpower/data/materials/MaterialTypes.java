package theking530.staticpower.data.materials;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import theking530.staticcore.fluid.StaticPowerFluidBundle;

public class MaterialTypes {
	public static final MaterialType<Block> OVERWORLD_ORE = new MaterialType<>("ore_overworld", "ore_%1$s", "ores/%1$s", Block.class);
	public static final MaterialType<Block> NETHER_ORE = new MaterialType<>("ore_nether", "ore_nether_%1$s", "ores/%1$s", Block.class);
	public static final MaterialType<Block> DEEPSLATE_ORE = new MaterialType<>("ore_deepslate", "ore_deepslate_%1$s", "ores/%1$s", Block.class);
	public static final MaterialType<Block> RAW_STOARGE_BLOCK = new MaterialType<>("raw_storage_block", "block_raw_%1$s", "storage_blocks/%1$s", Block.class);
	public static final MaterialType<Block> STORAGE_BLOCK = new MaterialType<>("storage_block", "block_%1$s", "storage_blocks/raw_%1$s", Block.class);
	public static final MaterialType<Block> CUT_STORAGE_BLOCK = new MaterialType<>("cut_storage_block", "block_cut_%1$s", "storage_blocks/cut_%1$s", Block.class);

	public static final MaterialType<Item> RAW_MATERIAL = new MaterialType<>("raw_material", "raw_%1$s", "raw_materials/%1$s", Item.class);
	public static final MaterialType<Item> INGOT = new MaterialType<>("ingot", "ingot_%1$s", "ingots/%1$s", Item.class);
	public static final MaterialType<Item> HEATED_INGOT = new MaterialType<>("ingot_heated", "ingot_%1$s_heated", Item.class);
	public static final MaterialType<Item> NUGGET = new MaterialType<>("nugget", "nugget_%1$s", "nuggets/%1$s", Item.class);
	public static final MaterialType<Item> DUST = new MaterialType<>("dust", "dust_%1$s", "dusts/%1$s", Item.class);
	public static final MaterialType<Item> GEAR = new MaterialType<>("gear", "gear_%1$s", "gears/%1$s", Item.class);
	public static final MaterialType<Item> GEAR_BOX = new MaterialType<>("gear_box", "gear_box_%1$s", Item.class);
	public static final MaterialType<Item> PLATE = new MaterialType<>("plate", "plate_%1$s", "plates/%1$s", Item.class);
	public static final MaterialType<Item> ROD = new MaterialType<>("rod", "rod_%1$s", "rods/%1$s", Item.class);
	public static final MaterialType<Item> CHUNKS = new MaterialType<>("chunks", "chunks_%1$s", "chunks/%1$s", Item.class);
	public static final MaterialType<Item> WIRE = new MaterialType<>("wire", "wire_%1$s", "wires/%1$s", Item.class);
	public static final MaterialType<Item> INSULATED_WIRE = new MaterialType<>("insulated_wire", "wire_insulated_%1$s", "wires/insulated/%1$s", Item.class);
	public static final MaterialType<Item> WIRE_COIL = new MaterialType<>("wire_coil", "wire_coil_%1$s", "wire_coil/%1$s", Item.class);
	public static final MaterialType<Item> INSULATED_WIRE_COIL = new MaterialType<>("insulated_wire_coil", "wire_coil_insulated_%1$s", "wire_coil/insulated/%1$s", Item.class);

	public static final MaterialType<StaticPowerFluidBundle> MOLTEN_FLUID = new MaterialType<>("fluid_molten", "molten_%1$s", Fluid.class);
}
