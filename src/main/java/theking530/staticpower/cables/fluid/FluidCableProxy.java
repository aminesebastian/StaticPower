package theking530.staticpower.cables.fluid;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.utilities.SDMath;

public class FluidCableProxy {
	public static final String FLUID_CAPACITY_DATA_TAG_KEY = "c";
	public static final String FLUID_STORED_DATA_TAG_KEY = "s";
	public static final String FLUID_TRANSFER_RATE_DATA_TAG_KEY = "r";
	public static final String FLUID_CABLE_INDUSTRIAL_DATA_TAG_KEY = "i";

	private final ServerCable cable;
	private final Set<Direction> suppliedFromDirections;

	public FluidCableProxy(ServerCable cable) {
		this.cable = cable;
		this.suppliedFromDirections = new HashSet<>();
	}

	public void clearSuppliedFromDirections() {
		this.suppliedFromDirections.clear();
	}

	public void addSuppliedFromDirection(Direction direction) {
		this.suppliedFromDirections.add(direction);
	}

	public boolean wasSuppliedFromDirection(Direction direction) {
		return this.suppliedFromDirections.contains(direction);
	}

	public int getCapacity() {
		return cable.getDataTag().getInt(FLUID_CAPACITY_DATA_TAG_KEY);
	}

	public void setCapacity(int capacity) {
		cable.getDataTag().putInt(FLUID_CAPACITY_DATA_TAG_KEY, Math.max(0, capacity));
		setStored(Math.min(getStored(), capacity));
	}

	public int getStored() {
		return cable.getDataTag().getInt(FLUID_STORED_DATA_TAG_KEY);
	}

	public void setStored(int stored) {
		cable.getDataTag().putInt(FLUID_STORED_DATA_TAG_KEY, SDMath.clamp(stored, 0, getCapacity()));
	}

	public BlockPos getPos() {
		return cable.getPos();
	}

	public int getTransferRate() {
		return cable.getDataTag().getInt(FLUID_TRANSFER_RATE_DATA_TAG_KEY);
	}

	public boolean isIndustrial() {
		return cable.getDataTag().getBoolean(FLUID_CABLE_INDUSTRIAL_DATA_TAG_KEY);
	}

	public int fill(int amount, boolean simulate) {
		int toFill = Math.min(getCapacity() - getStored(), amount);
		if (!simulate) {
			setStored(getStored() + toFill);
		}
		return toFill;
	}

	public int drain(int amount, boolean simulate) {
		int toDrain = Math.min(getStored(), amount);
		if (!simulate) {
			setStored(getStored() - toDrain);
		}
		return toDrain;
	}

	@Override
	public String toString() {
		return "FluidCableProxy [pos=" + cable.getPos() + ", capacity=" + getCapacity() + ", stored=" + getStored() + "]";
	}
}