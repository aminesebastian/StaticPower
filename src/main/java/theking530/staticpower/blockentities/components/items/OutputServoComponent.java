package theking530.staticpower.blockentities.components.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.blockentities.components.AbstractBlockEntityComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.utilities.InventoryUtilities;

public class OutputServoComponent extends AbstractBlockEntityComponent {
	public static final int DEFAULT_OUTPUT_TIME = 20;

	private int outputTimer;
	private int outputTime;
	private InventoryComponent inventory;
	private int[] slots;
	private MachineSideMode outputMode;

	public OutputServoComponent(String name, int outputTime, InventoryComponent inventory, MachineSideMode mode, int... slots) {
		super(name);
		this.outputTime = DEFAULT_OUTPUT_TIME;
		this.inventory = inventory;
		if (slots == null || slots.length == 0) {
			this.slots = new int[inventory.getSlots()];
			for (int i = 0; i < inventory.getSlots(); i++) {
				this.slots[i] = i;
			}
		} else {
			this.slots = slots;
		}

		this.outputMode = mode;
	}

	public OutputServoComponent(String name, int outputTime, InventoryComponent inventory, int... slots) {
		this(name, outputTime, inventory, inventory.getMode(), slots);
	}

	public OutputServoComponent(String name, int outputTime, InventoryComponent inventory) {
		this(name, outputTime, inventory, inventory.getMode(), null);
	}

	public OutputServoComponent(String name, InventoryComponent inventory, int... slots) {
		this(name, DEFAULT_OUTPUT_TIME, inventory, inventory.getMode(), slots);
	}

	public OutputServoComponent(String name, InventoryComponent inventory) {
		this(name, DEFAULT_OUTPUT_TIME, inventory, inventory.getMode(), null);
	}

	@Override
	public void preProcessUpdate() {
		// Do nothing if this component is not enabled.
		if (!isEnabled()) {
			return;
		}

		if (inventory == null) {
			return;
		}
		// If we have an empty inventory, do nothing.
		if (inventory.getSlots() == 0) {
			return;
		}

		// If on the server, get the sides we can output to.
		if (!getTileEntity().getLevel().isClientSide()) {
			// First, increment the output timer.
			outputTimer++;

			// If the timer has not elapsed, return early.
			if (outputTimer < outputTime) {
				return;
			}
			outputTimer = 0;

			// Allocate a list of handlers.
			List<IItemHandler> validHandlers = new LinkedList<IItemHandler>();

			// Get a random array of block sides.
			List<BlockSide> randomBlockSideList = new ArrayList<BlockSide>(Arrays.asList(BlockSide.values()));
			Collections.shuffle(randomBlockSideList);

			// Check every side.
			for (BlockSide side : randomBlockSideList) {
				// If we can output from that side.
				if (canOutputFromSide(side)) {
					// Get the facing direction of that side.
					Direction facing = getLevel().getBlockState(getPos()).getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING);
					Direction direction = SideConfigurationUtilities.getDirectionFromSide(side, facing);

					// Get the tile entity in that direction.
					BlockEntity te = getLevel().getBlockEntity(getPos().relative(direction));

					// If the tile entity exists.
					if (te != null) {
						// Get the item handler on that side.
						IItemHandler handler = te.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).orElse(null);

						// If that exists.
						if (handler != null) {
							// Check to see if we can insert any of the source inventory items into that
							// handler. If we can, add it as a candidate.
							for (ItemStack stack : inventory) {
								if (stack.isEmpty()) {
									continue;
								}
								if (InventoryUtilities.canPartiallyInsertItemIntoInventory(handler, stack)) {
									validHandlers.add(handler);
									break;
								}
							}

						}
					}
				}
			}

			// Iterate through all the valid sides.
			for (IItemHandler validSide : validHandlers) {
				// Try to insert an item into it.
				for (int i = 0; i < inventory.getSlots(); i++) {
					// Get the candidate to transfer.
					ItemStack candidate = inventory.extractItem(i, validSide.getSlotLimit(i), true);

					// Attempt the insert using a copy and capture the remaining.
					ItemStack remaining = InventoryUtilities.insertItemIntoInventory(validSide, candidate.copy(), false);

					// If any items were transfered, extract the transfered amount from our
					// inventory and then stop.
					if (candidate.getCount() != remaining.getCount()) {
						int transfered = candidate.getCount() - remaining.getCount();
						inventory.extractItem(i, transfered, false);
						break;
					}
				}
			}
		}
	}

	public OutputServoComponent setInventory(InventoryComponent inventory) {
		this.inventory = inventory;
		return this;
	}

	public boolean canOutputFromSide(BlockSide blockSide) {
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			// Get the side's machine side mode.
			SideConfigurationComponent sideModeConfiguration = getTileEntity().getComponent(SideConfigurationComponent.class);
			MachineSideMode sideMode = sideModeConfiguration
					.getWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(blockSide, getTileEntity().getFacingDirection()));

			// If the mode matches this servo's output mode OR the side is the generic
			// output side and this output mode is an output mode and there are no output
			// modes that match the specific mode.
			if (sideMode == outputMode || (sideMode == MachineSideMode.Output && outputMode.isOutputMode() && sideModeConfiguration.getCountOfSidesWithMode(outputMode) == 0)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
}
