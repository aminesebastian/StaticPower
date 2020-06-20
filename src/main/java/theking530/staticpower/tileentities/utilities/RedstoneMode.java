package theking530.staticpower.tileentities.utilities;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum RedstoneMode {
	Ignore, Low, High;

	public static boolean evaluateRedstoneMode(RedstoneMode mode, World world, BlockPos pos) {
		// Get the redstone signal at the block.
		int redstoneSignal = world.getRedstonePowerFromNeighbors(pos);

		// If we're ignoring, just return true.
		if (mode == RedstoneMode.Ignore) {
			return true;
		}

		// If we're set to low mode, only return true if the current redstone signal is
		// low (0).
		if (mode == RedstoneMode.Low) {
			return redstoneSignal == 0;
		}

		// If we're set to high mode, only return true if the current redstone signal is
		// greater than high (> 0).
		if (mode == RedstoneMode.High) {
			return redstoneSignal > 0;
		}

		return false;
	}
}
