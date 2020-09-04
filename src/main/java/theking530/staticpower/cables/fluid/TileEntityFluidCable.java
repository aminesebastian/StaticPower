package theking530.staticpower.cables.fluid;

import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFluidCable;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityFluidCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 2.0f / 16.0f, 100), TileEntityRenderFluidCable::new, ModBlocks.FluidCable);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityFluidCable> TYPE_INDUSTRIAL = new TileEntityTypeAllocator<TileEntityFluidCable>(
			(allocator) -> new TileEntityFluidCable(allocator, 3.5f / 16.0f, 1000), TileEntityRenderFluidCable::new, ModBlocks.IndustrialFluidCable);

	public FluidCableComponent fluidCableComponent;
	public float fluidRenderRadius;

	public TileEntityFluidCable(TileEntityTypeAllocator<TileEntityFluidCable> allocator, float radius, int capacity) {
		super(allocator);
		registerComponent(fluidCableComponent = new FluidCableComponent("FluidCableComponent", capacity));
		fluidRenderRadius = radius;
	}
}
