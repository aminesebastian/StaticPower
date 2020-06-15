package theking530.staticpower.tileentities.cables.power;

import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityPowerCable extends TileEntityBase {

	public TileEntityPowerCable() {
		super(ModTileEntityTypes.POWER_CABLE);
		registerComponent(new PowerCableComponent("PowerCableComponent"));
	}

	@Override
	public void process() {

	}
}
