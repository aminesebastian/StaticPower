package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.tileentities.TileEntityBase;

public abstract class BaseDigistoreTileEntity extends TileEntityBase {
	protected final DigistoreCableProviderComponent digistoreCableProvider;

	public BaseDigistoreTileEntity(TileEntityType<?> teType) {
		super(teType);
		registerComponent(digistoreCableProvider = new DigistoreCableProviderComponent("DigistoreCableProviderComponent"));
	}

}
