package theking530.staticpower.tileentities.powered.battery;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.client.rendering.blocks.BatteryBlockedBakedModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.tileentities.interfaces.IBreakSerializeable;

import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock.HasGuiType;

public class BlockBattery extends StaticPowerMachineBlock {

	public ResourceLocation tier;

	public BlockBattery(String name, ResourceLocation tier) {
		super(name);
		this.tier = tier;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		// Add tooltip for any break serializeable values.
		if (IBreakSerializeable.doesItemStackHaveSerializeData(stack)) {
			CompoundTag nbt = IBreakSerializeable.getSerializeDataFromItemStack(stack);
			if (nbt.contains("MainEnergyStorage")) {
				CompoundTag energyNbt = nbt.getCompound("MainEnergyStorage").getCompound("EnergyStorage");
				tooltip.add(GuiTextUtilities.createTooltipBulletpoint("gui.staticpower.stored_power", ChatFormatting.AQUA)
						.append(GuiTextUtilities.formatEnergyToString(energyNbt.getLong("current_power"))));
			}
		}

		tooltip.add(GuiTextUtilities.createTooltipBulletpoint("gui.staticpower.capacity", ChatFormatting.GREEN)
				.append(GuiTextUtilities.formatEnergyToString(StaticPowerConfig.getTier(tier).batteryCapacity.get())));
		tooltip.add(GuiTextUtilities.createTooltipBulletpoint("gui.staticpower.max_input", ChatFormatting.BLUE)
				.append(GuiTextUtilities.formatEnergyRateToString(StaticPowerConfig.getTier(tier).batteryMaxIO.get())));
		tooltip.add(GuiTextUtilities.createTooltipBulletpoint("gui.staticpower.max_output", ChatFormatting.GOLD)
				.append(GuiTextUtilities.formatEnergyRateToString(StaticPowerConfig.getTier(tier).batteryMaxIO.get())));
		tooltip.add(GuiTextUtilities.createTooltipBulletpoint("gui.staticpower.battery_block_charging_tooltip", ChatFormatting.GRAY));
	}

	@Override
	public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		if (blockAccess.getBlockEntity(pos) instanceof TileEntityBattery) {
			TileEntityBattery battery = (TileEntityBattery) blockAccess.getBlockEntity(pos);
			return battery.shouldOutputRedstoneSignal() ? 15 : 0;
		}
		return 0;
	}

	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		if (blockAccess.getBlockEntity(pos) instanceof TileEntityBattery) {
			TileEntityBattery battery = (TileEntityBattery) blockAccess.getBlockEntity(pos);
			return battery.shouldOutputRedstoneSignal() ? 15 : 0;
		}
		return 0;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (tier == StaticPowerTiers.BASIC) {
			return TileEntityBattery.TYPE_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return TileEntityBattery.TYPE_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return TileEntityBattery.TYPE_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return TileEntityBattery.TYPE_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return TileEntityBattery.TYPE_LUMUM.create(pos, state);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return TileEntityBattery.TYPE_CREATIVE.create(pos, state);
		}
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new BatteryBlockedBakedModel(existingModel);
	}
}
