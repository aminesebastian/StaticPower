package theking530.staticpower.items.tools.chainsaw;

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
import theking530.api.attributes.ItemAttributeRegistry.ItemAttributeRegisterEvent;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.rendering.AttributeRenderLayer;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.utilities.MetricConverter;
import theking530.staticcore.utilities.item.ItemTierUtilities;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.items.ChainsawBladeItemModel;
import theking530.staticpower.init.ModAttributes;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.ModItemSlots;
import theking530.staticpower.items.AbstractToolPart;

public class ChainsawBlade extends AbstractToolPart {
	public final Tier miningTier;

	@SuppressWarnings("unchecked")
	public ChainsawBlade(Tier miningTier, ResourceLocation tier) {
		super(tier, new Item.Properties().stacksTo(1).durability(1).tab(ModCreativeTabs.TOOLS), ModItemSlots.CHAINSAW_BLADE);
		this.miningTier = miningTier;
	}

	@Override
	protected int getBaseDurability() {
		return StaticCoreConfig.getTier(tier).toolConfiguration.chainsawBladeUses.get();
	}

	public Tier getMiningTier(ItemStack stack) {
		// Get the drill bit attributes, check if it has the promoted attribute. If it
		// does, promote the item.
		IAttributable drillBitAttributes = stack.getCapability(CapabilityAttributable.CAPABILITY_ATTRIBUTABLE).orElse(null);
		if (drillBitAttributes != null && drillBitAttributes.hasAttribute(ModAttributes.Promoted.get())) {
			return ModAttributes.Promoted.get().modifyItemTier(drillBitAttributes.getAttribute(ModAttributes.Promoted.get()), miningTier);
		}
		return miningTier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		// Add the mining tier.
		tooltip.add(Component.translatable("gui.staticcore.mining_tier").append(": ").append(ItemTierUtilities.getNameForItemTier(getMiningTier(stack))));

		// Add the durability.
		int remaining = getMaxDamage(stack) - getDamage(stack);
		String remainingString = new MetricConverter(remaining).getValueAsString(true);
		tooltip.add(Component.translatable("gui.staticcore.durability").append(" ").append(remainingString));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new ChainsawBladeItemModel(existingModel);
	}

	public static void registerAttributes(ItemAttributeRegisterEvent event, Item blade) {
		event.attach(blade, ModAttributes.Smelting.get(), false, new AttributeRenderLayer(StaticPowerAdditionalModels.BLADE_SMELTING, 1));
		event.attach(blade, ModAttributes.DiamondHardened.get(), false, new AttributeRenderLayer(StaticPowerAdditionalModels.CHAINSAW_BLADE_HARDENED_DIAMOND, 2));
		event.attach(blade, ModAttributes.EmeraldHardened.get(), false, new AttributeRenderLayer(StaticPowerAdditionalModels.CHAINSAW_BLADE_HARDENED_EMERALD, 2));
		event.attach(blade, ModAttributes.RubyHardened.get(), false, new AttributeRenderLayer(StaticPowerAdditionalModels.CHAINSAW_BLADE_HARDENED_RUBY, 2));
		event.attach(blade, ModAttributes.SapphireHardened.get(), false, new AttributeRenderLayer(StaticPowerAdditionalModels.CHAINSAW_BLADE_HARDENED_SAPPHIRE, 2));
		event.attach(blade, ModAttributes.Haste.get(), 0, new AttributeRenderLayer(StaticPowerAdditionalModels.BLADE_HASTE, 3));
	}
}
