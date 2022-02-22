package theking530.staticpower.tileentities.components.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.serialization.SaveSerialize;
import theking530.staticpower.utilities.InventoryUtilities;

public class InputServoComponent extends AbstractTileEntityComponent {
	public static final int DEFAULT_INPUT_TIME = 20;

	private int inputTimer;
	private int inputTime;
	private InventoryComponent inventory;
	private int[] slots;
	private MachineSideMode inputMode;
	private Predicate<ItemStack> filter;
	@SaveSerialize
	private boolean roundRobin;
	@SaveSerialize
	private int lastRoundRobinSlotIndex;

	public InputServoComponent(String name, int inputTime, InventoryComponent inventory, MachineSideMode mode, Predicate<ItemStack> filter, int... slots) {
		super(name);
		this.inventory = inventory;
		if (slots == null || slots.length == 0) {
			this.slots = new int[inventory.getSlots()];
			for (int i = 0; i < inventory.getSlots(); i++) {
				this.slots[i] = i;
			}
		} else {
			this.slots = slots;
		}

		this.inputMode = mode;
		this.roundRobin = false;
		this.inputTime = inputTime;
		this.lastRoundRobinSlotIndex = 0;
	}

	public InputServoComponent(String name, int inputTime, InventoryComponent inventory, MachineSideMode mode, int... slots) {
		this(name, inputTime, inventory, mode, null, slots);
	}

	public InputServoComponent(String name, int inputTime, InventoryComponent inventory, Predicate<ItemStack> filter, int... slots) {
		this(name, inputTime, inventory, MachineSideMode.Input, filter, slots);
	}

	public InputServoComponent(String name, int inputTime, InventoryComponent inventory, int... slots) {
		this(name, inputTime, inventory, MachineSideMode.Input, slots);
	}

	public InputServoComponent(String name, InventoryComponent inventory, int... slots) {
		this(name, DEFAULT_INPUT_TIME, inventory, MachineSideMode.Input, slots);
	}

	public InputServoComponent(String name, InventoryComponent inventory, MachineSideMode mode, Predicate<ItemStack> filter) {
		this(name, DEFAULT_INPUT_TIME, inventory, mode, filter, null);
	}

	public InputServoComponent(String name, InventoryComponent inventory, Predicate<ItemStack> filter) {
		this(name, DEFAULT_INPUT_TIME, inventory, MachineSideMode.Input, filter, null);
	}

	public InputServoComponent(String name, InventoryComponent inventory) {
		this(name, DEFAULT_INPUT_TIME, inventory, MachineSideMode.Input, null);
	}

	public MachineSideMode getMode() {
		return inputMode;
	}

	public InputServoComponent setRoundRobin(boolean roundRobin) {
		this.roundRobin = roundRobin;
		return this;
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

		// If on the server, get the sides we can input from.
		if (!getTileEntity().getLevel().isClientSide) {
			// First, increment the input timer.
			inputTimer++;

			// If the timer has not elapsed, return early.
			if (inputTimer < inputTime && !roundRobin) {
				return;
			} else if (inputTimer == 0) {
				return;
			}
			inputTimer = 0;

			// Allocate a list of handlers.
			List<IItemHandler> validHandlers = new LinkedList<IItemHandler>();

			// Get a random array of block sides.
			List<BlockSide> randomBlockSideList = new ArrayList<BlockSide>(Arrays.asList(BlockSide.values()));
			Collections.shuffle(randomBlockSideList);

			// Check every side.
			for (BlockSide side : randomBlockSideList) {
				// If we can output from that side.
				if (canInputFromSide(side)) {
					// Get the facing direction of that side.
					Direction facing = getWorld().getBlockState(getPos()).getValue(StaticPowerTileEntityBlock.FACING);
					Direction direction = SideConfigurationUtilities.getDirectionFromSide(side, facing);

					// Get the tile entity in that direction.
					BlockEntity te = getWorld().getBlockEntity(getPos().relative(direction));

					// If the tile entity exists.
					if (te != null) {
						// Get the item handler on that side.
						IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).orElse(null);

						// If that exists and is not empty, add is as a candidate.
						if (handler != null && !InventoryUtilities.isInventoryEmpty(handler)) {
							validHandlers.add(handler);
						}
					}
				}
			}

			// Iterate through all the valid sides.
			for (IItemHandler validSide : validHandlers) {
				// Try to insert an item into it.
				for (int i = 0; i < validSide.getSlots(); i++) {
					// Get the candidate to input.
					ItemStack candidate = validSide.extractItem(i, inventory.getSlotLimit(i), true);

					// Skip empty candidates.
					if (candidate.isEmpty()) {
						continue;
					}

					// Check the filter if it exists, and skip if it fails.
					if (filter != null && !filter.test(candidate)) {
						continue;
					}

					// Allocate the remaining.
					ItemStack remaining = ItemStack.EMPTY;

					// Attempt the insert using a copy and capture the remaining. If using round
					// robin, perform a round robin input.
					if (this.roundRobin) {
						// Get the round robbin slot to use.
						int slotToUse = slots[lastRoundRobinSlotIndex];

						// Change the size.
						candidate.setCount(1);

						// Capture the remaining.
						remaining = InventoryUtilities.insertItemIntoInventory(inventory, candidate.copy(), slotToUse, slotToUse, false);

						// Increment and wrap around.
						lastRoundRobinSlotIndex++;
						if (lastRoundRobinSlotIndex >= slots.length) {
							lastRoundRobinSlotIndex = 0;
						}
					} else {
						remaining = InventoryUtilities.insertItemIntoInventory(inventory, candidate.copy(), false);
					}

					// If any items were transfered, extract the transfered amount from our
					// inventory and then stop.
					if (candidate.getCount() != remaining.getCount()) {
						int transfered = candidate.getCount() - remaining.getCount();
						validSide.extractItem(i, transfered, false);
						break;
					}
				}
			}
		}
	}

	public InputServoComponent setInventory(InventoryComponent inventory) {
		this.inventory = inventory;
		return this;
	}

	public boolean canInputFromSide(BlockSide blockSide) {
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getTileEntity().getComponent(SideConfigurationComponent.class)
					.getWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(blockSide, getTileEntity().getFacingDirection())) == inputMode;
		}
		return true;
	}
}
