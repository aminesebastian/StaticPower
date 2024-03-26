package theking530.staticcore.blockentity.components.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import theking530.staticcore.blockentity.components.multiblock.manager.IMultiblockManager;
import theking530.staticcore.blockentity.components.multiblock.manager.MultiblockManager;

public class MultiblockUtilities {
	public boolean isBlockPartOfMultiblock(Level level, BlockPos pos) {
		IMultiblockManager mbManager = MultiblockManager.get(level);
		return mbManager.containsBlock(pos);
	}
}
