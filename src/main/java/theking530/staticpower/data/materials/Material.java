package theking530.staticpower.data.materials;

import java.util.function.Supplier;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.common.SoundActions;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.fluid.StaticPowerFluidBuilder;
import theking530.staticpower.fluid.StaticPowerFluidBundle;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.tags.ModBlockTags;
import theking530.staticpower.init.tags.ModItemTags;
import theking530.staticpower.items.StaticPowerItem;

public abstract class Material<T> {
	public enum MaterialClass {
		BLOCK, ITEM, FLUID
	}

	public enum MaterialDomain {
		VANILLA("minecraft"), FORGE("forge"), STATICPOWER(StaticPower.MOD_ID);

		private String domain;

		private MaterialDomain(String domain) {
			this.domain = domain;
		}

		public String getName() {
			return domain;
		}
	};

	private final MaterialType<T> type;
	private final MaterialClass materialClass;
	private final boolean shouldGenerate;
	private final boolean shouldGenerateTags;

	private String nameFormatOverride;
	private String tagFormatOverride;
	private MaterialDomain tagDomain;
	protected Supplier<T> material;
	protected TagKey<Item> itemTag;
	protected TagKey<Block> blockTag;

	private Material(MaterialType<T> type, MaterialClass materialClass, Supplier<T> material, boolean shouldGenerateMaterial, MaterialDomain tagDomain, String nameFormatOverride,
			String tagFormatOverride, TagKey<Item> itemTag, TagKey<Block> blockTag, boolean shouldGenerateTag) {
		shouldGenerate = shouldGenerateMaterial;
		shouldGenerateTags = shouldGenerateTag;

		this.materialClass = materialClass;
		this.material = material;
		this.type = type;
		this.tagDomain = tagDomain;
		this.nameFormatOverride = nameFormatOverride;
		this.tagFormatOverride = tagFormatOverride;
		this.itemTag = itemTag;
		this.blockTag = blockTag;
	}

	public MaterialClass getMaterialClass() {
		return materialClass;
	}

	@SuppressWarnings("unchecked")
	public void generateBlock(String name) {
		if (getMaterialClass() == MaterialClass.BLOCK && shouldGenerateMaterial()) {
			material = (Supplier<T>) ModBlocks.registerBlock(String.format(getNameFormat(), name), () -> (Block) registerBlock(name));
		}
	}

	@SuppressWarnings("unchecked")
	public void generateItem(String name) {
		if (getMaterialClass() == MaterialClass.ITEM && shouldGenerateMaterial()) {
			material = (Supplier<T>) ModItems.registerItem(String.format(getNameFormat(), name), () -> (Item) registerItem(name));
		}
	}

	@SuppressWarnings("unchecked")
	public void generateFluid(String name) {
		if (getMaterialClass() == MaterialClass.FLUID && shouldGenerateMaterial()) {
			StaticPowerFluidBundle fluid = ModFluids.registerFluid(registerFluid(String.format(getNameFormat(), name)));
			material = () -> (T) fluid;
		}

	}

	protected T registerBlock(String name) {
		return null;
	}

	protected T registerItem(String name) {
		return null;
	}

	protected StaticPowerFluidBuilder registerFluid(String name) {
		return null;

	}

	public void generateTag(String name) {
	}

	public Supplier<T> getSupplier() {
		return material;
	}

	public T get() {
		return material.get();
	}

	protected void setMaterial(Supplier<T> material) {
		this.material = material;
	}

	public boolean hasItemTag() {
		return itemTag != null;
	}

	public TagKey<Item> getItemTag() {
		return itemTag;
	}

	public boolean hasBlockTag() {
		return blockTag != null;
	}

	public TagKey<Block> getBlockTag() {
		return blockTag;
	}

	public boolean hasTag() {
		return itemTag != null || blockTag != null;
	}

	public MaterialType<T> getType() {
		return type;
	}

	public String getNameFormat() {
		return nameFormatOverride == null ? type.getNameFormat() : nameFormatOverride;
	}

