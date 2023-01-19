package theking530.staticpower.cables.power;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

public class BlockPowerCable extends AbstractCableBlock {
	private final boolean insulated;

	public BlockPowerCable(ResourceLocation tier, boolean insulated) {
		super(tier, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.5f);
		this.insulated = insulated;
	}

	public boolean isInsulated() {
		return insulated;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.SNEAKING_ONLY;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);

		PowerTooltips.addMaximumCurrentTooltip(tooltip, tierObject.cablePowerConfiguration.cableMaxCurrent.get());
		PowerTooltips.addPowerLossPerBlockTooltip(tooltip, tierObject.cablePowerConfiguration.cablePowerLossPerBlock.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		ResourceLocation extensionModel = null;
		ResourceLocation straightModel = null;
		ResourceLocation attachmentModel = null;

		if (tier == StaticPowerTiers.BASIC) {
			extensionModel = StaticPowerAdditionalModels.CABLE_POWER_BASIC_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_POWER_BASIC_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_POWER_BASIC_ATTACHMENT;
		} else if (tier == StaticPowerTiers.ADVANCED) {
			extensionModel = StaticPowerAdditionalModels.CABLE_POWER_ADVANCED_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_POWER_ADVANCED_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_POWER_ADVANCED_ATTACHMENT;
		} else if (tier == StaticPowerTiers.STATIC) {
			extensionModel = StaticPowerAdditionalModels.CABLE_POWER_STATIC_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_POWER_STATIC_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_POWER_STATIC_ATTACHMENT;
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			extensionModel = StaticPowerAdditionalModels.CABLE_POWER_ENERGIZED_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_POWER_ENERGIZED_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_POWER_ENERGIZED_ATTACHMENT;
		} else if (tier == StaticPowerTiers.LUMUM) {
			extensionModel = StaticPowerAdditionalModels.CABLE_POWER_LUMUM_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_POWER_LUMUM_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_POWER_LUMUM_ATTACHMENT;
		} else if (tier == StaticPowerTiers.CREATIVE) {
			extensionModel = StaticPowerAdditionalModels.CABLE_POWER_CREATIVE_EXTENSION;
			straightModel = StaticPowerAdditionalModels.CABLE_POWER_CREATIVE_STRAIGHT;
			attachmentModel = StaticPowerAdditionalModels.CABLE_POWER_CREATIVE_ATTACHMENT;
		}

		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntityPowerCable.TYPE.create(pos, state);
	}
}
