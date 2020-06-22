package theking530.staticpower.tileentities.cables.fluid;

import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityFluidCable extends TileEntityBase {
	public FluidCableComponent fluidCableComponent;

	public TileEntityFluidCable() {
		super(ModTileEntityTypes.FLUID_CABLE);
		registerComponent(fluidCableComponent = new FluidCableComponent("FluidCableComponent"));
	}
}