	public String getFormattedName(String name) {
		return String.format(getNameFormat(), name);
	}

	public String getTagFormat() {
		return tagFormatOverride == null ? type.getTagFormat() : tagFormatOverride;
	}

	public String getFormattedTag(String name) {
		return String.format(getTagFormat(), name);
	}

	public boolean shouldGenerateMaterial() {
		return shouldGenerate;
	}

	public boolean shouldGenerateTags() {
		return shouldGenerateTags;
	}

	public MaterialDomain getTagDomain() {
		return this.tagDomain;
	}

	public static class ItemMaterial<T extends Item> extends Material<T> {
		private Supplier<T> supplier;

		@SuppressWarnings("unchecked")
		private ItemMaterial(MaterialType<T> type, Supplier<T> material, boolean shouldGenerateMaterial, MaterialDomain tagDomain, String nameFormatOverride,
				String tagFormatOverride, TagKey<Item> itemTag, TagKey<Block> blockTag, boolean shouldGenerateTag) {
			super(type, MaterialClass.ITEM, material, shouldGenerateMaterial, tagDomain, nameFormatOverride, tagFormatOverride, itemTag, blockTag, shouldGenerateTag);

			if (material == null) {
				supplier = () -> (T) new StaticPowerItem(ModCreativeTabs.MATERIALS);
			} else {
				supplier = material;
			}
		}

		public static <T extends Item> ItemMaterial<T> generate(MaterialType<T> type, MaterialDomain domain, String nameFormatOverride, String tagFormatOverride) {
			return new ItemMaterial<T>(type, null, true, domain, nameFormatOverride, tagFormatOverride, null, null, true);
		}

		public static <T extends Item> ItemMaterial<T> generate(MaterialType<T> type, MaterialDomain domain) {
			return new ItemMaterial<T>(type, null, true, domain, null, null, null, null, true);
		}

		public static <T extends Item> ItemMaterial<T> generateNoTag(MaterialType<T> type) {
			return new ItemMaterial<T>(type, null, true, null, null, null, null, null, false);
		}

		public static <T extends Item> ItemMaterial<T> existing(MaterialType<T> type, MaterialDomain domain, String nameFormatOverride, String tagFormatOverride,
				Supplier<T> material) {
			return new ItemMaterial<T>(type, material, false, domain, nameFormatOverride, tagFormatOverride, null, null, true);
		}

		public static <T extends Item> ItemMaterial<T> existing(MaterialType<T> type, MaterialDomain domain, Supplier<T> material) {
			return new ItemMaterial<T>(type, material, false, domain, null, null, null, null, true);
		}

		public static <T extends Item> ItemMaterial<T> existing(MaterialType<T> type, Supplier<T> material, TagKey<Item> itemTag) {
			return new ItemMaterial<T>(type, material, false, null, null, null, itemTag, null, false);
		}

		public static <T extends Item> ItemMaterial<T> existingNoTag(MaterialType<T> type, Supplier<T> material) {
			return new ItemMaterial<T>(type, material, false, null, null, null, null, null, false);
		}

		@Override
		public T registerItem(String name) {
			return supplier.get();
		}

		@Override
		public void generateTag(String name) {
			if (getTagFormat() == null) {
				throw new RuntimeException(String.format("Material: %1$s of type: %2$s that expected a tag does not have a tag format supplied!", name, getType().getNameFormat()));
			}
			itemTag = ModItemTags.createTagOnDomain(getTagDomain().getName(), String.format(getTagFormat(), name));
		}
	}

	public static class BlockMaterial<T extends Block> extends Material<T> {
		private Supplier<T> supplier;

		@SuppressWarnings("unchecked")
		private BlockMaterial(MaterialType<T> type, Supplier<T> material, boolean shouldGenerateMaterial, MaterialDomain tagDomain, String nameFormatOverride,
				String tagFormatOverride, TagKey<Item> itemTag, TagKey<Block> blockTag, boolean shouldGenerateTag, Properties blockProperties) {
			super(type, MaterialClass.BLOCK, material, shouldGenerateMaterial, tagDomain, nameFormatOverride, tagFormatOverride, itemTag, blockTag, shouldGenerateTag);
			if (material == null) {
				supplier = () -> (T) new StaticPowerBlock(ModCreativeTabs.MATERIALS, blockProperties);
			} else {
				supplier = material;
			}
		}

