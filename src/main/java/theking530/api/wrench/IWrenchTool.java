package theking530.api.wrench;

import net.minecraft.world.item.ItemStack;

public interface IWrenchTool {
	public RegularWrenchMode getWrenchMode(ItemStack stack);
	public SneakWrenchMode getSneakWrenchMode(ItemStack stack);
}
