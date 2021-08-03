package theking530.staticpower.container.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreCraftingOutputSlot extends CraftingResultSlot {
	private final ContainerDigistoreCraftingTerminal container;
	private final CraftingInventory craftMatrix;

	public DigistoreCraftingOutputSlot(ContainerDigistoreCraftingTerminal container, PlayerEntity player, CraftingInventory craftingInventory, IInventory inventoryIn, int slotIndex,
			int xPosition, int yPosition) {
		super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
		this.container = container;
		this.craftMatrix = craftingInventory;
	}

	public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		// Get current recipe.
		ItemStack[] currentMatrixContents = new ItemStack[9];
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			currentMatrixContents[i] = craftMatrix.getStackInSlot(i).copy();
		}

		// Call the parent.
		super.onTake(thePlayer, stack);

		// On the client, set the items back to the original for a moment.
		if (container.getCableComponent().getWorld().isRemote()) {
			for (int i = 0; i < currentMatrixContents.length; i++) {
				ItemStack originalStack = currentMatrixContents[i];
				// Skip holes.
				if (originalStack.isEmpty()) {
					continue;
				}

				// Get the stack to put back into the slot.
				ItemStack putBackStack = originalStack.copy();
				if (putBackStack.hasContainerItem()) {
					putBackStack = putBackStack.getContainerItem();
				}

				// Put it back.
				craftMatrix.setInventorySlotContents(i, putBackStack);
			}
		} else {
			// On the server, use the items and handle any container items.
			for (int i = 0; i < currentMatrixContents.length; i++) {
				ItemStack originalStack = currentMatrixContents[i];

				// Skip holes.
				if (originalStack.isEmpty()) {
					craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
				} else {
					// Get the stack to put back into the slot.
					ItemStack putBackStack = originalStack.copy();
					if (putBackStack.hasContainerItem()) {
						putBackStack = putBackStack.getContainerItem();
					} else {
						putBackStack.shrink(1);
					}

					// Put it back.
					craftMatrix.setInventorySlotContents(i, putBackStack);
				}
			}
		}

		// If on the server, attempt to set the crafting inventory to the same items
		// again.
		if (!container.getCableComponent().getWorld().isRemote) {
			container.getDigistoreNetwork().ifPresent(digistoreModule -> {
				for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
					// Get the original item
					ItemStack originalItem = currentMatrixContents[i];

					// Skip holes in the recipe.
					if (originalItem.isEmpty()) {
						continue;
					}

					// Get the leftover item and track any container item re-inserts.
					ItemStack leftoverItem = craftMatrix.getStackInSlot(i);
					ItemStack containerItemInsertRemaining = ItemStack.EMPTY;

					// If the original item and this new item are both not empty, but are not equal,
					// then the item turned into a container.
					// Attempt to insert it back into the system. If not, skip this slot.
					if (!originalItem.isEmpty() && !leftoverItem.isEmpty() && !ItemUtilities.areItemStacksStackable(originalItem, leftoverItem)) {
						containerItemInsertRemaining = digistoreModule.insertItem(leftoverItem.copy(), false);
						craftMatrix.setInventorySlotContents(i, containerItemInsertRemaining);
					}

					// Only proceed if the container item was fully insert into the digi network (or
					// if there never was one).
					if (containerItemInsertRemaining.isEmpty()) {
						// Pull a replacement item
						ItemStack pulledItem = digistoreModule.extractItem(originalItem, 1, false);

						// Extract the item and put it into the craft matrix if something was extracted.
						if (!pulledItem.isEmpty()) {
							if (craftMatrix.getStackInSlot(i).isEmpty()) {
								craftMatrix.setInventorySlotContents(i, pulledItem);
							} else {
								craftMatrix.getStackInSlot(i).grow(pulledItem.getCount());
							}
						}
					}
				}
			});
		}
		stack.getItem().onCreated(stack, thePlayer.world, thePlayer);
		container.onItemCrafted(currentMatrixContents, stack);
		return stack;
	}
}
