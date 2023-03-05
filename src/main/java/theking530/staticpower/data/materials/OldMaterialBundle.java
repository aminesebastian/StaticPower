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

public class OldMaterialBundle {
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

	public OldMaterialBundle(String name, MaterialDomain materialDomain, MaterialType materialType) {
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
		if (oreTag == null && (shouldGenerateDeepslateOre() || shouldGenerateOverworldOre() || shouldGenerateNetherOre())) {
			oreTag = createBlockTag("ore/" + getName());
		}
		if (oreItemTag == null && (shouldGenerateDeepslateOre() || shouldGenerateOverworldOre() || shouldGenerateNetherOre())) {
			oreItemTag = createItemTag("ore/" + getName());
		}
		if (rawStorageBlockTag == null && shouldGenerateRawStorageBlock()) {
			rawStorageBlockTag = createBlockTag("storage_blocks/raw_" + getName());
		}
		if (rawStorageBlockItemTag == null && shouldGenerateRawStorageBlock()) {
			rawStorageBlockItemTag = createItemTag("storage_blocks/raw_" + getName());
		}
		if (storageBlockTag == null && shouldGenerateStorageBlock()) {
			storageBlockTag = createBlockTag("storage_blocks/" + getName());
		}
		if (storageBlockItemTag == null && shouldGenerateStorageBlock()) {
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
		if (rawMaterialTag == null && shouldGenerateRawMaterial()) {
			if (materialType == MaterialType.METAL) {
				rawMaterialTag = createItemTag("raw_materials/" + getName());
			} else {
				rawMaterialTag = createItemTag("gems/" + getName());
			}
		}
		if (ingotTag == null && shouldGenerateIngot()) {
			if (materialType == MaterialType.METAL) {
				ingotTag = createItemTag("ingots/" + getName());
			}
		}
		if (nuggetTag == null && shouldGenerateNugget()) {
			nuggetTag = createItemTag("nuggets/" + getName());
		}
		if (dustTag == null && shouldGenerateDust()) {
			dustTag = createItemTag("dusts/" + getName());
		}
		if (gearTag == null && shouldGenerateGear()) {
			gearTag = createItemTag("gears/" + getName());
		}
		if (plateTag == null && shouldGeneratePlate()) {
			plateTag = createItemTag("plates/" + getName());
		}
		if (rodTag == null && shouldGenerateRod()) {
			rodTag = createItemTag("tags/" + getName());
		}
		if (chunkTag == null && shouldGenerateChunks()) {
			chunkTag = createItemTag("chunks/" + getName());
		}
		if (wireTag == null && shouldGenerateWire()) {
			wireTag = createItemTag("wires/" + getName());
		}
		if (insulatedWireTag == null && shouldGenerateInsulatedWire()) {
			insulatedWireTag = createItemTag("wires/insulated/" + getName());
		}
		if (wireCoilTag == null && shouldGenerateWireCoil()) {
			wireCoilTag = createItemTag("wire_coil/" + getName());
		}
		if (insulatedWireCoilTag == null && shouldGenerateInsulatedWireCoil()) {
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

	public boolean shouldGenerateOverworldOre() {
		return generateOverworldOre;
	}

	public boolean hasOverworldOre() {
		return overworldOre != null;
	}

	public OldMaterialBundle generateOverworldOre(Properties properties, int minXP, int maxXP) {
		this.generateOverworldOre = true;
		this.overworldOreProperties = properties;
		this.overworldOreExperience = new Tuple<Integer, Integer>(minXP, maxXP);
		return this;
	}

	public boolean shouldGenerateDeepslateOre() {
		return generateDeepslateOre;
	}

	public boolean hasDeepslateOre() {
		return deepslateOre != null;
	}

	public OldMaterialBundle generateDeepslateOre(Properties properties, int minXP, int maxXP) {
		this.generateDeepslateOre = true;
		this.deepslateOreProperties = properties;
		this.deepslateOreExperience = new Tuple<Integer, Integer>(minXP, maxXP);
		return this;
	}

	public boolean shouldGenerateNetherOre() {
		return generateNetherackOre;
	}

	public boolean hasNetherOre() {
		return netherackOre != null;
	}

	public OldMaterialBundle generateNetherackOre(Properties properties, int minXP, int maxXP) {
		this.generateNetherackOre = true;
		this.netherackOreProperties = properties;
		this.netherackOreExperience = new Tuple<Integer, Integer>(minXP, maxXP);
		return this;
	}

	public boolean hasOreTag() {
		return getOreTag() != null && getOreItemTag() != null;
	}

	public boolean hasOres() {
		return hasOverworldOre() || hasDeepslateOre() || hasNetherOre();
	}

	public boolean hasRawStorageBlockTag() {
		return getRawStorageBlockTag() != null && getRawStorageBlockItemTag() != null;
	}

	public boolean hasRawStorageBlock() {
		return rawStorageBlock != null;
	}

	public OldMaterialBundle generateRawStorageBlock(Properties properties) {
		this.generateRawStorageBlock = true;
		this.rawStorageBlockProperties = properties;
		return this;
	}

	public boolean hasStorageBlockTag() {
		return getStorageBlockTag() != null && getStorageBlockItemTag() != null;
	}

	public boolean hasStorageBlock() {
		return storageBlock != null;
	}

	public OldMaterialBundle generateStorageBlock(Properties properties) {
		this.generateStorageBlock = true;
		this.storageBlockProperties = properties;
		return this;
	}

	public boolean hasCutStorageBlock() {
		return cutStorageBlock != null;
	}

	public OldMaterialBundle generateCutStorageBlock(Properties properties) {
		this.generateCutStorageBlock = true;
		this.cutMaterialStorageBlockProperties = properties;
		return this;
	}

	public boolean hasRawMaterial() {
		return rawMaterial != null;
	}

	public boolean hasRawMaterialTag() {
		return getRawMaterialTag() != null;
	}

	public OldMaterialBundle generateRawMaterial() {
		this.generateRawMaterial = true;
		return this;
	}

	public OldMaterialBundle generateRawMaterial(String prefix) {
		this.generateRawMaterial = true;
		this.rawMaterialPrefix = prefix;
		return this;
	}

	public boolean hasHeatedIngot() {
		return heatedIngot != null;
	}

	public OldMaterialBundle generateHeatedIngotMaterial() {
		this.generateHeatedIngot = true;
		return this;
	}

	public boolean hasIngotTag() {
		return getIngotTag() != null;
	}

	public boolean hasIngot() {
		return ingot != null;
	}

	public OldMaterialBundle generateIngot() {
		this.generateIngot = true;
		return this;
	}

	public OldMaterialBundle generateIngot(String prefix) {
		this.generateIngot = true;
		this.ingotPrefix = prefix;
		return this;
	}

	public boolean hasDustTag() {
		return getDustTag() != null;
	}

	public boolean hasDust() {
		return dust != null;
	}

	public OldMaterialBundle generateDust() {
		this.generateDust = true;
		return this;
	}

	public boolean hasGearTag() {
		return getGearTag() != null;
	}

	public boolean hasGear() {
		return gear != null;
	}

	public boolean hasGearBox() {
		return gearBox != null;
	}

	public OldMaterialBundle generateGear() {
		this.generateGear = true;
		return this;
	}

	public OldMaterialBundle generateGear(boolean hasGearBox) {
		generateGear();
		this.generateGearBox = hasGearBox;
		return this;
	}

	public boolean hasPlateTag() {
		return getPlateTag() != null;
	}

	public boolean hasPlate() {
		return plate != null;
	}

	public OldMaterialBundle generatePlate() {
		this.generatePlate = true;
		return this;
	}

	public boolean hasRodTag() {
		return getRodTag() != null;
	}

	public boolean hasRod() {
		return rod != null;
	}

	public OldMaterialBundle generateRod() {
		this.generateRod = true;
		return this;
	}

	public boolean hasNuggetTag() {
		return getNuggetTag() != null;
	}

	public boolean hasNugget() {
		return nugget != null;
	}

	public OldMaterialBundle generateNugget() {
		this.generateNugget = true;
		return this;
	}

	public boolean hasChunks() {
		return chunks != null;
	}

	public OldMaterialBundle generateChunks() {
		this.generateChunks = true;
		return this;
	}

	public boolean hasWireTag() {
		return getWireTag() != null;
	}

	public boolean hasWire() {
		return wire != null;
	}

	public OldMaterialBundle generateWire() {
		this.generateWire = true;
		return this;
	}

	public boolean hasInsulatedWireTag() {
		return getInsulatedWireTag() != null;
	}

	public boolean hasInsulatedWire() {
		return insulatedWire != null;
	}

	public OldMaterialBundle generateInsulatedWire() {
		this.generateInsulatedWire = true;
		return this;
	}

	public boolean hasWireCoilTag() {
		return getWireCoilTag() != null;
	}

	public boolean hasWireCoil() {
		return wireCoil != null;
	}

	public OldMaterialBundle generateWireCoil() {
		this.generateWireCoil = true;
		return this;
	}

	public boolean hasInsulatedWireCoilTag() {
		return getInsulatedWireCoilTag() != null;
	}

	public boolean hasInsulatedWireCoil() {
		return insulatedWireCoil != null;
	}

	public OldMaterialBundle generateInsulatedWireCoil() {
		this.generateInsulatedWireCoil = true;
		return this;
	}

	public OldMaterialBundle generateMoltenFluid(int viscosity, int density, int temperature, SDColor fluidColor) {
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

	public OldMaterialBundle setOverworldOre(Block ore) {
		this.overworldOre = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(ore), ForgeRegistries.BLOCKS);
		return this;
	}

	public OldMaterialBundle setNetherOre(Block ore) {
		this.netherackOre = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(ore), ForgeRegistries.BLOCKS);
		return this;
	}

	public OldMaterialBundle setDeepslateOre(Block ore) {
		this.deepslateOre = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(ore), ForgeRegistries.BLOCKS);
		return this;
	}

	public OldMaterialBundle setOreTag(TagKey<Item> itemTag, TagKey<Block> blockTag) {
		oreTag = blockTag;
		oreItemTag = itemTag;
		return this;
	}

	public OldMaterialBundle setRawStorageBlock(Block storageBlock) {
		this.rawStorageBlock = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(storageBlock), ForgeRegistries.BLOCKS);
		return this;
	}

