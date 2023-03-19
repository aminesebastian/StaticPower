package theking530.staticpower.items.tools.sword;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.item.multipartitem.AbstractToolPart;
import theking530.staticcore.utilities.MetricConverter;
import theking530.staticcore.utilities.item.ItemTierUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.BladeItemModel;
import theking530.staticpower.init.ModCreativeTabs;

public class Blade extends AbstractToolPart {
	public final Tiers miningTier;

	public Blade(Tiers miningTier, ResourceLocation tier) {
		super(tier, new Item.Properties().stacksTo(1).durability(1).tab(ModCreativeTabs.TOOLS));
		this.miningTier = miningTier;
	}

	@Override
	protected int getBaseDurability() {
		return StaticPowerConfig.getTier(tier).toolConfiguration.chainsawBladeUses.get();
	}

	public Tiers getMiningTier() {
		return miningTier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		// Add the mining tier.
		tooltip.add(Component.translatable("gui.staticpower.mining_tier").append(": ").append(ItemTierUtilities.getNameForItemTier(miningTier)));

		// Add the durability.
		int remaining = getMaxDamage(stack) - getDamage(stack);
		String remainingString = new MetricConverter(remaining).getValueAsString(true);
		tooltip.add(Component.translatable("gui.staticpower.durability").append(" ").append(remainingString));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new BladeItemModel(existingModel);
	}

}
