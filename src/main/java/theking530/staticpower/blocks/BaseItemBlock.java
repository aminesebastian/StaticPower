package theking530.staticpower.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.StaticPower;

public class BaseItemBlock extends BlockItem {

	/**
	 * Creates a default BlockItem with a stack size of 64 and no chance to repair.
	 * @param block The block this BlockItem represents.
	 * @param name The registry name to use when registering this blockitem.
	 */
	public BaseItemBlock(Block block, ResourceLocation name) {
		super(block, new Item.Properties().maxStackSize(64).group(StaticPower.CREATIVE_TAB));
		setRegistryName(name);
	}

	/**
	 * Checks the pressed state of the sneak key to see if hidden tooltips should be
	 * displayed to the user.
	 * 
	 * @return True if hidden tooltips should be shown, false otherwise.
	 */
	public boolean showHiddenTooltips() {
		return Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown();
	}
}
