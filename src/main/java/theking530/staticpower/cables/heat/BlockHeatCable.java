package theking530.staticpower.cables.heat;

import java.util.List;

import javax.annotation.Nullable;

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
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockHeatCable extends AbstractCableBlock {

	public BlockHeatCable(ResourceLocation tier) {
		super(tier, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.5f);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(HeatTooltipUtilities.getHeatConductivityTooltip(StaticPowerConfig.getTier(tier).cableHeatConfiguration.heatCableConductivity.get()));
		tooltip.add(HeatTooltipUtilities.getMaximumHeatTooltip(StaticPowerConfig.getTier(tier).cableHeatConfiguration.heatCableCapacity.get()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		ResourceLocation extensionModel = null;
		ResourceLocation straightModel = null;
		ResourceLocation attachmentModel = null;

		if (tier == StaticPowerTiers.COPPER) {
			extensionModel = StaticPowerAdditionalModels.CABLE_HEAT_COPPER_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_HEAT_COPPER_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_HEAT_COPPER_ATTACHMENT;
		} else if (tier == StaticPowerTiers.TIN) {
			extensionModel = StaticPowerAdditionalModels.CABLE_HEAT_TIN_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_HEAT_TIN_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_HEAT_TIN_ATTACHMENT;
		} else if (tier == StaticPowerTiers.SILVER) {
			extensionModel = StaticPowerAdditionalModels.CABLE_HEAT_SILVER_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_HEAT_SILVER_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_HEAT_SILVER_ATTACHMENT;
		} else if (tier == StaticPowerTiers.GOLD) {
			extensionModel = StaticPowerAdditionalModels.CABLE_HEAT_GOLD_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_HEAT_GOLD_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_HEAT_GOLD_ATTACHMENT;
		} else if (tier == StaticPowerTiers.ALUMINUM) {
			extensionModel = StaticPowerAdditionalModels.CABLE_HEAT_ALUMINUM_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_HEAT_ALUMINUM_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_HEAT_ALUMINUM_ATTACHMENT;
		}
		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (tier == StaticPowerTiers.COPPER) {
			return BlockEntityHeatCable.TYPE_COPPER.create(pos, state);
		} else if (tier == StaticPowerTiers.GOLD) {
			return BlockEntityHeatCable.TYPE_GOLD.create(pos, state);
		} else if (tier == StaticPowerTiers.ALUMINUM) {
			return BlockEntityHeatCable.TYPE_ALUMINUM.create(pos, state);
		}
		return null;
	}
}
