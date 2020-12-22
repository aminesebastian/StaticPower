package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.SDTime;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.StaticPowerItem;

public class TurbineBlades extends StaticPowerItem {
	protected final ResourceLocation tier;
	protected final ResourceLocation inWorldModel;

	public TurbineBlades(String name, ResourceLocation tierId, ResourceLocation inWorldModel) {
		super(name, new Item.Properties().maxStackSize(1).maxDamage(1).setNoRepair());
		this.tier = tierId;
		this.inWorldModel = inWorldModel;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return StaticPowerConfig.getTier(tier).turbineBladeDurabilityTicks.get();
	}

	public ResourceLocation getTier() {
		return tier;
	}

	public ResourceLocation getInWorldModel() {
		return inWorldModel;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.tier == StaticPowerTiers.CREATIVE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);

		// Add the generation time.
		String generationTime = SDTime.ticksToTimeString(getMaxDamage(stack) - getDamage(stack));
		tooltip.add(new StringTextComponent("Generation Time: " + generationTime));

		// Add the generation boost.
		double generationBoost = tierObject.turbineBladeGenerationBoost.get();
		if (generationBoost > 1) {
			tooltip.add(new StringTextComponent(
					TextFormatting.WHITE + new java.text.DecimalFormat("#").format(tierObject.turbineBladeGenerationBoost.get() * 100) + "%" + TextFormatting.GREEN + " Power Generation"));
		} else {
			tooltip.add(new StringTextComponent(
					TextFormatting.WHITE + new java.text.DecimalFormat("#").format(tierObject.turbineBladeGenerationBoost.get() * 100) + "%" + TextFormatting.RED + " Power Generation"));
		}
	}
}
