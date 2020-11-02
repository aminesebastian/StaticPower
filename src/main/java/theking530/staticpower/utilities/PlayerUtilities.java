package theking530.staticpower.utilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerUtilities {

	public static int getSlotForStackInPlayerInventory(PlayerEntity player, ItemStack stack) {
		for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
			if (!player.inventory.mainInventory.get(i).isEmpty() && ItemUtilities.areItemStacksExactlyEqual(stack, player.inventory.mainInventory.get(i))) {
				return i;
			}
		}

		return -1;
	}
}
