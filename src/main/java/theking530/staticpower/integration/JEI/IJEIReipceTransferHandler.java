package theking530.staticpower.integration.JEI;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Taking some design cues from Applied Energistics 2!
 * 
 * @author Amine Sebastian
 *
 */
public interface IJEIReipceTransferHandler {
	public void consumeJEITransferRecipe(PlayerEntity playerIn, ItemStack[][] recipe);
}
