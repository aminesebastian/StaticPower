package theking530.staticpower.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import theking530.staticpower.data.MaterialBundle;

public class ModMaterials {
	public static final Map<String, MaterialBundle> MATERIALS = new HashMap<>();

	public static final MaterialBundle RUBY = createNewBundle("ruby").generateOverworldOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5).generateCutStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK))
			.generateRawMaterialStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK)).generateRawMaterial("gem").generateDust().generateChunks();

	public static final MaterialBundle SAPPHIRE = createNewBundle("sapphire").generateOverworldOre(Properties.copy(Blocks.DIAMOND_ORE), 2, 5)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 2, 5).generateCutStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK))
			.generateRawMaterialStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK)).generateRawMaterial("gem").generateDust().generateChunks();

	public static final MaterialBundle TIN = createNewBundle("tin").generateOverworldOre(Properties.copy(Blocks.COPPER_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear().generateRod().generateChunks().generateNugget();

	public static final MaterialBundle ZINC = createNewBundle("zinc").generateOverworldOre(Properties.copy(Blocks.IRON_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle ALUMINUM = createNewBundle("aluminum").generateOverworldOre(Properties.copy(Blocks.COPPER_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_COPPER_ORE), 1, 2).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget().generateWire();

	public static final MaterialBundle MAGNESIUM = createNewBundle("magnesium").generateOverworldOre(Properties.copy(Blocks.IRON_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 1, 2).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle PLATINUM = createNewBundle("platinum").generateOverworldOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4).generateNetherackOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)
			.generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)).generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterial().generateSmeltedMaterial()

			.generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();
	public static final MaterialBundle LEAD = createNewBundle("lead").generateOverworldOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK))
			.generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle SILVER = createNewBundle("silver").generateOverworldOre(Properties.copy(Blocks.GOLD_ORE), 2, 4)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_GOLD_ORE), 2, 4).generateNetherackOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)
			.generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)).generateCutStorageBlock(Properties.copy(Blocks.GOLD_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterial().generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust()
			.generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle TUNGSTEN = createNewBundle("tungsten").generateOverworldOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.ANCIENT_DEBRIS), 1, 2).generateNetherackOre(Properties.copy(Blocks.NETHER_GOLD_ORE), 2, 5)
			.generateRawMaterialStorageBlock(Properties.copy(Blocks.ANCIENT_DEBRIS)).generateCutStorageBlock(Properties.copy(Blocks.ANCIENT_DEBRIS))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.ANCIENT_DEBRIS)).generateRawMaterial().generateSmeltedMaterial().generateHeatedSmeltedMaterial()
			.generateDust().generatePlate().generateChunks().generateNugget();

	public static final MaterialBundle URANIUM = createNewBundle("uranium").generateOverworldOre(Properties.copy(Blocks.IRON_ORE), 1, 2)
			.generateDeepslateOre(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), 2, 4).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterial().generateSmeltedMaterial().generateDust().generateChunks()
			.generateNugget();

	public static final MaterialBundle BRONZE = createNewBundle("bronze").generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateGear().generateNugget();

	public static final MaterialBundle BRASS = createNewBundle("brass").generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateGear().generateRod().generateNugget().generateWire().generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();

	public static final MaterialBundle STATIC_METAL = createNewStaticPowerMaterial("static_metal").generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK))
			.generateRawMaterial().generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear().generateRod().generateNugget()
			.generateWire().generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();
	public static final MaterialBundle ENERGIZED_METAL = createNewStaticPowerMaterial("energized_metal").generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear().generateRod().generateNugget().generateWire()
			.generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();
	public static final MaterialBundle LUMUM_METAL = createNewStaticPowerMaterial("lumum_metal").generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawMaterialStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)).generateRawMaterial()
			.generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear().generateRod().generateNugget().generateWire()
			.generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();

	public static final MaterialBundle REDSTONE_ALLOY = createNewStaticPowerMaterial("redstone_alloy").generateCutStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateNugget();
	public static final MaterialBundle INERT_INFUSION = createNewStaticPowerMaterial("inert_infusion").generateCutStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK))
			.generateSmeltedMaterialStorageBlock(Properties.copy(Blocks.IRON_BLOCK)).generateSmeltedMaterial().generateHeatedSmeltedMaterial().generateDust().generatePlate()
			.generateGear().generateRod().generateNugget();

	public static final MaterialBundle IRON = createNewBundle("iron").generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear();
	public static final MaterialBundle GOLD = createNewBundle("gold").generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear().generateWire();
	public static final MaterialBundle COPPER = createNewBundle("copper").generateHeatedSmeltedMaterial().generateDust().generatePlate().generateGear().generateWire()
			.generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();;

	public static final MaterialBundle DIAMOND = createNewBundle("diamond").generateCutStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK)).generateDust();
	public static final MaterialBundle EMERALD = createNewBundle("emerald").generateCutStorageBlock(Properties.copy(Blocks.EMERALD_BLOCK)).generateDust();

	private static MaterialBundle createNewBundle(String name) {
		MaterialBundle bundle = new MaterialBundle(name, false);
		validateAndCacheMaterialBundle(bundle);
		return bundle;
	}

	private static MaterialBundle createNewStaticPowerMaterial(String name) {
		MaterialBundle bundle = new MaterialBundle(name, true);
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
