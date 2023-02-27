package theking530.staticpower.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.common.Tags;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialBundle.MaterialDomain;
import theking530.staticpower.data.materials.MaterialBundle.MaterialType;

public class ModMaterials {
	public static final Map<String, MaterialBundle> MATERIALS = new HashMap<>();

	public static final MaterialBundle RUBY = createNewForgeBundle("ruby", MaterialType.GEM).generateOverworldOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5).generateCutStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK))
			.generateRawStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK)).generateRawMaterial("gem").generateDust().generateChunks();

	public static final MaterialBundle SAPPHIRE = createNewForgeBundle("sapphire", MaterialType.GEM).generateOverworldOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5).generateCutStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK))
			.generateRawStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK)).generateRawMaterial("gem").generateDust().generateChunks();

	public static final MaterialBundle TIN = createNewForgeBundle("tin", MaterialType.METAL).generateOverworldOre(Properties.copy(Blocks.COPPER_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2).generateRawStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK)).generateRawMaterial().generateIngot()
			.generateHeatedIngotMaterial().generateDust().generatePlate().generateGear(true).generateRod().generateChunks().generateNugget();

	public static final MaterialBundle ZINC = createNewForgeBundle("zinc", MaterialType.METAL).generateOverworldOre(Properties.copy(Blocks.IRON_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2).generateRawStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterial().generateIngot()
			.generateHeatedIngotMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle ALUMINUM = createNewForgeBundle("aluminum", MaterialType.METAL).generateOverworldOre(Properties.copy(Blocks.COPPER_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2).generateRawStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateRawMaterial().generateIngot()
			.generateHeatedIngotMaterial().generateDust().generatePlate().generateChunks().generateNugget().generateWire();

	public static final MaterialBundle MAGNESIUM = createNewForgeBundle("magnesium", MaterialType.METAL).generateOverworldOre(Properties.copy(Blocks.IRON_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2).generateRawStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterial().generateIngot()
			.generateHeatedIngotMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle PLATINUM = createNewForgeBundle("platinum", MaterialType.METAL).generateOverworldOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4).generateNetherackOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)
			.generateRawStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)).generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK))
			.generateStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterial().generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate()
			.generateChunks().generateNugget();

	public static final MaterialBundle LEAD = createNewForgeBundle("lead", MaterialType.METAL).generateOverworldOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4).generateRawStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterial().generateIngot()
			.generateHeatedIngotMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle SILVER = createNewForgeBundle("silver", MaterialType.METAL).generateOverworldOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4).generateNetherackOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)
			.generateRawStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)).generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK))
			.generateStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterial().generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate()
			.generateChunks().generateNugget();

	public static final MaterialBundle TUNGSTEN = createNewForgeBundle("tungsten", MaterialType.METAL).generateOverworldOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 1, 2).generateNetherackOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)
			.generateRawStorageBlock(Properties.copy(Blocks.ANCIENT_DEBRIS)).generateCutStorageBlock(Properties.copy(Blocks.ANCIENT_DEBRIS))
			.generateStorageBlock(Properties.copy(Blocks.ANCIENT_DEBRIS)).generateRawMaterial().generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate()
			.generateChunks().generateNugget();

	public static final MaterialBundle URANIUM = createNewForgeBundle("uranium", MaterialType.METAL).generateOverworldOre(Properties.copy(Blocks.IRON_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 2, 4).generateRawStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterial().generateIngot().generateDust().generateChunks().generateNugget();

	public static final MaterialBundle BRONZE = createNewForgeBundle("bronze", MaterialType.METAL).generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate().generateGear()
			.generateNugget();

	public static final MaterialBundle BRASS = createNewForgeBundle("brass", MaterialType.METAL).generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate().generateGear(true)
			.generateRod().generateNugget().generateWire().generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();

	public static final MaterialBundle STATIC_METAL = createNewStaticPowerMaterial("static_metal", MaterialType.METAL).generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateRawStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK)).generateRawMaterial().generateIngot()
			.generateHeatedIngotMaterial().generateDust().generatePlate().generateGear(true).generateRod().generateNugget().generateWire().generateWireCoil()
			.generateInsulatedWire().generateInsulatedWireCoil();
	public static final MaterialBundle ENERGIZED_METAL = createNewStaticPowerMaterial("energized_metal", MaterialType.METAL)
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateStorageBlock(Properties.copy(Blocks.IRON_BLOCK))
			.generateRawStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)).generateRawMaterial().generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate()
			.generateGear(true).generateRod().generateNugget().generateWire().generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();
	public static final MaterialBundle LUMUM_METAL = createNewStaticPowerMaterial("lumum_metal", MaterialType.METAL).generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)).generateRawMaterial().generateIngot()
			.generateHeatedIngotMaterial().generateDust().generatePlate().generateGear(true).generateRod().generateNugget().generateWire().generateWireCoil()
			.generateInsulatedWire().generateInsulatedWireCoil();

	public static final MaterialBundle REDSTONE_ALLOY = createNewStaticPowerMaterial("redstone_alloy", MaterialType.METAL)
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateStorageBlock(Properties.copy(Blocks.IRON_BLOCK))
			.generateRawStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)).generateRawMaterial().generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate()
			.generateNugget();
	public static final MaterialBundle INERT_INFUSION = createNewStaticPowerMaterial("inert_infusion_alloy", MaterialType.METAL)
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateStorageBlock(Properties.copy(Blocks.IRON_BLOCK))
			.generateRawStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)).generateRawMaterial().generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate()
			.generateGear(true).generateRod().generateNugget();

	public static final MaterialBundle IRON = createNewVanillaBundle("iron", MaterialType.METAL).generateHeatedIngotMaterial().generateDust().generateChunks().generatePlate()
			.generateGear().setOreTag(ItemTags.IRON_ORES, BlockTags.IRON_ORES).setStorageBlockItemTag(Tags.Items.STORAGE_BLOCKS_IRON, Tags.Blocks.STORAGE_BLOCKS_IRON)
			.setRawStorageBlockItemTag(Tags.Items.STORAGE_BLOCKS_RAW_IRON, Tags.Blocks.STORAGE_BLOCKS_RAW_IRON).setIngotTag(Tags.Items.INGOTS_IRON)
			.setRawMaterialTag(Tags.Items.RAW_MATERIALS_IRON).setNuggetTag(Tags.Items.NUGGETS_IRON).setRawMaterial(Items.RAW_IRON).setIngot(Items.IRON_INGOT)
			.setNugget(Items.IRON_NUGGET).setOverworldOre(Blocks.IRON_ORE).setDeepslateOre(Blocks.DEEPSLATE_IRON_ORE).setStorageBlock(Blocks.IRON_BLOCK)
			.setRawStorageBlock(Blocks.RAW_IRON_BLOCK);

	public static final MaterialBundle GOLD = createNewVanillaBundle("gold", MaterialType.METAL).generateHeatedIngotMaterial().generateDust().generateChunks().generatePlate()
			.generateGear().generateWire().setOreTag(ItemTags.GOLD_ORES, BlockTags.GOLD_ORES)
			.setStorageBlockItemTag(Tags.Items.STORAGE_BLOCKS_GOLD, Tags.Blocks.STORAGE_BLOCKS_GOLD)
			.setRawStorageBlockItemTag(Tags.Items.STORAGE_BLOCKS_RAW_GOLD, Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD).setIngotTag(Tags.Items.INGOTS_GOLD)
			.setRawMaterialTag(Tags.Items.RAW_MATERIALS_GOLD).setNuggetTag(Tags.Items.NUGGETS_GOLD).setRawMaterial(Items.RAW_GOLD).setIngot(Items.GOLD_INGOT)
			.setNugget(Items.GOLD_NUGGET).setOverworldOre(Blocks.GOLD_ORE).setDeepslateOre(Blocks.DEEPSLATE_GOLD_ORE).setNetherOre(Blocks.NETHER_GOLD_ORE)
			.setStorageBlock(Blocks.GOLD_BLOCK).setRawStorageBlock(Blocks.RAW_GOLD_BLOCK);

	public static final MaterialBundle COPPER = createNewVanillaBundle("copper", MaterialType.METAL).generateHeatedIngotMaterial().generateDust().generateChunks().generatePlate()
			.generateGear().generateWire().generateNugget().generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil()
			.setOreTag(ItemTags.COPPER_ORES, BlockTags.COPPER_ORES).setStorageBlockItemTag(Tags.Items.STORAGE_BLOCKS_COPPER, Tags.Blocks.STORAGE_BLOCKS_COPPER)
			.setRawStorageBlockItemTag(Tags.Items.STORAGE_BLOCKS_RAW_COPPER, Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER).setIngotTag(Tags.Items.INGOTS_COPPER)
			.setRawMaterialTag(Tags.Items.RAW_MATERIALS_COPPER).setRawMaterial(Items.RAW_COPPER).setIngot(Items.COPPER_INGOT).setOverworldOre(Blocks.COPPER_ORE)
			.setDeepslateOre(Blocks.DEEPSLATE_COPPER_ORE).setStorageBlock(Blocks.COPPER_BLOCK).setRawStorageBlock(Blocks.RAW_COPPER_BLOCK);

	public static final MaterialBundle DIAMOND = createNewVanillaBundle("diamond", MaterialType.GEM).generateCutStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK)).generateDust()
			.generateChunks().setOreTag(ItemTags.DIAMOND_ORES, BlockTags.DIAMOND_ORES).setStorageBlockItemTag(Tags.Items.STORAGE_BLOCKS_DIAMOND, Tags.Blocks.STORAGE_BLOCKS_DIAMOND)
			.setRawMaterialTag(Tags.Items.GEMS_DIAMOND).setRawMaterial(Items.DIAMOND).setOverworldOre(Blocks.DIAMOND_ORE).setDeepslateOre(Blocks.DEEPSLATE_DIAMOND_ORE)
			.setRawStorageBlock(Blocks.DIAMOND_BLOCK);

	public static final MaterialBundle EMERALD = createNewVanillaBundle("emerald", MaterialType.GEM).generateCutStorageBlock(Properties.copy(Blocks.EMERALD_BLOCK)).generateDust()
			.generateChunks().setOreTag(ItemTags.EMERALD_ORES, BlockTags.EMERALD_ORES).setStorageBlockItemTag(Tags.Items.STORAGE_BLOCKS_EMERALD, Tags.Blocks.STORAGE_BLOCKS_EMERALD)
			.setRawMaterialTag(Tags.Items.GEMS_EMERALD).setRawMaterial(Items.EMERALD).setOverworldOre(Blocks.EMERALD_ORE).setDeepslateOre(Blocks.DEEPSLATE_EMERALD_ORE)
			.setRawStorageBlock(Blocks.EMERALD_BLOCK);

	public static final MaterialBundle COAL = createNewVanillaBundle("coal", MaterialType.GEM).generateDust().generateChunks().setOreTag(ItemTags.COAL_ORES, BlockTags.COAL_ORES)
			.setStorageBlockItemTag(Tags.Items.STORAGE_BLOCKS_COAL, Tags.Blocks.STORAGE_BLOCKS_COAL).setRawMaterial(Items.COAL).setOverworldOre(Blocks.COAL_ORE)
			.setDeepslateOre(Blocks.DEEPSLATE_COAL_ORE).setRawStorageBlock(Blocks.COAL_BLOCK);

	public static final MaterialBundle CHARCOAL = createNewVanillaBundle("charcoal", MaterialType.GEM).generateDust().setRawMaterial(Items.CHARCOAL);
	public static final MaterialBundle QUARTZ = createNewVanillaBundle("quartz", MaterialType.GEM).generateChunks().setOreTag(Tags.Items.ORES_QUARTZ, Tags.Blocks.ORES_QUARTZ)
			.setRawMaterialTag(Tags.Items.GEMS_QUARTZ).setRawMaterial(Items.QUARTZ).setRawStorageBlock(Blocks.QUARTZ_BLOCK).setNetherOre(Blocks.NETHER_QUARTZ_ORE);
	public static final MaterialBundle LAPIS = createNewVanillaBundle("lapis", MaterialType.GEM).generateChunks().setOreTag(Tags.Items.ORES_LAPIS, Tags.Blocks.ORES_LAPIS)
			.setRawMaterialTag(Tags.Items.GEMS_LAPIS).setRawMaterial(Items.LAPIS_LAZULI).setRawStorageBlock(Blocks.LAPIS_BLOCK).setOverworldOre(Blocks.LAPIS_ORE)
			.setDeepslateOre(Blocks.DEEPSLATE_LAPIS_ORE);

	public static final MaterialBundle REDSTONE = createNewVanillaBundle("redstone", MaterialType.DUST).generateChunks()
			.setOreTag(Tags.Items.ORES_REDSTONE, Tags.Blocks.ORES_REDSTONE).setOverworldOre(Blocks.REDSTONE_ORE).setDeepslateOre(Blocks.DEEPSLATE_REDSTONE_ORE)
			.setDust(Items.REDSTONE).setDustTag(Tags.Items.DUSTS_REDSTONE);

	private static MaterialBundle createNewForgeBundle(String name, MaterialType materialType) {
		MaterialBundle bundle = new MaterialBundle(name, MaterialDomain.FORGE, materialType);
		validateAndCacheMaterialBundle(bundle);
		return bundle;
	}

	private static MaterialBundle createNewVanillaBundle(String name, MaterialType materialType) {
		MaterialBundle bundle = new MaterialBundle(name, MaterialDomain.VANILLA, materialType);
		validateAndCacheMaterialBundle(bundle);
		return bundle;
	}

	private static MaterialBundle createNewStaticPowerMaterial(String name, MaterialType materialType) {
		MaterialBundle bundle = new MaterialBundle(name, MaterialDomain.STATICPOWER, materialType);
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
