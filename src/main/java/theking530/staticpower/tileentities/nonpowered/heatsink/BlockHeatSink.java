package theking530.staticpower.tileentities.nonpowered.heatsink;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.components.heat.HeatUtilities;
import theking530.staticpower.utilities.HarvestLevel;

public class BlockHeatSink extends StaticPowerBlock {

	public BlockHeatSink(String name) {
		super(name, Block.Properties.create(Material.IRON).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3.5f).harvestLevel(HarvestLevel.IRON_TOOL.ordinal()));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		super.getBasicTooltip(stack, worldIn, tooltip);
		tooltip.add(HeatUtilities.getHeatTooltip(25.0f));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.HEAT_SINK.create();
	}
}
