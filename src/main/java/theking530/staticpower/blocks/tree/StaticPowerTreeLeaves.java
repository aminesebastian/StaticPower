package theking530.staticpower.blocks.tree;

import net.minecraft.block.LeavesBlock;
import net.minecraft.item.BlockItem;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.blocks.interfaces.IItemBlockProvider;

public class StaticPowerTreeLeaves extends LeavesBlock implements IItemBlockProvider {

	public StaticPowerTreeLeaves(String name, Properties properties) {
		super(properties);
		this.setRegistryName(name);
	}

	@Override
	public BlockItem getItemBlock() {
		return new StaticPowerItemBlock(this);
	}
}
