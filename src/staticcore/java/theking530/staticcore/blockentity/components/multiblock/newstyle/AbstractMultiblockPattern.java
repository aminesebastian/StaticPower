package theking530.staticcore.blockentity.components.multiblock.newstyle;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState.MultiblockStateEntry;

public abstract class AbstractMultiblockPattern {

	public abstract boolean isValidBlockForPosition(BlockState state, BlockPos relativePos,
			boolean bypassExistingCheck);

	public abstract MultiblockState isStateStillValid(MultiblockState existingStat, Level level);

	public abstract MultiblockState checkWellFormed(Level level, BlockPos startPos);

	public abstract boolean isValidBlock(BlockState state);

	public abstract int getMaxX();

	public abstract int getMaxY();

	public abstract int getMaxZ();

	public abstract boolean canBeMaster(BlockPos pos, BlockState state);

	public void onWellFormedOnPlaceEvent(MultiblockState state, BlockEvent.EntityPlaceEvent event) {
		for (MultiblockStateEntry entry : state.getBlocks()) {
			// Update the block state.
			BlockState blockState = entry.blockState();
			if (!blockState.isAir() && blockState.hasProperty(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
				BlockState newState = blockState.setValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK, true);

				if (entry.pos().equals(state.getMasterPos())) {
					newState = newState.setValue(MultiblockBlockStateProperties.IS_MASTER, true);
				}

				newState = modifyBlockStateOnFormed(newState, entry.pos(), event.getLevel());
				event.getLevel().setBlock(entry.pos(), newState, 3);
			}

			// Update the multiblock components.
			BlockEntity be = event.getLevel().getBlockEntity(entry.pos());
			if (be instanceof BlockEntityBase) {
				BlockEntityBase beb = (BlockEntityBase) be;
				NewMultiblockComponent<?> component = beb.getComponent(NewMultiblockComponent.class);
				component.addedToMultiblock(state);
			}
		}
	}

	protected BlockState modifyBlockStateOnFormed(BlockState in, BlockPos pos, LevelAccessor levelAccessor) {
		return in;
	}

	public void onMultiblockBroken(MultiblockState state, Level level) {
		for (MultiblockStateEntry entry : state.getBlocks()) {
			// Update the multiblock components.
			BlockEntity be = level.getBlockEntity(entry.pos());
			if (be instanceof BlockEntityBase) {
				BlockEntityBase beb = (BlockEntityBase) be;
				NewMultiblockComponent<?> component = beb.getComponent(NewMultiblockComponent.class);
				component.onRemovedFromMultiblock(MultiblockState.FAILED);
			}

			// Update the block state.
			BlockState existingState = level.getBlockState(entry.pos());
			if (!existingState.isAir()
					&& existingState.hasProperty(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
				BlockState newState = existingState
						.setValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK, false)
						.setValue(MultiblockBlockStateProperties.IS_MASTER, false);
				newState = modifyBlockStateOnBroken(newState, entry.pos(), level);

				level.setBlock(entry.pos(), newState, 3);
			}
		}
	}

	protected BlockState modifyBlockStateOnBroken(BlockState in, BlockPos pos, Level level) {
		return in;
	}
}
