package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.items.StaticPowerItem;

public class SolderingIron extends StaticPowerItem implements ISolderingIron {

	public SolderingIron(String name, int maxUses) {
		super(name, new Item.Properties().maxStackSize(1).maxDamage(maxUses).setNoRepair());
	}

	@Override
	public boolean useSolderingItem(ItemStack stack) {
		stack.setDamage(stack.getDamage() + 1);
		if (stack.getDamage() >= stack.getMaxDamage()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canSolder(ItemStack solderingIron) {
		if (solderingIron.getDamage() < solderingIron.getMaxDamage()) {
			return true;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("Max Uses: " + getMaxDamage(stack)));
		tooltip.add(new StringTextComponent("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
	}
}
