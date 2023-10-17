package theking530.staticpower.blockentities.machines.refinery.powertap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlock;

public class BlockRefineryPowerTap extends BaseRefineryBlock {

	public BlockRefineryPowerTap() {
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityRefineryPowerTap.TYPE.create(pos, state);
	}
}
