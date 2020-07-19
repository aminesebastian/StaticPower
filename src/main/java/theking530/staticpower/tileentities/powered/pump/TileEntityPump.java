package theking530.staticpower.tileentities.powered.pump;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.FluidContainerComponent;
import theking530.staticpower.tileentities.components.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;

public class TileEntityPump extends TileEntityMachine {
	public static final Logger LOGGER = LogManager.getLogger(TileEntityPump.class);
	public static final int DEFAULT_PUMP_RATE = 40;

	public final InventoryComponent fluidContainerInventory;
	public final FluidTankComponent fluidTankComponent;
	public final MachineProcessingComponent processingComponent;
	private final Queue<BlockPos> positionsToPump;

	public TileEntityPump() {
		super(ModTileEntityTypes.PUMP);

		// Add the tank component.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 8000).setCapabilityExposedModes(MachineSideMode.Output).setCanFill(false));

		// Add the fluid output servo to deliver fluid to adjacent blocks.
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Register components to allow the pump to fill buckets in the GUI.
		registerComponent(fluidContainerInventory = new InventoryComponent("FluidContainerInventory", 2, MachineSideMode.Never));
		registerComponent(new FluidContainerComponent("FluidFillContainerServo", fluidContainerInventory, fluidTankComponent, 0, 1).setMode(FluidContainerInteractionMode.FILL));

		// Regsiter the processing component to handle the pumping.
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PUMP_RATE, this::canProcess, this::canProcess, this::pump, true));

		// Set the default side configuration.
		ioSideConfiguration.setDefaultConfiguration(MachineSideMode.Never, MachineSideMode.Output, MachineSideMode.Output, MachineSideMode.Output, MachineSideMode.Output, MachineSideMode.Output);

		// Disable face interaction.
		DisableFaceInteraction = false;

		// Initialize the positions to pump container.
		positionsToPump = new LinkedList<BlockPos>();
	}

	@Override()
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side != BlockSide.TOP && mode != MachineSideMode.Never) {
			return false;
		}
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output;
	}

	/**
	 * This method returns true so long as we have enough space to pump a block.
	 * 
	 * @return
	 */
	public boolean canProcess() {
		return (fluidTankComponent.getFluidAmount() + FluidAttributes.BUCKET_VOLUME) <= fluidTankComponent.getCapacity();
	}

	/**
	 * Pumps a single block off the queue, or attempts to rebuild the queue if
	 * something has gone wrong.
	 * 
	 * @return
	 */
	public boolean pump() {
		// If we have capacity to pump.
		if ((fluidTankComponent.getFluidAmount() + FluidAttributes.BUCKET_VOLUME) <= fluidTankComponent.getCapacity()) {
			// If the positions to pump is empty, try to start again.
			if (positionsToPump.size() == 0) {
				BlockPos newPos = getInitialPumpBlock();
				if (newPos != null) {
					positionsToPump.add(newPos);
				}
			}

			// If we have a position to pump, attempt to pump it.
			if (positionsToPump.size() > 0) {
				// Get the fluid state at the position to pump.
				BlockPos position = positionsToPump.poll();
				IFluidState fluidState = getWorld().getFluidState(position);

				while (!fluidState.isSource() && !positionsToPump.isEmpty()) {
					position = positionsToPump.poll();
					fluidState = getWorld().getFluidState(position);
				}

				if (position != null) {
					// If the fluid is pumpable, pump it. If not, something has changed drastically,
					// rebuild the queue.
					if (fluidState.getFluid().isSource(fluidState)) {
						// Play the sound.
						getWorld().playSound(null, getPos(), fluidState.getFluid() == Fluids.LAVA ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);

						// Pump the fluid.
						FluidStack pumpedStack = new FluidStack(fluidState.getFluid(), FluidAttributes.BUCKET_VOLUME);
						fluidTankComponent.fill(pumpedStack, FluidAction.EXECUTE);
						getWorld().setBlockState(position, Blocks.AIR.getDefaultState());

						markTileEntityForSynchronization();

						// If this is water, we just stop. No recursion as water is infinite anyway.
						if (pumpedStack.getFluid() == Fluids.WATER) {
							positionsToPump.clear();
							return true;
						}
					}

					// No matter what, search around the pumped block.
					searchAroundPumpedBlock(position);
					// Log the pump queue creation.
					LOGGER.info(String.format("Rebuilt Pump Queue to size: %1$d for Pump at position: %2$s in Dimension: %3$s.", positionsToPump.size(), getPos(), getWorld().getDimension().getType()));
				}
			}
		}

		// Always return true so the machine processing component always proceeds.
		return true;
	}

	private void searchAroundPumpedBlock(BlockPos position) {
		if (positionsToPump.size() >= 100) {
			return;
		}
		// Search on all six sides.
		for (Direction dir : Direction.values()) {
			BlockPos testPos = position.offset(dir);
			IFluidState fluidState = getWorld().getFluidState(testPos);
			if (!fluidState.isEmpty() && !positionsToPump.contains(testPos)) {
				positionsToPump.add(testPos);
			}
		}
	}

	private @Nullable BlockPos getInitialPumpBlock() {
		// Check from the block below the pump all the way down for a fluid state that
		// is not empty. Stop if we hit a solid block.
		for (int i = getPos().getY() - 1; i >= 0; i--) {
			BlockPos samplePos = new BlockPos(getPos().getX(), i, getPos().getZ());

			// If we hit a non fluid block that is not just AIR, stop.
			if (!(getWorld().getBlockState(samplePos).getBlock() instanceof FlowingFluidBlock) && getWorld().getBlockState(samplePos).getBlock() != Blocks.AIR) {
				return null;
			}

			// Search for a fluid block that is not empty.
			IFluidState fluidState = getWorld().getFluidState(samplePos);
			if (!fluidState.isEmpty()) {
				return samplePos;
			}
		}
		return null;
	}

	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		super.serializeSaveNbt(nbt);

		// Serialize the queued positions.
		ListNBT queuedPositions = new ListNBT();
		positionsToPump.forEach(pos -> {
			CompoundNBT posTag = new CompoundNBT();
			posTag.putLong("pos", pos.toLong());
			queuedPositions.add(posTag);
		});
		nbt.put("queued_positions", queuedPositions);

		return nbt;
	}

	public void deserializeSaveNbt(CompoundNBT nbt) {
		super.deserializeSaveNbt(nbt);

		// Clear the queue just in case.
		positionsToPump.clear();

		// Deserialize the queued positions.
		ListNBT queuedPositions = nbt.getList("queued_positions", Constants.NBT.TAG_COMPOUND);
		for (INBT posTag : queuedPositions) {
			CompoundNBT posTagCompound = (CompoundNBT) posTag;
			positionsToPump.add(BlockPos.fromLong(posTagCompound.getLong("pos")));
		}

		LOGGER.info(String.format("Deserialized Pump at position: %1$s with: %2$d queued positions.", getPos(), positionsToPump.size()));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPump(windowId, inventory, this);
	}
}
