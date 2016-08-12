package theking530.staticpower.items;

import java.util.List;

import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EnergizedCrop extends ItemFood {
	
	public EnergizedCrop() {
		super(8, 1.0f, false);
		setUnlocalizedName("EnergizedCrop");
		setRegistryName("EnergizedCrop");
		setCreativeTab(StaticPower.StaticPower);
		setAlwaysEdible();
	}		
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add("These Seem to Radiate");
		list.add("Energy...Yummy");
    }
	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
	    super.onFoodEaten(stack, world, player);
	}
}

