package theking530.staticpower.tileentities.cables.power;

import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.cables.AbstractCableTileEntity;
import theking530.staticpower.tileentities.components.EnergyStorageComponent;

public class TileEntityPowerCable extends AbstractCableTileEntity<PowerCableWrapper> {

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
		if (!world.isRemote) {
			validateTrackedByNetwork();
			//getWrapper().getNetwork().updateGraph(world, getPos());
		}
	}

	@Override
	public PowerCableWrapper createWrapper() {
		return new PowerCableWrapper(world, getPos());
	}
}
