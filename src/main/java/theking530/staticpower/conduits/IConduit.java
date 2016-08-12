package theking530.staticpower.conduits;

import net.minecraft.util.math.BlockPos;

public interface IConduit {

	public boolean isConduit(BlockPos pos);
	public boolean isReciever(BlockPos pos);
}
