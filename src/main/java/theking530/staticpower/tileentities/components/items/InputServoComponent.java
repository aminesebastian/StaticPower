package theking530.staticpower.tileentities.components.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.utilities.InventoryUtilities;

public class InputServoComponent extends AbstractTileEntityComponent {
	public static final int DEFAULT_INPUT_TIME = 10;

	private int inputTimer;
	private int inputTime;
	private InventoryComponent inventory;
	private int[] slots;
	private MachineSideMode inputMode;
	private Predicate<ItemStack> filter;

	public InputServoComponent(String name, int inputTime, InventoryComponent inventory, MachineSideMode mode, Predicate<ItemStack> filter, int... slots) {
		super(name);
		this.inputTime = inputTime;
		this.inventory = inventory;
		if (slots.length == 0) {
			this.slots = new int[inventory.getSlots()];
			for (int i = 0; i < inventory.getSlots(); i++) {
				this.slots[i] = i;
			}
		} else {
			this.slots = slots;
		}

		this.inputMode = mode;
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

	public MachineSideMode getMode() {
		return inputMode;
	}

	@Override
	public void preProcessUpdate() {
		// Do nothing if this component is not enabled.
		if (!isEnabled()) {
			return;
		}

		// If we have an empty inventory, do nothing.
		if (inventory.getSlots() == 0) {
			return;
		}

		// If on the server, get the sides we can input from.
		if (!getTileEntity().getWorld().isRemote) {
			// First, increment the input timer.
			inputTimer++;

			// If the timer has not elapsed, return early.
			if (inputTimer < inputTime) {
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
					Direction facing = getWorld().getBlockState(getPos()).get(StaticPowerTileEntityBlock.FACING);
					Direction direction = SideConfigurationUtilities.getDirectionFromSide(side, facing);

					// Get the tile entity in that direction.
					TileEntity te = getWorld().getTileEntity(getPos().offset(direction));

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
					ItemStack candidate = validSide.getStackInSlot(i);

					// Check the filter if it exists, and skip if it fails.
					if (filter != null && !filter.test(candidate)) {
						continue;
					}

					// Attempt the insert using a copy and capture the remaining.
					ItemStack remaining = InventoryUtilities.insertItemIntoInventory(inventory, candidate.copy(), false);

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

	public boolean canInputFromSide(BlockSide blockSide) {
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getTileEntity().getComponent(SideConfigurationComponent.class)
					.getWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(blockSide, getTileEntity().getFacingDirection())) == inputMode;
		}
		return true;
	}
}
