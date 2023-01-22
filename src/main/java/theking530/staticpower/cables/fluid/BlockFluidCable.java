package theking530.staticpower.cables.fluid;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

public class BlockFluidCable extends AbstractCableBlock {

	public BlockFluidCable(ResourceLocation tier) {
		super(tier, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.5f);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		GuiTextUtilities.addColoredBulletTooltip(tooltip, "gui.staticpower.max_fluid_rate", ChatFormatting.AQUA,
				GuiTextUtilities.formatFluidRateToString(StaticPowerConfig.getTier(tier).cableFluidConfiguration.cableFluidTransferRate.get()).getString());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		ResourceLocation extensionModel = null;
		ResourceLocation straightModel = null;
		ResourceLocation attachmentModel = StaticPowerAdditionalModels.CABLE_FLUID_DEFAULT_ATTACHMENT;

		if (tier == StaticPowerTiers.BASIC) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_BASIC_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_BASIC_STRAIGHT;
		} else if (tier == StaticPowerTiers.ADVANCED) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_ADVANCED_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_ADVANCED_STRAIGHT;
		} else if (tier == StaticPowerTiers.STATIC) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_STATIC_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_STATIC_STRAIGHT;
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_ENERGIZED_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_ENERGIZED_STRAIGHT;
		} else if (tier == StaticPowerTiers.LUMUM) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_LUMUM_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_LUMUM_STRAIGHT;
		} else if (tier == StaticPowerTiers.CREATIVE) {
			extensionModel = StaticPowerAdditionalModels.CABLE_FLUID_CREATIVE_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_FLUID_CREATIVE_STRAIGHT;
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
		return RenderType.cutout();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityFluidCable.TYPE_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityFluidCable.TYPE_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityFluidCable.TYPE_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityFluidCable.TYPE_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityFluidCable.TYPE_LUMUM.create(pos, state);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return BlockEntityFluidCable.TYPE_CREATIVE.create(pos, state);
		}
		return null;
	}
}
