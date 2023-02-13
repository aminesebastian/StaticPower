package theking530.staticpower.data;

import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerOre;
import theking530.staticpower.items.HeatedIngot;
import theking530.staticpower.items.StaticPowerItem;

public class MaterialBundle {
	private final String name;

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
	private RegistryObject<? extends Item> rawMaterial;

	private boolean generateSmeltedMaterial;
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

	public MaterialBundle(String name) {
		this.name = name;
	}

	public void generateBlocks(DeferredRegister<Block> registry) {
		if (shouldGenerateOverworldOre()) {
			overworldOre = registry.register("ore_" + getName(), () -> new StaticPowerOre(overworldOreProperties, overworldOreExperience.getA(), overworldOreExperience.getB()));
		}
		if (shouldGenerateDeepslateOre()) {
			deepslateOre = registry.register("ore_deepslate" + getName(),
					() -> new StaticPowerOre(deepslateOreProperties, deepslateOreExperience.getA(), deepslateOreExperience.getB()));
		}
		if (shouldGenerateNetherackOre()) {
			deepslateOre = registry.register("ore_nether" + getName(),
					() -> new StaticPowerOre(netherackOreProperties, netherackOreExperience.getA(), netherackOreExperience.getB()));
		}
		if (shouldGenerateRawMaterialStorageBlock()) {
			rawMaterialStorageBlock = registry.register("block_raw_" + getName(), () -> new StaticPowerBlock(rawMaterialStorageBlockProperties));
		}
		if (shouldGenerateSmeltedMaterialStorageBlock()) {
			smeltedMaterialStorageBlock = registry.register("block_" + getName(), () -> new StaticPowerBlock(smeltedMaterialStorageBlockProperties));
		}
		if (shouldGenerateCutStorageBlock()) {
			cutStorageBlock = registry.register("block_cut_" + getName(), () -> new StaticPowerBlock(cutMaterialStorageBlockProperties));
		}
	}

	public void generateItems(DeferredRegister<Item> registry) {
		if (shouldGenerateRawMaterial()) {
			rawMaterial = registry.register("raw_" + getName(), () -> new StaticPowerItem());
		}
		if (shouldGenerateSmeltedMaterial()) {
			smeltedMaterial = registry.register("ingot_" + getName(), () -> new StaticPowerItem());
		}
		if (shouldGenerateHeatedSmeltedMaterial()) {
			heatedSmeltedMaterial = registry.register("ingot_" + getName() + "_heated", () -> new HeatedIngot(() -> smeltedMaterial.get()));
		}
		if (shouldGenerateNugget()) {
			nugget = registry.register("nugget_" + getName(), () -> new StaticPowerItem());
		}
		if (shouldGenerateDust()) {
			dust = registry.register("dust_" + getName(), () -> new StaticPowerItem());
		}
		if (shouldGenerateGear()) {
			gear = registry.register("gear_" + getName(), () -> new StaticPowerItem());
		}
		if (shouldGeneratePlate()) {
			plate = registry.register("plate_" + getName(), () -> new StaticPowerItem());
		}
		if (shouldGenerateRod()) {
			rod = registry.register("rod_" + getName(), () -> new StaticPowerItem());
		}
		if (shouldGenerateChunks()) {
			chunks = registry.register("chunks_" + getName(), () -> new StaticPowerItem());
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

	public RegistryObject<? extends Item> getRawMaterial() {
		return rawMaterial;
	}

	public RegistryObject<? extends Item> getHeatedSmeltedMaterial() {
		return heatedSmeltedMaterial;
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
}
