package theking530.staticpower.tileentities.digistorenetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.ServerCable;

public abstract class BaseDigistoreBlock extends StaticPowerMachineBlock {

	public BaseDigistoreBlock() {
		super();
	}

	// TODO: Is this still needed??
	@Override
	public void onStaticPowerNeighborChanged(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor,
			boolean isMoving) {
		super.onStaticPowerNeighborChanged(state, world, pos, neighbor, isMoving);
		if (!world.isClientSide()) {
			ServerCable cable = CableNetworkManager.get((ServerLevel) world).getCable(pos);
			if (cable != null && cable.getNetwork() != null) {
				cable.getNetwork().updateGraph((ServerLevel) world, pos, true);
			}
		}
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return false;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		return 0;
	}
}
