package theking530.staticpower.cables.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.power.CapabilityStaticVolt;

public class DestinationWrapper {
	public enum DestinationType {
		ITEM, POWER, FORGE_POWER, FLUID, HEAT, REDSTONE_SOURCE
	}

	private final World world;
	private final TileEntity tileEntity;
	private final BlockPos pos;
	private final Map<Direction, HashSet<DestinationType>> supportedTypes;
	private final Map<BlockPos, Direction> connectedCables;
	private final BlockPos initialConnectedCable;

	public DestinationWrapper(World world, BlockPos pos, @Nullable TileEntity tileEntity, BlockPos connectedCable, Direction destinationSide) {
		this.world = world;
		this.tileEntity = tileEntity;
		this.pos = pos;
		this.connectedCables = new HashMap<BlockPos, Direction>();
		this.initialConnectedCable = connectedCable;
		this.connectedCables.put(connectedCable, destinationSide);
		supportedTypes = new HashMap<Direction, HashSet<DestinationType>>();
		populateDestinationTypes();
	}

	public TileEntity getTileEntity() {
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
	public boolean shouldBeDropped() {
		return supportedTypes.isEmpty();
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

	public void addConnectedCable(BlockPos cablePosition, Direction sideOfTargetCableIsOn) {
		this.connectedCables.put(cablePosition, sideOfTargetCableIsOn);
	}

	public HashSet<DestinationType> getTypesForSide(Direction side) {
		return supportedTypes.get(side);
	}

	public boolean supportsTypeOnSide(Direction side, DestinationType type) {
		return supportedTypes.get(side).contains(type);
	}

	public boolean supportsType(DestinationType type) {
		for (Direction dir : Direction.values()) {
			if (supportedTypes.get(dir).contains(type)) {
				return true;
			}
		}
		return false;
	}

	private void populateDestinationTypes() {
		supportedTypes.clear();
		for (Direction dir : Direction.values()) {
			HashSet<DestinationType> types = new HashSet<DestinationType>();

			// Check for all tile entity based destinations.
			if (tileEntity != null) {
				// Check for fluid capabilities.
				if (tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir).isPresent()) {
					types.add(DestinationType.FLUID);
				}
				// Check for power capabilities.
				if (tileEntity.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY, dir).isPresent()) {
					types.add(DestinationType.POWER);
				}
				// Check for forge power capabilities.
				if (tileEntity.getCapability(CapabilityEnergy.ENERGY, dir).isPresent()) {
					types.add(DestinationType.FORGE_POWER);
				}
				// Check for item capabilites.
				if (tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir).isPresent()) {
					types.add(DestinationType.ITEM);
				}
				// Check for heat capabilites.
				if (tileEntity.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, dir).isPresent()) {
					types.add(DestinationType.HEAT);
				}

			}

			// Check for redstone power.
			if (world.getBlockState(pos).getStrongPower(world, pos, dir) > 0 || world.getBlockState(pos).getStrongPower(world, pos, dir) > 0) {
				types.add(DestinationType.REDSTONE_SOURCE);
			}

			// Put the types into the supported types array.
			supportedTypes.put(dir, types);
		}
	}
}
