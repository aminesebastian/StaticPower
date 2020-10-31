package theking530.staticpower.cables.heat;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.CableBoundsCache;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockHeatCable extends AbstractCableBlock {
	public final ResourceLocation tier;

	public BlockHeatCable(String name, ResourceLocation tier) {
		super(name, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)));
		this.tier = tier;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		tooltip.add(HeatTooltipUtilities.getHeatRateTooltip(StaticPowerConfig.getTier(tier).heatCableConductivity.get()));
		tooltip.add(HeatTooltipUtilities.getHeatCapacityTooltip(StaticPowerConfig.getTier(tier).heatCableCapacity.get()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		IBakedModel extensionModel = null;
		IBakedModel straightModel = null;
		IBakedModel attachmentModel = null;

		if (tier == StaticPowerTiers.COPPER) {
			extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_COPPER_EXTENSION);
			straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_COPPER_STRAIGHT);
			attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_COPPER_ATTACHMENT);
		} else if (tier == StaticPowerTiers.TIN) {
			extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_TIN_EXTENSION);
			straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_TIN_STRAIGHT);
			attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_TIN_ATTACHMENT);
		} else if (tier == StaticPowerTiers.SILVER) {
			extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_SILVER_EXTENSION);
			straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_SILVER_STRAIGHT);
			attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_SILVER_ATTACHMENT);
		} else if (tier == StaticPowerTiers.GOLD) {
			extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_GOLD_EXTENSION);
			straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_GOLD_STRAIGHT);
			attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_GOLD_ATTACHMENT);
		} else if (tier == StaticPowerTiers.ALUMINIUM) {
			extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_ALUMINIUM_EXTENSION);
			straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_ALUMINIUM_STRAIGHT);
			attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_HEAT_ALUMINIUM_ATTACHMENT);
		}
		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		if (tier == StaticPowerTiers.COPPER) {
			return TileEntityHeatCable.TYPE_COPPER.create();
		} else if (tier == StaticPowerTiers.TIN) {
			return TileEntityHeatCable.TYPE_TIN.create();
		} else if (tier == StaticPowerTiers.SILVER) {
			return TileEntityHeatCable.TYPE_SILVER.create();
		} else if (tier == StaticPowerTiers.GOLD) {
			return TileEntityHeatCable.TYPE_GOLD.create();
		} else if (tier == StaticPowerTiers.ALUMINIUM) {
			return TileEntityHeatCable.TYPE_ALUMINIUM.create();
		}
		return null;
	}
}
