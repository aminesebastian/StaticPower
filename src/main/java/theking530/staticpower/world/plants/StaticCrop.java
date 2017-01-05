package theking530.staticpower.world.plants;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;

public class StaticCrop extends ItemFood {
	
	public StaticCrop() {
		super(4, 0.5f, false);
	}
	public static Item StaticCrop;{
		setMaxStackSize(64);
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName("StaticCrop");
		setRegistryName("StaticCrop");
		setAlwaysEdible();
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add("These Seem to Radiate");
		list.add("Energy...Yummy");
	}
}
		


