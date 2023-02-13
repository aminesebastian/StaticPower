package theking530.staticpower.init;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import theking530.staticpower.data.MaterialBundle;

public class ModMaterials {
	public static final MaterialBundle RUBY = new MaterialBundle("ruby").generateOverworldOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5).generateCutStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK)).generateSmeltedMaterial().generateDust().generateChunks();

	public static final MaterialBundle SAPPHIRE = new MaterialBundle("sapphire").generateOverworldOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5).generateCutStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK)).generateSmeltedMaterial().generateDust().generateChunks();

	public static final MaterialBundle TIN = new MaterialBundle("tin").generateOverworldOre(Properties.copy(Blocks.COPPER_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear().generateRod().generateChunks().generateNugget();

	public static final MaterialBundle ZINC = new MaterialBundle("zinc").generateOverworldOre(Properties.copy(Blocks.IRON_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle ALUMINUM = new MaterialBundle("aluminum").generateOverworldOre(Properties.copy(Blocks.COPPER_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle MAGNESIUM = new MaterialBundle("magnesium").generateOverworldOre(Properties.copy(Blocks.IRON_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle PLATINUM = new MaterialBundle("platinum").generateOverworldOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4).generateNetherackOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)
			.generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)).generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterial().generateSmeltedMaterial()

			.generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();
	public static final MaterialBundle LEAD = new MaterialBundle("lead").generateOverworldOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle SILVER = new MaterialBundle("silver").generateOverworldOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4).generateNetherackOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)
			.generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)).generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterial().generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust()
			.generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle TUNGSTEN = new MaterialBundle("tungsten").generateOverworldOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 1, 2).generateNetherackOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)
			.generateRawMaterialStorageBlock(Properties.copy(Blocks.ANCIENT_DEBRIS)).generateCutStorageBlock(Properties.copy(Blocks.ANCIENT_DEBRIS))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.ANCIENT_DEBRIS)).generateRawMaterial().generateSmeltedMaterial().generateHeatedSmeltedMaterial()
			.generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle URANIUM = new MaterialBundle("uranium").generateOverworldOre(Properties.copy(Blocks.IRON_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 2, 4).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle BRONZE = new MaterialBundle("bronze").generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateGear().generateNugget();

	public static final MaterialBundle BRASS = new MaterialBundle("brass").generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateGear().generateRod().generateNugget();

	public static final MaterialBundle STATIC_METAL = new MaterialBundle("static_metal").generateCutStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateGear().generateRod().generateNugget();
	public static final MaterialBundle ENERGIZED_METAL = new MaterialBundle("energized_metal").generateCutStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateGear().generateRod().generateNugget();
	public static final MaterialBundle LUMUM_METAL = new MaterialBundle("lumum_metal").generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateGear().generateRod().generateNugget();

	public static final MaterialBundle REDSTONE_ALLOY = new MaterialBundle("redstone_alloy").generateCutStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateNugget();
	public static final MaterialBundle INERT_INFUSION = new MaterialBundle("inert_infusion").generateCutStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateGear().generateRod().generateNugget();

	public static final MaterialBundle IRON = new MaterialBundle("iron").generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear();
	public static final MaterialBundle GOLD = new MaterialBundle("gold").generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear();
	public static final MaterialBundle COPPER = new MaterialBundle("copper").generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear();
}
