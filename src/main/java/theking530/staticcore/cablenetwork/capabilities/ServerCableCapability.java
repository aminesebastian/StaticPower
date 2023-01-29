package theking530.staticcore.cablenetwork.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.cablenetwork.Cable;

public abstract class ServerCableCapability {
	private final ServerCableCapabilityType<?> type;
	private final Cable owningCable;

	public ServerCableCapability(ServerCableCapabilityType<?> type, Cable owningCable) {
		this.type = type;
		this.owningCable = owningCable;
	}

	public final Cable getOwningCable() {
		return owningCable;
	}

	public final BlockPos getPos() {
		return owningCable.getPos();
	}

	public final ServerCableCapabilityType<?> getType() {
		return type;
	}

	public abstract void save(CompoundTag tag);

	public abstract void load(CompoundTag tag);
}