		public static <T extends Block> BlockMaterial<T> generate(MaterialType<T> type, MaterialDomain tagDomain, Properties blockProperties) {
			return new BlockMaterial<T>(type, null, true, tagDomain, null, null, null, null, true, blockProperties);
		}

		public static <T extends Block> BlockMaterial<T> generate(MaterialType<T> type, MaterialDomain tagDomain, Supplier<T> customSupplier) {
			return new BlockMaterial<T>(type, customSupplier, true, tagDomain, null, null, null, null, true, null);
		}

		public static <T extends Block> BlockMaterial<T> generate(MaterialType<T> type, MaterialDomain tagDomain, String nameFormatOverride, String tagFormatOverride,
				Properties blockProperties) {
			return new BlockMaterial<T>(type, null, true, tagDomain, nameFormatOverride, tagFormatOverride, null, null, true, blockProperties);
		}

		public static <T extends Block> BlockMaterial<T> generateNoTag(MaterialType<T> type, Properties blockProperties) {
			return new BlockMaterial<T>(type, null, true, null, null, null, null, null, false, blockProperties);
		}

		public static <T extends Block> BlockMaterial<T> generateNoTag(MaterialType<T> type, MaterialDomain tagDomain, Supplier<T> customSupplier) {
			return new BlockMaterial<T>(type, customSupplier, true, tagDomain, null, null, null, null, false, null);
		}

		public static <T extends Block> BlockMaterial<T> existing(MaterialType<T> type, MaterialDomain tagDomain, Supplier<T> material) {
			return new BlockMaterial<T>(type, material, false, tagDomain, null, null, null, null, true, null);
		}

		public static <T extends Block> BlockMaterial<T> existing(MaterialType<T> type, Supplier<T> material, TagKey<Item> itemTag, TagKey<Block> blockTag) {
			return new BlockMaterial<T>(type, material, false, null, null, null, itemTag, blockTag, false, null);
		}

		public static <T extends Block> BlockMaterial<T> existingNoTag(MaterialType<T> type, Supplier<T> material) {
			return new BlockMaterial<T>(type, material, false, null, null, null, null, null, false, null);
		}

		@Override
		public T registerBlock(String name) {
			return supplier.get();
		}

		@Override
		public void generateTag(String name) {
			if (getTagFormat() == null) {
				throw new RuntimeException(String.format("Material: %1$s of type: %2$s that expected a tag does not have a tag format supplied!", name, getType()));
			}
			blockTag = ModBlockTags.createTagOnDomain(getTagDomain().getName(), String.format(getTagFormat(), name));
			itemTag = ModItemTags.createTagOnDomain(getTagDomain().getName(), String.format(getTagFormat(), name));
		}
	}

	public static class FluidMaterial extends Material<StaticPowerFluidBundle> {
		private int viscosity;
		private int density;
		private int temperature;
		private SDColor fluidColor;

		public FluidMaterial(MaterialType<StaticPowerFluidBundle> type, int viscosity, int density, int temperature, SDColor fluidColor) {
			super(type, MaterialClass.FLUID, null, true, null, null, null, null, null, false);
			this.viscosity = viscosity;
			this.density = density;
			this.temperature = temperature;
			this.fluidColor = fluidColor;
		}

		public static FluidMaterial generate(MaterialType<StaticPowerFluidBundle> type, int viscosity, int density, int temperature, SDColor fluidColor) {
			return new FluidMaterial(type, viscosity, density, temperature, fluidColor);
		}

		@Override
		public StaticPowerFluidBuilder registerFluid(String name) {
			return new StaticPowerFluidBuilder(name, fluidColor).addProperties(builder -> {
				builder.viscosity(viscosity).density(density).temperature(temperature).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
						.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
			});
		}
	}
}
