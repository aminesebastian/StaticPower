package theking530.staticpower.tileentities.nonpowered.heatsink;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.components.heat.HeatUtilities;
import theking530.staticpower.utilities.HarvestLevel;

public class BlockHeatSink extends StaticPowerBlock {
	public final ResourceLocation tier;

	public BlockHeatSink(String name, ResourceLocation tier) {
		super(name, Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.5f).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()));
		this.tier = tier;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		super.getBasicTooltip(stack, worldIn, tooltip);
		tooltip.add(HeatUtilities.getHeatRateTooltip(StaticPowerDataRegistry.getTier(tier).getHeatCableConductivity()));
		tooltip.add(HeatUtilities.getHeatCapacityTooltip(StaticPowerDataRegistry.getTier(tier).getHeatCableCapacity()));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		if (tier == StaticPowerTiers.COPPER) {
			return ModTileEntityTypes.HEAT_SINK_COPPER.create();
		} else if (tier == StaticPowerTiers.TIN) {
			return ModTileEntityTypes.HEAT_SINK_TIN.create();
		} else if (tier == StaticPowerTiers.SILVER) {
			return ModTileEntityTypes.HEAT_SINK_SILVER.create();
		} else if (tier == StaticPowerTiers.GOLD) {
			return ModTileEntityTypes.HEAT_SINK_GOLD.create();
		} else if (tier == StaticPowerTiers.ALUMINIUM) {
			return ModTileEntityTypes.HEAT_SINK_ALUMINIUM.create();
		}
		return null;
	}
}
