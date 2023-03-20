package theking530.staticpower.cables.fluid;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.cablenetwork.AbstractCableBlock;
import theking530.staticcore.cablenetwork.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.gui.GuiTextUtilities;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.StaticPowerAdditionalModels.CableModelSet;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.init.ModCreativeTabs;

public class BlockCapillaryFluidCable extends AbstractCableBlock {

	public BlockCapillaryFluidCable(ResourceLocation tier) {
		super(ModCreativeTabs.CABLES, tier, new CableBoundsCache(1.25D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.5f);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		GuiTextUtilities.addColoredBulletTooltip(tooltip, "gui.staticpower.max_fluid_rate", ChatFormatting.AQUA,
				GuiTextUtilities.formatFluidRateToString(StaticCoreConfig.getTier(getTier()).cableFluidConfiguration.cableCapillaryFluidTransferRate.get()).getString());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		CableModelSet model = StaticPowerAdditionalModels.CAPILLARY_FLUID_CABLES.get(getTier());
		return new CableBakedModel(existingModel, model);
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
		return BlockEntityFluidCable.TYPE_CAPILLARY.create(pos, state);
	}

	@Override
	public BlockItem createItemBlock() {
		return new StaticPowerItemBlock(this);
	}
}
