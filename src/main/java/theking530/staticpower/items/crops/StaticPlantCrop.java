package theking530.staticpower.items.crops;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.items.StaticPowerItem;

public class StaticPlantCrop extends StaticPowerItem {
	
	public StaticPlantCrop(String name, Food food) {
		super(name, new Item.Properties().food(food));
	}		
	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new StringTextComponent("These Seem to Radiate"));
		tooltip.add(new StringTextComponent("Energy...Yummy"));
	}
}
		


