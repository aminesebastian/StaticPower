package theking530.staticpower.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;


public class SideUtilities {
	
	public enum BlockSide {
		TOP("Top"),
		BOTTOM("Bottom"),
		FRONT("Front"),
		BACK("Back"),
		RIGHT("Right"),
		LEFT("Left");
		
		private String name;
		
		private BlockSide(String name) {
			this.name = name;
		}
		public String getName() {
			return "side." + name;
		}
		public String getLocalizedName() {
			return I18n.format(getName());
		}
		public BlockSide getOpposite() {
			if(this == TOP){
				return BOTTOM;
			}
			if(this == BOTTOM){
				return TOP;
			}
			if(this == FRONT){
				return BACK;
			}
			if(this == BACK){
				return FRONT;
			}
			if(this == RIGHT){
				return LEFT;
			}
			if(this == LEFT){
				return RIGHT;
			}
			return null;
		}
	}
	/***
	 * @param hitSide = The Side that we want to translate to a BlockSide.
	 * @param machineFacing - The Facing Direction of the block. 
	 * @return We take the Facing Direction of the block and then return the relative blockside versus the requested hitside.
	 */
	public static BlockSide getBlockSide(Direction hitSide, Direction machineFacing) {
		switch(hitSide) {
		case UP: return BlockSide.TOP;
		case DOWN: return BlockSide.BOTTOM;
		case WEST: 
			switch(machineFacing) {
				case NORTH: return BlockSide.RIGHT;
				case SOUTH: return BlockSide.LEFT;
				case EAST:  return BlockSide.BACK;
				case WEST: return BlockSide.FRONT;
				default:
					break;
			}
		case EAST:
			switch(machineFacing) {
				case NORTH:return BlockSide.LEFT;
				case SOUTH:return BlockSide.RIGHT;
				case EAST: return BlockSide.FRONT;
				case WEST:  return BlockSide.BACK;
				default:
					break;
			}
		case NORTH: 
			switch(machineFacing) {
				case NORTH: return BlockSide.FRONT;
				case SOUTH: return BlockSide.BACK;
				case EAST: return BlockSide.RIGHT;
				case WEST:	return BlockSide.LEFT;
				default:
					break;
			}
		case SOUTH:
			switch(machineFacing) {
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
	public static Direction getAdjustedDirection(Direction facing, Direction horizontal) {
		int metadata = horizontal.ordinal();

		switch(facing) {
		case DOWN: return Direction.DOWN;
		case UP: return Direction.UP;
		case NORTH:
			switch(metadata) {
			case 0: return Direction.NORTH;
			case 1: return Direction.WEST;
			case 2: return Direction.SOUTH;
			case 3: return Direction.EAST;
			}
		case SOUTH:
			switch(metadata) {
			case 0: return Direction.SOUTH;
			case 1: return Direction.EAST;
			case 2: return Direction.NORTH;
			case 3: return Direction.WEST;
			}
		case EAST:
			switch(metadata) {
			case 0: return Direction.EAST;
			case 1: return Direction.NORTH;
			case 2: return Direction.WEST;
			case 3: return Direction.SOUTH;
			}
		case WEST:
			switch(metadata) {
			case 0: return Direction.WEST;
			case 1: return Direction.SOUTH;
			case 2: return Direction.EAST;
			case 3: return Direction.NORTH;
			}
		}
		return Direction.UP;
	}
	public static Direction getDirectionFromSide(BlockSide side, Direction facing) {
		if(facing.ordinal() == 2) {
	    	switch(side) {
	    	case BOTTOM : 
	    		return Direction.DOWN;
	    	case TOP : 
	    		return Direction.UP;
	    	case FRONT : 
	    		return Direction.NORTH;
	    	case BACK : 
	    		return Direction.SOUTH;
	    	case RIGHT : 
	    		return Direction.WEST;
	    	case LEFT : 
	    		return Direction.EAST;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 3) {
	    	switch(side) {
	    	case BOTTOM : 
	    		return Direction.DOWN;
	    	case TOP : 
	    		return Direction.UP;
	    	case BACK : 
	    		return Direction.NORTH;
	    	case FRONT : 
	    		return Direction.SOUTH;
	    	case LEFT : 
	    		return Direction.WEST;
	    	case RIGHT : 
	    		return Direction.EAST;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 4) {
	    	switch(side) {
	    	case BOTTOM : 
	    		return Direction.DOWN;
	    	case TOP : 
	    		return Direction.UP;
	    	case LEFT : 
	    		return Direction.NORTH;
	    	case RIGHT : 
	    		return Direction.SOUTH;
	    	case FRONT : 
	    		return Direction.WEST;
	    	case BACK : 
	    		return Direction.EAST;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 5) {
	    	switch(side) {
	    	case BOTTOM : 
	    		return Direction.DOWN;
	    	case TOP : 
	    		return Direction.UP;
	    	case RIGHT : 
	    		return Direction.NORTH;
	    	case LEFT : 
	    		return Direction.SOUTH;
	    	case BACK : 
	    		return Direction.WEST;
	    	case FRONT : 
	    		return Direction.EAST;
	    		default :
	    			break;
	    	}
		}
		return null;
	}
	public static Direction getBlockHorizontal(BlockState block){
		if(block.has(BlockStateProperties.FACING)) {
			return block.get(BlockStateProperties.FACING);		
		}
		return null;
	}
	public static Direction getDirectionFromRenderingInt(int renderingInt, Direction machineFacing) {
		if(machineFacing == Direction.SOUTH) {
			return Direction.values()[renderingInt];
		}else if(machineFacing == Direction.NORTH) {
			if(renderingInt == 4) {
				return Direction.EAST;
			}
			if(renderingInt == 5) {
				return Direction.WEST;
			}
			if(renderingInt == 2) {
				return Direction.SOUTH;
			}
		}else if(machineFacing == Direction.EAST) {
			if(renderingInt == 4) {
				return Direction.SOUTH;
			}
			if(renderingInt == 5) {
				return Direction.NORTH;
			}
			if(renderingInt == 2) {
				return Direction.WEST;
			}
		}else if(machineFacing == Direction.WEST) {
			if(renderingInt == 4) {
				return Direction.NORTH;
			}
			if(renderingInt == 5) {
				return Direction.SOUTH;
			}
			if(renderingInt == 2) {
				return Direction.EAST;
			}
		}
		return Direction.values()[renderingInt];
	}
}
