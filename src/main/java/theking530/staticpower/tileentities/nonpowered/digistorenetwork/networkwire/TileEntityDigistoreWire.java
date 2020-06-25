package theking530.staticpower.tileentities.nonpowered.digistorenetwork.networkwire;

import net.minecraft.client.renderer.texture.ITickable;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityDigistoreWire extends TileEntityBase implements ITickable {

	public TileEntityDigistoreWire() {
		super(ModTileEntityTypes.DIGISTORE_WIRE);
	}
}
