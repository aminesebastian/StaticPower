package theking530.staticpower.items.tools;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.ISolderingIron;
import theking530.staticpower.items.StaticPowerItem;

public class SolderingIron extends StaticPowerItem implements ISolderingIron {

	public SolderingIron(String name, int maxUses) {
		super(name, new Item.Properties().maxStackSize(1).maxDamage(maxUses).setNoRepair());
	}

	@Override
	public boolean useSolderingItem(ItemStack stack) {
		Random rand = new Random();
		if (stack.attemptDamageItem(1, rand, null)) {
			stack.setCount(0);
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
	protected void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		if (showAdvanced) {
			tooltip.add(new StringTextComponent("Max Uses: " + getMaxDamage(stack)));
			tooltip.add(new StringTextComponent("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
		}
	}
}
