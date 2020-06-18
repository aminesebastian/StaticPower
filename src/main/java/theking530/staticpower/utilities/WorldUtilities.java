package theking530.staticpower.utilities;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldUtilities {

	public static TileEntity[] getAdjacentEntities(World world, BlockPos pos) {
		TileEntity[] tempArray = new TileEntity[6];
		for (int i = 0; i < 6; i++) {
			Direction facing = Direction.values()[i];
			tempArray[i] = world.getTileEntity(pos.offset(facing));
		}
		return tempArray;
	}

	public static Direction getXFacingFromPos(BlockPos pos1, BlockPos pos2) {
		int direction = pos1.getX() - pos2.getX();
		if (direction > 1) {
			return Direction.EAST;
		} else {
			return Direction.WEST;
		}
	}

	public static Direction getYFacingFromPos(BlockPos pos1, BlockPos pos2) {
		int direction = pos1.getY() - pos2.getY();
		if (direction > 1) {
			return Direction.UP;
		} else {
			return Direction.DOWN;
		}
	}

	public static Direction getZFacingFromPos(BlockPos pos1, BlockPos pos2) {
		int direction = pos1.getZ() - pos2.getZ();
		if (direction > 1) {
			return Direction.NORTH;
		} else {
			return Direction.SOUTH;
		}
	}

	public static Direction getFacingFromPos(BlockPos source, BlockPos query) {
		if (source != null && query != null) {
			if (source.getY() > query.getY()) {
				return Direction.DOWN;
			}
			if (source.getY() < query.getY()) {
				return Direction.UP;
			}
			if (source.getZ() > query.getZ()) {
				return Direction.NORTH;
			}
			if (source.getZ() < query.getZ()) {
				return Direction.SOUTH;
			}
			if (source.getX() > query.getX()) {
				return Direction.WEST;
			}
			if (source.getX() < query.getX()) {
				return Direction.EAST;
			}
		}
		return Direction.UP;
	}

	public static void writeBlockPosToNBT(CompoundNBT nbt, BlockPos pos, String name) {
		nbt.putInt(name + "X", pos.getX());
		nbt.putInt(name + "Y", pos.getY());
		nbt.putInt(name + "Z", pos.getZ());
	}

	public static BlockPos readBlockPosFromNBT(CompoundNBT nbt, String name) {
		return new BlockPos(nbt.getInt(name + "X"), nbt.getInt(name + "Y"), nbt.getInt(name + "Z"));
	}

	public static int getAreaBetweenCorners(BlockPos pos1, BlockPos pos2) {
		BlockPos temp1 = pos2.subtract(pos1);
		BlockPos absPos = new BlockPos(Math.abs(temp1.getX()), Math.max(pos1.getY(), pos2.getY()), Math.abs(temp1.getZ()));
		return absPos.getX() * absPos.getY() * absPos.getZ();
	}

	public static String formatBlockPos(BlockPos pos) {
		return pos.toString().substring(9, pos.toString().length() - 1);
	}

	public static BlockPos blockPosFromIntArray(int[] ints) {
		if (ints != null && ints.length > 2) {
			return new BlockPos(ints[0], ints[1], ints[2]);
		}
		return null;
	}

	public static ItemEntity dropItem(World worldIn, double x, double y, double z, ItemStack stack, int count) {
		ItemStack droppedStack = stack.copy();
		droppedStack.setCount(count);
		ItemEntity itemEntity = new ItemEntity(worldIn, x, y, z, droppedStack);
		worldIn.addEntity(itemEntity);
		return itemEntity;
	}

	public static ItemEntity dropItem(World worldIn, Direction direction, double x, double y, double z, ItemStack stack, int count) {
		ItemEntity item = null;
		if (direction == Direction.EAST) {
			item = dropItem(worldIn, x + 0.5f, y + 0.5f, z + 0.5f, stack, count);
		} else if (direction == Direction.NORTH) {
			item = dropItem(worldIn, x + 0.5f, y + 0.5f, z - 0.5f, stack, count);
		} else if (direction == Direction.SOUTH) {
			item = dropItem(worldIn, x + 0.5f, y + 0.5f, z + 0.5f, stack, count);
		} else if (direction == Direction.UP) {
			item = dropItem(worldIn, x + 0.5f, y + 0.5f, z + 0.5f, stack, count);
		} else if (direction == Direction.DOWN) {
			item = dropItem(worldIn, x + 0.5f, y - 0.5f, z + 0.5f, stack, count);
		} else if (direction == Direction.WEST) {
			item = dropItem(worldIn, x - 0.5f, y + 0.5f, z + 0.5f, stack, count);
		}

		// Don't tell me why, but this helps make it NOT launch...go figure.
		// item.setVelocity(100.0f, 0.0f, 0.0f);
		return item;
	}

	public static ItemEntity dropItem(World worldIn, BlockPos pos, ItemStack stack, int count) {
		return dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack, count);
	}

	public static ItemEntity dropItem(World worldIn, BlockPos pos, ItemStack stack) {
		return dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack, stack.getCount());
	}

	public static ItemEntity dropItem(World worldIn, Direction facing, BlockPos pos, ItemStack stack, int count) {
		return dropItem(worldIn, facing, pos.getX(), pos.getY(), pos.getZ(), stack, count);
	}
}
