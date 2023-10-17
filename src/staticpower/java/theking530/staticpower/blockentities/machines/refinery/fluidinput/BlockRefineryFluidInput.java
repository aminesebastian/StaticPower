package theking530.staticpower.blockentities.machines.refinery.fluidinput;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlock;

public class BlockRefineryFluidInput extends BaseRefineryBlock {

	public BlockRefineryFluidInput() {
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityRefineryFluidInput.INPUT_TYPE.create(pos, state);
	}
}
