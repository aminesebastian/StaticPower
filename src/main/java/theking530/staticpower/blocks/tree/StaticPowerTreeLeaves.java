package theking530.staticpower.blocks.tree;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.LeavesBlock;
import theking530.staticcore.block.IBlockLootTableProvider;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.blocks.interfaces.IItemBlockProvider;

public class StaticPowerTreeLeaves extends LeavesBlock implements IItemBlockProvider, IBlockLootTableProvider {

	public StaticPowerTreeLeaves(Properties properties) {
		super(properties);
	}

	@Override
	public BlockItem getItemBlock() {
		return new StaticPowerItemBlock(this);
	}

	@Override
	public BlockDropType getBlockDropType() {
		return BlockDropType.CUSTOM;
	}
}
