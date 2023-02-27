package theking530.staticpower.data.materials;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerOre;
import theking530.staticpower.fluid.AbstractStaticPowerFluid.Source;
import theking530.staticpower.fluid.StaticPowerFluidBuilder;
import theking530.staticpower.fluid.StaticPowerFluidBundle;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.tags.ModBlockTags;
import theking530.staticpower.init.tags.ModItemTags;
import theking530.staticpower.items.GearBox;
import theking530.staticpower.items.HeatedIngot;
import theking530.staticpower.items.StaticPowerItem;

public class MaterialBundle {
	public enum MaterialDomain {
		VANILLA, FORGE, STATICPOWER
	};

	public enum MaterialType {
		METAL, GEM, DUST
	};

	private final String name;
	/**
	 * Indicates whether or not this material should create minecraft, forge, or
	 * static power tags.
	 */
	private final MaterialDomain materialDomain;
	private final MaterialType materialType;

	private TagKey<Block> oreTag;
	private TagKey<Item> oreItemTag;

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

	private boolean generateRawStorageBlock;
	private Properties rawStorageBlockProperties;
	private RegistryObject<? extends Block> rawStorageBlock;
	private TagKey<Block> rawStorageBlockTag;
	private TagKey<Item> rawStorageBlockItemTag;

	private boolean generateStorageBlock;
	private Properties storageBlockProperties;
	private RegistryObject<? extends Block> storageBlock;
	private TagKey<Block> storageBlockTag;
	private TagKey<Item> storageBlockItemTag;

	private boolean generateCutStorageBlock;
	private Properties cutMaterialStorageBlockProperties;
	private RegistryObject<? extends Block> cutStorageBlock;

	private boolean generateRawMaterial;
	private String rawMaterialPrefix;
	private RegistryObject<? extends Item> rawMaterial;
	private TagKey<Item> rawMaterialTag;

	private boolean generateIngot;
	private String ingotPrefix;
	private RegistryObject<? extends Item> ingot;
	private TagKey<Item> ingotTag;

	private boolean generateHeatedIngot;
	private RegistryObject<? extends Item> heatedIngot;

	private boolean generateNugget;
	private RegistryObject<? extends Item> nugget;
	private TagKey<Item> nuggetTag;

	private boolean generateDust;
	private RegistryObject<? extends Item> dust;
	private TagKey<Item> dustTag;

	private boolean generateGear;
	private RegistryObject<? extends Item> gear;
	private TagKey<Item> gearTag;

	private boolean generateGearBox;
	private RegistryObject<? extends Item> gearBox;

	private boolean generatePlate;
	private RegistryObject<? extends Item> plate;
	private TagKey<Item> plateTag;

	private boolean generateRod;
	private RegistryObject<? extends Item> rod;
	private TagKey<Item> rodTag;

	private boolean generateChunks;
	private RegistryObject<? extends Item> chunks;
	private TagKey<Item> chunkTag;

	private boolean generateWire;
	private RegistryObject<? extends Item> wire;
	private TagKey<Item> wireTag;

	private boolean generateInsulatedWire;
	private RegistryObject<? extends Item> insulatedWire;
	private TagKey<Item> insulatedWireTag;

	private boolean generateWireCoil;
	private RegistryObject<? extends Item> wireCoil;
	private TagKey<Item> wireCoilTag;

	private boolean generateInsulatedWireCoil;
	private RegistryObject<? extends Item> insulatedWireCoil;
	private TagKey<Item> insulatedWireCoilTag;

	private boolean generateMoltenFluid;
	private SDColor moltenFluidColor;
	private int moltenFluidViscosity;
	private int moltenFluidDensity;
	private int moltenFluidTemperature;
	private StaticPowerFluidBundle moltenFluid;

	public MaterialBundle(String name, MaterialDomain materialDomain, MaterialType materialType) {
		this.name = name;
		this.materialDomain = materialDomain;
		this.materialType = materialType;
		this.rawMaterialPrefix = "raw";
		this.ingotPrefix = "ingot";
	}

	public boolean isStaticPowerMaterial() {
		return materialDomain == MaterialDomain.STATICPOWER;
	}

	public MaterialType getMaterialType() {
		return materialType;
	}

	public MaterialDomain getMaterialDomain() {
		return this.materialDomain;
	}

