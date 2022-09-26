package theking530.staticpower.blockentities.machines.hydroponicpod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockHydroponicPod extends StaticPowerMachineBlock {
	public static final VoxelShape SHAPE;
	static {
		VoxelShape result = Block.box(0, 0, 0, 16, 3, 16);
		result = Shapes.join(result, Block.box(0, 14, 0, 16, 16, 16), BooleanOp.OR);
		result = Shapes.join(result, Block.box(1, 2, 1, 15, 14, 15), BooleanOp.OR);	
		SHAPE = result;
	}

	public BlockHydroponicPod() {
		super(StaticPowerTiers.ADVANCED, Properties.of(Material.GLASS, MaterialColor.TERRACOTTA_WHITE).noOcclusion());
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
		return BlockEntityHydroponicPod.TYPE.create(pos, state);
	}
}
