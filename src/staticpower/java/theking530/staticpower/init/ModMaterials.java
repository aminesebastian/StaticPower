package theking530.staticpower.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.common.Tags;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blocks.StaticPowerOre;
import theking530.staticpower.data.materials.Material.BlockMaterial;
import theking530.staticpower.data.materials.Material.FluidMaterial;
import theking530.staticpower.data.materials.Material.ItemMaterial;
import theking530.staticpower.data.materials.Material.MaterialDomain;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialBundle.MaterialBundleType;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.items.GearBox;
import theking530.staticpower.items.HeatedIngot;

public class ModMaterials {
	public static final Map<String, MaterialBundle> MATERIALS = new HashMap<>();

	// @formatter:off
	public static final MaterialBundle RUBY = registerMaterial("ruby", MaterialBundleType.GEM)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.DIAMOND_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.DIAMOND_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE, "gem_%1$s", "gems/%1$s"))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE));
	
	public static final MaterialBundle SAPPHIRE = registerMaterial("sapphire", MaterialBundleType.GEM)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.DIAMOND_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.DIAMOND_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE, "gem_%1$s", "gems/%1$s"))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE));
	
	public static final MaterialBundle TIN = registerMaterial("tin", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.COPPER_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.COPPER_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.RAW_COPPER_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.CUT_COPPER)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.TIN.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.GEAR_BOX, () -> new GearBox()))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.ROD, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 4000, 32, 2800, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle ZINC = registerMaterial("zinc", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.COPPER_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.COPPER_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.RAW_COPPER_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.CUT_COPPER)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.ZINC.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 4000, 32, 420, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle ALUMINUM = registerMaterial("aluminum", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.COPPER_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.COPPER_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.RAW_COPPER_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.CUT_COPPER)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.ALUMINUM.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 2000, 32, 660, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle MAGNESIUM = registerMaterial("magnesium", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.IRON_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.IRON_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.RAW_IRON_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.IRON_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.MAGNESIUM.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 8000, 32, 1202, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle PLATINUM = registerMaterial("platinum", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.NETHER_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.RAW_GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.GOLD_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.PLATINUM.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 4000, 32, 1770, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle LEAD = registerMaterial("lead", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.RAW_GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.GOLD_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.LEAD.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 2000, 32, 328, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle SILVER = registerMaterial("silver", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.NETHER_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.RAW_GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.GOLD_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.SILVER.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 3000, 32, 961, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle TUNGSTEN = registerMaterial("tungsten", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 2, 4)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 2, 4)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.NETHER_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 2, 5)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.ANCIENT_DEBRIS)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.ANCIENT_DEBRIS)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.ANCIENT_DEBRIS)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 3000, 32, 961, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle URANIUM = registerMaterial("uranium", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.OVERWORLD_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.IRON_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.DEEPSLATE_ORE, MaterialDomain.FORGE, () -> new StaticPowerOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.IRON_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.RAW_IRON_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.URANIUM.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE));
	
	public static final MaterialBundle BRONZE = registerMaterial("bronze", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.IRON_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.IRON_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.BRONZE.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 3000, 32, 950, new SDColor(1.0f, 0.25f, 0.1f)));

	public static final MaterialBundle BRASS = registerMaterial("brass", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.IRON_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.FORGE, Properties.copy(Blocks.IRON_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.BRASS.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.ROD, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.GEAR_BOX, () -> new GearBox()))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE_COIL, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE_COIL, MaterialDomain.STATICPOWER))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 3000, 32, 950, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle STATIC_METAL = registerMaterial("static_metal", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.COPPER_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.COPPER_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.RAW_COPPER_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.ROD, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.GEAR_BOX, () -> new GearBox()))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE_COIL, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE_COIL, MaterialDomain.STATICPOWER));
	
	public static final MaterialBundle ENERGIZED_METAL = registerMaterial("energized_metal", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.IRON_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.IRON_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.RAW_IRON_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.ROD, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.GEAR_BOX, () -> new GearBox()))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE_COIL, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE_COIL, MaterialDomain.STATICPOWER));
	
	public static final MaterialBundle LUMUM_METAL = registerMaterial("lumum_metal", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.RAW_GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.RAW_GOLD_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.ROD, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.GEAR_BOX, () -> new GearBox()))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE_COIL, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE_COIL, MaterialDomain.STATICPOWER));
	
	public static final MaterialBundle REDSTONE_ALLOY = registerMaterial("redstone_alloy", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.RAW_GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.RAW_GOLD_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.STATICPOWER));
	
	public static final MaterialBundle INERT_INFUSION = registerMaterial("inert_infusion_alloy", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.generate(MaterialTypes.STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.RAW_GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.CUT_STORAGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.GOLD_BLOCK)))
			.blockMaterial(BlockMaterial.generate(MaterialTypes.RAW_STOARGE_BLOCK, MaterialDomain.STATICPOWER, Properties.copy(Blocks.RAW_GOLD_BLOCK)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.RAW_MATERIAL, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INGOT, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.INERT_INFUSION.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.ROD, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.STATICPOWER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.GEAR_BOX, () -> new GearBox()));
	
	public static final MaterialBundle COPPER = registerMaterial("copper", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.existing(MaterialTypes.OVERWORLD_ORE, () -> Blocks.COPPER_ORE, Tags.Items.ORES_COPPER, Tags.Blocks.ORES_COPPER))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_COPPER_ORE, Tags.Items.ORES_COPPER, Tags.Blocks.ORES_COPPER))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.STORAGE_BLOCK, () -> Blocks.COPPER_BLOCK,  Tags.Items.STORAGE_BLOCKS_COPPER, Tags.Blocks.STORAGE_BLOCKS_COPPER))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.RAW_STOARGE_BLOCK, () -> Blocks.RAW_COPPER_BLOCK,  Tags.Items.STORAGE_BLOCKS_RAW_COPPER, Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.RAW_MATERIAL, () -> Items.RAW_COPPER, Tags.Items.RAW_MATERIALS_COPPER))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.NUGGET, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.INGOT, () -> Items.COPPER_INGOT, Tags.Items.INGOTS_COPPER))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(ModMaterials.COPPER.get(MaterialTypes.INGOT).getSupplier())))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE_COIL, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.INSULATED_WIRE_COIL, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 4000, 32, 1084, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle IRON = registerMaterial("iron", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.existing(MaterialTypes.OVERWORLD_ORE, () -> Blocks.IRON_ORE, Tags.Items.ORES_IRON, Tags.Blocks.ORES_IRON))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_IRON_ORE, Tags.Items.ORES_IRON, Tags.Blocks.ORES_IRON))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.STORAGE_BLOCK, () -> Blocks.IRON_BLOCK,  Tags.Items.STORAGE_BLOCKS_IRON, Tags.Blocks.STORAGE_BLOCKS_IRON))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.RAW_STOARGE_BLOCK, () -> Blocks.RAW_IRON_BLOCK,  Tags.Items.STORAGE_BLOCKS_RAW_IRON, Tags.Blocks.STORAGE_BLOCKS_RAW_IRON))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.RAW_MATERIAL, () -> Items.RAW_IRON, Tags.Items.RAW_MATERIALS_IRON))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.NUGGET, () -> Items.IRON_NUGGET, Tags.Items.NUGGETS_IRON))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.INGOT, () -> Items.IRON_INGOT, Tags.Items.INGOTS_IRON))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(()->Items.IRON_INGOT)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 4000, 32, 2800, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle GOLD = registerMaterial("gold", MaterialBundleType.METAL)
			.blockMaterial(BlockMaterial.existing(MaterialTypes.OVERWORLD_ORE, () -> Blocks.GOLD_ORE, Tags.Items.ORES_GOLD, Tags.Blocks.ORES_GOLD))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_GOLD_ORE, Tags.Items.ORES_GOLD, Tags.Blocks.ORES_GOLD))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.NETHER_ORE, () -> Blocks.NETHER_GOLD_ORE, Tags.Items.ORES_COAL, Tags.Blocks.ORES_COAL))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.STORAGE_BLOCK, () -> Blocks.GOLD_BLOCK,  Tags.Items.STORAGE_BLOCKS_GOLD, Tags.Blocks.STORAGE_BLOCKS_GOLD))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.RAW_STOARGE_BLOCK, () -> Blocks.RAW_GOLD_BLOCK,  Tags.Items.STORAGE_BLOCKS_RAW_GOLD, Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.RAW_MATERIAL, () -> Items.RAW_GOLD, Tags.Items.RAW_MATERIALS_GOLD))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.NUGGET, () -> Items.GOLD_NUGGET, Tags.Items.NUGGETS_GOLD))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.INGOT, () -> Items.GOLD_INGOT, Tags.Items.INGOTS_GOLD))
			.itemMaterial(ItemMaterial.generateNoTag(MaterialTypes.HEATED_INGOT, ()-> new HeatedIngot(()->Items.GOLD_INGOT)))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.PLATE, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.GEAR, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.WIRE, MaterialDomain.FORGE))
			.fluidMaterial(FluidMaterial.generate(MaterialTypes.MOLTEN_FLUID, 6000, 32, 1064, new SDColor(1.0f, 0.25f, 0.1f)));
	
	public static final MaterialBundle DIAMOND = registerMaterial("diamond", MaterialBundleType.GEM)
			.blockMaterial(BlockMaterial.existing(MaterialTypes.OVERWORLD_ORE, () -> Blocks.DIAMOND_ORE, Tags.Items.ORES_DIAMOND, Tags.Blocks.ORES_DIAMOND))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_DIAMOND_ORE, Tags.Items.ORES_DIAMOND, Tags.Blocks.ORES_DIAMOND))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.RAW_STOARGE_BLOCK, () -> Blocks.DIAMOND_BLOCK, Tags.Items.STORAGE_BLOCKS_DIAMOND, Tags.Blocks.STORAGE_BLOCKS_DIAMOND))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.CUT_STORAGE_BLOCK, Properties.copy(Blocks.DIAMOND_BLOCK)))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.RAW_MATERIAL, () -> Items.DIAMOND, Tags.Items.GEMS_DIAMOND))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE));
	
	public static final MaterialBundle EMERALD = registerMaterial("emerald", MaterialBundleType.GEM)
			.blockMaterial(BlockMaterial.existing(MaterialTypes.OVERWORLD_ORE, () -> Blocks.EMERALD_ORE, Tags.Items.ORES_EMERALD, Tags.Blocks.ORES_EMERALD))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_EMERALD_ORE, Tags.Items.ORES_EMERALD, Tags.Blocks.ORES_EMERALD))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.RAW_STOARGE_BLOCK, () -> Blocks.EMERALD_BLOCK, Tags.Items.STORAGE_BLOCKS_EMERALD, Tags.Blocks.STORAGE_BLOCKS_EMERALD))
			.blockMaterial(BlockMaterial.generateNoTag(MaterialTypes.CUT_STORAGE_BLOCK, Properties.copy(Blocks.EMERALD_BLOCK)))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.RAW_MATERIAL, () -> Items.EMERALD, Tags.Items.GEMS_EMERALD))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE));
	
	public static final MaterialBundle COAL = registerMaterial("coal", MaterialBundleType.GEM)
			.blockMaterial(BlockMaterial.existing(MaterialTypes.OVERWORLD_ORE, () -> Blocks.COAL_ORE, Tags.Items.ORES_COAL, Tags.Blocks.ORES_COAL))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_COAL_ORE, Tags.Items.ORES_COAL, Tags.Blocks.ORES_COAL))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.RAW_STOARGE_BLOCK, () -> Blocks.COAL_BLOCK, Tags.Items.STORAGE_BLOCKS_COAL, Tags.Blocks.STORAGE_BLOCKS_COAL))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.RAW_MATERIAL,  MaterialDomain.FORGE, () -> Items.COAL))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE));
	
	public static final MaterialBundle CHARCOAL = registerMaterial("charcoal", MaterialBundleType.GEM)
			.itemMaterial(ItemMaterial.existing(MaterialTypes.RAW_MATERIAL, MaterialDomain.FORGE, () -> Items.CHARCOAL))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.DUST, MaterialDomain.FORGE));
	
	public static final MaterialBundle QUARTZ = registerMaterial("quartz", MaterialBundleType.GEM)
			.blockMaterial(BlockMaterial.existing(MaterialTypes.NETHER_ORE, () -> Blocks.NETHER_QUARTZ_ORE, Tags.Items.ORES_QUARTZ, Tags.Blocks.ORES_QUARTZ))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.RAW_STOARGE_BLOCK, () -> Blocks.QUARTZ_BLOCK, Tags.Items.STORAGE_BLOCKS_QUARTZ, Tags.Blocks.STORAGE_BLOCKS_QUARTZ))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.RAW_MATERIAL, () -> Items.QUARTZ, Tags.Items.GEMS_QUARTZ))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE));

	public static final MaterialBundle LAPIS = registerMaterial("lapis", MaterialBundleType.GEM)
			.blockMaterial(BlockMaterial.existing(MaterialTypes.OVERWORLD_ORE, () -> Blocks.LAPIS_ORE, Tags.Items.ORES_LAPIS, Tags.Blocks.ORES_LAPIS))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_LAPIS_ORE, Tags.Items.ORES_LAPIS, Tags.Blocks.ORES_LAPIS))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.RAW_STOARGE_BLOCK, () -> Blocks.LAPIS_BLOCK, Tags.Items.STORAGE_BLOCKS_LAPIS, Tags.Blocks.STORAGE_BLOCKS_LAPIS))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.RAW_MATERIAL, () -> Items.QUARTZ, Tags.Items.GEMS_LAPIS))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE));
	
	public static final MaterialBundle REDSTONE = registerMaterial("redstone", MaterialBundleType.DUST)
			.blockMaterial(BlockMaterial.existing(MaterialTypes.OVERWORLD_ORE, () -> Blocks.REDSTONE_ORE, Tags.Items.ORES_REDSTONE, Tags.Blocks.ORES_REDSTONE))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.DEEPSLATE_ORE, () -> Blocks.DEEPSLATE_REDSTONE_ORE, Tags.Items.ORES_REDSTONE, Tags.Blocks.ORES_REDSTONE))
			.blockMaterial(BlockMaterial.existing(MaterialTypes.RAW_STOARGE_BLOCK, () -> Blocks.REDSTONE_BLOCK, Tags.Items.STORAGE_BLOCKS_REDSTONE, Tags.Blocks.STORAGE_BLOCKS_REDSTONE))
			.itemMaterial(ItemMaterial.existing(MaterialTypes.DUST, () -> Items.REDSTONE, Tags.Items.DUSTS_REDSTONE))
			.itemMaterial(ItemMaterial.generate(MaterialTypes.CHUNKS, MaterialDomain.FORGE));
	
	// @formatter:on

	public static void init() {
		for (MaterialBundle bundle : MATERIALS.values()) {
			bundle.generateTag();
		}
	}

	private static MaterialBundle registerMaterial(String name, MaterialBundleType type) {
		MaterialBundle bundle = new MaterialBundle(name, type);
		validateAndCacheMaterialBundle(bundle);
		return bundle;
	}

	private static void validateAndCacheMaterialBundle(MaterialBundle bundle) {
		bundle.validate();
		if (MATERIALS.containsKey(bundle.getName())) {
			throw new RuntimeException(String.format("Material names must be unique. Attempted to add a duplicate material with name: %1$s.", bundle.getName()));
		}
		MATERIALS.put(bundle.getName(), bundle);
	}
}