	public void generateFluids() {
		if (generateMoltenFluid) {
			moltenFluid = ModFluids.registerFluid(new StaticPowerFluidBuilder("molten_" + getName(), moltenFluidColor).addProperties(builder -> {
				builder.viscosity(moltenFluidViscosity).density(moltenFluidDensity).temperature(moltenFluidTemperature)
						.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
			}));

		}
	}

	public void generateBlocks() {
		if (oreTag == null) {
			oreTag = createBlockTag("ore/" + getName());
		}
		if (oreItemTag == null) {
			oreItemTag = createItemTag("ore/" + getName());
		}
		if (rawStorageBlockTag == null) {
			rawStorageBlockTag = createBlockTag("storage_blocks/raw_" + getName());
		}
		if (rawStorageBlockItemTag == null) {
			rawStorageBlockItemTag = createItemTag("storage_blocks/raw_" + getName());
		}
		if (storageBlockTag == null) {
			storageBlockTag = createBlockTag("storage_blocks/" + getName());
		}
		if (storageBlockItemTag == null) {
			storageBlockItemTag = createItemTag("storage_blocks/" + getName());
		}

		if (generateOverworldOre) {
			overworldOre = ModBlocks.registerBlock("ore_" + getName(),
					() -> new StaticPowerOre(overworldOreProperties, overworldOreExperience.getA(), overworldOreExperience.getB()));
		}
		if (generateDeepslateOre) {
			deepslateOre = ModBlocks.registerBlock("ore_deepslate_" + getName(),
					() -> new StaticPowerOre(deepslateOreProperties, deepslateOreExperience.getA(), deepslateOreExperience.getB()));
		}
		if (generateNetherackOre) {
			netherackOre = ModBlocks.registerBlock("ore_nether_" + getName(),
					() -> new StaticPowerOre(netherackOreProperties, netherackOreExperience.getA(), netherackOreExperience.getB()));
		}
		if (generateRawStorageBlock) {
			rawStorageBlock = ModBlocks.registerBlock("block_raw_" + getName(), () -> new StaticPowerBlock(ModCreativeTabs.MATERIALS, rawStorageBlockProperties));
		}
		if (generateStorageBlock) {
			storageBlock = ModBlocks.registerBlock("block_" + getName(), () -> new StaticPowerBlock(ModCreativeTabs.MATERIALS, storageBlockProperties));
		}
		if (generateCutStorageBlock) {
			cutStorageBlock = ModBlocks.registerBlock("block_cut_" + getName(), () -> new StaticPowerBlock(ModCreativeTabs.MATERIALS, cutMaterialStorageBlockProperties));
		}
	}

