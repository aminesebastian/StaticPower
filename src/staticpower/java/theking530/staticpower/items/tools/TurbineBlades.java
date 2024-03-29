package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.utilities.SDTime;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.items.StaticPowerItem;

public class TurbineBlades extends StaticPowerItem {
	protected final ResourceLocation tier;
	protected final ResourceLocation inWorldModel;

	public TurbineBlades(ResourceLocation tierId, ResourceLocation inWorldModel) {
		super(new Item.Properties().stacksTo(1).durability(1).setNoRepair().tab(ModCreativeTabs.GENERAL));
		this.tier = tierId;
		this.inWorldModel = inWorldModel;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return StaticCoreConfig.getTier(tier).turbineBladeDurabilityTicks.get();
	}

	public ResourceLocation getTier() {
		return tier;
	}

	public ResourceLocation getInWorldModel() {
		return inWorldModel;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return this.tier == StaticPowerTiers.CREATIVE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		StaticCoreTier tierObject = StaticCoreConfig.getTier(tier);

		// Add the generation time.
		String generationTime = SDTime.ticksToTimeString(getMaxDamage(stack) - getDamage(stack));
		tooltip.add(Component.literal("Generation Time: " + generationTime));

		// Add the generation boost.
		double generationBoost = tierObject.turbineBladeGenerationBoost.get();
		if (generationBoost > 1) {
			tooltip.add(Component.literal(ChatFormatting.WHITE + new java.text.DecimalFormat("#").format(tierObject.turbineBladeGenerationBoost.get() * 100) + "%"
					+ ChatFormatting.GREEN + " Power Generation"));
		} else {
			tooltip.add(Component.literal(ChatFormatting.WHITE + new java.text.DecimalFormat("#").format(tierObject.turbineBladeGenerationBoost.get() * 100) + "%"
					+ ChatFormatting.RED + " Power Generation"));
		}
	}
}
