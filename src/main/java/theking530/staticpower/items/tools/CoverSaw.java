package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.items.StaticPowerItem;

public class CoverSaw extends StaticPowerItem {

	public CoverSaw(int maxUses) {
		super(new Item.Properties().stacksTo(1).durability(maxUses).setNoRepair());
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		if (repair.getItem() == Items.IRON_INGOT) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		ItemStack stackCopy = stack.copy();
		if (stackCopy.hurt(1, RandomSource.create(), null)) {
			stackCopy.shrink(1);
			stackCopy.setDamageValue(0);
		}
		return stackCopy;
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
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
