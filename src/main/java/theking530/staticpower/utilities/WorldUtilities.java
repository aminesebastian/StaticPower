package theking530.staticpower.utilities;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;

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

	/**
	 * Attempts to pick up a fluid in the world and put it in an empty container
	 * item.
	 *
	 * @param emptyContainer The empty container to fill. Will not be modified
	 *                       directly, if modifications are necessary a modified
	 *                       copy is returned in the result.
	 * @param playerIn       The player filling the container. Optional.
	 * @param worldIn        The world the fluid is in.
	 * @param pos            The position of the fluid in the world.
	 * @param side           The side of the fluid that is being drained.
	 * @return a {@link FluidActionResult} holding the result and the resulting
	 *         container.
	 */
	@Nonnull
	public static FluidActionResult tryPickUpFluid(@Nonnull ItemStack container, @Nullable Player playerIn, Level worldIn, BlockPos pos, Direction side) {
		// If any required inputs are null or empty, return fail.
		if (container.isEmpty() || worldIn == null || pos == null) {
			return FluidActionResult.FAILURE;
		}

		// Get the fluid state. If it is not a source or is empty, return a failure.
		FluidState state = worldIn.getFluidState(pos);
		if (state.isEmpty() || !state.isSource()) {
			return FluidActionResult.FAILURE;
		}

		// Get the fluid handler for the item. Return fail if it does not exist.
		IFluidHandlerItem handler = FluidUtil.getFluidHandler(container).orElse(null);
		if (handler == null) {
			return FluidActionResult.FAILURE;
		}

		// Get the stack we want to fill into the container.
		FluidStack fillableStack = new FluidStack(state.getType(), FluidAttributes.BUCKET_VOLUME);

		// Simulate a fill. If it is not the total fluid stack, return a fail.
		int filledAmount = handler.fill(fillableStack, FluidAction.SIMULATE);
		if (filledAmount != fillableStack.getAmount()) {
			return FluidActionResult.FAILURE;
		}

		// Execute the fill. Remove the fluid from the world.
		handler.fill(fillableStack, FluidAction.EXECUTE);
		worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 1 | 2);

		// If the player is not null, play the pickup sound.
		if (playerIn != null) {
			SoundEvent soundevent = fillableStack.getFluid().getAttributes().getFillSound(fillableStack);
			if (soundevent == null) {
				soundevent = fillableStack.getFluid().is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
			}
			playerIn.level.playSound(null, playerIn.getX(), playerIn.getY() + 0.5, playerIn.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
		}

		return new FluidActionResult(container);
	}

	/**
	 * Tries to place a fluid resource into the world as a block and drains the
	 * fluidSource. Makes a fluid emptying or vaporization sound when successful.
	 * Honors the amount of fluid contained by the used container. Checks if
	 * water-like fluids should vaporize like in the nether.
	 *
	 * Modeled after
	 * {@link ItemBucket#tryPlaceContainedLiquid(PlayerEntity, World, BlockPos)}
	 *
	 * @param player      Player who places the fluid. May be null for blocks like
	 *                    dispensers.
	 * @param world       World to place the fluid in
	 * @param hand
	 * @param pos         The position in the world to place the fluid block
	 * @param fluidSource The fluid source holding the fluidStack to place
	 * @param resource    The fluidStack to place.
	 * @return true if the placement was successful, false otherwise
	 */
	public static boolean tryPlaceFluid(@Nullable Player player, Level world, InteractionHand hand, BlockPos pos, IFluidHandler fluidSource, FluidStack resource) {
		if (world == null || pos == null) {
			return false;
		}

		Fluid fluid = resource.getFluid();
		if (fluid == null || !fluid.getAttributes().canBePlacedInWorld(world, pos, resource)) {
			return false;
		}

		if (fluidSource.drain(resource, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
			return false;
		}

		BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(player, hand, new BlockHitResult(Vec3.ZERO, Direction.UP, pos, false))); // TODO: This nreds proper
																																									// context...
		// check that we can place the fluid at the destination
		BlockState destBlockState = world.getBlockState(pos);
		Material destMaterial = destBlockState.getMaterial();
		boolean isDestNonSolid = !destMaterial.isSolid();
		boolean isDestReplaceable = destBlockState.canBeReplaced(context);
		if (!world.isEmptyBlock(pos) && !isDestNonSolid && !isDestReplaceable) {
			return false; // Non-air, solid, unreplacable block. We can't put fluid here.
		}

		if (world.dimensionType().ultraWarm() && fluid.getAttributes().doesVaporize(world, pos, resource)) {
			FluidStack result = fluidSource.drain(resource, IFluidHandler.FluidAction.EXECUTE);
			if (!result.isEmpty()) {
				result.getFluid().getAttributes().vaporize(player, world, pos, result);
				return true;
			}
		} else {
			// This fluid handler places the fluid block when filled
			BlockState state = fluid.getAttributes().getBlock(world, pos, fluid.defaultFluidState());
			IFluidHandler handler = new BlockWrapper(state, world, pos);
			FluidStack result = FluidUtil.tryFluidTransfer(handler, fluidSource, resource, true);
			if (!result.isEmpty()) {
				if (player != null) {
					SoundEvent soundevent = result.getFluid().getAttributes().getEmptySound(result);
					if (soundevent == null) {
						soundevent = result.getFluid().is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
					}
					player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
				return true;
			}
		}
		return false;
	}

	public static ItemEntity dropItem(Level worldIn, double x, double y, double z, ItemStack stack, int count) {
		ItemStack droppedStack = stack.copy();
		droppedStack.setCount(count);
		ItemEntity itemEntity = new ItemEntity(worldIn, x + 0.5, y + 0.5, z + 0.5, droppedStack);
		worldIn.addFreshEntity(itemEntity);
		return itemEntity;
	}

	public static ItemEntity dropItem(Level worldIn, Direction direction, double x, double y, double z, ItemStack stack, int count) {
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

	/**
	 * Gets all the drops for the provided block. Returns an empty list if called on
	 * the client.
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public static List<ItemStack> getBlockDrops(Level world, BlockPos pos) {
		if (world.isClientSide) {
			throw new RuntimeException("The #getBlockDrops method was excuted on the client. This should only be excuted on the server.");
		}
		if (!world.isClientSide) {
			NonNullList<ItemStack> output = NonNullList.create();
			output.addAll(Block.getDrops(world.getBlockState(pos), (ServerLevel) world, pos, null));
			return output;
		}
		return Collections.emptyList();
	}
}
