package theking530.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ISolderingIron {

	/**
	 * @param stack
	 * @return Was iron completly used up and destroyed.
	 */
	boolean useSolderingItem(Level level, ItemStack stack);

	boolean canSolder(ItemStack stack);
}
