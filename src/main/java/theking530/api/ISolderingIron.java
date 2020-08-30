package theking530.api;

import net.minecraft.item.ItemStack;

public interface ISolderingIron {

	/**
	 * @param stack
	 * @return Was iron completly used up and destroyed.
	 */
	boolean useSolderingItem(ItemStack stack);

	boolean canSolder(ItemStack stack);
}
