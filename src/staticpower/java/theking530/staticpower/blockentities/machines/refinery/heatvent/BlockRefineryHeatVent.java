package theking530.staticpower.blockentities.machines.refinery.heatvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlock;

public class BlockRefineryHeatVent extends BaseRefineryBlock {

	public BlockRefineryHeatVent() {
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityRefineryHeatVent.TYPE.create(pos, state);
	}
}
