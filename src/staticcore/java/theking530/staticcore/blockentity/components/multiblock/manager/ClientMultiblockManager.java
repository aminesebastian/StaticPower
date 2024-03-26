package theking530.staticcore.blockentity.components.multiblock.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState.MultiblockStateEntry;

public class ClientMultiblockManager implements IMultiblockManager {
	private Map<BlockPos, MultiblockState> blocksToMasterMap;

	public ClientMultiblockManager() {
		blocksToMasterMap = new HashMap<>();
	}

	@Override
	public boolean containsBlock(BlockPos pos) {
		return blocksToMasterMap.containsKey(pos);
	}

	@Override
	public MultiblockState getMultiblockState(BlockPos pos) {
		return blocksToMasterMap.get(pos);
	}

	public void recieveSyncedStates(Collection<MultiblockState> states) {
		blocksToMasterMap.clear();
		for (MultiblockState state : states) {
			for (MultiblockStateEntry entry : state.getBlocks()) {
				blocksToMasterMap.put(entry.pos(), state);
			}
		}
	}
}
