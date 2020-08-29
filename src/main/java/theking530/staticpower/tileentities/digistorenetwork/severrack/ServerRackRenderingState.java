package theking530.staticpower.tileentities.digistorenetwork.severrack;

import net.minecraft.item.ItemStack;

public class ServerRackRenderingState {
	public final ItemStack[] cards;
	public final float filledPercentage;

	public ServerRackRenderingState(ItemStack[] cards, float filledPercentage) {
		this.cards = cards;
		this.filledPercentage = filledPercentage;
	}
}
