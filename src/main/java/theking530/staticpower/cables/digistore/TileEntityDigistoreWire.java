package theking530.staticpower.cables.digistore;

import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityDigistoreWire extends BaseDigistoreTileEntity {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityDigistoreWire> TYPE = new TileEntityTypeAllocator<TileEntityDigistoreWire>((type) -> new TileEntityDigistoreWire(),
			ModBlocks.DigistoreWire);

	public TileEntityDigistoreWire() {
		super(TYPE);
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return false;
	}
}
