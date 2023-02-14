package theking530.staticpower.data;

import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerOre;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.items.HeatedIngot;
import theking530.staticpower.items.StaticPowerItem;

public class MaterialBundle {
	private final String name;
	/**
	 * Indicates whether or not this material should create forge tags or static
	 * power tags.
	 */
	private final boolean isStaticPowerMaterial;

	private boolean generateOverworldOre;
	private Properties overworldOreProperties;
	private Tuple<Integer, Integer> overworldOreExperience;
	private RegistryObject<? extends Block> overworldOre;

	private boolean generateDeepslateOre;
	private Properties deepslateOreProperties;
	private Tuple<Integer, Integer> deepslateOreExperience;
	private RegistryObject<? extends Block> deepslateOre;

	private boolean generateNetherackOre;
	private Properties netherackOreProperties;
	private Tuple<Integer, Integer> netherackOreExperience;
	private RegistryObject<? extends Block> netherackOre;

	private boolean generateRawMaterialStorageBlock;
	private Properties rawMaterialStorageBlockProperties;
	private RegistryObject<? extends Block> rawMaterialStorageBlock;

	private boolean generateSmeltedMaterialStorageBlock;
	private Properties smeltedMaterialStorageBlockProperties;
	private RegistryObject<? extends Block> smeltedMaterialStorageBlock;

	private boolean generateCutStorageBlock;
	private Properties cutMaterialStorageBlockProperties;
	private RegistryObject<? extends Block> cutStorageBlock;

	private boolean generateRawMaterial;
	private String rawMaterialPrefix;
	private RegistryObject<? extends Item> rawMaterial;

	private boolean generateSmeltedMaterial;
	private String smeltedMaterialPrefix;
	private RegistryObject<? extends Item> smeltedMaterial;

	private boolean generateHeatedSmeltedMaterial;
	private RegistryObject<? extends Item> heatedSmeltedMaterial;

	private boolean generateNugget;
	private RegistryObject<? extends Item> nugget;

	private boolean generateDust;
	private RegistryObject<? extends Item> dust;

	private boolean generateGear;
	private RegistryObject<? extends Item> gear;

	private boolean generatePlate;
	private RegistryObject<? extends Item> plate;

	private boolean generateRod;
	private RegistryObject<? extends Item> rod;

	private boolean generateChunks;
	private RegistryObject<? extends Item> chunks;

	private boolean generateWire;
	private RegistryObject<? extends Item> wire;

	private boolean generateInsulatedWire;
	private RegistryObject<? extends Item> insulatedWire;

	private boolean generateWireCoil;
	private RegistryObject<? extends Item> wireCoil;

	private boolean generateInsulatedWireCoil;
	private RegistryObject<? extends Item> insulatedWireCoil;

	public MaterialBundle(String name, boolean isStaticPowerMaterial) {
		this.name = name;
		this.isStaticPowerMaterial = isStaticPowerMaterial;
		this.rawMaterialPrefix = "raw";
		this.smeltedMaterialPrefix = "ingot";
	}

	public boolean isStaticPowerMaterial() {
		return isStaticPowerMaterial;
	}

	public void generateBlocks() {
		if (shouldGenerateOverworldOre()) {
			overworldOre = ModBlocks.registerBlock("ore_" + getName(), () -> new StaticPowerOre(overworldOreProperties, overworldOreExperience.getA(), overworldOreExperience.getB()));
		}
		if (shouldGenerateDeepslateOre()) {
			deepslateOre = ModBlocks.registerBlock("ore_deepslate_" + getName(),
					() -> new StaticPowerOre(deepslateOreProperties, deepslateOreExperience.getA(), deepslateOreExperience.getB()));
		}
		if (shouldGenerateNetherackOre()) {
			netherackOre = ModBlocks.registerBlock("ore_nether_" + getName(),
					() -> new StaticPowerOre(netherackOreProperties, netherackOreExperience.getA(), netherackOreExperience.getB()));
		}
		if (shouldGenerateRawMaterialStorageBlock()) {
			rawMaterialStorageBlock = ModBlocks.registerBlock("block_raw_" + getName(), () -> new StaticPowerBlock(ModCreativeTabs.MATERIALS, rawMaterialStorageBlockProperties));
		}
		if (shouldGenerateSmeltedMaterialStorageBlock()) {
			smeltedMaterialStorageBlock = ModBlocks.registerBlock("block_" + getName(), () -> new StaticPowerBlock(ModCreativeTabs.MATERIALS, smeltedMaterialStorageBlockProperties));
		}
		if (shouldGenerateCutStorageBlock()) {
			cutStorageBlock = ModBlocks.registerBlock("block_cut_" + getName(), () -> new StaticPowerBlock(ModCreativeTabs.MATERIALS, cutMaterialStorageBlockProperties));
		}
	}

