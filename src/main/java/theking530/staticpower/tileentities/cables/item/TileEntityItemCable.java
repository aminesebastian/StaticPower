package theking530.staticpower.tileentities.cables.item;

import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.cables.network.factories.cables.CableTypes;

public class TileEntityItemCable extends TileEntityBase {
	public final ItemCableComponent cableComponent;

	public TileEntityItemCable() {
		super(ModTileEntityTypes.ITEM_CABLE);
		registerComponent(cableComponent = new ItemCableComponent("ItemCableComponent", CableTypes.BASIC_ITEM));
	}
}
