package theking530.staticpower.tileentities.cables;

import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.TileEntityNetwork;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;

public class TileEntityPowerCable extends TileEntityBase {
	/**
	 * The container to keep track of the network. This only exists on the server.
	 */
	private TileEntityNetwork<TileEntity> network;
	/**
	 * The energy buffer for the cable.
	 */
	public final EnergyStorageComponent energyStorage;

	public TileEntityPowerCable() {
		super(ModTileEntityTypes.POWER_CABLE);
		registerComponent(energyStorage = new EnergyStorageComponent("PowerBuffer", 5));
	}

	@Override
	public void process() {
		if (!world.isRemote() && energyStorage.getEnergyStored() > 0 && network != null) {
			network.updateNetwork(this, true);
			supplyPower();
		}
	}

	@Override
	public void onInitializedInWorld(World world, BlockPos pos) {
		super.onInitializedInWorld(world, pos);
		if (!world.isRemote()) {
			makeNetwork();
		}
	}

	private void supplyPower() {
		// Visit all the tiles in the network and try to supply power.
		for (TileEntity te : network.getAllNetworkTiles().values()) {
			// Make sure we have enough power. If we don't stop looping.
			if (energyStorage.getEnergyStored() > 0) {
				// Get the energy storage and attempt to supply it with power.
				LazyOptional<IEnergyStorage> energy = te.getCapability(CapabilityEnergy.ENERGY, Direction.WEST);
				energy.ifPresent((storage) -> {
					int transfered = storage.receiveEnergy(energyStorage.getEnergyStored(), false);
					energyStorage.extractEnergy(transfered, false);
				});
			} else {
				break;
			}
		}
	}

	private void makeNetwork() {
		if (!world.isRemote()) {
			network = new TileEntityNetwork<TileEntity>(getWorld(), this::isValidTileEntityForNetwork, this::isValidExtenderForNetwork);
		}
	}

	private boolean isValidTileEntityForNetwork(TileEntity tileEntity, Direction dir) {
		// If the tile entity is another power cable, don't consider supplying it with
		// power.
		if (tileEntity instanceof TileEntityPowerCable) {
			return false;
		}

		// Get the energy storage. If it is not present, return false.
		LazyOptional<IEnergyStorage> energy = tileEntity.getCapability(CapabilityEnergy.ENERGY, dir);
		if (!energy.isPresent()) {
			return false;
		}

		// Check to see if the storage can recieve.
		AtomicBoolean canRecieve = new AtomicBoolean();
		energy.ifPresent((storage) -> {
			canRecieve.set(storage.canReceive());
		});
		return canRecieve.get();
	}

	private boolean isValidExtenderForNetwork(BlockPos position, Direction dir) {
		if (world.getTileEntity(position) instanceof TileEntityPowerCable) {
			return true;
		}
		return false;
	}
}
