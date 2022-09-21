package theking530.staticpower.integration.JEI;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Taking some design cues from Applied Energistics 2!
 * 
 * @author Amine Sebastian
 *
 */
public interface IJEIReipceTransferHandler {
	public void consumeJEITransferRecipe(Player playerIn, ItemStack[][] recipe);
}
