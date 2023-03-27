package theking530.staticpower.cables.heat;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.cablenetwork.AbstractCableBlock;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.StaticPowerAdditionalModels.CableModelSet;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.init.ModCreativeTabs;

public class BlockHeatCable extends AbstractCableBlock {

	public BlockHeatCable(ResourceLocation tier) {
		super(ModCreativeTabs.CABLES, tier, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.5f);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(HeatTooltipUtilities.getHeatConductivityTooltip(StaticCoreConfig.getTier(getTier()).cableHeatConfiguration.heatCableConductivity.get()));
		tooltip.add(HeatTooltipUtilities.getMaximumHeatTooltip(StaticCoreConfig.getTier(getTier()).cableHeatConfiguration.heatCableCapacity.get()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		CableModelSet model = null;

		if (getTier() == StaticCoreTiers.COPPER) {
			model = StaticPowerAdditionalModels.CABLE_HEAT_COPPER;
		} else if (getTier() == StaticCoreTiers.GOLD) {
			model = StaticPowerAdditionalModels.CABLE_HEAT_GOLD;
		} else if (getTier() == StaticCoreTiers.ALUMINUM) {
			model = StaticPowerAdditionalModels.CABLE_HEAT_ALUMINUM;
		}
		return new CableBakedModel(existingModel, model);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntityHeatCable.TYPE.create(pos, state);
	}

	@Override
	public BlockItem createItemBlock() {
		return new StaticPowerItemBlock(this);
	}
}
