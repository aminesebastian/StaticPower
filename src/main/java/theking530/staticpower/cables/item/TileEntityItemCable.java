package theking530.staticpower.cables.item;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityItemCable extends TileEntityBase {
	public final ItemCableComponent cableComponent;

	public TileEntityItemCable(TileEntityType<TileEntityItemCable> type, ResourceLocation tier) {
		super(type);
		StaticPowerTier tierObject = StaticPowerDataRegistry.getTier(tier);
		registerComponent(cableComponent = new ItemCableComponent("ItemCableComponent", tierObject.getItemCableMaxSpeed(), tierObject.getItemCableFriction(), 1.0f / Math.max(tierObject.getItemCableAcceleration(), 0.00000001f)));
	}
}
