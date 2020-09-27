package theking530.staticpower.tileentities.powered.heatsink;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
import net.minecraftforge.common.ToolType;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.utilities.HarvestLevel;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;

public class BlockHeatSink extends StaticPowerTileEntityBlock {
	public final ResourceLocation tier;

	public BlockHeatSink(String name, ResourceLocation tier) {
		super(name, Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.5f).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()));
		this.tier = tier;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		super.getBasicTooltip(stack, worldIn, tooltip);
		tooltip.add(HeatTooltipUtilities.getHeatRateTooltip(TierReloadListener.getTier(tier).getHeatSinkConductivity()));
		tooltip.add(HeatTooltipUtilities.getHeatCapacityTooltip(TierReloadListener.getTier(tier).getHeatSinkCapacity()));
		tooltip.add(HeatTooltipUtilities.getHeatGenerationTooltip(TierReloadListener.getTier(tier).getHeatSinkElectricHeatGeneration()));
		tooltip.add(new StringTextComponent(TextFormatting.GRAY.toString()).append(new StringTextComponent("Generation Usage: "))
				.append(new StringTextComponent(TextFormatting.RED.toString()))
				.append(GuiTextUtilities.formatEnergyRateToString(TierReloadListener.getTier(tier).getHeatSinkElectricHeatPowerUsage())));
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
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
