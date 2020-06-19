package theking530.staticpower.tileentities.cables.item;

import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityItemCable extends TileEntityBase {
	public final ItemCableComponent cableComponent;

	public TileEntityItemCable() {
		super(ModTileEntityTypes.ITEM_CABLE);
		registerComponent(cableComponent = new ItemCableComponent("ItemCableComponent"));
	}
}
