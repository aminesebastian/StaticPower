package theking530.staticpower;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blocks.crops.ModPlants;

public class StaticPowerItemGroup extends ItemGroup {

	public StaticPowerItemGroup(int id, String unlocalizedName) {
		super(id, unlocalizedName);
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon() {
		// TODO Auto-generated method stub
		return new ItemStack(ModPlants.EnergizedCrop);
	}
}