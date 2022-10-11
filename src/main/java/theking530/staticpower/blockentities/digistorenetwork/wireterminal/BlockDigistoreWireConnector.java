package theking530.staticpower.blockentities.digistorenetwork.wireterminal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blockentities.power.wireconnector.BlockWireConnector;

public class BlockDigistoreWireConnector extends BlockWireConnector {

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityDigitstoreWireConnector.TYPE.create(pos, state);
	}
}
