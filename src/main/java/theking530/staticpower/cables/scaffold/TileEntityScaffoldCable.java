package theking530.staticpower.cables.scaffold;

import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityScaffoldCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityScaffoldCable> TYPE = new BlockEntityTypeAllocator<TileEntityScaffoldCable>((allocator) -> new TileEntityScaffoldCable(allocator),
			ModBlocks.ScaffoldCable);

	public TileEntityScaffoldCable(BlockEntityTypeAllocator<TileEntityScaffoldCable> allocator) {
		super(allocator);
		registerComponent(new ScaffoldCableComponent("ScaffoldCableComponent"));
	}
}
