package theking530.staticcore.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import theking530.staticcore.utilities.item.ItemUtilities;

public class PlayerUtilities {

	public static int getSlotForStackInPlayerInventory(Player player, ItemStack stack) {
		for (int i = 0; i < player.getInventory().items.size(); ++i) {
			if (!player.getInventory().items.get(i).isEmpty() && ItemUtilities.areItemStacksExactlyEqual(stack, player.getInventory().items.get(i))) {
				return i;
			}
		}

		return -1;
	}

	@SuppressWarnings("resource")
	public static Fluid getFluidAtEyeLevel() {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			FluidState fluidState = player.getLevel().getFluidState(new BlockPos(player.getEyePosition()));
			return fluidState.getType();

		}
		return Fluids.EMPTY;
	}
}
