package theking530.staticpower.tileentity;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;

public class SidePicker {
	public enum Side {
		XNeg, XPos, YNeg, YPos, ZNeg, ZPos;

		public static Side fromForgeDirection(Direction dir) {
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

		public Direction toForgeDirection() {
			switch (this) {
			case XNeg:
				return Direction.WEST;
			case XPos:
				return Direction.EAST;
			case YNeg:
				return Direction.DOWN;
			case YPos:
				return Direction.UP;
			case ZNeg:
				return Direction.NORTH;
			case ZPos:
				return Direction.SOUTH;
			default:
				throw new IllegalArgumentException(toString());
			}
		}
	}

	public static class HitCoord {
		public final Side side;
		public final Vec3d coord;

		public HitCoord(Side side, Vec3d coord) {
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
		negX = negY = negZ = -halfSize;
		posX = posY = posZ = +halfSize;
	}

	private Vec3d calculateXPoint(Vec3d near, Vec3d diff, double x) {
		double p = (x - near.x) / diff.x;

		double y = near.y + diff.y * p;
		double z = near.z + diff.z * p;

		if (negY <= y && y <= posY && negZ <= z && z <= posZ)
			return new Vec3d(x, y, z);

		return null;
	}

	private Vec3d calculateYPoint(Vec3d near, Vec3d diff, double y) {
		double p = (y - near.y) / diff.y;

		double x = near.x + diff.x * p;
		double z = near.z + diff.z * p;

		if (negX <= x && x <= posX && negZ <= z && z <= posZ)
			return new Vec3d(x, y, z);

		return null;
	}

	private Vec3d calculateZPoint(Vec3d near, Vec3d diff, double z) {
		double p = (z - near.z) / diff.z;

		double x = near.x + diff.x * p;
		double y = near.y + diff.y * p;

		if (negX <= x && x <= posX && negY <= y && y <= posY)
			return new Vec3d(x, y, z);

		return null;
	}

	private static void addPoint(Map<Side, Vec3d> map, Side side, Vec3d value) {
		if (value != null)
			map.put(side, value);
	}

	private Map<Side, Vec3d> calculateHitPoints(Vec3d near, Vec3d far) {
		Vec3d diff = far.subtract(near);

		Map<Side, Vec3d> result = Maps.newEnumMap(Side.class);
		addPoint(result, Side.XNeg, calculateXPoint(near, diff, negX));
		addPoint(result, Side.XPos, calculateXPoint(near, diff, posX));

		addPoint(result, Side.YNeg, calculateYPoint(near, diff, negY));
		addPoint(result, Side.YPos, calculateYPoint(near, diff, posY));

		addPoint(result, Side.ZNeg, calculateZPoint(near, diff, negZ));
		addPoint(result, Side.ZPos, calculateZPoint(near, diff, posZ));
		return result;
	}
}