	public OldMaterialBundle setRawStorageBlockTag(TagKey<Item> itemTag, TagKey<Block> blockTag) {
		rawStorageBlockTag = blockTag;
		rawStorageBlockItemTag = itemTag;
		return this;
	}

	public OldMaterialBundle setStorageBlock(Block storageBlock) {
		this.storageBlock = RegistryObject.create(ForgeRegistries.BLOCKS.getKey(storageBlock), ForgeRegistries.BLOCKS);
		return this;
	}

	public OldMaterialBundle setStorageBlockItemTag(TagKey<Item> itemTag, TagKey<Block> blockTag) {
		storageBlockTag = blockTag;
		storageBlockItemTag = itemTag;
		return this;
	}

	public OldMaterialBundle setRawMaterial(Item rawMaterial) {
		this.rawMaterial = RegistryObject.create(ForgeRegistries.ITEMS.getKey(rawMaterial), ForgeRegistries.ITEMS);
		return this;
	}

	public OldMaterialBundle setRawMaterialTag(TagKey<Item> tag) {
		rawMaterialTag = tag;
		return this;
	}

	public OldMaterialBundle setDust(Item dust) {
		this.dust = RegistryObject.create(ForgeRegistries.ITEMS.getKey(dust), ForgeRegistries.ITEMS);
		return this;
	}

