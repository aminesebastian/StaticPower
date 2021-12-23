package theking530.staticpower.tileentities.digistorenetwork;

import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.tileentities.TileEntityBase;

public abstract class BaseDigistoreTileEntity extends TileEntityBase {
	public final DigistoreCableProviderComponent digistoreCableProvider;

	public BaseDigistoreTileEntity(BlockEntityTypeAllocator<? extends BaseDigistoreTileEntity> allocator) {
		this(allocator, 0);

	}

	public BaseDigistoreTileEntity(BlockEntityTypeAllocator<? extends BaseDigistoreTileEntity> allocator, int powerUsage) {
		super(allocator);
		registerComponent(digistoreCableProvider = new DigistoreCableProviderComponent("DigistoreCableProviderComponent", powerUsage).setShouldControlOnState());
	}

	public boolean isManagerPresent() {
		return digistoreCableProvider.isManagerPresent();
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return true;
	}
}
