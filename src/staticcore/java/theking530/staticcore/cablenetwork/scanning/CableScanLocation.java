package theking530.staticcore.cablenetwork.scanning;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class CableScanLocation {
	private final BlockPos location;
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

	public Direction getSide() {
		return side;
	}

	public boolean isSparseLink() {
		return isSparseLink;
	}

}
