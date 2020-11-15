package theking530.staticpower.items.tools.miningdrill;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
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
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.api.attributes.capability.AttributeableHandler;
import theking530.api.attributes.defenitions.DiamondHardenedDefenition;
import theking530.api.attributes.defenitions.EmeraldHardenedDefenition;
import theking530.api.attributes.defenitions.FortuneAttributeDefenition;
import theking530.api.attributes.defenitions.GrindingAttributeDefenition;
import theking530.api.attributes.defenitions.HasteAttributeDefenition;
import theking530.api.attributes.defenitions.RubyHardenedDefenition;
import theking530.api.attributes.defenitions.SapphireHardenedDefenition;
import theking530.api.attributes.defenitions.SilkTouchAttributeDefenition;
import theking530.api.attributes.defenitions.SmeltingAttributeDefenition;
import theking530.api.attributes.rendering.AttributableItemRenderLayers;
import theking530.api.attributes.rendering.BasicAttributeRenderLayer;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.utilities.ItemTierUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.items.DrillBitItemModel;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.MetricConverter;

public class DrillBit extends StaticPowerItem implements ICustomModelSupplier {
	public final ResourceLocation tier;
	public final ItemTier miningTier;
	public final AttributableItemRenderLayers renderLayers;

	public DrillBit(String name, ItemTier miningTier, ResourceLocation tier) {
		super(name, new Item.Properties().maxStackSize(1).maxDamage(1));
		this.miningTier = miningTier;
		this.tier = tier;

		renderLayers = new AttributableItemRenderLayers();
		renderLayers.addLayer(GrindingAttributeDefenition.ID, new BasicAttributeRenderLayer(StaticPowerSprites.DRILL_BIT_GRINDING, -1));
		
		renderLayers.addLayer(SmeltingAttributeDefenition.ID, new BasicAttributeRenderLayer(StaticPowerSprites.DRILL_BIT_SMELTING, 1));
		
		renderLayers.addLayer(DiamondHardenedDefenition.ID, new BasicAttributeRenderLayer(StaticPowerSprites.DRILL_BIT_HARDENED_DIAMOND, 2));
		renderLayers.addLayer(RubyHardenedDefenition.ID, new BasicAttributeRenderLayer(StaticPowerSprites.DRILL_BIT_HARDENED_RUBY, 2));
		renderLayers.addLayer(SapphireHardenedDefenition.ID, new BasicAttributeRenderLayer(StaticPowerSprites.DRILL_BIT_HARDENED_SAPPHIRE, 2));
		renderLayers.addLayer(EmeraldHardenedDefenition.ID, new BasicAttributeRenderLayer(StaticPowerSprites.DRILL_BIT_HARDENED_EMERALD, 2));
		
	
		renderLayers.addLayer(HasteAttributeDefenition.ID, new BasicAttributeRenderLayer(StaticPowerSprites.DRILL_BIT_HASTE, 3));
		renderLayers.addLayer(SilkTouchAttributeDefenition.ID, new BasicAttributeRenderLayer(StaticPowerSprites.DRILL_BIT_SILK_TOUCH, 3));
		
		renderLayers.addLayer(FortuneAttributeDefenition.ID, new BasicAttributeRenderLayer(StaticPowerSprites.DRILL_BIT_FORTUNE, 10));
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		AttributeableHandler handler = new AttributeableHandler("attributes");
		handler.addAttribute(FortuneAttributeDefenition.ID);
		handler.addAttribute(HasteAttributeDefenition.ID);
		handler.addAttribute(GrindingAttributeDefenition.ID);
		handler.addAttribute(SmeltingAttributeDefenition.ID);
		handler.addAttribute(SilkTouchAttributeDefenition.ID);

		handler.addAttribute(DiamondHardenedDefenition.ID);
		handler.addAttribute(RubyHardenedDefenition.ID);
		handler.addAttribute(SapphireHardenedDefenition.ID);
		handler.addAttribute(EmeraldHardenedDefenition.ID);
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
		return StaticPowerConfig.getTier(((DrillBit) stack.getItem()).tier).drillBitUses.get();
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

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new DrillBitItemModel(existingModel);
	}
}