	public OldMaterialBundle setDustTag(TagKey<Item> tag) {
		dustTag = tag;
		return this;
	}

	public OldMaterialBundle setIngot(Item ingot) {
		this.ingot = RegistryObject.create(ForgeRegistries.ITEMS.getKey(ingot), ForgeRegistries.ITEMS);
		return this;
	}

	public OldMaterialBundle setIngotTag(TagKey<Item> tag) {
		ingotTag = tag;
		return this;
	}

	public OldMaterialBundle setNugget(Item nugget) {
		this.nugget = RegistryObject.create(ForgeRegistries.ITEMS.getKey(nugget), ForgeRegistries.ITEMS);
		return this;
	}

	public OldMaterialBundle setNuggetTag(TagKey<Item> tag) {
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

	public boolean shouldGenerateRawStorageBlock() {
		return generateRawStorageBlock;
	}

	public boolean shouldGenerateStorageBlock() {
		return generateStorageBlock;
	}

	public boolean isGenerateCutStorageBlock() {
		return generateCutStorageBlock;
	}

	public boolean shouldGenerateRawMaterial() {
		return generateRawMaterial;
	}

	public boolean shouldGenerateIngot() {
		return generateIngot;
	}

	public boolean isGenerateHeatedIngot() {
		return generateHeatedIngot;
	}

	public boolean shouldGenerateNugget() {
		return generateNugget;
	}

	public boolean shouldGenerateDust() {
		return generateDust;
	}

	public boolean shouldGenerateGear() {
		return generateGear;
	}

	public boolean isGenerateGearBox() {
		return generateGearBox;
	}

	public boolean shouldGeneratePlate() {
		return generatePlate;
	}

	public boolean shouldGenerateRod() {
		return generateRod;
	}

	public boolean shouldGenerateChunks() {
		return generateChunks;
	}

	public boolean shouldGenerateWire() {
		return generateWire;
	}

	public boolean shouldGenerateInsulatedWire() {
		return generateInsulatedWire;
	}

	public boolean shouldGenerateWireCoil() {
		return generateWireCoil;
	}

	public boolean shouldGenerateInsulatedWireCoil() {
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