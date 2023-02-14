package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.ISolderingIron;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.items.StaticPowerItem;

public class SolderingIron extends StaticPowerItem implements ISolderingIron {

	public SolderingIron(int maxUses) {
		super(new Item.Properties().stacksTo(1).durability(maxUses).setNoRepair().tab(ModCreativeTabs.TOOLS));
	}

	@Override
	public boolean useSolderingItem(Level level, ItemStack stack) {
		if (stack.hurt(1, level.getRandom(), null)) {
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
			tooltip.add(Component.literal("Max Uses: " + getMaxDamage(stack)));
			tooltip.add(Component.literal("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
		}
	}
}
