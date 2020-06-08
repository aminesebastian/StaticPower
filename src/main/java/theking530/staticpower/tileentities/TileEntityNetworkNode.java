package theking530.staticpower.tileentities;

import net.minecraft.tileentity.TileEntity;

public class TileEntityNetworkNode<T extends TileEntity> {
	private final T entry;
	private final TileEntity[] adjacents;

	public TileEntityNetworkNode(T entry, TileEntity... adjacents) {
		this.entry = entry;
		this.adjacents = adjacents;
	}

	public T getEntry() {
		return entry;
	}

	public TileEntity[] getAdjacents() {
		return adjacents;
	}
}
