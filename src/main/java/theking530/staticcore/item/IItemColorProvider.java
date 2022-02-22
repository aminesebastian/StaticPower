package theking530.staticcore.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IItemColorProvider {
	@OnlyIn(Dist.CLIENT)
	public ItemColor getColorProvider();
}
