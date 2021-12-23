package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.items.StaticPowerItem;

public class WireCutters extends StaticPowerItem {
	private final ResourceLocation tier;
	private final Item repairItem;

	public WireCutters(String name, ResourceLocation tier, Item repairItem) {
		super(name, new Item.Properties().stacksTo(1));
		this.tier = tier;
		this.repairItem = repairItem;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		if (repair.getItem() == repairItem) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		ItemStack stackCopy = stack.copy();
		if (stackCopy.hurt(1, RANDOM, null)) {
			stackCopy.shrink(1);
			stackCopy.setDamageValue(0);
		}
		return stackCopy;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return StaticPowerConfig.getTier(tier).wireCutterUses.get();
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return true;
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(new TextComponent("Max Uses: " + getMaxDamage(stack)));
		tooltip.add(new TextComponent("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
	}
}
