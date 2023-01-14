package theking530.staticcore.cablenetwork.manager;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.Cable;

public interface ICableNetworkManager {

	public Level getLevel();

	public String getName();

	public @Nullable Cable getCable(BlockPos currentPosition);

	public boolean isTrackingCable(BlockPos position);

	public @Nullable CableNetwork getNetworkById(long id);
}
