package theking530.staticpower.items.tools.miningdrill;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.api.itemattributes.attributes.AttributeRegistry;
import theking530.api.itemattributes.attributes.FortuneAttributeDefenition;
import theking530.api.itemattributes.attributes.GrindingAttributeDefenition;
import theking530.api.itemattributes.attributes.HasteAttributeDefenition;
import theking530.api.itemattributes.capability.AttributeableHandler;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
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

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		FortuneAttributeDefenition fortuneAttribute = (FortuneAttributeDefenition) AttributeRegistry.createInstance(FortuneAttributeDefenition.ID);
		HasteAttributeDefenition hasteAttribute = (HasteAttributeDefenition) AttributeRegistry.createInstance(HasteAttributeDefenition.ID);
		GrindingAttributeDefenition grindingAttribute = (GrindingAttributeDefenition) AttributeRegistry.createInstance(GrindingAttributeDefenition.ID);

		AttributeableHandler handler = new AttributeableHandler("attributes");
		handler.addAttribute(fortuneAttribute);
		handler.addAttribute(hasteAttribute);
		handler.addAttribute(grindingAttribute);
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(handler);
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
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new TranslationTextComponent("gui.staticpower.mining_tier").appendString(": ").append(ItemTierUtilities.getNameForItemTier(miningTier)));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		int remaining = getMaxDamage(stack) - getDamage(stack);
		int max = getMaxDamage(stack);
		String remainingString = "(" + new MetricConverter(remaining).getValueAsString(true) + "/" + new MetricConverter(max).getValueAsString(true) + ")";
		tooltip.add(new TranslationTextComponent("gui.staticpower.block_remaining").appendString(": ").appendString(remainingString));
	}
}
