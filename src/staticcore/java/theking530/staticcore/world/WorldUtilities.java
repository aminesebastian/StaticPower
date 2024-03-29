package theking530.staticcore.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.NonNullList;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.staticcore.utilities.TriFunction;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.utilities.tags.TagUtilities;

public class WorldUtilities {

	public static BlockEntity[] getAdjacentEntities(Level world, BlockPos pos) {
		BlockEntity[] tempArray = new BlockEntity[6];
		for (int i = 0; i < 6; i++) {
			Direction facing = Direction.values()[i];
			tempArray[i] = world.getBlockEntity(pos.relative(facing));
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

	public static BlockPos projectPositionOntoLine(BlockPos position, BlockPos line, Axis plane) {
		switch (plane) {
		case X:
			return new BlockPos(position.getX(), line.getY(), line.getZ());
		case Y:
			return new BlockPos(line.getX(), position.getY(), line.getZ());
		case Z:
			return new BlockPos(line.getX(), line.getY(), position.getZ());
		}
		return position;
	}

	/**
	 * Checks where one block pos is relative to another. This ONLY works for block
	 * positions exactly one block apart.
	 * 
	 * @param source
	 * @param query
	 * @return
	 */
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

	public static Direction getDirectionBetweenBlocks(BlockPos source, BlockPos query) {
		Vector3D thisPos = new Vector3D(source);
		Vector3D linkPos = new Vector3D(query);
		Vector3D pointingVector = linkPos.subtract(thisPos);
		pointingVector.normalize();

		return Direction.getNearest(pointingVector.getX(), pointingVector.getY(), pointingVector.getZ());
	}

	public static void writeBlockPosToNBT(CompoundTag nbt, BlockPos pos, String name) {
		nbt.putInt(name + "X", pos.getX());
		nbt.putInt(name + "Y", pos.getY());
		nbt.putInt(name + "Z", pos.getZ());
	}

	public static BlockPos readBlockPosFromNBT(CompoundTag nbt, String name) {
		return new BlockPos(nbt.getInt(name + "X"), nbt.getInt(name + "Y"), nbt.getInt(name + "Z"));
	}

	public static int getAreaBetweenCorners(BlockPos pos1, BlockPos pos2) {
		BlockPos temp1 = pos2.subtract(pos1);
		BlockPos absPos = new BlockPos(Math.abs(temp1.getX()), Math.max(pos1.getY(), pos2.getY()),
				Math.abs(temp1.getZ()));
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

	public static ItemEntity dropItem(Level worldIn, double x, double y, double z, ItemStack stack, int count) {
		ItemStack droppedStack = stack.copy();
		droppedStack.setCount(count);
		ItemEntity itemEntity = new ItemEntity(worldIn, x + 0.5, y + 0.5, z + 0.5, droppedStack);
		worldIn.addFreshEntity(itemEntity);
		return itemEntity;
	}

	public static ItemEntity dropItem(Level worldIn, Direction direction, double x, double y, double z, ItemStack stack,
			int count) {
		ItemEntity item = null;
		if (direction == Direction.EAST) {
			item = dropItem(worldIn, x, y, z, stack, count);
		} else if (direction == Direction.NORTH) {
			item = dropItem(worldIn, x, y, z, stack, count);
		} else if (direction == Direction.SOUTH) {
			item = dropItem(worldIn, x, y, z, stack, count);
		} else if (direction == Direction.UP) {
			item = dropItem(worldIn, x, y, z, stack, count);
		} else if (direction == Direction.DOWN) {
			item = dropItem(worldIn, x, y, z, stack, count);
		} else if (direction == Direction.WEST) {
			item = dropItem(worldIn, x, y, z, stack, count);
		}

		return item;
	}

	public static ItemEntity dropItem(Level worldIn, BlockPos pos, ItemStack stack, int count) {
		return dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack, count);
	}

	public static ItemEntity dropItem(Level worldIn, BlockPos pos, ItemStack stack) {
		return dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack, stack.getCount());
	}

	public static ItemEntity dropItem(Level worldIn, Direction facing, BlockPos pos, ItemStack stack, int count) {
		return dropItem(worldIn, facing, pos.getX(), pos.getY(), pos.getZ(), stack, count);
	}

	public static void dropExperience(Level worldIn, Direction facing, BlockPos pos, int amount) {
		Vector3D direction = new Vector3D(facing);
		dropExperience(worldIn, new BlockPos(pos.getX() + 0.5f + direction.getX(), pos.getY() + 0.5f + direction.getY(),
				pos.getZ() + 0.5f + direction.getZ()), amount);
	}

	public static void dropExperience(Level worldIn, BlockPos pos, int amount) {
		ExperienceOrb orb = new ExperienceOrb(worldIn, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, amount);

		// Set a random X and Z velocity.
		float random = worldIn.getRandom().nextFloat();
		random *= 2;
		random -= 1;
		random *= 0.02;
		orb.setDeltaMovement(random, 0.25, random);

		// Add the entity orb to the world.
		worldIn.addFreshEntity(orb);
	}

	public static ChunkPos getChunkPosFromBlockPos(BlockPos pos) {
		int chunkX = SectionPos.blockToSectionCoord(pos.getX());
		int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
		return new ChunkPos(chunkX, chunkZ);
	}

	/**
	 * Gets all the drops for the provided block. Returns an empty list if called on
	 * the client.
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public static List<ItemStack> getBlockDrops(Level world, BlockPos pos) {
		if (world.isClientSide()) {
			throw new RuntimeException(
					"The #getBlockDrops method was excuted on the client. This should only be excuted on the server.");
		}
		if (!world.isClientSide()) {
			NonNullList<ItemStack> output = NonNullList.create();
			output.addAll(Block.getDrops(world.getBlockState(pos), (ServerLevel) world, pos, null));
			return output;
		}
		return Collections.emptyList();
	}

	public static List<ItemStack> getBlockDrops(Level world, BlockPos pos, BlockState state) {
		if (!world.isClientSide()) {
			NonNullList<ItemStack> output = NonNullList.create();
			output.addAll(Block.getDrops(state, (ServerLevel) world, pos, null));
			return output;
		}
		return Collections.emptyList();
	}

	public static boolean tryPlaceFluid(FluidStack fluid, @Nullable Player player, Level world, BlockPos pos,
			@Nullable BlockHitResult hitResult) {
		if (fluid.getAmount() < 1000) {
			return false;
		}

		Fluid content = fluid.getFluid();
		BlockState blockstate = world.getBlockState(pos);
		Block block = blockstate.getBlock();
		Material material = blockstate.getMaterial();
		boolean flag = blockstate.canBeReplaced(content);
		boolean flag1 = blockstate.isAir() || flag || block instanceof LiquidBlockContainer
				&& ((LiquidBlockContainer) block).canPlaceLiquid(world, pos, blockstate, content);
		if (!flag1) {
			return hitResult != null && tryPlaceFluid(fluid, player, world,
					hitResult.getBlockPos().relative(hitResult.getDirection()), (BlockHitResult) null);
		} else if (world.dimensionType().ultraWarm() && TagUtilities.matches(FluidTags.WATER, content)) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			world.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
					2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

			for (int l = 0; l < 8; ++l) {
				world.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(),
						(double) k + Math.random(), 0.0D, 0.0D, 0.0D);
			}
			return true;
		} else if (block instanceof LiquidBlockContainer
				&& ((LiquidBlockContainer) block).canPlaceLiquid(world, pos, blockstate, content)) {
			((LiquidBlockContainer) block).placeLiquid(world, pos, blockstate,
					((FlowingFluid) content).getSource(false));
			playBucketEmptySound(fluid, player, world, pos);
			return true;
		} else {
			if (!world.isClientSide && flag && !material.isLiquid()) {
				world.destroyBlock(pos, true);
			}

			if (!world.setBlock(pos, content.defaultFluidState().createLegacyBlock(), 11)
					&& !blockstate.getFluidState().isSource()) {
				return false;
			} else if (blockstate.isAir()) {
				playBucketEmptySound(fluid, player, world, pos);
				return true;
			}
		}
		return false;
	}

	public static void playBucketFillSound(FluidStack incomingFluid, @Nullable Player pPlayer, LevelAccessor pLevel,
			BlockPos pPos) {
		Fluid content = incomingFluid.getFluid();
		SoundEvent soundevent = content.getFluidType().getSound(SoundActions.BUCKET_FILL);
		if (soundevent == null)
			soundevent = TagUtilities.matches(FluidTags.LAVA, content) ? SoundEvents.BUCKET_FILL_LAVA
					: SoundEvents.BUCKET_EMPTY_LAVA;
		pLevel.playSound(pPlayer, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
		pLevel.gameEvent(pPlayer, GameEvent.FLUID_PLACE, pPos);
	}

	public static void playBucketEmptySound(FluidStack outgoingFluid, @Nullable Player pPlayer, LevelAccessor pLevel,
			BlockPos pPos) {
		Fluid content = outgoingFluid.getFluid();
		SoundEvent soundevent = content.getFluidType().getSound(SoundActions.BUCKET_EMPTY);
		if (soundevent == null)
			soundevent = TagUtilities.matches(FluidTags.LAVA, content) ? SoundEvents.BUCKET_EMPTY_LAVA
					: SoundEvents.BUCKET_EMPTY;
		pLevel.playSound(pPlayer, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
		pLevel.gameEvent(pPlayer, GameEvent.FLUID_PLACE, pPos);
	}

	public static boolean canBlockContainFluid(IFluidHandler fluidHandler, Level worldIn, BlockPos posIn,
			BlockState blockstate) {
		Fluid content = fluidHandler.getFluidInTank(0).getFluid();
		return blockstate.getBlock() instanceof LiquidBlockContainer
				&& ((LiquidBlockContainer) blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, content);
	}

	public static <T> Map<BlockPos, T> bfsTraverseWorld(Level level, BlockPos startingPos,
			TriFunction<BlockPos, BlockState, BlockEntity, T> shouldCapture) {
		Map<BlockPos, T> output = new HashMap<BlockPos, T>();

		Set<BlockPos> visited = new HashSet<>();
		visited.add(startingPos);

		Queue<BlockPos> toCheck = new LinkedList<>();
		for (Direction dir : Direction.values()) {
			toCheck.add(startingPos.relative(dir));
		}

		while (!toCheck.isEmpty()) {
			BlockPos target = toCheck.remove();
			BlockState state = level.getBlockState(target);
			BlockEntity be = level.getBlockEntity(target);
			visited.add(target);

			T captured = shouldCapture.apply(target, state, be);
			if (captured != null) {
				output.put(target, captured);
				for (Direction dir : Direction.values()) {
					BlockPos newTarget = target.relative(dir);
					if (!visited.contains(newTarget)) {
						toCheck.add(newTarget);
					}
				}
			}
		}

		return output;
	}

	public static boolean isBlockPosInLoadedChunk(Level level, BlockPos pos) {
		return level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
	}
}
