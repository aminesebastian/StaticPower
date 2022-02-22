package theking530.staticpower.cables.fluid;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableBoundsCache;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockIndustrialFluidCable extends AbstractCableBlock {
	private ResourceLocation tier;

	public BlockIndustrialFluidCable(String name, ResourceLocation tier) {
		super(name, new CableBoundsCache(3.5D, new Vector3D(4.25f, 4.25f, 2.0f)), 4.5f);
		this.tier = tier;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
			boolean isShowingAdvanced) {
		tooltip.add(new TranslatableComponent("gui.staticpower.max_fluid_rate"));
		tooltip.add(new TextComponent("• ").append(new TextComponent(ChatFormatting.AQUA + GuiTextUtilities
				.formatFluidRateToString(StaticPowerConfig.getTier(tier).cableIndustrialFluidCapacity.get())
				.getString())));
		tooltip.add(new TextComponent("• ").append(
				new TranslatableComponent("gui.staticpower.industrial_cable_warning").withStyle(ChatFormatting.RED)));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelBakeEvent event) {
		BakedModel extensionModel = null;
		BakedModel straightModel = null;
		BakedModel attachmentModel = event.getModelRegistry()
				.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_DEFAULT_ATTACHMENT);

		if (tier == StaticPowerTiers.BASIC) {
			extensionModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_BASIC_EXTENSION);
			straightModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_BASIC_STRAIGHT);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			extensionModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_ADVANCED_EXTENSION);
			straightModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_ADVANCED_STRAIGHT);
		} else if (tier == StaticPowerTiers.STATIC) {
			extensionModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_STATIC_EXTENSION);
			straightModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_STATIC_STRAIGHT);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			extensionModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_ENERGIZED_EXTENSION);
			straightModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_ENERGIZED_STRAIGHT);
		} else if (tier == StaticPowerTiers.LUMUM) {
			extensionModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_LUMUM_EXTENSION);
			straightModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_LUMUM_STRAIGHT);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			extensionModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_CREATIVE_EXTENSION);
			straightModel = event.getModelRegistry()
					.get(StaticPowerAdditionalModels.CABLE_FLUID_INDUSTRIAL_CREATIVE_STRAIGHT);
		}

		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(world, pos);
		if (cable instanceof FluidCableComponent) {
			FluidStack fluid = ((FluidCableComponent) cable).getFluidInTank(0);
			if (!fluid.isEmpty()) {
				FluidAttributes attributes = fluid.getFluid().getAttributes();
				return attributes.getLuminosity(fluid);
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
			return TileEntityFluidCable.TYPE_INDUSTRIAL_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return TileEntityFluidCable.TYPE_INDUSTRIAL_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return TileEntityFluidCable.TYPE_INDUSTRIAL_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return TileEntityFluidCable.TYPE_INDUSTRIAL_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return TileEntityFluidCable.TYPE_INDUSTRIAL_LUMUM.create(pos, state);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return TileEntityFluidCable.TYPE_INDUSTRIAL_CREATIVE.create(pos, state);
		}
		return null;
	}
}
