package theking530.staticpower.cables.network;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class CableScanLocation {
	private final BlockPos location;
	@Nullable
	private final Direction side;
	private final boolean isSparseLink;

	public CableScanLocation(BlockPos location, @Nullable Direction side, boolean isSparseLink) {
		this.location = location;
		this.side = side;
		this.isSparseLink = isSparseLink;
	}

	public BlockPos getLocation() {
		return location;
	}

	@Nullable
	public Direction getSide() {
		return side;
	}

	public boolean isSparseLink() {
		return isSparseLink;
	}

}
