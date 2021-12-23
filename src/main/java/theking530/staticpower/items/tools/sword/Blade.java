package theking530.staticpower.items.tools.sword;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.api.attributes.capability.AttributeableHandler;
import theking530.api.attributes.defenitions.HasteAttributeDefenition;
import theking530.api.attributes.defenitions.SmeltingAttributeDefenition;
import theking530.api.attributes.rendering.AttributableItemRenderLayers;
import theking530.api.attributes.rendering.BasicAttributeRenderLayer;
import theking530.api.multipartitem.AbstractToolPart;
import theking530.staticcore.utilities.ItemTierUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.items.BladeItemModel;
import theking530.staticpower.utilities.MetricConverter;

public class Blade extends AbstractToolPart {
	public final Tiers miningTier;

	public Blade(String name, Tiers miningTier, ResourceLocation tier) {
		super(name, tier, new Item.Properties().stacksTo(1).durability(1));
		this.miningTier = miningTier;
	}

	@Override
	protected void initializeAttributes(AttributeableHandler handler) {
		handler.addAttribute(HasteAttributeDefenition.ID);
		handler.addAttribute(SmeltingAttributeDefenition.ID);
	}

	@Override
	protected void initializeRenderLayers(AttributableItemRenderLayers renderLayers) {
		renderLayers.addLayer(SmeltingAttributeDefenition.ID, new BasicAttributeRenderLayer(StaticPowerAdditionalModels.BLADE_SMELTING, 2));
		renderLayers.addLayer(HasteAttributeDefenition.ID, new BasicAttributeRenderLayer(StaticPowerAdditionalModels.BLADE_HASTE, 3));
	}

	@Override
	protected int getBaseDurability() {
		return StaticPowerConfig.getTier(tier).chainsawBladeUses.get();
	}

	public Tiers getMiningTier() {
		return miningTier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		// Add the mining tier.
		tooltip.add(new TranslatableComponent("gui.staticpower.mining_tier").append(": ").append(ItemTierUtilities.getNameForItemTier(miningTier)));

		// Add the durability.
		int remaining = getMaxDamage(stack) - getDamage(stack);
		String remainingString = new MetricConverter(remaining).getValueAsString(true);
		tooltip.add(new TranslatableComponent("gui.staticpower.durability").append(" ").append(remainingString));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new BladeItemModel(existingModel);
	}

}
