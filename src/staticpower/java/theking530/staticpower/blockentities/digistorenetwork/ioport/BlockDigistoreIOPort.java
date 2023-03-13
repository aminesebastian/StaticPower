package theking530.staticpower.blockentities.digistorenetwork.ioport;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blockentities.digistorenetwork.BaseDigistoreBlock;

public class BlockDigistoreIOPort extends BaseDigistoreBlock {

	public BlockDigistoreIOPort() {
		super();
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityDigistoreIOPort.TYPE.create(pos, state);
	}
}
