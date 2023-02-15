package theking530.staticpower.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import theking530.staticpower.data.MaterialBundle;
import theking530.staticpower.data.MaterialBundle.MaterialDomain;
import theking530.staticpower.data.MaterialBundle.MaterialType;

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
			.generateStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate().generateGear(true).generateRod()
			.generateNugget().generateWire().generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();

	public static final MaterialBundle STATIC_METAL = createNewStaticPowerMaterial("static_metal", MaterialType.METAL).generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateStorageBlock(Properties.copy(Blocks.COPPER_BLOCK)).generateRawStorageBlock(Properties.copy(Blocks.RAW_COPPER_BLOCK)).generateRawMaterial().generateIngot()
			.generateHeatedIngotMaterial().generateDust().generatePlate().generateGear(true).generateRod().generateNugget().generateWire().generateWireCoil().generateInsulatedWire()
			.generateInsulatedWireCoil();
	public static final MaterialBundle ENERGIZED_METAL = createNewStaticPowerMaterial("energized_metal", MaterialType.METAL)
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateStorageBlock(Properties.copy(Blocks.IRON_BLOCK))
			.generateRawStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)).generateRawMaterial().generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate()
			.generateGear(true).generateRod().generateNugget().generateWire().generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();
	public static final MaterialBundle LUMUM_METAL = createNewStaticPowerMaterial("lumum_metal", MaterialType.METAL).generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER))
			.generateStorageBlock(Properties.copy(Blocks.GOLD_BLOCK)).generateRawStorageBlock(Properties.copy(Blocks.RAW_GOLD_BLOCK)).generateRawMaterial().generateIngot()
			.generateHeatedIngotMaterial().generateDust().generatePlate().generateGear(true).generateRod().generateNugget().generateWire().generateWireCoil().generateInsulatedWire()
			.generateInsulatedWireCoil();

	public static final MaterialBundle REDSTONE_ALLOY = createNewStaticPowerMaterial("redstone_alloy", MaterialType.METAL)
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateStorageBlock(Properties.copy(Blocks.IRON_BLOCK))
			.generateRawStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)).generateRawMaterial().generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate()
			.generateNugget();
	public static final MaterialBundle INERT_INFUSION = createNewStaticPowerMaterial("inert_infusion_alloy", MaterialType.METAL)
			.generateCutStorageBlock(Properties.copy(Blocks.CUT_COPPER)).generateStorageBlock(Properties.copy(Blocks.IRON_BLOCK))
			.generateRawStorageBlock(Properties.copy(Blocks.RAW_IRON_BLOCK)).generateRawMaterial().generateIngot().generateHeatedIngotMaterial().generateDust().generatePlate()
			.generateGear(true).generateRod().generateNugget();

	public static final MaterialBundle IRON = createNewVanillaBundle("iron", MaterialType.METAL).generateHeatedIngotMaterial().generateDust().generatePlate().generateGear();
	public static final MaterialBundle GOLD = createNewVanillaBundle("gold", MaterialType.METAL).generateHeatedIngotMaterial().generateDust().generatePlate().generateGear()
			.generateWire();
	public static final MaterialBundle COPPER = createNewVanillaBundle("copper", MaterialType.METAL).generateHeatedIngotMaterial().generateDust().generatePlate().generateGear()
			.generateWire().generateNugget().generateWireCoil().generateInsulatedWire().generateInsulatedWireCoil();

	public static final MaterialBundle DIAMOND = createNewVanillaBundle("diamond", MaterialType.GEM).generateCutStorageBlock(Properties.copy(Blocks.DIAMOND_BLOCK)).generateDust();
	public static final MaterialBundle EMERALD = createNewVanillaBundle("emerald", MaterialType.GEM).generateCutStorageBlock(Properties.copy(Blocks.EMERALD_BLOCK)).generateDust();

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