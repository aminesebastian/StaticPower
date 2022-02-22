package theking530.staticpower.utilities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerUtilities {

	public static int getSlotForStackInPlayerInventory(Player player, ItemStack stack) {
		for (int i = 0; i < player.getInventory().items.size(); ++i) {
			if (!player.getInventory().items.get(i).isEmpty()
					&& ItemUtilities.areItemStacksExactlyEqual(stack, player.getInventory().items.get(i))) {
				return i;
			}
		}

		return -1;
	}
}
