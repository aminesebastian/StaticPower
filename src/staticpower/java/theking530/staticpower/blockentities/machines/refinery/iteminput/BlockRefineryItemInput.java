package theking530.staticpower.blockentities.machines.refinery.iteminput;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlock;

public class BlockRefineryItemInput extends BaseRefineryBlock {

	public BlockRefineryItemInput() {
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityRefineryItemInput.TYPE.create(pos, state);
	}
}
