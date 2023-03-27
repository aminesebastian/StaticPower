package theking530.staticpower.container.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.utilities.item.ItemUtilities;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.ContainerDigistoreCraftingTerminal;

public class DigistoreCraftingOutputSlot extends ResultSlot {
	private final ContainerDigistoreCraftingTerminal container;
	private final CraftingContainer craftMatrix;

	public DigistoreCraftingOutputSlot(ContainerDigistoreCraftingTerminal container, Player player, CraftingContainer craftingInventory, Container inventoryIn, int slotIndex,
			int xPosition, int yPosition) {
		super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
		this.container = container;
		this.craftMatrix = craftingInventory;
	}

	public void onTake(Player thePlayer, ItemStack stack) {
		// Get current recipe and cache a copy.
		ItemStack[] originalrecipe = new ItemStack[9];
		for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
			originalrecipe[i] = craftMatrix.getItem(i).copy();
		}

		// Call the parent. This handles firing events and checking advancements.
		super.onTake(thePlayer, stack);

		// On the client, set the items back to the original for a moment.
		if (container.getCableComponent().getLevel().isClientSide()) {
			for (int i = 0; i < originalrecipe.length; i++) {
				ItemStack originalStack = originalrecipe[i];

				// Get the stack to put back into the slot.
				ItemStack putBackStack = originalStack.copy();
				if (putBackStack.hasCraftingRemainingItem()) {
					putBackStack = putBackStack.getCraftingRemainingItem();
				}

				// Put it back.
				craftMatrix.setItem(i, putBackStack);
			}
		} else {
			// On the server, use the items and handle any container items.
			for (int i = 0; i < originalrecipe.length; i++) {
				ItemStack originalStack = originalrecipe[i];

				// Skip holes.
				if (originalStack.isEmpty()) {
					craftMatrix.setItem(i, ItemStack.EMPTY);
				} else {
					// Get the stack to put back into the slot.
					ItemStack putBackStack = originalStack.copy();
					if (putBackStack.hasCraftingRemainingItem()) {
						putBackStack = putBackStack.getCraftingRemainingItem();
					} else {
						putBackStack.shrink(1);
					}

					// Put it back.
					craftMatrix.setItem(i, putBackStack);
				}
			}
		}

		// If on the server, attempt to set the crafting inventory to the same items
		// again.
		if (!container.getCableComponent().getLevel().isClientSide()) {
			container.getDigistoreNetwork().ifPresent(digistoreModule -> {
				for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
					// Get the original item
					ItemStack originalItem = originalrecipe[i];

					// Skip holes in the recipe.
					if (originalItem.isEmpty()) {
						continue;
					}

					// Get the leftover item and track any container item re-inserts.
					ItemStack leftoverItem = craftMatrix.getItem(i);
					ItemStack containerItemInsertRemaining = ItemStack.EMPTY;

					// If the original item and this new item are both not empty, but are not equal,
					// then the item turned into a container.
					// Attempt to insert it back into the system. If not, skip this slot.
					if (!originalItem.isEmpty() && !leftoverItem.isEmpty() && !ItemUtilities.areItemStacksStackable(originalItem, leftoverItem)) {
						containerItemInsertRemaining = digistoreModule.insertItem(leftoverItem.copy(), false);
						craftMatrix.setItem(i, containerItemInsertRemaining);
					}

					// Only proceed if the container item was fully insert into the digi network (or
					// if there never was one).
					if (containerItemInsertRemaining.isEmpty()) {
						// Pull a replacement item
						ItemStack pulledItem = digistoreModule.extractItem(originalItem, 1, false);

						// Extract the item and put it into the craft matrix if something was extracted
						// and it won't somehow overflow the stack size.
						if (!pulledItem.isEmpty() && craftMatrix.getItem(i).getCount() + pulledItem.getCount() < originalItem.getMaxStackSize()) {
							if (craftMatrix.getItem(i).isEmpty()) {
								craftMatrix.setItem(i, pulledItem);
							} else {
								craftMatrix.getItem(i).grow(pulledItem.getCount());
							}
						}
					}
				}
			});
		}

		// Raise the on created and on crafted methods.
		stack.getItem().onCraftedBy(stack, thePlayer.level, thePlayer);
		container.onItemCrafted(originalrecipe, stack);
	}
}
