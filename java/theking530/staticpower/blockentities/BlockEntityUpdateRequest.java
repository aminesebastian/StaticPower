package theking530.staticpower.blockentities;

import net.minecraft.world.level.block.Block;

public class BlockEntityUpdateRequest {
	private final int flags;
	private final boolean dataSync;
	private final boolean renderOnDataSync;

	private BlockEntityUpdateRequest(int flags) {
		this.flags = flags;
		this.dataSync = false;
		renderOnDataSync = false;
	}

	private BlockEntityUpdateRequest(int flags, boolean dataSync, boolean renderOnDataSync) {
		this.flags = flags;
		this.dataSync = dataSync;
		this.renderOnDataSync = renderOnDataSync;
	}

	public int getFlags() {
		return this.flags;
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

	public static BlockEntityUpdateRequest render() {
		return new BlockEntityUpdateRequest(Block.UPDATE_IMMEDIATE);
	}

	public static BlockEntityUpdateRequest syncDataOnly(boolean renderOnDataSync) {
		return new BlockEntityUpdateRequest(0, true, renderOnDataSync);
	}
}
