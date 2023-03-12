package theking530.staticpower.items.tools.miningdrill;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.api.attributes.Attributes;
import theking530.api.attributes.capability.AttributeableHandler;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.rendering.AttributableItemRenderLayers;
import theking530.api.attributes.rendering.BasicAttributeRenderLayer;
import theking530.api.multipartitem.AbstractToolPart;
import theking530.staticcore.utilities.ItemTierUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.items.DrillBitItemModel;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.utilities.MetricConverter;

public class DrillBit extends AbstractToolPart {
	public final Tier miningTier;

	public DrillBit(Tier miningTier, ResourceLocation tier) {
		super(tier, new Item.Properties().stacksTo(1).durability(1).tab(ModCreativeTabs.TOOLS));
		this.miningTier = miningTier;

	}

	@Override
	protected void initializeAttributes(AttributeableHandler handler) {
		handler.addAttribute(Attributes.Fortune.get(), 0);
		handler.addAttribute(Attributes.Haste.get(), 0);
		handler.addAttribute(Attributes.Grinding.get(), false);
		handler.addAttribute(Attributes.Smelting.get(), false);
		handler.addAttribute(Attributes.SilkTouch.get(), false);

		handler.addAttribute(Attributes.DiamondHardened.get(), false);
		handler.addAttribute(Attributes.RubyHardened.get(), false);
		handler.addAttribute(Attributes.SapphireHardened.get(), false);
		handler.addAttribute(Attributes.EmeraldHardened.get(), false);
		handler.addAttribute(Attributes.Promoted.get(), false);
	}

	@Override
	protected void initializeRenderLayers(AttributableItemRenderLayers renderLayers) {
		renderLayers.addLayer(Attributes.Grinding.get(), new BasicAttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_GRINDING, -1));

		renderLayers.addLayer(Attributes.Smelting.get(), new BasicAttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_SMELTING, 1));

		renderLayers.addLayer(Attributes.DiamondHardened.get(), new BasicAttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_HARDENED_DIAMOND, 2));
		renderLayers.addLayer(Attributes.RubyHardened.get(), new BasicAttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_HARDENED_RUBY, 2));
		renderLayers.addLayer(Attributes.SapphireHardened.get(), new BasicAttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_HARDENED_SAPPHIRE, 2));
		renderLayers.addLayer(Attributes.EmeraldHardened.get(), new BasicAttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_HARDENED_EMERALD, 2));

		renderLayers.addLayer(Attributes.Haste.get(), new BasicAttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_HASTE, 3));
		renderLayers.addLayer(Attributes.SilkTouch.get(), new BasicAttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_SILK_TOUCH, 3));

		renderLayers.addLayer(Attributes.Fortune.get(), new BasicAttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_FORTUNE, 10));
	}

	public Tier getMiningTier(ItemStack stack) {
		// Get the drill bit attributes, check if it has the promoted attribute. If it
		// does, promote the item.
		IAttributable drillBitAttributes = stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);
		if (drillBitAttributes != null && drillBitAttributes.hasAttribute(Attributes.Promoted.get())) {
			return Attributes.Promoted.get().modifyItemTier(drillBitAttributes.getAttribute(Attributes.Promoted.get()), miningTier);
		}
		return miningTier;
	}

	@Override
	protected int getBaseDurability() {
		return StaticPowerConfig.getTier(tier).toolConfiguration.drillBitUses.get();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		// Add the mining tier.
		tooltip.add(Component.translatable("gui.staticpower.mining_tier").append(": ").append(ItemTierUtilities.getNameForItemTier(getMiningTier(stack))));

		// Add the durability.
		int remaining = getMaxDamage(stack) - getDamage(stack);
		String remainingString = new MetricConverter(remaining).getValueAsString(true);
		tooltip.add(Component.translatable("gui.staticpower.durability").append(" ").append(remainingString));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new DrillBitItemModel(existingModel);
	}
}
