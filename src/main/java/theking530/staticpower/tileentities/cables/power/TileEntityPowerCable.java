package theking530.staticpower.tileentities.cables.power;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.cables.AbstractCableTileEntity;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;

public class TileEntityPowerCable extends AbstractCableTileEntity {

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

	private void supplyPower() {
		// Visit all the tiles in the network and try to supply power.
		for (TileEntity te : network.getAllNetworkTiles().values()) {
			// Make sure we have enough power. If we don't stop looping.
			if (energyStorage.getEnergyStored() > 0) {
				// Get the energy storage and attempt to supply it with power.
				LazyOptional<IEnergyStorage> energy = te.getCapability(CapabilityEnergy.ENERGY, Direction.WEST);
				energy.ifPresent((storage) -> {
					if (storage.canReceive()) {
						int transfered = storage.receiveEnergy(energyStorage.getEnergyStored(), false);
						energyStorage.extractEnergy(transfered, false);
					}
				});
			} else {
				break;
			}
		}
	}

	@Override
	public boolean isValidDestinationForNetwork(TileEntity tileEntity, Direction dir) {
		if (tileEntity == null) {
			return false;
		}
		// If the tile entity is another power cable, don't consider supplying it with
		// power.
		if (tileEntity instanceof TileEntityPowerCable) {
			return false;
		}

		// Get the energy storage. If it is not present, return false.
		return tileEntity.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).isPresent();
	}

	@Override
	public boolean isValidCableForNetwork(BlockPos position, Direction dir) {
		if (world.getTileEntity(position) instanceof TileEntityPowerCable) {
			return true;
		}
		return false;
	}
}
