package theking530.staticpower.tileentities.components.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;

public class CompoundInventoryComponent extends AbstractTileEntityComponent implements IItemHandlerModifiable {

	private final List<InventoryComponentWrapper> inventories;
	private final int slotCount;
	private MachineSideMode inventoryMode;

	public CompoundInventoryComponent(String name, MachineSideMode mode, InventoryComponent... inventories) {
		this(name, mode, Arrays.asList(inventories));
	}

	public CompoundInventoryComponent(String name, MachineSideMode mode, List<InventoryComponent> inventories) {
		super(name);
		this.inventoryMode = mode;
		this.inventories = new ArrayList<InventoryComponentWrapper>();

		// Capture all the inventories and slot count.
		int currentIndex = 0;
		int slotCount = 0;
		for (InventoryComponent inv : inventories) {
			this.inventories.add(new InventoryComponentWrapper(currentIndex, inv));
			currentIndex += inv.getSlots();
			slotCount += inv.getSlots();
		}

		// Cache the slot count.
		this.slotCount = slotCount;
	}

	/**
	 * Gets the mode of this inventory.
	 * 
	 * @return The {@link MachineSideMode} of this inventory.
	 */
	public MachineSideMode getMode() {
		return inventoryMode;
	}

	/**
	 * Sets the input/output mode for this inventory.
	 * 
	 * @param mode
	 * @return
	 */
	public CompoundInventoryComponent setMode(MachineSideMode mode) {
		this.inventoryMode = mode;
		return this;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (isEnabled()) {
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && inventoryMode != MachineSideMode.Never) {
				// Check if the owner is side configurable. If it is, check to make sure it's
				// not disabled, if not, return the inventory.
				Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getTileEntity());
				if (side == null || !sideConfig.isPresent() || sideConfig.get().getWorldSpaceDirectionConfiguration(side) == inventoryMode) {
					return LazyOptional.of(() -> this).cast();
				}
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		for (InventoryComponentWrapper wrapper : inventories) {
			if (wrapper.isIndexInInventory(slot)) {
				wrapper.inventory.setStackInSlot(wrapper.getAdjustedSlotIndex(slot), stack);
				return;
			}
		}
	}

	@Override
	public int getSlots() {
		return slotCount;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		for (InventoryComponentWrapper wrapper : inventories) {
			if (wrapper.isIndexInInventory(slot)) {
				return wrapper.inventory.getStackInSlot(wrapper.getAdjustedSlotIndex(slot));
			}
		}
		throw new RuntimeException("Slot index: " + slot + " out of range!");
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		for (InventoryComponentWrapper wrapper : inventories) {
			if (wrapper.isIndexInInventory(slot)) {
				if (wrapper.inventory.getMode().isInputMode()) {
					return wrapper.inventory.insertItem(wrapper.getAdjustedSlotIndex(slot), stack, simulate);
				} else {
					return stack;
				}
			}
		}
		throw new RuntimeException("Slot index: " + slot + " out of range!");
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		for (InventoryComponentWrapper wrapper : inventories) {
			if (wrapper.isIndexInInventory(slot)) {
				if (wrapper.inventory.getMode().isOutputMode()) {
					return wrapper.inventory.extractItem(wrapper.getAdjustedSlotIndex(slot), amount, simulate);
				} else {
					return ItemStack.EMPTY;
				}
			}
		}
		throw new RuntimeException("Slot index: " + slot + " out of range!");
	}

	@Override
	public int getSlotLimit(int slot) {
		for (InventoryComponentWrapper wrapper : inventories) {
			if (wrapper.isIndexInInventory(slot)) {
				return wrapper.inventory.getSlotLimit(wrapper.getAdjustedSlotIndex(slot));
			}
		}
		throw new RuntimeException("Slot index: " + slot + " out of range!");
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		for (InventoryComponentWrapper wrapper : inventories) {
			if (wrapper.isIndexInInventory(slot)) {
				return wrapper.inventory.isItemValid(slot, stack);
			}
		}
		throw new RuntimeException("Slot index: " + slot + " out of range!");
	}

	public class InventoryComponentWrapper {
		public final int startIndex;
		public final int endIndex;
		public final InventoryComponent inventory;

		public InventoryComponentWrapper(int startIndex, InventoryComponent inventory) {
			super();
			this.startIndex = startIndex;
			this.endIndex = startIndex + inventory.getSlots() - 1;
			this.inventory = inventory;
		}

		public int getAdjustedSlotIndex(int slot) {
			return slot - startIndex;
		}

		public boolean isIndexInInventory(int index) {
			return index >= startIndex && index <= endIndex;
		}
	}
}
