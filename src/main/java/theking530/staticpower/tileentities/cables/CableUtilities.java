package theking530.staticpower.tileentities.cables;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableAttachmentState;
import theking530.staticpower.tileentities.cables.power.BlockPowerCable;

public class CableUtilities {

	public static CableAttachmentState getConnectionState(BlockState state, Direction side) {
		switch (side) {
		case NORTH:
			if (state.get(BlockPowerCable.CABLE_NORTH)) {
				return CableAttachmentState.CABLE;
			} else if (state.get(BlockPowerCable.TILE_ENTITY_NORTH)) {
				return CableAttachmentState.TILE_ENTITY;
			} else {
				return CableAttachmentState.NONE;
			}
		case SOUTH:
			if (state.get(BlockPowerCable.CABLE_SOUTH)) {
				return CableAttachmentState.CABLE;
			} else if (state.get(BlockPowerCable.TILE_ENTITY_SOUTH)) {
				return CableAttachmentState.TILE_ENTITY;
			} else {
				return CableAttachmentState.NONE;
			}
		case EAST:
			if (state.get(BlockPowerCable.CABLE_EAST)) {
				return CableAttachmentState.CABLE;
			} else if (state.get(BlockPowerCable.TILE_ENTITY_EAST)) {
				return CableAttachmentState.TILE_ENTITY;
			} else {
				return CableAttachmentState.NONE;
			}
		case WEST:
			if (state.get(BlockPowerCable.CABLE_WEST)) {
				return CableAttachmentState.CABLE;
			} else if (state.get(BlockPowerCable.TILE_ENTITY_WEST)) {
				return CableAttachmentState.TILE_ENTITY;
			} else {
				return CableAttachmentState.NONE;
			}
		case UP:
			if (state.get(BlockPowerCable.CABLE_UP)) {
				return CableAttachmentState.CABLE;
			} else if (state.get(BlockPowerCable.TILE_ENTITY_UP)) {
				return CableAttachmentState.TILE_ENTITY;
			} else {
				return CableAttachmentState.NONE;
			}
		case DOWN:
			if (state.get(BlockPowerCable.CABLE_DOWN)) {
				return CableAttachmentState.CABLE;
			} else if (state.get(BlockPowerCable.TILE_ENTITY_DOWN)) {
				return CableAttachmentState.TILE_ENTITY;
			} else {
				return CableAttachmentState.NONE;
			}
		}
		return CableAttachmentState.NONE;
	}
}
