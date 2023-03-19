package theking530.staticpower.blocks;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import theking530.staticcore.block.StaticCoreItemBlock;
import theking530.staticcore.utilities.IBlockItemCreativeTabProvider;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.tags.ModItemTags;

public class StaticPowerItemBlock extends StaticCoreItemBlock {

	/**
	 * Creates a default BlockItem with a stack size of 64 and no chance to repair.
	 * 
	 * @param block The block this BlockItem represents.
	 * @param name  The registry name to use when registering this block item.
	 */
	public StaticPowerItemBlock(Block block) {
		this(block, new Item.Properties().tab(ModCreativeTabs.GENERAL));
	}

	public StaticPowerItemBlock(Block block, Item.Properties properties) {
		super(block, properties.stacksTo(64).tab(getCreativeTab(block)));
	}

	public static CreativeModeTab getCreativeTab(Block block) {
		if (block instanceof IBlockItemCreativeTabProvider) {
			return ((IBlockItemCreativeTabProvider) block).getCreativeModeTab();
		}
		return ModCreativeTabs.GENERAL;
	}

	@Override
	public Collection<CreativeModeTab> getCreativeTabs() {
		List<CreativeModeTab> output = new LinkedList<>();
		output.add(ModCreativeTabs.GENERAL);

		if (ModItemTags.matches(ModItemTags.MATERIALS, this)) {
			output.add(ModCreativeTabs.MATERIALS);
		}

		if (ModItemTags.matches(ModItemTags.TOOLS, this)) {
			output.add(ModCreativeTabs.TOOLS);
		}

		if (ModItemTags.matches(ModItemTags.CABLES, this)) {
			output.add(ModCreativeTabs.CABLES);
		}

		return output;
	}
}
