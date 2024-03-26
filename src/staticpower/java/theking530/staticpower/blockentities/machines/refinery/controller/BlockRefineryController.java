package theking530.staticpower.blockentities.machines.refinery.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import theking530.staticcore.blockentity.components.multiblock.MultiblockBlockStateProperties;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlock;

public class BlockRefineryController extends BaseRefineryBlock {

	public BlockRefineryController() {
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		return super.getDefaultStateForRegistration().setValue(MultiblockBlockStateProperties.IS_MASTER, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(MultiblockBlockStateProperties.IS_MASTER);
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityRefineryController.TYPE.create(pos, state);
	}
}
