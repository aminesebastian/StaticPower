package theking530.staticpower.items.tools;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.ISolderingIron;
import theking530.staticpower.items.StaticPowerItem;

public class SolderingIron extends StaticPowerItem implements ISolderingIron {

	public SolderingIron(String name, int maxUses) {
		super(name, new Item.Properties().stacksTo(1).durability(maxUses).setNoRepair());
	}

	@Override
	public boolean useSolderingItem(ItemStack stack) {
		Random rand = new Random();
		if (stack.hurt(1, rand, null)) {
			stack.setCount(0);
			return true;
		}

		return false;
	}

	@Override
	public boolean canSolder(ItemStack solderingIron) {
		if (solderingIron.getDamageValue() < solderingIron.getMaxDamage()) {
			return true;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		if (showAdvanced) {
			tooltip.add(new TextComponent("Max Uses: " + getMaxDamage(stack)));
			tooltip.add(new TextComponent("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
		}
	}
}
