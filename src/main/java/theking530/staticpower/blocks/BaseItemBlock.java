package theking530.staticpower.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class BaseItemBlock extends ItemBlock{

	public BaseItemBlock(Block block, String name) {
		super(block);
		this.setRegistryName(name);
		this.setUnlocalizedName(name);
	}
}