	public void generateItems(DeferredRegister<Item> registry) {
		if (rawMaterialTag == null) {
			if (materialType == MaterialType.METAL) {
				rawMaterialTag = createItemTag("raw_materials/" + getName());
			} else {
				rawMaterialTag = createItemTag("gems/" + getName());
			}
		}
		if (ingotTag == null) {
			if (materialType == MaterialType.METAL) {
				ingotTag = createItemTag("ingots/" + getName());
			}
		}
		if (nuggetTag == null) {
			nuggetTag = createItemTag("nuggets/" + getName());
		}
		if (dustTag == null) {
			dustTag = createItemTag("dusts/" + getName());
		}
		if (gearTag == null) {
			gearTag = createItemTag("gears/" + getName());
		}
		if (plateTag == null) {
			plateTag = createItemTag("plates/" + getName());
		}
		if (rodTag == null) {
			rodTag = createItemTag("tags/" + getName());
		}
		if (chunkTag == null) {
			chunkTag = createItemTag("chunks/" + getName());
		}
		if (wireTag == null) {
			wireTag = createItemTag("wires/" + getName());
		}
		if (insulatedWireTag == null) {
			insulatedWireTag = createItemTag("wires/insulated/" + getName());
		}
		if (wireCoilTag == null) {
			wireCoilTag = createItemTag("wire_coil/" + getName());
		}
		if (insulatedWireCoilTag == null) {
			insulatedWireCoilTag = createItemTag("wire_coil/insulated/" + getName());
		}

		if (generateRawMaterial) {
			rawMaterial = registry.register(rawMaterialPrefix + "_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateIngot) {
			ingot = registry.register(ingotPrefix + "_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateHeatedIngot) {
			heatedIngot = registry.register("ingot_" + getName() + "_heated", () -> new HeatedIngot(() -> ingot.get()));
		}
		if (generateNugget) {
			nugget = registry.register("nugget_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateDust) {
			dust = registry.register("dust_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateGear) {
			gear = registry.register("gear_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generatePlate) {
			plate = registry.register("plate_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateRod) {
			rod = registry.register("rod_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateChunks) {
			chunks = registry.register("chunks_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateWire) {
			wire = registry.register("wire_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateInsulatedWire) {
			insulatedWire = registry.register("wire_insulated_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateWireCoil) {
			wireCoil = registry.register("wire_coil_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateInsulatedWireCoil) {
			insulatedWireCoil = registry.register("wire_coil_insulated_" + getName(), () -> new StaticPowerItem(ModCreativeTabs.MATERIALS));
		}
		if (generateGearBox) {
			gearBox = registry.register("gear_box_" + getName(), () -> new GearBox());
		}
	}

	public String getName() {
		return name;
	}

	public boolean hasOverworldOre() {
		return overworldOre != null;
	}

	public MaterialBundle generateOverworldOre(Properties properties, int minXP, int maxXP) {
		this.generateOverworldOre = true;
		this.overworldOreProperties = properties;
		this.overworldOreExperience = new Tuple<Integer, Integer>(minXP, maxXP);
		return this;
	}

	public boolean hasDeepslateOre() {
		return deepslateOre != null;
	}

	public MaterialBundle generateDeepslateOre(Properties properties, int minXP, int maxXP) {
		this.generateDeepslateOre = true;
		this.deepslateOreProperties = properties;
		this.deepslateOreExperience = new Tuple<Integer, Integer>(minXP, maxXP);
		return this;
	}

	public boolean hasNetherOre() {
		return netherackOre != null;
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

	public boolean hasRawStorageBlock() {
		return rawStorageBlock != null;
	}

	public MaterialBundle generateRawStorageBlock(Properties properties) {
		this.generateRawStorageBlock = true;
		this.rawStorageBlockProperties = properties;
		return this;
	}

	public boolean hasStorageBlock() {
		return storageBlock != null;
	}

	public MaterialBundle generateStorageBlock(Properties properties) {
		this.generateStorageBlock = true;
		this.storageBlockProperties = properties;
		return this;
	}

	public boolean hasCutStorageBlock() {
		return cutStorageBlock != null;
	}

	public MaterialBundle generateCutStorageBlock(Properties properties) {
		this.generateCutStorageBlock = true;
		this.cutMaterialStorageBlockProperties = properties;
		return this;
	}

	public boolean hasRawMaterial() {
		return rawMaterial != null;
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

	public boolean hasHeatedIngot() {
		return heatedIngot != null;
	}

	public MaterialBundle generateHeatedIngotMaterial() {
		this.generateHeatedIngot = true;
		return this;
	}

	public boolean hasIngot() {
		return ingot != null;
	}

	public MaterialBundle generateIngot() {
		this.generateIngot = true;
		return this;
	}

	public MaterialBundle generateIngot(String prefix) {
		this.generateIngot = true;
		this.ingotPrefix = prefix;
		return this;
	}

	public boolean hasDust() {
		return dust != null;
	}

	public MaterialBundle generateDust() {
		this.generateDust = true;
		return this;
	}

	public boolean hasGear() {
		return gear != null;
	}

	public boolean hasGearBox() {
		return gearBox != null;
	}

	public MaterialBundle generateGear() {
		this.generateGear = true;
		return this;
	}

	public MaterialBundle generateGear(boolean hasGearBox) {
		generateGear();
		this.generateGearBox = hasGearBox;
		return this;
	}

	public boolean hasPlate() {
		return plate != null;
	}

	public MaterialBundle generatePlate() {
		this.generatePlate = true;
		return this;
	}

	public boolean hasRod() {
		return rod != null;
	}

	public MaterialBundle generateRod() {
		this.generateRod = true;
		return this;
	}

	public boolean hasNugget() {
		return nugget != null;
	}

	public MaterialBundle generateNugget() {
		this.generateNugget = true;
		return this;
	}

	public boolean hasChunks() {
		return chunks != null;
	}

	public MaterialBundle generateChunks() {
		this.generateChunks = true;
		return this;
	}

	public boolean hasWire() {
		return wire != null;
	}

	public MaterialBundle generateWire() {
		this.generateWire = true;
		return this;
	}

	public boolean hasInsulatedWire() {
		return insulatedWire != null;
	}

	public MaterialBundle generateInsulatedWire() {
		this.generateInsulatedWire = true;
		return this;
	}

	public boolean hasWireCoil() {
		return wireCoil != null;
	}

	public MaterialBundle generateWireCoil() {
		this.generateWireCoil = true;
		return this;
	}

	public boolean hasInsulatedWireCoil() {
		return insulatedWireCoil != null;
	}

	public MaterialBundle generateInsulatedWireCoil() {
		this.generateInsulatedWireCoil = true;
		return this;
	}

	public MaterialBundle generateMoltenFluid(int viscosity, int density, int temperature, SDColor fluidColor) {
		this.generateMoltenFluid = true;
		this.moltenFluidViscosity = viscosity;
		this.moltenFluidDensity = density;
		this.moltenFluidTemperature = temperature;
		this.moltenFluidColor = fluidColor;
		return this;
	}

	public boolean hasMoltenFluid() {
		return moltenFluid != null;
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
		return rawStorageBlock;
	}

	public RegistryObject<? extends Block> getStorageBlock() {
		return storageBlock;
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

	public RegistryObject<? extends Item> getHeatedIngot() {
		return heatedIngot;
	}

	public String getSmeltedMaterialPrefix() {
		return ingotPrefix;
	}

	public RegistryObject<? extends Item> getIngot() {
		return ingot;
	}

	public RegistryObject<? extends Item> getDust() {
		return dust;
	}

	public RegistryObject<? extends Item> getGear() {
		return gear;
	}

	public RegistryObject<? extends Item> getGearBox() {
		return gearBox;
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

	public StaticPowerFluidBundle getMoltenFluidBundle() {
		return moltenFluid;
	}

	public RegistryObject<Source> getMoltenFluid() {
		return moltenFluid.getSource();
	}

	public TagKey<Block> getOreTag() {
		return oreTag;
	}

	public TagKey<Item> getOreItemTag() {
		return oreItemTag;
	}

	public TagKey<Block> getRawStorageBlockTag() {
		return rawStorageBlockTag;
	}

	public TagKey<Item> getRawStorageBlockItemTag() {
		return rawStorageBlockItemTag;
	}

	public TagKey<Block> getStorageBlockTag() {
		return storageBlockTag;
	}

	public TagKey<Item> getStorageBlockItemTag() {
		return storageBlockItemTag;
	}

	public TagKey<Item> getRawMaterialTag() {
		return rawMaterialTag;
	}

	public TagKey<Item> getIngotTag() {
		return ingotTag;
	}

	public TagKey<Item> getNuggetTag() {
		return nuggetTag;
	}

	public TagKey<Item> getDustTag() {
		return dustTag;
	}

	public TagKey<Item> getGearTag() {
		return gearTag;
	}

	public TagKey<Item> getPlateTag() {
		return plateTag;
	}

	public TagKey<Item> getChunkTag() {
		return chunkTag;
	}

	public TagKey<Item> getRodTag() {
		return rodTag;
	}

	public TagKey<Item> getWireTag() {
		return wireTag;
	}

	public TagKey<Item> getInsulatedWireTag() {
		return insulatedWireTag;
	}

	public TagKey<Item> getWireCoilTag() {
		return wireCoilTag;
	}

	public TagKey<Item> getInsulatedWireCoilTag() {
		return insulatedWireCoilTag;
	}

	public MaterialBundle setOverworldOre(Block ore) {
		this.overworldOre = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(ore), ForgeRegistries.BLOCKS);
		return this;
	}

	public MaterialBundle setNetherOre(Block ore) {
		this.netherackOre = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(ore), ForgeRegistries.BLOCKS);
		return this;
	}

	public MaterialBundle setDeepslateOre(Block ore) {
		this.deepslateOre = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(ore), ForgeRegistries.BLOCKS);
		return this;
	}

	public MaterialBundle setOreTag(TagKey<Item> itemTag, TagKey<Block> blockTag) {
		oreTag = blockTag;
		oreItemTag = itemTag;
		return this;
	}

	public MaterialBundle setRawStorageBlock(Block storageBlock) {
		this.rawStorageBlock = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(storageBlock), ForgeRegistries.BLOCKS);
		return this;
	}

	public MaterialBundle setRawStorageBlockItemTag(TagKey<Item> itemTag, TagKey<Block> blockTag) {
		rawStorageBlockTag = blockTag;
		rawStorageBlockItemTag = itemTag;
		return this;
	}

	public MaterialBundle setStorageBlock(Block storageBlock) {
		this.storageBlock = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(storageBlock), ForgeRegistries.BLOCKS);
		return this;
	}

	public MaterialBundle setStorageBlockItemTag(TagKey<Item> itemTag, TagKey<Block> blockTag) {
		storageBlockTag = blockTag;
		storageBlockItemTag = itemTag;
		return this;
	}

	public MaterialBundle setRawMaterial(Item rawMaterial) {
		this.rawMaterial = RegistryObject.create(ForgeRegistries.ITEMS.getKey(rawMaterial), ForgeRegistries.ITEMS);
		return this;
	}

	public MaterialBundle setRawMaterialTag(TagKey<Item> tag) {
		rawMaterialTag = tag;
		return this;
	}

	public MaterialBundle setDust(Item dust) {
		this.dust = RegistryObject.create(ForgeRegistries.ITEMS.getKey(dust), ForgeRegistries.ITEMS);
		return this;
	}

	public MaterialBundle setDustTag(TagKey<Item> tag) {
		dustTag = tag;
		return this;
	}

	public MaterialBundle setIngot(Item ingot) {
		this.ingot = RegistryObject.create(ForgeRegistries.ITEMS.getKey(ingot), ForgeRegistries.ITEMS);
		return this;
	}

	public MaterialBundle setIngotTag(TagKey<Item> tag) {
		ingotTag = tag;
		return this;
	}

	public MaterialBundle setNugget(Item nugget) {
		this.nugget = RegistryObject.create(ForgeRegistries.ITEMS.getKey(nugget), ForgeRegistries.ITEMS);
		return this;
	}

	public MaterialBundle setNuggetTag(TagKey<Item> tag) {
		nuggetTag = tag;
		return this;
	}

	private TagKey<Item> createItemTag(String name) {
		if (isStaticPowerMaterial()) {
			return ModItemTags.create(name);
		}
		return ModItemTags.createForgeTag(name);
	}

	private TagKey<Block> createBlockTag(String name) {
		if (isStaticPowerMaterial()) {
			return ModBlockTags.create(name);
		}
		return ModBlockTags.createForgeTag(name);
	}

	public boolean hasGeneratedOverworldOre() {
		return generateOverworldOre;
	}

	public boolean hasGeneratedDeepslateOre() {
		return generateDeepslateOre;
	}

	public boolean hasGeneratedNetherackOre() {
		return generateNetherackOre;
	}

	public boolean isGenerateRawStorageBlock() {
		return generateRawStorageBlock;
	}

	public boolean isGenerateStorageBlock() {
		return generateStorageBlock;
	}

	public boolean isGenerateCutStorageBlock() {
		return generateCutStorageBlock;
	}

	public boolean isGenerateRawMaterial() {
		return generateRawMaterial;
	}

	public boolean isGenerateIngot() {
		return generateIngot;
	}

	public boolean isGenerateHeatedIngot() {
		return generateHeatedIngot;
	}

	public boolean isGenerateNugget() {
		return generateNugget;
	}

	public boolean isGenerateDust() {
		return generateDust;
	}

	public boolean isGenerateGear() {
		return generateGear;
	}

	public boolean isGenerateGearBox() {
		return generateGearBox;
	}

	public boolean isGeneratePlate() {
		return generatePlate;
	}

	public boolean isGenerateRod() {
		return generateRod;
	}

	public boolean isGenerateChunks() {
		return generateChunks;
	}

	public boolean isGenerateWire() {
		return generateWire;
	}

	public boolean isGenerateInsulatedWire() {
		return generateInsulatedWire;
	}

	public boolean isGenerateWireCoil() {
		return generateWireCoil;
	}

	public boolean isGenerateInsulatedWireCoil() {
		return generateInsulatedWireCoil;
	}

	public void validate() {
		if (hasOres() && !hasRawMaterial()) {
			throw new RuntimeException("All materials that generate an ore must also generate a raw material!");
		}

		if (getName() == null || getName().length() == 0) {
			throw new RuntimeException("All materials must have a valid name (non-null & non-empty.");
		}
	}
}