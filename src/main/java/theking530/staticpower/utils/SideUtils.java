package theking530.staticpower.utils;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.utils.OldSidePicker.BlockSide;
import theking530.staticpower.utils.SidePicker.Side;


public class SideUtils {
	
	public enum BlockSide {
		TOP,
		BOTTOM,
		FRONT,
		BACK,
		RIGHT,
		LEFT;
	}
	public static BlockSide getBlockSide(EnumFacing hitSide, EnumFacing metadata) {
		switch(hitSide) {
		case UP: return BlockSide.TOP;
		case DOWN: return BlockSide.BOTTOM;
		case WEST: 
			switch(metadata) {
				case NORTH: return BlockSide.RIGHT;
				case SOUTH: return BlockSide.LEFT;
				case EAST:  return BlockSide.BACK;
				case WEST: return BlockSide.FRONT;
				default:
					break;
			}
		case EAST:
			switch(metadata) {
				case NORTH:return BlockSide.LEFT;
				case SOUTH:return BlockSide.RIGHT;
				case EAST: return BlockSide.FRONT;
				case WEST:  return BlockSide.BACK;
				default:
					break;
			}
		case NORTH: 
			switch(metadata) {
				case NORTH: return BlockSide.FRONT;
				case SOUTH: return BlockSide.BACK;
				case EAST: return BlockSide.RIGHT;
				case WEST:	return BlockSide.LEFT;
				default:
					break;
			}
		case SOUTH:
			switch(metadata) {
				case NORTH:  return BlockSide.BACK;
				case SOUTH: return BlockSide.FRONT;
				case EAST: return BlockSide.LEFT;
				case WEST: return BlockSide.RIGHT;
				default:
					break;
			}
		}
		return null;
	}
	public static EnumFacing toEnumFacing(Side sIDE) {
		switch (sIDE) {
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
	public static EnumFacing getAdjustedEnumFacing(EnumFacing facing, EnumFacing horizontal) {
		int metadata = horizontal.ordinal() - 2;
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
	public static EnumFacing getEnumFacingFromSide(BlockSide side, EnumFacing facing) {
		if(facing.ordinal() != 2 && facing.ordinal() != 4 && facing.ordinal() != 5) {
	    	switch(side) {
	    	case BOTTOM : 
	    		return EnumFacing.DOWN;
	    	case TOP : 
	    		return EnumFacing.UP;
	    	case BACK : 
	    		return EnumFacing.NORTH;
	    	case FRONT : 
	    		return EnumFacing.SOUTH;
	    	case LEFT : 
	    		return EnumFacing.WEST;
	    	case RIGHT : 
	    		return EnumFacing.EAST;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 2) {
	    	switch(side) {
	    	case BOTTOM : 
	    		return EnumFacing.DOWN;
	    	case TOP : 
	    		return EnumFacing.UP;
	    	case FRONT : 
	    		return EnumFacing.NORTH;
	    	case BACK : 
	    		return EnumFacing.SOUTH;
	    	case RIGHT : 
	    		return EnumFacing.WEST;
	    	case LEFT : 
	    		return EnumFacing.EAST;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 4) {
	    	switch(side) {
	    	case BOTTOM : 
	    		return EnumFacing.DOWN;
	    	case TOP : 
	    		return EnumFacing.UP;
	    	case LEFT : 
	    		return EnumFacing.NORTH;
	    	case RIGHT : 
	    		return EnumFacing.SOUTH;
	    	case FRONT : 
	    		return EnumFacing.WEST;
	    	case BACK : 
	    		return EnumFacing.EAST;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 5) {
	    	switch(side) {
	    	case BOTTOM : 
	    		return EnumFacing.DOWN;
	    	case TOP : 
	    		return EnumFacing.UP;
	    	case RIGHT : 
	    		return EnumFacing.NORTH;
	    	case LEFT : 
	    		return EnumFacing.SOUTH;
	    	case BACK : 
	    		return EnumFacing.WEST;
	    	case FRONT : 
	    		return EnumFacing.EAST;
	    		default :
	    			break;
	    	}
		}
		return null;
	}
	public static EnumFacing getBlockHorizontal(IBlockState block){
		return block.getValue(BlockHorizontal.FACING);
	}
}
