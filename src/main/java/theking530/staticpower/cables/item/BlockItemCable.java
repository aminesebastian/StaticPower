package theking530.staticpower.cables.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockItemCable extends AbstractCableBlock {

	public BlockItemCable(ResourceLocation tier) {
		super(tier, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.5f);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityItemCable.TYPE_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityItemCable.TYPE_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityItemCable.TYPE_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityItemCable.TYPE_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityItemCable.TYPE_LUMUM.create(pos, state);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return BlockEntityItemCable.TYPE_CREATIVE.create(pos, state);
		}
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		ResourceLocation extensionModel = null;
		ResourceLocation straightModel = null;
		ResourceLocation attachmentModel = StaticPowerAdditionalModels.CABLE_ITEM_DEFAULT_ATTACHMENT;

		if (tier == StaticPowerTiers.BASIC) {
			extensionModel = StaticPowerAdditionalModels.CABLE_ITEM_BASIC_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_ITEM_BASIC_STRAIGHT;
		} else if (tier == StaticPowerTiers.ADVANCED) {
			extensionModel = StaticPowerAdditionalModels.CABLE_ITEM_ADVANCED_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_ITEM_ADVANCED_STRAIGHT;
		} else if (tier == StaticPowerTiers.STATIC) {
			extensionModel = StaticPowerAdditionalModels.CABLE_ITEM_STATIC_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_ITEM_STATIC_STRAIGHT;
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			extensionModel = StaticPowerAdditionalModels.CABLE_ITEM_ENERGIZED_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_ITEM_ENERGIZED_STRAIGHT;
		} else if (tier == StaticPowerTiers.LUMUM) {
			extensionModel = StaticPowerAdditionalModels.CABLE_ITEM_LUMUM_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_ITEM_LUMUM_STRAIGHT;
		} else if (tier == StaticPowerTiers.CREATIVE) {
			extensionModel = StaticPowerAdditionalModels.CABLE_ITEM_CREATIVE_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_ITEM_CREATIVE_STRAIGHT;
		}

		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		double blocksPerTick = StaticPowerConfig.getTier(tier).cableItemConfiguration.itemCableMaxSpeed.get();

		tooltip.add(Component.translatable("gui.staticpower.max_transfer_rate"));
		tooltip.add(Component.literal("• ").append(Component.translatable("gui.staticpower.item_cable_transfer_rate",
				ChatFormatting.GREEN + GuiTextUtilities.formatUnitRateToString(blocksPerTick).getString(), Component.translatable("gui.staticpower.blocks").getString())));
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(Component.literal(""));
		tooltip.add(Component.translatable("gui.staticpower.item_cable_acceleration", ChatFormatting.BLUE
				+ GuiTextUtilities.formatUnitRateToString(StaticPowerConfig.getTier(tier).cableItemConfiguration.itemCableAcceleration.get() * 100).getString()));
		tooltip.add(Component.translatable("gui.staticpower.item_cable_friction",
				ChatFormatting.RED + GuiTextUtilities.formatUnitRateToString(StaticPowerConfig.getTier(tier).cableItemConfiguration.itemCableFriction.get() * 100).getString()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}
}
