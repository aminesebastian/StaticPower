package theking530.staticpower;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StaticPowerItemGroup extends ItemGroup {

	public StaticPowerItemGroup() {
		super("StaticPower");
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon() {
		// TODO Auto-generated method stub
		return new ItemStack(Items.ACACIA_BOAT);
	}
}