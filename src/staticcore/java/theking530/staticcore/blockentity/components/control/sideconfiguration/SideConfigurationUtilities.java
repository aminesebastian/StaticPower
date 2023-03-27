package theking530.staticcore.blockentity.components.control.sideconfiguration;

import com.mojang.math.Vector3f;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import theking530.staticcore.StaticCore;

public class SideConfigurationUtilities {

	public enum BlockSide {
		TOP("Top", Direction.AxisDirection.POSITIVE), BOTTOM("Bottom", Direction.AxisDirection.NEGATIVE), FRONT("Front", Direction.AxisDirection.POSITIVE),
		BACK("Back", Direction.AxisDirection.NEGATIVE), RIGHT("Right", Direction.AxisDirection.POSITIVE), LEFT("Left", Direction.AxisDirection.NEGATIVE);

		private String name;
		private Direction.AxisDirection sign;

		private BlockSide(String name, Direction.AxisDirection sign) {
			this.name = name;
			this.sign = sign;
		}

		public MutableComponent getName() {
			return Component.translatable("gui.staticcore.side." + name.toLowerCase());
		}

		public Direction.AxisDirection getSign() {
			return sign;
		}

		public BlockSide getOpposite() {
			if (this == TOP) {
				return BOTTOM;
			}
			if (this == BOTTOM) {
				return TOP;
			}
			if (this == FRONT) {
				return BACK;
			}
			if (this == BACK) {
				return FRONT;
			}
			if (this == RIGHT) {
				return LEFT;
			}
			if (this == LEFT) {
				return RIGHT;
			}
			return null;
		}
	}

	/***
	 * @param hitSide       = The Side that we want to translate to a BlockSide.
	 * @param machineFacing - The Facing Direction of the block.
	 * @return We take the Facing Direction of the block and then return the
	 *         relative blockside versus the requested hitside.
	 */
	public static BlockSide getBlockSide(Direction hitSide, Direction machineFacing) {
		if (machineFacing == Direction.UP) {
			switch (hitSide) {
			case UP:
				return BlockSide.FRONT;
			case DOWN:
				return BlockSide.BACK;
			case EAST:
				return BlockSide.RIGHT;
			case NORTH:
				return BlockSide.TOP;
			case SOUTH:
				return BlockSide.BOTTOM;
			case WEST:
				return BlockSide.LEFT;
			}
		} else if (machineFacing == Direction.DOWN) {
			switch (hitSide) {
			case UP:
				return BlockSide.BACK;
			case DOWN:
				return BlockSide.FRONT;
			case EAST:
				return BlockSide.LEFT;
			case NORTH:
				return BlockSide.BOTTOM;
			case SOUTH:
				return BlockSide.TOP;
			case WEST:
				return BlockSide.RIGHT;
			}
		}

		Vector3f facingNormal = new Vector3f(machineFacing.getNormal().getX(), machineFacing.getNormal().getY(), machineFacing.getNormal().getZ());
		Vector3f hitNormal = new Vector3f(hitSide.getNormal().getX(), hitSide.getNormal().getY(), hitSide.getNormal().getZ());
		double angle = Math.toDegrees(Math.acos(hitNormal.dot(facingNormal)));
		Vector3f axis = hitNormal.copy();
		axis.cross(facingNormal);
		axis.normalize();

		if (hitSide == Direction.UP) {
			return BlockSide.TOP;
		} else if (hitSide == Direction.DOWN) {
			return BlockSide.BOTTOM;
		} else if (angle == 0) {
			return BlockSide.FRONT;
		} else if (angle == 180) {
			return BlockSide.BACK;
		} else if (axis.y() > 0.0f) {
			return BlockSide.LEFT;
		} else if (axis.y() < 0.0f) {
			return BlockSide.RIGHT;
		}

		StaticCore.LOGGER.error(String.format("Encountered null BlockSide given %1$s hitSide and %2$s machineFacing.", hitSide, machineFacing));
		return BlockSide.FRONT;
	}

	public static Direction getDirectionFromSide(BlockSide side, Direction facing) {
		if (facing.ordinal() == 2) {
			switch (side) {
			case BOTTOM:
				return Direction.DOWN;
			case TOP:
				return Direction.UP;
			case FRONT:
				return Direction.NORTH;
			case BACK:
				return Direction.SOUTH;
			case RIGHT:
				return Direction.WEST;
			case LEFT:
				return Direction.EAST;
			default:
				break;
			}
		}
		if (facing.ordinal() == 3) {
			switch (side) {
			case BOTTOM:
				return Direction.DOWN;
			case TOP:
				return Direction.UP;
			case BACK:
				return Direction.NORTH;
			case FRONT:
				return Direction.SOUTH;
			case LEFT:
				return Direction.WEST;
			case RIGHT:
				return Direction.EAST;
			default:
				break;
			}
		}
		if (facing.ordinal() == 4) {
			switch (side) {
			case BOTTOM:
				return Direction.DOWN;
			case TOP:
				return Direction.UP;
			case LEFT:
				return Direction.NORTH;
			case RIGHT:
				return Direction.SOUTH;
			case FRONT:
				return Direction.WEST;
			case BACK:
				return Direction.EAST;
			default:
				break;
			}
		}
		if (facing.ordinal() == 5) {
			switch (side) {
			case BOTTOM:
				return Direction.DOWN;
			case TOP:
				return Direction.UP;
			case RIGHT:
				return Direction.NORTH;
			case LEFT:
				return Direction.SOUTH;
			case BACK:
				return Direction.WEST;
			case FRONT:
				return Direction.EAST;
			default:
				break;
			}
		}
		return null;
	}
}
