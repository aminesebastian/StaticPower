package theking530.staticcore.blockentity.components.multiblock;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.network.NetworkHooks;

public abstract class AbstractMultiblockPattern {

	/**
	 * 
	 * @param level               The level containing the multiblock we're
	 *                            checking.
	 * @param state               The state of the block to check.
	 * @param absolutePos         The world position of the block to check.
	 * @param relativePos         The position of the block to check relative to the
	 *                            start position.
	 * @param bypassExistingCheck If true, we won't check if the block is already in
	 *                            a multiblock.
	 * @return
	 */
	public abstract boolean isValidBlockForPosition(Level level, BlockState state, BlockPos absolutePos,
			BlockPos relativePos, boolean bypassExistingCheck);

	public abstract MultiblockState isStateStillValid(Level level, MultiblockState existingState);

	public abstract MultiblockState checkWellFormed(Level level, BlockPos startPos);

	public abstract boolean isValidBlock(BlockState state);

	public abstract int getMaxX();

	public abstract int getMaxY();

	public abstract int getMaxZ();

	public abstract boolean canBeMaster(BlockPos pos, BlockState state);

	public abstract Collection<MultiblockMatchClass> getMatchClasses();

	public void onMemberBlockRightClicked(MultiblockState state, RightClickBlock event) {
		BlockPos masterPos = state.getMasterPos();
		BlockEntity masterBe = event.getLevel().getBlockEntity(masterPos);
		if (masterBe instanceof MenuProvider) {
			NetworkHooks.openScreen((ServerPlayer) event.getEntity(), (MenuProvider) masterBe, masterBe.getBlockPos());
		}
	}

	public void onWellFormedOnPlaceEvent(MultiblockState state, Level level) {

	}

	public BlockState modifyBlockStateOnFormed(BlockState in, BlockPos pos, LevelAccessor levelAccessor) {
		return in;
	}

	public void onMultiblockBroken(MultiblockState state, Level level) {

	}

	public BlockState modifyBlockStateOnBroken(BlockState in, BlockPos pos, Level level) {
		return in;
	}

	public void onMultiblockStateChanged(MultiblockState oldState, MultiblockState newState, Level level) {

	}
}
