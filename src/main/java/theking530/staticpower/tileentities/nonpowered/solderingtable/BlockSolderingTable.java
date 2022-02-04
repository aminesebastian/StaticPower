package theking530.staticpower.tileentities.nonpowered.solderingtable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;

public class BlockSolderingTable extends StaticPowerTileEntityBlock {
	protected static final VoxelShape SHAPE;

	static {
		VoxelShape top = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 14.0D, 16.0D);
		top = Shapes.or(top, Block.box(0.0D, 0.0D, 14.0D, 2.0D, 14.0D, 16.0D));
		top = Shapes.or(top, Block.box(0.0D, 0.0D, 0.0D, 2.0D, 14.0D, 2.0D));
		top = Shapes.or(top, Block.box(14.0D, 0.0D, 0.0D, 16.0D, 14.0D, 2.0D));
		top = Shapes.or(top, Block.box(14.0D, 0.0D, 14.0D, 16.0D, 14.0D, 16.0D));
		SHAPE = top;
	}

	public BlockSolderingTable(String name) {
		super(name, Block.Properties.of(Material.METAL).noOcclusion().strength(3.5f).harvestTool(ToolType.PICKAXE));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return TileEntitySolderingTable.TYPE.create(pos, state);
	}
}
