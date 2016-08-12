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

public class SidePicker {

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
	public static Side fromForgeDirection(EnumFacing dir) {
		switch (dir) {
			case WEST:
				return XNeg;
			case EAST:
				return XPos;
			case DOWN:
				return YNeg;
			case UP:
				return YPos;
			case NORTH:
				return ZNeg;
			case SOUTH:
				return ZPos;
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
	public static Side getSideFromBlockSide(BlockSide side, EnumFacing facing) {
		if(facing.ordinal() != 2 && facing.ordinal() != 4 && facing.ordinal() != 5) {
	    	switch(side) {
	    	case Bottom : 
	    		return YNeg;
	    	case Top : 
	    		return YPos;
	    	case Back : 
	    		return ZNeg;
	    	case Front : 
	    		return ZPos;
	    	case Left : 
	    		return XNeg;
	    	case Right : 
	    		return XPos;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 2) {
	    	switch(side) {
	    	case Bottom : 
	    		return YNeg;
	    	case Top : 
	    		return YPos;
	    	case Front : 
	    		return ZNeg;
	    	case Back : 
	    		return ZPos;
	    	case Right : 
	    		return XNeg;
	    	case Left : 
	    		return XPos;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 4) {
	    	switch(side) {
	    	case Bottom : 
	    		return YNeg;
	    	case Top : 
	    		return YPos;
	    	case Left : 
	    		return ZNeg;
	    	case Right : 
	    		return ZPos;
	    	case Front : 
	    		return XNeg;
	    	case Back : 
	    		return XPos;
	    		default :
	    			break;
	    	}
		}
		if(facing.ordinal() == 5) {
	    	switch(side) {
	    	case Bottom : 
	    		return YNeg;
	    	case Top : 
	    		return YPos;
	    	case Right : 
	    		return ZNeg;
	    	case Left : 
	    		return ZPos;
	    	case Back : 
	    		return XNeg;
	    	case Front : 
	    		return XPos;
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
	}
	
	public static class HitCoord {
		public final Side side;
		public final BlockPos coord;
	
		public HitCoord(Side side, BlockPos coord) {
			this.side = side;
			this.coord = coord;
		}
	}
	
	private final double negX, negY, negZ;
	private final double posX, posY, posZ;
	
	public SidePicker(double negX, double negY, double negZ, double posX, double posY, double posZ) {
		this.negX = negX;
		this.negY = negY;
		this.negZ = negZ;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}
	
	public SidePicker(double halfSize) {
		negX = negY = negZ = -0.2*halfSize;
		posX = posY = posZ = +halfSize;
	}
	
	private static BlockPos getMouseVector(float z) {
		return projectionHelper.unproject(Mouse.getX(), Mouse.getY(), z);
	}
	
	private BlockPos calculateXPoint(BlockPos near, BlockPos diff, double x) {
		double p = (x - near.getX()) / diff.getX();
	
		double y = near.getY() + diff.getY() * p;
		double z = near.getZ() + diff.getZ() * p;
	
		if (negY <= y && y <= posY && negZ <= z && z <= posZ) return new BlockPos(x, y, z);
	
		return null;
	}
	
	private BlockPos calculateYPoint(BlockPos near, BlockPos diff, double y) {
		double p = (y - near.getY()) / diff.getY();
	
		double x = near.getX() + diff.getX() * p;
		double z = near.getZ() + diff.getZ() * p;
	
		if (negX <= x && x <= posX && negZ <= z && z <= posZ) return new BlockPos(x, y, z);
	
		return null;
	}
	
	private BlockPos calculateZPoint(BlockPos near, BlockPos diff, double z) {
		double p = (z - near.getZ()) / diff.getZ();
	
		double x = near.getX() + diff.getX() * p;
		double y = near.getY() + diff.getY() * p;
	
		if (negX <= x && x <= posX && negY <= y && y <= posY) return new BlockPos(x, y, z);
	
		return null;
	}
	
	private static void addPoint(Map<Side, BlockPos> map, Side side, BlockPos value) {
		if (value != null) map.put(side, value);
	}
	
	private Map<Side, BlockPos> calculateHitPoints(BlockPos near, BlockPos far) {
		BlockPos diff = far.subtract(near);
	
		Map<Side, BlockPos> result = Maps.newEnumMap(Side.class);
		addPoint(result, Side.XNeg, calculateXPoint(near, diff, negX));
		addPoint(result, Side.XPos, calculateXPoint(near, diff, posX));
	
		addPoint(result, Side.YNeg, calculateYPoint(near, diff, negY));
		addPoint(result, Side.YPos, calculateYPoint(near, diff, posY));
	
		addPoint(result, Side.ZNeg, calculateZPoint(near, diff, negZ));
		addPoint(result, Side.ZPos, calculateZPoint(near, diff, posZ));
		return result;
	}
	
	public Map<Side, BlockPos> calculateMouseHits() {
		projectionHelper.updateMatrices();
		BlockPos near = getMouseVector(0);
		BlockPos far = getMouseVector(1);
	
		return calculateHitPoints(near, far);
	}
	
	public HitCoord getNearestHit() {
		projectionHelper.updateMatrices();
		BlockPos near = getMouseVector(0);
		BlockPos far = getMouseVector(1);
	
		Map<Side, BlockPos> hits = calculateHitPoints(near, far);
	
		if (hits.isEmpty()) return null;
	
		Side minSide = null;
		double minDist = Double.MAX_VALUE;
	
		// yeah, I know there are two entries max, but... meh
		for (Map.Entry<Side, BlockPos> e : hits.entrySet()) {
			BlockPos temp = e.getValue().subtract(near);
			double dist = e.getValue().getDistance(0, 0, 0);
			if (dist < minDist) {
				minDist = dist;
				minSide = e.getKey();
			}
		}
	
		if (minSide == null) return null; // !?
	
		return new HitCoord(minSide, hits.get(minSide));
	}

}