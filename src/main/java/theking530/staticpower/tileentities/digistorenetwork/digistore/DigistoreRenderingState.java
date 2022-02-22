package theking530.staticpower.tileentities.digistorenetwork.digistore;

import net.minecraft.world.item.ItemStack;

public class DigistoreRenderingState {
	public final ItemStack card;
	public final float filledPercentage;

	public DigistoreRenderingState(ItemStack card, float filledPercentage) {
		this.card = card;
		this.filledPercentage = filledPercentage;
	}
}
