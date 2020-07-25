package theking530.staticpower.tileentities.nonpowered.solderingtable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;

public class BlockSolderingTable extends StaticPowerTileEntityBlock {
	protected static final VoxelShape SHAPE;

	static {
		VoxelShape top = Block.makeCuboidShape(0.0D, 12.0D, 0.0D, 16.0D, 14.0D, 16.0D);
		top = VoxelShapes.or(top, Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 2.0D, 14.0D, 16.0D));
		top = VoxelShapes.or(top, Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 14.0D, 2.0D));
		top = VoxelShapes.or(top, Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 14.0D, 2.0D));
		top = VoxelShapes.or(top, Block.makeCuboidShape(14.0D, 0.0D, 14.0D, 16.0D, 14.0D, 16.0D));
		SHAPE = top;
	}

	public BlockSolderingTable(String name) {
		super(name, Block.Properties.create(Material.IRON).notSolid().hardnessAndResistance(3.5f).harvestTool(ToolType.PICKAXE));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.SOLDERING_TABLE.create();
	}
}
