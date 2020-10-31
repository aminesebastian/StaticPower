package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BaseCentrifugeUpgrade extends BaseUpgrade {

	public BaseCentrifugeUpgrade(String name, ResourceLocation tier) {
		super(name, tier, new Properties().maxStackSize(1), UpgradeType.CENTRIFUGE);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		int speedUpgrade = getTier().maxCentrifugeSpeedUpgrade.get();
		double powerUsageUpgrade = getTier().centrifugeUpgradedPowerIncrease.get();

		tooltip.add(new StringTextComponent(TextFormatting.WHITE + new java.text.DecimalFormat("#").format(speedUpgrade) + TextFormatting.GREEN + " RPM Max Speed"));
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format(powerUsageUpgrade * 100) + "%" + TextFormatting.RED + " RF Per Tick"));
	}
}
