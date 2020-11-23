package theking530.staticpower.items.tools.sword;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.api.attributes.capability.AttributeableHandler;
import theking530.api.attributes.defenitions.HasteAttributeDefenition;
import theking530.api.attributes.defenitions.SmeltingAttributeDefenition;
import theking530.api.attributes.rendering.AttributableItemRenderLayers;
import theking530.api.attributes.rendering.BasicAttributeRenderLayer;
import theking530.api.multipart.AbstractToolPart;
import theking530.staticcore.utilities.ItemTierUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.items.BladeItemModel;
import theking530.staticpower.utilities.MetricConverter;

public class Blade extends AbstractToolPart {
	public final ItemTier miningTier;

	public Blade(String name, ItemTier miningTier, ResourceLocation tier) {
		super(name, tier, new Item.Properties().maxStackSize(1).maxDamage(1));
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

	public ItemTier getMiningTier() {
		return miningTier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		// Add the mining tier.
		tooltip.add(new TranslationTextComponent("gui.staticpower.mining_tier").appendString(": ").append(ItemTierUtilities.getNameForItemTier(miningTier)));

		// Add the durability.
		int remaining = getMaxDamage(stack) - getDamage(stack);
		String remainingString = new MetricConverter(remaining).getValueAsString(true);
		tooltip.add(new TranslationTextComponent("gui.staticpower.durability").appendString(" ").appendString(remainingString));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new BladeItemModel(existingModel);
	}

}
