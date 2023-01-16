package theking530.staticpower.blockentities.power.rectifier;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;

public class BlockRectifier extends StaticPowerMachineBlock {
	public static final VoxelShape X_AXIS_SHAPE;
	public static final VoxelShape Z_AXIS_SHAPE;

	static {
		VoxelShape base = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
		base = Shapes.join(base, Block.box(1.0D, 2.0D, 1.0D, 15.0D, 4.0D, 15.0D), BooleanOp.OR);
		base = Shapes.join(base, Block.box(2.5D, 4.0D, 2.5D, 13.5D, 15.0D, 13.5D), BooleanOp.OR);

		VoxelShape zAxis = Shapes.join(base, Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 3.0D), BooleanOp.OR);
		zAxis = Shapes.join(zAxis, Block.box(5.0D, 5.0D, 13.0D, 11.0D, 11.0D, 16.0D), BooleanOp.OR);
		zAxis = Shapes.join(zAxis, Block.box(2.5D, 15.0D, 2.5D, 3.5D, 16.0D, 13.5D), BooleanOp.OR);
		zAxis = Shapes.join(zAxis, Block.box(4.5D, 15.0D, 2.5D, 5.5D, 16.0D, 13.5D), BooleanOp.OR);
		zAxis = Shapes.join(zAxis, Block.box(6.5D, 15.0D, 2.5D, 7.5D, 16.0D, 13.5D), BooleanOp.OR);
		zAxis = Shapes.join(zAxis, Block.box(8.5D, 15.0D, 2.5D, 9.5D, 16.0D, 13.5D), BooleanOp.OR);
		zAxis = Shapes.join(zAxis, Block.box(10.5D, 15.0D, 2.5D, 11.5D, 16.0D, 13.5D), BooleanOp.OR);
		zAxis = Shapes.join(zAxis, Block.box(12.5D, 15.0D, 2.5D, 13.5D, 16.0D, 13.5D), BooleanOp.OR);
		Z_AXIS_SHAPE = zAxis;

		VoxelShape xAxis = Shapes.join(base, Block.box(0.0D, 5.0D, 5.0D, 3.0D, 11.0D, 11.0D), BooleanOp.OR);
		xAxis = Shapes.join(xAxis, Block.box(13.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D), BooleanOp.OR);
		xAxis = Shapes.join(xAxis, Block.box(2.5D, 15.0D, 2.5D, 13.5D, 16.0D, 3.5D), BooleanOp.OR);
		xAxis = Shapes.join(xAxis, Block.box(2.5D, 15.0D, 4.5D, 13.5D, 16.0D, 5.5D), BooleanOp.OR);
		xAxis = Shapes.join(xAxis, Block.box(2.5D, 15.0D, 6.5D, 13.5D, 16.0D, 7.5D), BooleanOp.OR);
		xAxis = Shapes.join(xAxis, Block.box(2.5D, 15.0D, 8.5D, 13.5D, 16.0D, 9.5D), BooleanOp.OR);
		xAxis = Shapes.join(xAxis, Block.box(2.5D, 15.0D, 10.5D, 13.5D, 16.0D, 11.5D), BooleanOp.OR);
		xAxis = Shapes.join(xAxis, Block.box(2.5D, 15.0D, 12.5D, 13.5D, 16.0D, 13.5D), BooleanOp.OR);

		X_AXIS_SHAPE = xAxis;
	}

	public BlockRectifier(ResourceLocation tier) {
		super(tier);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING).getAxis() == Axis.Z) {
			return Z_AXIS_SHAPE;
		} else {
			return X_AXIS_SHAPE;
		}
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {

	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityRectifier.TYPE_BASIC.create(pos, state);
	}
}
