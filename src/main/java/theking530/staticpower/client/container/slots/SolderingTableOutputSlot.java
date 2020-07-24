package theking530.staticpower.client.container.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.tileentities.nonpowered.solderingtable.ContainerSolderingTable;

public class SolderingTableOutputSlot extends CraftingResultSlot {
	private final ContainerSolderingTable container;

	public SolderingTableOutputSlot(ContainerSolderingTable container, PlayerEntity player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
		super(player, null, inventoryIn, slotIndex, xPosition, yPosition);
		this.container = container;
	}

	public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		// Call the parent.
		onCrafting(stack);

		container.onItemCrafted(stack);
		return stack;
	}
}