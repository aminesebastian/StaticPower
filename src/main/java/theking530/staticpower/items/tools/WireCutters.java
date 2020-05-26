package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.items.StaticPowerItem;

public class WireCutters extends StaticPowerItem {

	public WireCutters(String name, int maxUses) {
		super(name, new Item.Properties().maxStackSize(1).maxDamage(maxUses).setNoRepair());
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		if (repair.getItem() == Items.IRON_INGOT) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return stack.getDamage() < stack.getMaxDamage() ? new ItemStack(stack.getItem(), 1) : ItemStack.EMPTY;
	}

	@Override
	public boolean hasContainerItem() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("Max Uses: " + getMaxDamage(stack)));
		tooltip.add(new StringTextComponent("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
	}
}
