package theking530.staticpower.data.materials;

import java.util.function.Supplier;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.tags.ModBlockTags;
import theking530.staticpower.init.tags.ModItemTags;
import theking530.staticpower.items.StaticPowerItem;

public abstract class Material<T extends ItemLike> {
	public enum MaterialTagDomain {
		VANILLA("minecraft"), FORGE("forge"), STATICPOWER(StaticPower.MOD_ID);

		private String domain;

		private MaterialTagDomain(String domain) {
			this.domain = domain;
		}

		public String getName() {
			return domain;
		}
	};

	private final String name;
	private final String tagLocation;
	private final boolean shouldGenerate;
	private final MaterialTagDomain tagDomain;
	protected RegistryObject<T> material;
	protected TagKey<Item> itemTag;
	protected TagKey<Block> blockTag;

	private Material(String name, MaterialTagDomain tagDomain, String tagLocation, boolean shouldGenerate) {
		this.name = name;
		this.tagLocation = tagLocation;
		this.shouldGenerate = shouldGenerate;
		this.tagDomain = tagDomain;
	}

	public RegistryObject<T> generateBlock() {
		return null;
	}

	public RegistryObject<T> generateItem() {
		return null;
	}

	public void generateTag() {
	}

	public RegistryObject<T> getMaterial() {
		return material;
	}

	protected void setMaterial(RegistryObject<T> material) {
		this.material = material;
	}

	public TagKey<Item> getItemTag() {
		return itemTag;
	}

	public TagKey<Block> getBlockTag() {
		return blockTag;
	}

	public String getName() {
		return name;
	}

	public String getTagLocation() {
		return tagLocation;
	}

	public boolean isShouldGenerate() {
		return shouldGenerate;
	}

	public MaterialTagDomain getTagDomain() {
		return this.tagDomain;
	}

	public static class ItemMaterial<T extends Item> extends Material<T> {
		private Supplier<T> supplier;

		@SuppressWarnings("unchecked")
		private ItemMaterial(String name, MaterialTagDomain tagDomain, String tagLocation, RegistryObject<T> material) {
			super(name, tagDomain, tagLocation, material == null);
			setMaterial(material);
			supplier = () -> (T) new StaticPowerItem(ModCreativeTabs.MATERIALS);
		}

		public static <T extends Item> ItemMaterial<T> create(String name, MaterialTagDomain tagDomain, String tagLocation) {
			return new ItemMaterial<T>(name, tagDomain, tagLocation, null);
		}

		public static <T extends Item> ItemMaterial<T> create(String name, MaterialTagDomain tagDomain, String tagLocation, RegistryObject<T> material) {
			return new ItemMaterial<T>(name, tagDomain, tagLocation, material);
		}

		public static <T extends Item> ItemMaterial<T> create(String name, MaterialTagDomain tagDomain, String tagLocation, Supplier<T> supplier) {
			ItemMaterial<T> output = new ItemMaterial<T>(name, tagDomain, tagLocation, null);
			output.supplier = supplier;
			return output;
		}

		@Override
		public RegistryObject<T> generateItem() {
			return ModItems.registerItem(getTagLocation(), supplier);
		}

		@Override
		public void generateTag() {
			itemTag = ModItemTags.createTagOnDomain(getTagDomain().getName(), getTagLocation());
		}
	}

	public static class BlockMaterial<T extends Block> extends Material<T> {
		private Supplier<T> supplier;

		@SuppressWarnings("unchecked")
		private BlockMaterial(String name, MaterialTagDomain tagDomain, String tagLocation, RegistryObject<T> material, Properties blockProperties) {
			super(name, tagDomain, tagLocation, material == null);
			setMaterial(material);
			supplier = () -> (T) new StaticPowerBlock(ModCreativeTabs.MATERIALS, blockProperties);
		}

		public static <T extends Block> BlockMaterial<T> create(String name, MaterialTagDomain tagDomain, String tagLocation, RegistryObject<T> material) {
			return new BlockMaterial<T>(name, tagDomain, tagLocation, material, null);
		}

		public static <T extends Block> BlockMaterial<T> create(String name, MaterialTagDomain tagDomain, String tagLocation, Properties blockProperties) {
			return new BlockMaterial<T>(name, tagDomain, tagLocation, null, blockProperties);
		}

		public static <T extends Block> BlockMaterial<T> create(String name, MaterialTagDomain tagDomain, String tagLocation, Supplier<T> blockSupplier) {
			BlockMaterial<T> output = new BlockMaterial<T>(name, tagDomain, tagLocation, null, null);
			output.supplier = blockSupplier;
			return output;
		}

		@Override
		public RegistryObject<T> generateBlock() {
			return ModBlocks.registerBlock(getTagLocation(), supplier);
		}

		@Override
		public void generateTag() {
			blockTag = ModBlockTags.createTagOnDomain(getTagDomain().getName(), getTagLocation());
			itemTag = ModItemTags.createTagOnDomain(getTagDomain().getName(), getTagLocation());
		}
	}
}