	public void generateItems(DeferredRegister<Item> registry) {
		if (shouldGenerateRawMaterial()) {
			rawMaterial = registry.register(rawMaterialPrefix + "_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateSmeltedMaterial()) {
			smeltedMaterial = registry.register(smeltedMaterialPrefix + "_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateHeatedSmeltedMaterial()) {
			heatedSmeltedMaterial = registry.register("ingot_" + getName() + "_heated", () -> new HeatedIngot(() -> smeltedMaterial.get()));
		}
		if (shouldGenerateNugget()) {
			nugget = registry.register("nugget_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateDust()) {
			dust = registry.register("dust_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateGear()) {
			gear = registry.register("gear_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGeneratePlate()) {
			plate = registry.register("plate_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateRod()) {
			rod = registry.register("rod_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateChunks()) {
			chunks = registry.register("chunks_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateWire()) {
			wire = registry.register("wire_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateInsulatedWire()) {
			insulatedWire = registry.register("wire_insulated_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateWireCoil()) {
			wireCoil = registry.register("wire_coil_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (shouldGenerateInsulatedWireCoil()) {
			insulatedWireCoil = registry.register("wire_coil_insulated_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
	}

	public String getName() {
		return name;
	}

	public boolean shouldGenerateOverworldOre() {
		return generateOverworldOre;
	}

	public MaterialBundle generateOverworldOre(Properties properties, int minXP, int maxXP) {
		this.generateOverworldOre = true;
		this.overworldOreProperties = properties;
		this.overworldOreExperience = new Tuple<Integer, Integer>(minXP, maxXP);
		return this;
	}

	public boolean shouldGenerateDeepslateOre() {
		return generateDeepslateOre;
	}

	public MaterialBundle generateDeepslateOre(Properties properties, int minXP, int maxXP) {
		this.generateDeepslateOre = true;
		this.deepslateOreProperties = properties;
		this.deepslateOreExperience = new Tuple<Integer, Integer>(minXP, maxXP);
		return this;
	}

	public boolean shouldGenerateNetherackOre() {
		return generateNetherackOre;
	}

	public MaterialBundle generateNetherackOre(Properties properties, int minXP, int maxXP) {
		this.generateNetherackOre = true;
		this.netherackOreProperties = properties;
		this.netherackOreExperience = new Tuple<Integer, Integer>(minXP, maxXP);
		return this;
	}

	public boolean hasOres() {
		return generateOverworldOre || generateDeepslateOre || generateNetherackOre;
	}

	public boolean shouldGenerateRawMaterialStorageBlock() {
		return generateRawMaterialStorageBlock;
	}

	public MaterialBundle generateRawMaterialStorageBlock(Properties properties) {
		this.generateRawMaterialStorageBlock = true;
		this.rawMaterialStorageBlockProperties = properties;
		return this;
	}

	public boolean shouldGenerateSmeltedMaterialStorageBlock() {
		return generateSmeltedMaterialStorageBlock;
	}

	public MaterialBundle generateSmeltedMaterialStorageBlock(Properties properties) {
		this.generateSmeltedMaterialStorageBlock = true;
		this.smeltedMaterialStorageBlockProperties = properties;
		return this;
	}

	public boolean shouldGenerateCutStorageBlock() {
		return generateCutStorageBlock;
	}

	public MaterialBundle generateCutStorageBlock(Properties properties) {
		this.generateCutStorageBlock = true;
		this.cutMaterialStorageBlockProperties = properties;
		return this;
	}

	public boolean shouldGenerateRawMaterial() {
		return generateRawMaterial;
	}

	public MaterialBundle generateRawMaterial() {
		this.generateRawMaterial = true;
		return this;
	}

	public MaterialBundle generateRawMaterial(String prefix) {
		this.generateRawMaterial = true;
		this.rawMaterialPrefix = prefix;
		return this;
	}

	public boolean shouldGenerateHeatedSmeltedMaterial() {
		return generateHeatedSmeltedMaterial;
	}

	public MaterialBundle generateHeatedSmeltedMaterial() {
		this.generateHeatedSmeltedMaterial = true;
		return this;
	}

	public boolean shouldGenerateSmeltedMaterial() {
		return generateSmeltedMaterial;
	}

	public MaterialBundle generateSmeltedMaterial() {
		this.generateSmeltedMaterial = true;
		return this;
	}

	public MaterialBundle generateSmeltedMaterial(String prefix) {
		this.generateSmeltedMaterial = true;
		this.smeltedMaterialPrefix = prefix;
		return this;
	}

	public boolean shouldGenerateDust() {
		return generateDust;
	}

	public MaterialBundle generateDust() {
		this.generateDust = true;
		return this;
	}

	public boolean shouldGenerateGear() {
		return generateGear;
	}

	public MaterialBundle generateGear() {
		this.generateGear = true;
		return this;
	}

	public boolean shouldGeneratePlate() {
		return generatePlate;
	}

	public MaterialBundle generatePlate() {
		this.generatePlate = true;
		return this;
	}

	public boolean shouldGenerateRod() {
		return generateRod;
	}

	public MaterialBundle generateRod() {
		this.generateRod = true;
		return this;
	}

	public boolean shouldGenerateNugget() {
		return generateNugget;
	}

	public MaterialBundle generateNugget() {
		this.generateNugget = true;
		return this;
	}

	public boolean shouldGenerateChunks() {
		return generateChunks;
	}

	public MaterialBundle generateChunks() {
		this.generateChunks = true;
		return this;
	}

	public boolean shouldGenerateWire() {
		return generateWire;
	}

	public MaterialBundle generateWire() {
		this.generateWire = true;
		return this;
	}

	public boolean shouldGenerateInsulatedWire() {
		return generateInsulatedWire;
	}

	public MaterialBundle generateInsulatedWire() {
		this.generateInsulatedWire = true;
		return this;
	}

	public boolean shouldGenerateWireCoil() {
		return generateWireCoil;
	}

	public MaterialBundle generateWireCoil() {
		this.generateWireCoil = true;
		return this;
	}

	public boolean shouldGenerateInsulatedWireCoil() {
		return generateInsulatedWireCoil;
	}

	public MaterialBundle generateInsulatedWireCoil() {
		this.generateInsulatedWireCoil = true;
		return this;
	}

	public RegistryObject<? extends Block> getOverworldOre() {
		return overworldOre;
	}

	public RegistryObject<? extends Block> getDeepslateOre() {
		return deepslateOre;
	}

	public RegistryObject<? extends Block> getNetherackOre() {
		return netherackOre;
	}

	public RegistryObject<? extends Block> getRawMaterialStorageBlock() {
		return rawMaterialStorageBlock;
	}

	public RegistryObject<? extends Block> getSmeltedMaterialStorageBlock() {
		return smeltedMaterialStorageBlock;
	}

	public RegistryObject<? extends Block> getCutStorageBlock() {
		return cutStorageBlock;
	}

	public String getRawMaterialPrefix() {
		return rawMaterialPrefix;
	}

	public RegistryObject<? extends Item> getRawMaterial() {
		return rawMaterial;
	}

	public RegistryObject<? extends Item> getHeatedSmeltedMaterial() {
		return heatedSmeltedMaterial;
	}

	public String getSmeltedMaterialPrefix() {
		return smeltedMaterialPrefix;
	}

	public RegistryObject<? extends Item> getSmeltedMaterial() {
		return smeltedMaterial;
	}

	public RegistryObject<? extends Item> getDust() {
		return dust;
	}

	public RegistryObject<? extends Item> getGear() {
		return gear;
	}

	public RegistryObject<? extends Item> getPlate() {
		return plate;
	}

	public RegistryObject<? extends Item> getRod() {
		return rod;
	}

	public RegistryObject<? extends Item> getChunks() {
		return chunks;
	}

	public RegistryObject<? extends Item> getNugget() {
		return nugget;
	}

	public RegistryObject<? extends Item> getWire() {
		return wire;
	}

	public RegistryObject<? extends Item> getInsulatedWire() {
		return insulatedWire;
	}

	public RegistryObject<? extends Item> getWireCoil() {
		return wireCoil;
	}

	public RegistryObject<? extends Item> getInsulatedWireCoil() {
		return insulatedWireCoil;
	}

	public void validate() {
		if (hasOres() && !shouldGenerateRawMaterial()) {
			throw new RuntimeException("All materials that generate an ore must also generate a raw material!");
		}

		if (getName() == null || getName().length() == 0) {
			throw new RuntimeException("All materials must have a valid name (non-null & non-empty.");
		}
	}
}
