package theking530.staticpower.tileentities;

import net.minecraftforge.common.util.Constants.BlockFlags;

public class TileEntityUpdateRequest {
	private final int flags;
	private final boolean dataSync;

	private TileEntityUpdateRequest(int flags) {
		this.flags = flags;
		this.dataSync = false;
	}

	private TileEntityUpdateRequest(int flags, boolean dataSync) {
		this.flags = flags;
		this.dataSync = true;
	}

	public int getFlags() {
		return this.flags;
	}

	public boolean syncData() {
		return dataSync;
	}

	public static TileEntityUpdateRequest blockUpdate() {
		return new TileEntityUpdateRequest(BlockFlags.BLOCK_UPDATE);
	}

	public static TileEntityUpdateRequest blockUpdateAndNotifyNeighbors() {
		return new TileEntityUpdateRequest(BlockFlags.DEFAULT);
	}

	public static TileEntityUpdateRequest blockUpdateAndNotifyNeighborsAndRender() {
		return new TileEntityUpdateRequest(BlockFlags.DEFAULT_AND_RERENDER);
	}

	public static TileEntityUpdateRequest render() {
		return new TileEntityUpdateRequest(BlockFlags.RERENDER_MAIN_THREAD);
	}

	public static TileEntityUpdateRequest syncDataOnly() {
		return new TileEntityUpdateRequest(0, true);
	}
}
