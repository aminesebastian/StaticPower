package theking530.staticpower.blockentities.nonpowered.solderingtable;

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
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;

public class BlockSolderingTable extends StaticPowerRotateableBlockEntityBlock {
	protected static final VoxelShape SHAPE;

	static {
		VoxelShape top = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 14.0D, 16.0D);
		top = Shapes.or(top, Block.box(0.0D, 0.0D, 14.0D, 2.0D, 14.0D, 16.0D));
		top = Shapes.or(top, Block.box(0.0D, 0.0D, 0.0D, 2.0D, 14.0D, 2.0D));
		top = Shapes.or(top, Block.box(14.0D, 0.0D, 0.0D, 16.0D, 14.0D, 2.0D));
		top = Shapes.or(top, Block.box(14.0D, 0.0D, 14.0D, 16.0D, 14.0D, 16.0D));
		SHAPE = top;
	}

	public BlockSolderingTable() {
		super(Block.Properties.of(Material.METAL).noOcclusion().strength(3.5f));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntitySolderingTable.TYPE.create(pos, state);
	}
}
