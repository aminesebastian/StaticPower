package theking530.staticpower.tileentities.powered.battery;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.client.rendering.blocks.BatteryBlockedBakedModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockBattery extends StaticPowerMachineBlock {

	public ResourceLocation tier;

	public BlockBattery(String name, ResourceLocation tier) {
		super(name);
		this.tier = tier;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new StringTextComponent(TextFormatting.GREEN.toString() + "• Capacity ")
				.append(GuiTextUtilities.formatEnergyToString(StaticPowerConfig.getTier(tier).batteryCapacity.get())));
		tooltip.add(new StringTextComponent(TextFormatting.BLUE.toString() + "• Max Input ")
				.append(GuiTextUtilities.formatEnergyRateToString(StaticPowerConfig.getTier(tier).batteryMaxIO.get())));
		tooltip.add(new StringTextComponent(TextFormatting.GOLD.toString() + "• Max Output ")
				.append(GuiTextUtilities.formatEnergyRateToString(StaticPowerConfig.getTier(tier).batteryMaxIO.get())));
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (blockAccess.getTileEntity(pos) instanceof TileEntityBattery) {
			TileEntityBattery battery = (TileEntityBattery) blockAccess.getTileEntity(pos);
			return battery.shouldOutputRedstoneSignal() ? 15 : 0;
		}
		return 0;
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (blockAccess.getTileEntity(pos) instanceof TileEntityBattery) {
			TileEntityBattery battery = (TileEntityBattery) blockAccess.getTileEntity(pos);
			return battery.shouldOutputRedstoneSignal() ? 15 : 0;
		}
		return 0;
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		if (tier == StaticPowerTiers.BASIC) {
			return TileEntityBattery.TYPE_BASIC.create();
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return TileEntityBattery.TYPE_ADVANCED.create();
		} else if (tier == StaticPowerTiers.STATIC) {
			return TileEntityBattery.TYPE_STATIC.create();
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return TileEntityBattery.TYPE_ENERGIZED.create();
		} else if (tier == StaticPowerTiers.LUMUM) {
			return TileEntityBattery.TYPE_LUMUM.create();
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return TileEntityBattery.TYPE_CREATIVE.create();
		}
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new BatteryBlockedBakedModel(existingModel);
	}
}
