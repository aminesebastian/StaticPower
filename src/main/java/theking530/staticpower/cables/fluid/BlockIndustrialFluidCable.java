package theking530.staticpower.cables.fluid;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockIndustrialFluidCable extends AbstractCableBlock {

	public BlockIndustrialFluidCable(ResourceLocation tier) {
		super(tier, new CableBoundsCache(3.5D, new Vector3D(4.25f, 4.25f, 2.0f)), 4.5f);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		GuiTextUtilities.addColoredBulletTooltip(tooltip, "gui.staticpower.max_fluid_rate", ChatFormatting.AQUA,
				GuiTextUtilities.formatFluidRateToString(StaticPowerConfig.getTier(tier).cableFluidConfiguration.cableIndustrialFluidTransferRate.get()).getString());
		tooltip.add(Component.translatable("gui.staticpower.industrial_cable_warning").withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(true)));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		ResourceLocation extensionModel = null;
		ResourceLocation straightModel = null;
		ResourceLocation attachmentModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_DEFAULT_ATTACHMENT;

		if (tier == StaticPowerTiers.BASIC) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_BASIC_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_BASIC_STRAIGHT;
		} else if (tier == StaticPowerTiers.ADVANCED) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_ADVANCED_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_ADVANCED_STRAIGHT;
		} else if (tier == StaticPowerTiers.STATIC) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_STATIC_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_STATIC_STRAIGHT;
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_ENERGIZED_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_ENERGIZED_STRAIGHT;
		} else if (tier == StaticPowerTiers.LUMUM) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_LUMUM_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_LUMUM_STRAIGHT;
		} else if (tier == StaticPowerTiers.CREATIVE) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_CREATIVE_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_CREATIVE_STRAIGHT;
		}

		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(world, pos);
		if (cable instanceof FluidCableComponent) {
			FluidStack fluid = ((FluidCableComponent) cable).getFluidInTank(0);
			if (!fluid.isEmpty()) {
				return fluid.getFluid().getFluidType().getLightLevel();
			}
		}
		return super.getLightEmission(state, world, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.translucent();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityFluidCable.TYPE_INDUSTRIAL_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityFluidCable.TYPE_INDUSTRIAL_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityFluidCable.TYPE_INDUSTRIAL_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityFluidCable.TYPE_INDUSTRIAL_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityFluidCable.TYPE_INDUSTRIAL_LUMUM.create(pos, state);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return BlockEntityFluidCable.TYPE_INDUSTRIAL_CREATIVE.create(pos, state);
		}
		return null;
	}
}
