package theking530.staticcore.blockentity.components.items;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.math.SDMath;

public class FluidContainerInventoryComponent extends InventoryComponent {
	public enum FluidContainerInteractionMode {
		/**
		 * Fills the provided container with the fluid in the fluid handler.
		 */
		FILL,
		/**
		 * Drains the provided container and fills the fluid handler with the drained
		 * fluid.
		 */
		DRAIN;

		public FluidContainerInteractionMode getInverseMode() {
			if (this == DRAIN) {
				return FILL;
			} else {
				return DRAIN;
			}
		}
	}

	public static final int DEFAULT_FLUID_TO_CONTAINER_RATE = 1000;
	public static final int FLUID_DRAIN_CYCLE_TIME = 4;

	private final IFluidHandler fluidHandler;
	private final int fluidToContainerRate;
	private int fluidDrainTimer;
	private Set<FluidContainerInteractionMode> allowedModes;

	@UpdateSerialize
	private FluidContainerInteractionMode interactionMode;

	public FluidContainerInventoryComponent(String name, IFluidHandler fluidHandler) {
		super(name, 2);
		this.fluidHandler = fluidHandler;
		interactionMode = FluidContainerInteractionMode.DRAIN;
		fluidToContainerRate = DEFAULT_FLUID_TO_CONTAINER_RATE;
		allowedModes = new HashSet<>();
		setCapabilityExtractEnabled(false);
		setCapabilityInsertEnabled(false);
		setShiftClickEnabled(true);
		setShiftClickPriority(-1);
		setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return FluidUtil.getFluidHandler(stack).isPresent();
			}
		});
	}

	@Override
	public void preProcessUpdate() {
		// Do nothing if this component is not enabled.
		if (!isEnabled()) {
			return;
		}

		// Only do work on the server.
		if (getLevel().isClientSide()) {
			return;
		}

		// Tick forward the drain cycle timer.
		if (fluidDrainTimer < FLUID_DRAIN_CYCLE_TIME - 1) {
			fluidDrainTimer++;
			return;
		}

		// If we made it this far, the timer elapsed. Set it back to 0 and proceed.
		fluidDrainTimer = 0;

		// Get the impetus for the transaction.
		ItemStack primaryStack = getStackInSlot(0);

		// If it is empty, do nothing.
		if (primaryStack.isEmpty()) {
			return;
		}

		// Perform the operation.
		if (interactionMode == FluidContainerInteractionMode.DRAIN) {
			drainFromContainer(primaryStack);
		} else if (interactionMode == FluidContainerInteractionMode.FILL) {
			fillToContainer(primaryStack);
		}
	}

	public FluidContainerInventoryComponent setAllowedModes(boolean canFillToContainer, boolean canDrainFromContainer) {
		allowedModes.clear();

		if (canFillToContainer) {
			allowedModes.add(FluidContainerInteractionMode.FILL);
		}
		if (canDrainFromContainer) {
			allowedModes.add(FluidContainerInteractionMode.DRAIN);
		}

		if (allowedModes.size() == 1) {
			setMode(allowedModes.iterator().next());
		}

		return this;
	}

	/**
	 * Fills the container from the owning fluid handler.
	 * 
	 * @param container
	 */
	protected void fillToContainer(ItemStack container) {
		IFluidHandlerItem containerHandler = FluidUtil.getFluidHandler(container).orElse(null);
		if (containerHandler != null) {
			FluidStack simulatedDrain = fluidHandler.drain(fluidToContainerRate, FluidAction.SIMULATE);
			int filledAmount = containerHandler.fill(simulatedDrain, FluidAction.EXECUTE);
			if (filledAmount > 0) {
				fluidHandler.drain(filledAmount, FluidAction.EXECUTE);

				if (filledAmount >= 1000) {
					float minSound = Math.max(
							1.0f - (float) fluidHandler.getFluidInTank(0).getAmount() / fluidHandler.getTankCapacity(0),
							0.55f) * 1.1f;
					float maxSound = minSound + 0.1f;
					// Play the sound.
					getLevel().playSound(null, getPos(),
							simulatedDrain.getFluid() == Fluids.LAVA ? SoundEvents.BUCKET_FILL_LAVA
									: SoundEvents.BUCKET_FILL,
							SoundSource.BLOCKS, 0.35f,
							SDMath.clamp(getLevel().getRandom().nextFloat(), minSound, maxSound));
				}

				// If the container is empty, transfer it to the empty container slot.
				if (containerHandler.getFluidInTank(0).getAmount() == containerHandler.getTankCapacity(0)) {
					if (InventoryUtilities.canFullyInsertStackIntoSlot(this, 1, containerHandler.getContainer())) {
						// Perform the insert.
						ItemStack insertedItem = insertItem(1, containerHandler.getContainer(), false);
						// If successfully, extract the item from the primary slot.
						if (insertedItem.isEmpty()) {
							extractItem(0, 1, false);
						}
					}
				}
			}
		}
	}

	/**
	 * Drains fluid from the container into the owning fluid handler.
	 * 
	 * @param container
	 */
	protected void drainFromContainer(ItemStack container) {
		IFluidHandlerItem containerHandler = FluidUtil.getFluidHandler(container).orElse(null);
		if (containerHandler != null) {
			FluidStack simulatedDrain = containerHandler.drain(fluidToContainerRate, FluidAction.SIMULATE);
			int filledAmount = fluidHandler.fill(simulatedDrain, FluidAction.EXECUTE);

			if (filledAmount > 0) {
				containerHandler.drain(filledAmount, FluidAction.EXECUTE);

				if (filledAmount >= 1000) {
					float minSound = Math.max(
							1.0f - (float) fluidHandler.getFluidInTank(0).getAmount() / fluidHandler.getTankCapacity(0),
							0.55f) * 1.1f;
					float maxSound = minSound + 0.1f;
					// Play the sound.
					getLevel().playSound(null, getPos(),
							simulatedDrain.getFluid() == Fluids.LAVA ? SoundEvents.BUCKET_EMPTY_LAVA
									: SoundEvents.BUCKET_EMPTY,
							SoundSource.BLOCKS, 0.35f,
							SDMath.clamp(getLevel().getRandom().nextFloat(), minSound, maxSound));
				}

				// If the container is empty, transfer it to the empty container slot.
				if (containerHandler.getFluidInTank(0).getAmount() == 0) {
					if (InventoryUtilities.canFullyInsertStackIntoSlot(this, 1, containerHandler.getContainer())) {
						// Perform the insert.
						ItemStack insertedItem = insertItem(1, containerHandler.getContainer(), false);
						// If successfully, extract the item from the primary alot.
						if (insertedItem.isEmpty()) {
							extractItem(0, 1, false);
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the new fill/drain mode.
	 * 
	 * @param newMode
	 * @return
	 */
	public FluidContainerInventoryComponent setMode(FluidContainerInteractionMode newMode) {
		if (!allowedModes.contains(newMode)) {
			return this;
		}
		interactionMode = newMode;
		return this;
	}

	/**
	 * Gets the current fill or drain mode of the fluid container handler.
	 * 
	 * @return
	 */
	public FluidContainerInteractionMode getFluidInteractionMode() {
		return interactionMode;
	}
}