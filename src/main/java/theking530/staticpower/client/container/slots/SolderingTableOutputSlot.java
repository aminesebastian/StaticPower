package theking530.staticpower.client.container.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.tileentities.nonpowered.solderingtable.ContainerSolderingTable;
import theking530.staticpower.utilities.InventoryUtilities;

public class SolderingTableOutputSlot extends CraftingResultSlot {
	private final ContainerSolderingTable container;

	public SolderingTableOutputSlot(ContainerSolderingTable container, PlayerEntity player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
		super(player, null, inventoryIn, slotIndex, xPosition, yPosition);
		this.container = container;
	}

	public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		// Get current recipe.
		IItemHandler patternInv = container.getTileEntity().patternInventory;
		ItemStack[] currentMatrixContents = new ItemStack[9];
		for (int i = 0; i < patternInv.getSlots(); i++) {
			currentMatrixContents[i] = ItemHandlerHelper.copyStackWithSize(patternInv.getStackInSlot(i).copy(), 1);
		}

		// Call the parent.
		onCrafting(stack);

		// If on the server, attempt to set the crafting inventory to the same items
		// again.
		if (!container.getTileEntity().getWorld().isRemote) {
			for (int i = 0; i < currentMatrixContents.length; i++) {
				// Get the used item.
				ItemStack item = currentMatrixContents[i];

				// Skip holes in the recipe.
				if (item.isEmpty()) {
					continue;
				}

				int index = InventoryUtilities.getFirstSlotContainingItem(item, container.getTileEntity().inventory);
				if (index >= 0) {
					container.getTileEntity().inventory.extractItem(index, 1, false);
				}
			}
		}

		container.onItemCrafted(stack);
		return stack;
	}
}