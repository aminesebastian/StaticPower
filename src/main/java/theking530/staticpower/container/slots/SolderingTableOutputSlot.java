package theking530.staticpower.container.slots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;

public class SolderingTableOutputSlot extends ResultSlot {
	public SolderingTableOutputSlot(Player player, Container inventoryIn, int slotIndex, int xPosition, int yPosition) {
		super(player, null, inventoryIn, slotIndex, xPosition, yPosition);
	}

	public ItemStack onCrafted(Player thePlayer, ItemStack stack) {
		// Call the parent.
		checkTakeAchievements(stack);
		return stack;
	}
}