package theking530.staticpower.tileentities.powered.solarpanels;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockSolarPanel extends StaticPowerTileEntityBlock {
	public static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.5D, 16.0D);
	public final ResourceLocation tierType;

	public BlockSolarPanel(String name, ResourceLocation tierType) {
		super(name);
		this.tierType = tierType;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		if (tierType == StaticPowerTiers.BASIC) {
			return TileEntitySolarPanel.TYPE_BASIC.create();
		} else if (tierType == StaticPowerTiers.STATIC) {
			return TileEntitySolarPanel.TYPE_STATIC.create();
		} else if (tierType == StaticPowerTiers.ENERGIZED) {
			return TileEntitySolarPanel.TYPE_ENERGIZED.create();
		} else if (tierType == StaticPowerTiers.LUMUM) {
			return TileEntitySolarPanel.TYPE_LUMUM.create();
		} else if (tierType == StaticPowerTiers.CREATIVE) {
			return TileEntitySolarPanel.TYPE_CREATIVE.create();
		}
		return null;
	}
}
