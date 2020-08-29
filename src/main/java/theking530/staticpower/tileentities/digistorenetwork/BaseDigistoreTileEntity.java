package theking530.staticpower.tileentities.digistorenetwork;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.tileentities.TileEntityBase;

public abstract class BaseDigistoreTileEntity extends TileEntityBase {
	public final DigistoreCableProviderComponent digistoreCableProvider;

	public BaseDigistoreTileEntity(TileEntityType<?> teType) {
		super(teType);
		registerComponent(digistoreCableProvider = new DigistoreCableProviderComponent("DigistoreCableProviderComponent"));
	}

	public boolean isManagerPresent() {
		return digistoreCableProvider.isManagerPresent();
	}

}
