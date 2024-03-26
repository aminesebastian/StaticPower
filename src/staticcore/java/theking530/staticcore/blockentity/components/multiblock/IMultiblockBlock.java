package theking530.staticcore.blockentity.components.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IMultiblockBlock {
	public void addedToMultiblock(Level level, BlockPos pos, MultiblockState state);

	public void onMultiblockStateChanged(Level level, BlockPos pos, MultiblockState oldState, MultiblockState newState);

	public void onRemovedFromMultiblock(Level level, BlockPos pos, MultiblockState state);
}
