package theking530.staticcore.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.utilities.Color;

public class MaterialItemColor implements ItemColor {

	private final Color color;
	private final int encodedColor;

	public MaterialItemColor(Color color) {
		this.color = color;
		this.encodedColor = color.encodeInInteger();
	}

	public Color getColor() {
		return color;
	}

	@Override
	public int getColor(ItemStack stack, int tintIndex) {
		return encodedColor;
	}
}