package theking530.staticpower.cables.fluid;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityFluidCable extends TileEntityBase {
	public FluidCableComponent fluidCableComponent;
	public float fluidRenderRadius;

	public TileEntityFluidCable(TileEntityType<TileEntityFluidCable> type, float radius, int capacity) {
		super(type);
		registerComponent(fluidCableComponent = new FluidCableComponent("FluidCableComponent", capacity));
		fluidRenderRadius = radius;
	}
}
