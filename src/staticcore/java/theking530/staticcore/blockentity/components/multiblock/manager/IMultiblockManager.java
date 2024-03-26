package theking530.staticcore.blockentity.components.multiblock.manager;

import net.minecraft.core.BlockPos;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState;

public interface IMultiblockManager {
	public boolean containsBlock(BlockPos pos);

	public MultiblockState getMultiblockState(BlockPos pos);
}
