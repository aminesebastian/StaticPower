package theking530.staticpower.data.materials;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import theking530.staticpower.data.materials.Material.BlockMaterial;
import theking530.staticpower.data.materials.Material.FluidMaterial;
import theking530.staticpower.data.materials.Material.ItemMaterial;
import theking530.staticpower.init.tags.ModBlockTags;
import theking530.staticpower.init.tags.ModItemTags;

public class MaterialBundle {
	public enum MaterialBundleType {
		METAL, GEM, DUST
	};

	private final String name;
	private final MaterialBundleType type;

	private Map<IMaterialType<?>, Material<?>> materials;

	private TagKey<Item> oreItemTag;
	private TagKey<Block> oreBlockTag;

	public MaterialBundle(String name, MaterialBundleType type) {
		this.name = name;
		this.type = type;
		materials = new HashMap<>();
	}

	public MaterialBundleType getMaterialType() {
		return type;
	}

	public Collection<Material<?>> getMaterials() {
		return materials.values();
	}

	public void generateTag() {
		for (Material<?> material : materials.values()) {
			if (material.shouldGenerateTags()) {
				material.generateTag(getName());
			}
		}

		if (hasOre()) {
			oreBlockTag = ModBlockTags.createForgeTag(String.format(MaterialTypes.OVERWORLD_ORE.getTagFormat(), name));
			oreItemTag = ModItemTags.createForgeTag(String.format(MaterialTypes.OVERWORLD_ORE.getTagFormat(), name));
		}
	}

	public void generateFluids() {
		for (Material<?> material : materials.values()) {
			if (material.shouldGenerateMaterial()) {
				material.generateFluid(getName());
			}
		}
	}

	public void generateBlocks() {
		for (Material<?> material : materials.values()) {
			if (material.shouldGenerateMaterial()) {
				material.generateBlock(getName());
			}
		}
	}

	public void generateItems(DeferredRegister<Item> registry) {
		for (Material<?> material : materials.values()) {
			if (material.shouldGenerateMaterial()) {
				material.generateItem(getName());
			}
		}
	}

	public TagKey<Item> getOreItemTag() {
		return oreItemTag;
	}

	public TagKey<Block> getOreBlockTag() {
		return oreBlockTag;
	}

	public String getName() {
		return name;
	}

	public boolean hasOre() {
		return materials.containsKey(MaterialTypes.OVERWORLD_ORE) || materials.containsKey(MaterialTypes.DEEPSLATE_ORE) || materials.containsKey(MaterialTypes.NETHER_ORE);
	}

	public boolean hasGeneratedOre() {
		if (!hasOre()) {
			return false;
		}
		if (materials.containsKey(MaterialTypes.OVERWORLD_ORE)) {
			if (materials.get(MaterialTypes.OVERWORLD_ORE).shouldGenerateMaterial()) {
				return true;
			}
		}
		if (materials.containsKey(MaterialTypes.DEEPSLATE_ORE)) {
			if (materials.get(MaterialTypes.DEEPSLATE_ORE).shouldGenerateMaterial()) {
				return true;
			}
		}
		if (materials.containsKey(MaterialTypes.NETHER_ORE)) {
			if (materials.get(MaterialTypes.NETHER_ORE).shouldGenerateMaterial()) {
				return true;
			}
		}
		return false;
	}

	public <T extends Block> MaterialBundle blockMaterial(BlockMaterial<T> material) {
		if (materials.containsKey(material.getType())) {
			throw new RuntimeException(String.format("Material bundle: %1$s already has a material of type: %2$s registered!", name, material.getType()));
		}
		materials.put(material.getType(), material);

		return this;
	}

	public <T extends Item> MaterialBundle itemMaterial(ItemMaterial<T> material) {
		if (materials.containsKey(material.getType())) {
			throw new RuntimeException(String.format("Material bundle: %1$s already has a material of type: %2$s registered!", name, material.getType()));
		}
		materials.put(material.getType(), material);
		return this;
	}

	public MaterialBundle fluidMaterial(FluidMaterial material) {
		if (materials.containsKey(material.getType())) {
			throw new RuntimeException(String.format("Material bundle: %1$s already has a material of type: %2$s registered!", name, material.getType()));
		}
		materials.put(material.getType(), material);
		return this;
	}

	public boolean has(IMaterialType<?> type) {
		return materials.containsKey(type);
	}

	public boolean hasWithTag(IMaterialType<?> type) {
		return has(type) && materials.get(type).hasTag();
	}

	public boolean hasGeneratedMaterial(IMaterialType<?> type) {
		if (!has(type)) {
			return false;
		}
		return materials.get(type).shouldGenerateMaterial();
	}

	public boolean hasGeneratedTag(IMaterialType<?> type) {
		if (!has(type)) {
			return false;
		}
		return materials.get(type).shouldGenerateTags();
	}

	public void validate() {

	}

	@SuppressWarnings("unchecked")
	public <T> Material<T> get(IMaterialType<T> type) {
		return (Material<T>) materials.get(type);
	}
}
