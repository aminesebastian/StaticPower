package theking530.staticcore.blockentity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityUpdateRequest {
	private final int flags;
	private final boolean dataSync;
	private final boolean renderOnDataSync;
	private final BlockState newState;

	private BlockEntityUpdateRequest(int flags) {
		this(flags, false, false, null);
	}

	private BlockEntityUpdateRequest(int flags, boolean dataSync, boolean renderOnDataSync) {
		this(flags, dataSync, renderOnDataSync, null);

	}

	private BlockEntityUpdateRequest(int flags, boolean dataSync, boolean renderOnDataSync, BlockState newState) {
		this.flags = flags;
		this.dataSync = dataSync;
		this.renderOnDataSync = renderOnDataSync;
		this.newState = newState;
	}

	public int getFlags() {
		return this.flags;
	}

	public BlockState getNewBlockState() {
		return newState;
	}

	public boolean getShouldSyncData() {
		return dataSync;
	}

	public boolean getShouldRenderOnDataSync() {
		return renderOnDataSync;
	}

	public static BlockEntityUpdateRequest blockUpdate() {
		return new BlockEntityUpdateRequest(Block.UPDATE_CLIENTS);
	}

	public static BlockEntityUpdateRequest blockUpdateAndNotifyNeighbors() {
		return new BlockEntityUpdateRequest(Block.UPDATE_ALL);
	}

	public static BlockEntityUpdateRequest blockUpdateAndNotifyNeighborsAndRender() {
		return new BlockEntityUpdateRequest(Block.UPDATE_ALL_IMMEDIATE);
	}

	public static BlockEntityUpdateRequest updateBlockState(BlockState state) {
		return new BlockEntityUpdateRequest(Block.UPDATE_ALL_IMMEDIATE, false, false, state);
	}

	public static BlockEntityUpdateRequest render() {
		return new BlockEntityUpdateRequest(Block.UPDATE_IMMEDIATE);
	}

	public static BlockEntityUpdateRequest syncDataOnly(boolean renderOnDataSync) {
		return new BlockEntityUpdateRequest(0, true, renderOnDataSync);
	}
}
