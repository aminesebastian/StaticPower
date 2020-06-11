package theking530.staticpower.tileentities.cables.power;

import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.CableWrapperProviderComponent;
import theking530.staticpower.tileentities.network.factories.cables.CableTypes;

public class TileEntityPowerCable extends TileEntityBase {

	public TileEntityPowerCable() {
		super(ModTileEntityTypes.POWER_CABLE);
		registerComponent(new CableWrapperProviderComponent("EnergyCableComponent", CableTypes.BASIC_POWER));
		registerComponent(new PowerCableComponent("PowerCableComponent"));
	}

	@Override
	public void process() {

	}
}
