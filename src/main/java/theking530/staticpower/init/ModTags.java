package theking530.staticpower.init;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
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
	public static final Named<Item> LOG = createItemWrapper(new ResourceLocation("minecraft", "logs"));
	public static final Named<Item> LEAVES = createItemWrapper(new ResourceLocation("minecraft", "leaves"));
	public static final Named<Item> SAPLING = createItemWrapper(new ResourceLocation("minecraft", "saplings"));
	public static final Named<Item> INGOT = createItemWrapper(new ResourceLocation("forge", "ingots"));
	public static final Named<Item> COVER_SAW = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "saw"));
	public static final Named<Item> FARMING_AXE = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "farming_axe"));
	public static final Named<Item> FARMING_HOE = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "farming_hoe"));
	public static final Named<Item> SOLDERING_IRON = createItemWrapper(new ResourceLocation(StaticPower.MOD_ID, "soldering_iron"));

	public static Named<Item> createItemWrapper(ResourceLocation name) {
		return createWrapperTag(ItemTags.getWrappers(), name, ItemTags::bind);
	}

	public static Named<Block> createBlockWrapper(ResourceLocation name) {
		return createWrapperTag(BlockTags.getWrappers(), name, BlockTags::bind);
	}

	private static <T> Named<T> createWrapperTag(List<? extends Named<T>> allExisting, ResourceLocation name, Function<String, Named<T>> createNew) {
		Optional<? extends Named<T>> existing = allExisting.stream().filter(tag -> tag.getName().equals(name)).findAny();
		if (existing.isPresent()) {
			return existing.get();
		} else {
			return createNew.apply(name.toString());
		}
	}
}
