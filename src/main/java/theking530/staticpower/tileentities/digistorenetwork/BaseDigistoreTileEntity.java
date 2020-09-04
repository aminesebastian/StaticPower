package theking530.staticpower.tileentities.digistorenetwork;

import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.tileentities.TileEntityBase;

public abstract class BaseDigistoreTileEntity extends TileEntityBase {
	public final DigistoreCableProviderComponent digistoreCableProvider;

	public BaseDigistoreTileEntity(TileEntityTypeAllocator allocator) {
		super(allocator);
		registerComponent(digistoreCableProvider = new DigistoreCableProviderComponent("DigistoreCableProviderComponent").setShouldControlOnState());
	}

	public boolean isManagerPresent() {
		return digistoreCableProvider.isManagerPresent();
	}

}
