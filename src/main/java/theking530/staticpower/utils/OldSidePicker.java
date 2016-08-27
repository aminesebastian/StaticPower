package theking530.staticpower.utils;

/**
 * Thanks to mikeemoo and the openmods team for an amazing open source mod.
 */

import java.util.Map;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Maps;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

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
	public static Side fromEnumFacing(EnumFacing dir) {
		switch (dir) {
			case WEST:
				return Side.XNeg;
			case EAST:
				return Side.XPos;
			case DOWN:
				return Side.YNeg;
			case UP:
				return Side.YPos;
			case NORTH:
				return Side.ZNeg;
			case SOUTH:
				return Side.ZPos;
			default:
				break;
		}
		return null;
	}
	public static EnumFacing toEnumFacing(Side side) {
		switch (side) {
			case XNeg:
				return EnumFacing.WEST;
			case XPos:
				return EnumFacing.EAST;
			case YNeg:
				return EnumFacing.DOWN;
			case YPos:
				return EnumFacing.UP;
			case ZNeg:
				return EnumFacing.NORTH;
			case ZPos:
				return EnumFacing.SOUTH;
			default:
				return null;
		}
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
	public static Side getSideFromBlockSide(BlockSide side, EnumFacing facing) {
		if(facing.ordinal() != 2 && facing.ordinal() != 4 && facing.ordinal() != 5) {
	    	switch(side) {
	    	case Bottom : 
	    		return Side.YNeg;
	    	case Top : 
	    		return Side.YPos;
	    	case Back : 
	    		return Side.ZNeg;
	    	case Front : 
	    		return Side.ZPos;
	    	case Left : 
	    		return Side.XNeg;
	    	case Right : 
	    		return Side.XPos;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 2) {
	    	switch(side) {
	    	case Bottom : 
	    		return Side.YNeg;
	    	case Top : 
	    		return Side.YPos;
	    	case Front : 
	    		return Side.ZNeg;
	    	case Back : 
	    		return Side.ZPos;
	    	case Right : 
	    		return Side.XNeg;
	    	case Left : 
	    		return Side.XPos;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 4) {
	    	switch(side) {
	    	case Bottom : 
	    		return Side.YNeg;
	    	case Top : 
	    		return Side.YPos;
	    	case Left : 
	    		return Side.ZNeg;
	    	case Right : 
	    		return Side.ZPos;
	    	case Front : 
	    		return Side.XNeg;
	    	case Back : 
	    		return Side.XPos;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 5) {
	    	switch(side) {
	    	case Bottom : 
	    		return Side.YNeg;
	    	case Top : 
	    		return Side.YPos;
	    	case Right : 
	    		return Side.ZNeg;
	    	case Left : 
	    		return Side.ZPos;
	    	case Back : 
	    		return Side.XNeg;
	    	case Front : 
	    		return Side.XPos;
	    		default :
	    			break;
	    	}
		}
		return null;
	}
	public static int getIntFromBlockSide(BlockSide side, EnumFacing facing) {
		Side coordSide = getSideFromBlockSide(side, facing);
		int sideInt = toSideNumber(coordSide);
		return sideInt;
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