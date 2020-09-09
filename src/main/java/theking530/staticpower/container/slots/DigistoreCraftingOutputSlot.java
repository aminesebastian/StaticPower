package theking530.staticpower.container.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.ContainerDigistoreCraftingTerminal;

public class DigistoreCraftingOutputSlot extends CraftingResultSlot {
	private final ContainerDigistoreCraftingTerminal container;
	private final CraftingInventory craftMatrix;

	public DigistoreCraftingOutputSlot(ContainerDigistoreCraftingTerminal container, PlayerEntity player, CraftingInventory craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition,
			int yPosition) {
		super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
		this.container = container;
		this.craftMatrix = craftingInventory;
	}

	public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		// Get current recipe.
		ItemStack[] currentMatrixContents = new ItemStack[9];
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			currentMatrixContents[i] = ItemHandlerHelper.copyStackWithSize(craftMatrix.getStackInSlot(i).copy(), 1);
		}

		// Call the parent.
		super.onTake(thePlayer, stack);

		// If on the server, attempt to set the crafting inventory to the same items
		// again.
		if (!container.getCableComponent().getWorld().isRemote) {
			container.getDigistoreNetwork().ifPresent(digistoreModule -> {
				for (int i = 0; i < currentMatrixContents.length; i++) {
					// Skip any matrix slots that still have items.
					if (!craftMatrix.getStackInSlot(i).isEmpty()) {
						continue;
					}

					// Get the used item.
					ItemStack item = currentMatrixContents[i];

					// Skip holes in the recipe.
					if (item.isEmpty()) {
						continue;
					}

					ItemStack extracted = digistoreModule.extractItem(item, 1, false);
					if (!extracted.isEmpty()) {
						craftMatrix.setInventorySlotContents(i, extracted);
					}
				}
			});
		}
		stack.getItem().onCreated(stack, thePlayer.world, thePlayer);
		container.onItemCrafted(stack);
		return stack;
	}
}
