package theking530.staticcore.cablenetwork.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.destinations.CableDestination;

public class DestinationWrapper {
	private final Level level;
	private final BlockEntity tileEntity;
	private final BlockPos pos;
	private final BlockPos initialConnectedCable;
	private final Map<Direction, HashSet<CableDestination>> supportedDestinationTypes;
	private final Map<BlockPos, Direction> connectedCables;

	public DestinationWrapper(Level level, BlockPos pos, @Nullable BlockEntity tileEntity, BlockPos connectedCable, Direction destinationSide) {
		this.level = level;
		this.tileEntity = tileEntity;
		this.pos = pos;
		this.connectedCables = new HashMap<BlockPos, Direction>();
		this.initialConnectedCable = connectedCable;
		this.supportedDestinationTypes = new HashMap<Direction, HashSet<CableDestination>>();
		addConnectedCable(connectedCable, destinationSide);
	}

	public BlockEntity getTileEntity() {
		return tileEntity;
	}

	public boolean hasTileEntity() {
		return tileEntity != null;
	}

	public BlockPos getPos() {
		return pos;
	}

	/**
	 * Checks whether or not this wrapper should be dropped. This entails checking
	 * if this destinations supports any of our network types.
	 * 
	 * @return
	 */
	public boolean hasSupportedDestinationTypes(Direction side, Cable cable) {
		// If we have no types on that side, forget it.
		if (!supportedDestinationTypes.containsKey(side) || cable.getSupportedDestinationTypes().isEmpty()) {
			return false;
		}

		// Check to see if we support a supported destination type on this side.
		for (CableDestination type : cable.getSupportedDestinationTypes()) {
			if (supportedDestinationTypes.get(side).contains(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This gets the side of the destination that this cable is connected on.
	 * Meaning, when performing capability checks, this is the direction to use.
	 * 
	 * @return
	 */
	public Map<BlockPos, Direction> getConnectedCables() {
		return connectedCables;
	}

	public BlockPos getFirstConnectedCable() {
		return initialConnectedCable;
	}

	public Direction getFirstConnectedDestinationSide() {
		return connectedCables.get(initialConnectedCable);
	}

	public void addConnectedCable(BlockPos cablePosition, Direction cableSide) {
		connectedCables.put(cablePosition, cableSide.getOpposite());
		populateDestinationTypes(cablePosition, cableSide);
	}

	public boolean supportsTypeOnSide(Direction side, CableDestination type) {
		return supportedDestinationTypes.containsKey(side) && supportedDestinationTypes.get(side).contains(type);
	}

	public boolean supportsType(CableDestination type) {
		for (Direction dir : Direction.values()) {
			if (supportedDestinationTypes.containsKey(dir) && supportedDestinationTypes.get(dir).contains(type)) {
				return true;
			}
		}
		return false;
	}

	private void populateDestinationTypes(BlockPos cablePosition, Direction cableSide) {
		// Allocate a set to track all the supported types.
		HashSet<CableDestination> types = new HashSet<CableDestination>();

		// Check for all destinations to see if they're supported.
		for (CableDestination type : StaticCoreRegistries.CableDestinationRegistry()) {
			if (type.match(level, cablePosition, cableSide, pos, cableSide.getOpposite(), getTileEntity())) {
				types.add(type);
			}
		}

		// Put the types into the supported types array.
		if (!types.isEmpty()) {
			supportedDestinationTypes.put(cableSide.getOpposite(), types);
		}
	}
}
