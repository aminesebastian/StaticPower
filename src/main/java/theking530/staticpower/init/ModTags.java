package theking530.staticpower.init;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.StaticPower;

/**
 * Special thanks to:
 * https://github.com/BluSunrize/ImmersiveEngineering/blob/1.16.3/src/main/java/blusunrize/immersiveengineering/common/data/DataGenUtils.java
 * for the create methods.
 * 
 * @author amine
 *
 */
public class ModTags {
	public static final INamedTag<Item> LOG = createItemWrapper(new ResourceLocation("minecraft", "logs"));
	public static final INamedTag<Item> LEAVES = createItemWrapper(new ResourceLocation("minecraft", "leaves"));
	public static final INamedTag<Item> SAPLING = createItemWrapper(new ResourceLocation("minecraft", "saplings"));
	public static final INamedTag<Item> INGOT = createItemWrapper(new ResourceLocation("forge", "ingots"));
	public static final INamedTag<Item> COVER_SAW = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "saw"));
	public static final INamedTag<Item> FARMING_AXE = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "farming_axe"));
	public static final INamedTag<Item> FARMING_HOE = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "farming_hoe"));
	public static final INamedTag<Item> SOLDERING_IRON = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "soldering_iron"));

	public static INamedTag<Item> createItemWrapper(ResourceLocation name) {
		return createWrapperTag(ItemTags.getAllTags(), name, ItemTags::makeWrapperTag);
	}

	public static INamedTag<Block> createBlockWrapper(ResourceLocation name) {
		return createWrapperTag(BlockTags.getAllTags(), name, BlockTags::makeWrapperTag);
	}

	private static <T> INamedTag<T> createWrapperTag(List<? extends INamedTag<T>> allExisting, ResourceLocation name, Function<String, INamedTag<T>> createNew) {
		Optional<? extends INamedTag<T>> existing = allExisting.stream().filter(tag -> tag.getName().equals(name)).findAny();
		if (existing.isPresent()) {
			return existing.get();
		} else {
			return createNew.apply(name.toString());
		}
	}
}
