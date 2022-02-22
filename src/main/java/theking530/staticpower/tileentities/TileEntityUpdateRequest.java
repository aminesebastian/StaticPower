package theking530.staticpower.tileentities;

import net.minecraft.world.level.block.Block;

public class TileEntityUpdateRequest {
	private final int flags;
	private final boolean dataSync;
	private final boolean renderOnDataSync;

	private TileEntityUpdateRequest(int flags) {
		this.flags = flags;
		this.dataSync = false;
		renderOnDataSync = false;
	}

	private TileEntityUpdateRequest(int flags, boolean dataSync, boolean renderOnDataSync) {
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

	public static TileEntityUpdateRequest blockUpdate() {
		return new TileEntityUpdateRequest(Block.UPDATE_CLIENTS);
	}

	public static TileEntityUpdateRequest blockUpdateAndNotifyNeighbors() {
		return new TileEntityUpdateRequest(Block.UPDATE_ALL);
	}

	public static TileEntityUpdateRequest blockUpdateAndNotifyNeighborsAndRender() {
		return new TileEntityUpdateRequest(Block.UPDATE_ALL_IMMEDIATE);
	}

	public static TileEntityUpdateRequest render() {
		return new TileEntityUpdateRequest(Block.UPDATE_IMMEDIATE);
	}

	public static TileEntityUpdateRequest syncDataOnly(boolean renderOnDataSync) {
		return new TileEntityUpdateRequest(0, true, renderOnDataSync);
	}
}
