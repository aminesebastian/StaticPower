package theking530.staticpower.cables.power;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.gui.text.PowerTooltips;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockIndustrialPowerCable extends AbstractCableBlock {

	public BlockIndustrialPowerCable(ResourceLocation tier) {
		super(tier, new CableBoundsCache(3.5D, new Vector3D(4.25f, 4.25f, 2.0f)), 4.5f);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.SNEAKING_ONLY;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		
		PowerTooltips.addMaximumCurrentTooltip(tooltip, tierObject.cablePowerConfiguration.cableIndustrialPowerMaxPower.get());
		PowerTooltips.addPowerLossPerBlockTooltip(tooltip, tierObject.cablePowerConfiguration.cableIndustrialPowerLossPerBlock.get());
		tooltip.add(Component.translatable("gui.staticpower.industrial_cable_warning").withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(true)));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		BakedModel extensionModel = null;
		BakedModel straightModel = null;
		BakedModel attachmentModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_DEFAULT_ATTACHMENT);

		if (tier == StaticPowerTiers.BASIC) {
			extensionModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_BASIC_EXTENSION);
			straightModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_BASIC_STRAIGHT);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			extensionModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_ADVANCED_EXTENSION);
			straightModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_ADVANCED_STRAIGHT);
		} else if (tier == StaticPowerTiers.STATIC) {
			extensionModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_STATIC_EXTENSION);
			straightModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_STATIC_STRAIGHT);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			extensionModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_ENERGIZED_EXTENSION);
			straightModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_ENERGIZED_STRAIGHT);
		} else if (tier == StaticPowerTiers.LUMUM) {
			extensionModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_LUMUM_EXTENSION);
			straightModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_LUMUM_STRAIGHT);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			extensionModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_CREATIVE_EXTENSION);
			straightModel = event.getModels().get(StaticPowerAdditionalModels.CABLE_POWER_INDUSTRIAL_CREATIVE_STRAIGHT);
		}

		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityPowerCable.TYPE_INDUSTRIAL_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityPowerCable.TYPE_INDUSTRIAL_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityPowerCable.TYPE_INDUSTRIAL_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityPowerCable.TYPE_INDUSTRIAL_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityPowerCable.TYPE_INDUSTRIAL_LUMUM.create(pos, state);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return BlockEntityPowerCable.TYPE_INDUSTRIAL_CREATIVE.create(pos, state);
		}
		return null;
	}
}
