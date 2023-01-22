package theking530.staticpower.blockentities.power.battery;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
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
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.gui.text.PowerTooltips;
import theking530.staticcore.gui.text.TooltipUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.client.rendering.blocks.BatteryBlockedBakedModel;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockBattery extends StaticPowerMachineBlock {

	public BlockBattery(ResourceLocation tier) {
		super(tier);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.getMaximumBatteryInputVoltage();
	}

	@Override
	public double getMaximumInputPower() {
		if (tier == null) {
			return 0;
		}
		return StaticPowerConfig.getTier(tier).powerConfiguration.batteryMaximumPowerInput.get();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);

		PowerTooltips.addPowerCapacityTooltip(tooltip, tierObject.powerConfiguration.batteryCapacity.get());
		if (!isShowingAdvanced) {
			super.getAdvancedTooltip(stack, worldIn, tooltip);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		PowerTooltips.addOutputVoltageTooltip(tooltip, tierObject.powerConfiguration.batteryOutputVoltage.get());
		PowerTooltips.addMaximumOutputPowerTooltip(tooltip, tierObject.powerConfiguration.batteryMaximumPowerOutput.get());
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.battery_block_charging_tooltip", ChatFormatting.GRAY);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityBattery.TYPE_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityBattery.TYPE_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityBattery.TYPE_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityBattery.TYPE_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityBattery.TYPE_LUMUM.create(pos, state);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return BlockEntityBattery.TYPE_CREATIVE.create(pos, state);
		}
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new BatteryBlockedBakedModel(existingModel);
	}
}
