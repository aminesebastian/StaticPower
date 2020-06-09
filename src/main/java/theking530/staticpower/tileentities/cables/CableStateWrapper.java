package theking530.staticpower.tileentities.cables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import theking530.staticpower.tileentities.cables.power.BlockPowerCable;

public class CableStateWrapper {
	public enum CableConnectionState {
		EMPTY, CABLE, ATTACHED
	}

	private BlockState cableBlockState;

	public CableStateWrapper(BlockState state) {
		cableBlockState = state;
	}

	public CableConnectionState getConnectionState(Direction side) {
		switch (side) {
		case NORTH:
			if (cableBlockState.get(BlockPowerCable.CABLE_NORTH)) {
				return CableConnectionState.CABLE;
			} else if (cableBlockState.get(BlockPowerCable.ATTACHMENT_NORTH)) {
				return CableConnectionState.ATTACHED;
			} else {
				return CableConnectionState.EMPTY;
			}
		case SOUTH:
			if (cableBlockState.get(BlockPowerCable.CABLE_SOUTH)) {
				return CableConnectionState.CABLE;
			} else if (cableBlockState.get(BlockPowerCable.ATTACHMENT_SOUTH)) {
				return CableConnectionState.ATTACHED;
			} else {
				return CableConnectionState.EMPTY;
			}
		case EAST:
			if (cableBlockState.get(BlockPowerCable.CABLE_EAST)) {
				return CableConnectionState.CABLE;
			} else if (cableBlockState.get(BlockPowerCable.ATTACHMENT_EAST)) {
				return CableConnectionState.ATTACHED;
			} else {
				return CableConnectionState.EMPTY;
			}
		case WEST:
			if (cableBlockState.get(BlockPowerCable.CABLE_WEST)) {
				return CableConnectionState.CABLE;
			} else if (cableBlockState.get(BlockPowerCable.ATTACHMENT_WEST)) {
				return CableConnectionState.ATTACHED;
			} else {
				return CableConnectionState.EMPTY;
			}
		case UP:
			if (cableBlockState.get(BlockPowerCable.CABLE_UP)) {
				return CableConnectionState.CABLE;
			} else if (cableBlockState.get(BlockPowerCable.ATTACHMENT_UP)) {
				return CableConnectionState.ATTACHED;
			} else {
				return CableConnectionState.EMPTY;
			}
		case DOWN:
			if (cableBlockState.get(BlockPowerCable.CABLE_DOWN)) {
				return CableConnectionState.CABLE;
			} else if (cableBlockState.get(BlockPowerCable.ATTACHMENT_DOWN)) {
				return CableConnectionState.ATTACHED;
			} else {
				return CableConnectionState.EMPTY;
			}
		}
		return CableConnectionState.EMPTY;
	}

	public VoxelShape getShape(double cableRadius) {
		double coreMin = 8.0D - cableRadius;
		double coreMax = 8.0D + cableRadius;
		VoxelShape output = Block.makeCuboidShape(coreMin, coreMin, coreMin, coreMax, coreMax, coreMax);

		if (getConnectionState(Direction.NORTH) == CableConnectionState.CABLE) {
			output = VoxelShapes.or(output, Block.makeCuboidShape(coreMin, coreMin, 0.0D, coreMax, coreMax, coreMin));
		}
		if (getConnectionState(Direction.SOUTH) == CableConnectionState.CABLE) {
			output = VoxelShapes.or(output, Block.makeCuboidShape(coreMin, coreMin, coreMax, coreMax, coreMax, 16.0D));
		}
		if (getConnectionState(Direction.EAST) == CableConnectionState.CABLE) {
			output = VoxelShapes.or(output, Block.makeCuboidShape(coreMax, coreMin, coreMin, 16.0D, coreMax, coreMax));
		}
		if (getConnectionState(Direction.WEST) == CableConnectionState.CABLE) {
			output = VoxelShapes.or(output, Block.makeCuboidShape(0.0D, coreMin, coreMin, coreMin, coreMax, coreMax));
		}
		if (getConnectionState(Direction.UP) == CableConnectionState.CABLE) {
			output = VoxelShapes.or(output, Block.makeCuboidShape(coreMin, coreMax, coreMin, coreMax, 16.0D, coreMax));
		}
		if (getConnectionState(Direction.DOWN) == CableConnectionState.CABLE) {
			output = VoxelShapes.or(output, Block.makeCuboidShape(coreMin, 0.0D, coreMin, coreMax, coreMin, coreMax));
		}

		return output;
	}
}
