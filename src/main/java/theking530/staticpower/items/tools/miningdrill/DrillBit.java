package theking530.staticpower.items.tools.miningdrill;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.ItemTierUtilities;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.MetricConverter;

public class DrillBit extends StaticPowerItem {
	public final ResourceLocation tier;
	public final ItemTier miningTier;

	public DrillBit(String name, ItemTier miningTier, ResourceLocation tier) {
		super(name, new Item.Properties().maxStackSize(1).maxDamage(1));
		this.miningTier = miningTier;
		this.tier = tier;
	}

	public ItemTier getMiningTier() {
		return miningTier;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.tier == StaticPowerTiers.CREATIVE;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		// Extra step here to ensure the correct tier is used.
		return TierReloadListener.getTier(((DrillBit) stack.getItem()).tier).getDrillBitUses();
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new TranslationTextComponent("gui.staticpower.mining_tier").appendString(": ").append(ItemTierUtilities.getNameForItemTier(miningTier)));
		int remaining = getMaxDamage(stack) - getDamage(stack);
		int max = getMaxDamage(stack);
		
		tooltip.add(new TranslationTextComponent("gui.staticpower.block_remaining").appendString(": ").appendString(new MetricConverter(remaining).getValueAsString(true)));
		tooltip.add(new TranslationTextComponent("gui.staticpower.max_drillable_blocks").appendString(": ").appendString(new MetricConverter(max).getValueAsString(true)));
	}
}
