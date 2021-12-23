package theking530.staticpower.tileentities.powered.heatsink;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.utilities.HarvestLevel;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;

import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock.HasGuiType;

public class BlockHeatSink extends StaticPowerTileEntityBlock {
	public final ResourceLocation tier;

	public BlockHeatSink(String name, ResourceLocation tier) {
		super(name, Block.Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE).strength(3.5f).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()));
		this.tier = tier;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		tooltip.add(HeatTooltipUtilities.getHeatConductivityTooltip(tierObject.heatSinkConductivity.get()));
		tooltip.add(HeatTooltipUtilities.getHeatCapacityTooltip(tierObject.heatSinkCapacity.get()));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		super.getAdvancedTooltip(stack, worldIn, tooltip);
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		tooltip.add(HeatTooltipUtilities.getHeatGenerationTooltip(tierObject.heatSinkElectricHeatGeneration.get()));
		tooltip.add(new TextComponent(ChatFormatting.GRAY + "Generation Usage: ").append(GuiTextUtilities.formatEnergyRateToString(tierObject.heatSinkElectricHeatPowerUsage.get()))
				.withStyle(ChatFormatting.RED));
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (tier == StaticPowerTiers.COPPER) {
			return TileEntityHeatSink.TYPE_COPPER.create();
		} else if (tier == StaticPowerTiers.TIN) {
			return TileEntityHeatSink.TYPE_TIN.create();
		} else if (tier == StaticPowerTiers.SILVER) {
			return TileEntityHeatSink.TYPE_SILVER.create();
		} else if (tier == StaticPowerTiers.GOLD) {
			return TileEntityHeatSink.TYPE_GOLD.create();
		} else if (tier == StaticPowerTiers.ALUMINIUM) {
			return TileEntityHeatSink.TYPE_ALUMINIUM.create();
		}
		return null;
	}
}
