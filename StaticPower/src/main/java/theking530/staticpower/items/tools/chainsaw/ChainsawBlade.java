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
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.staticcore.attribiutes.Attributes;
import theking530.staticcore.item.multipartitem.AbstractToolPart;
import theking530.staticcore.utilities.MetricConverter;
import theking530.staticcore.utilities.item.ItemTierUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.ChainsawBladeItemModel;
import theking530.staticpower.init.ModCreativeTabs;

public class ChainsawBlade extends AbstractToolPart {
	public final Tier miningTier;

	public ChainsawBlade(Tier miningTier, ResourceLocation tier) {
		super(tier, new Item.Properties().stacksTo(1).durability(1).tab(ModCreativeTabs.TOOLS));
		this.miningTier = miningTier;
	}

	@Override
	protected int getBaseDurability() {
		return StaticPowerConfig.getTier(tier).toolConfiguration.chainsawBladeUses.get();
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
		return new ChainsawBladeItemModel(existingModel);
	}

}
