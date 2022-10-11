package theking530.staticcore.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.utilities.SDColor;

public class MaterialItemColor implements ItemColor {

	private final SDColor color;
	private final int encodedColor;

	public MaterialItemColor(SDColor color) {
		this.color = color;
		this.encodedColor = color.encodeInInteger();
	}

	public SDColor getColor() {
		return color;
	}

	@Override
	public int getColor(ItemStack stack, int tintIndex) {
		return encodedColor;
	}
}