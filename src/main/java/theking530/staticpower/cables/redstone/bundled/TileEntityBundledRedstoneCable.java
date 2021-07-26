package theking530.staticpower.cables.redstone.bundled;

import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityBundledRedstoneCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityBundledRedstoneCable> TYPE = new TileEntityTypeAllocator<TileEntityBundledRedstoneCable>(
			(allocator) -> new TileEntityBundledRedstoneCable(allocator), ModBlocks.BundledRedstoneCable);

	public final BundledRedstoneCableComponent cableComponent;

	public TileEntityBundledRedstoneCable(TileEntityTypeAllocator<TileEntityBundledRedstoneCable> allocator) {
		super(allocator);
		registerComponent(cableComponent = new BundledRedstoneCableComponent("RedstoneCableComponent"));
	}
}
