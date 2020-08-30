package theking530.staticpower.container.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;

public class SolderingTableOutputSlot extends CraftingResultSlot {
	public SolderingTableOutputSlot(PlayerEntity player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
		super(player, null, inventoryIn, slotIndex, xPosition, yPosition);
	}

	public ItemStack onCrafted(PlayerEntity thePlayer, ItemStack stack) {
		// Call the parent.
		onCrafting(stack);
		return stack;
	}
}