package theking530.staticpower.blocks;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class BaseItemBlock extends ItemBlock {

	public BaseItemBlock(Block block, String name) {
		super(block);
		this.setRegistryName(name);
		this.setUnlocalizedName(name);
	}
	public boolean showHiddenTooltips() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
}
