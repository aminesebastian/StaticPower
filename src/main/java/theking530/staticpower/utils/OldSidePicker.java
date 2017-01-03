package theking530.staticpower.utils;

import net.minecraft.util.EnumFacing;

/**
 * Thanks to mikeemoo and the openmods team for an amazing open source mod.
 */
public class OldSidePicker {

	private static final ProjectionHelper projectionHelper = new ProjectionHelper();

	public enum BlockSide {
		Front,
		Back,
		Top,
		Bottom,
		Right,
		Left;
	}
	public enum Side {
		XNeg,
		XPos,
		YNeg,
		YPos,
		ZNeg,
		ZPos;
	}


	public static EnumFacing getMachineFront(int metadata) {
		switch(metadata) {
			case 0: return EnumFacing.NORTH;
			case 1: return EnumFacing.EAST;
			case 2: return EnumFacing.SOUTH;
			case 3: return EnumFacing.WEST;
		}
		return EnumFacing.NORTH;
	}

		/**
		 * 
		 * @return Returns default of 6.
		 */
	public static int toSideNumber(Side side) {
		switch (side) {
			case XNeg:
				return 4;
			case XPos:
				return 5;
			case YNeg:
				return 0;
			case YPos:
				return 1;
			case ZNeg:
				return 2;
			case ZPos:
				return 3;
			default:
				return 6;
		}
	}
	public static BlockSide getBlockSideFromEnumFacing(EnumFacing facing, int metadata) {
		switch(facing) {
		case DOWN: return BlockSide.Bottom;
		case UP: return BlockSide.Top;
		case NORTH:
			switch(metadata) {
			case 0: return BlockSide.Back;
			case 1: return BlockSide.Left;
			case 2: return BlockSide.Front;
			case 3: return BlockSide.Right;
			}
		case SOUTH:
			switch(metadata) {
			case 0: return BlockSide.Front;
			case 1: return BlockSide.Right;
			case 2: return BlockSide.Back;
			case 3: return BlockSide.Left;
			}
		case EAST:
			switch(metadata) {
			case 0: return BlockSide.Right;
			case 1: return BlockSide.Back;
			case 2: return BlockSide.Left;
			case 3: return BlockSide.Front;
			}
		case WEST:
			switch(metadata) {
			case 0: return BlockSide.Left;
			case 1: return BlockSide.Front;
			case 2: return BlockSide.Right;
			case 3: return BlockSide.Back;
			}
		}
		return BlockSide.Top;
	}
	public static EnumFacing getAdjustedEnumFacing(EnumFacing facing, int metadata) {
		switch(facing) {
		case DOWN: return EnumFacing.DOWN;
		case UP: return EnumFacing.UP;
		case NORTH:
			switch(metadata) {
			case 0: return EnumFacing.NORTH;
			case 1: return EnumFacing.WEST;
			case 2: return EnumFacing.SOUTH;
			case 3: return EnumFacing.EAST;
			}
		case SOUTH:
			switch(metadata) {
			case 0: return EnumFacing.SOUTH;
			case 1: return EnumFacing.EAST;
			case 2: return EnumFacing.NORTH;
			case 3: return EnumFacing.WEST;
			}
		case EAST:
			switch(metadata) {
			case 0: return EnumFacing.EAST;
			case 1: return EnumFacing.NORTH;
			case 2: return EnumFacing.WEST;
			case 3: return EnumFacing.SOUTH;
			}
		case WEST:
			switch(metadata) {
			case 0: return EnumFacing.WEST;
			case 1: return EnumFacing.SOUTH;
			case 2: return EnumFacing.EAST;
			case 3: return EnumFacing.NORTH;
			}
		}
		return EnumFacing.UP;
	}
}