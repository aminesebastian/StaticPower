package theking530.staticpower.cables.power;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityPowerCable extends TileEntityBase {

	public TileEntityPowerCable(TileEntityType<TileEntityPowerCable> type, ResourceLocation tier) {
		super(type);
		registerComponent(new PowerCableComponent("PowerCableComponent", StaticPowerDataRegistry.getTier(tier).getCablePowerCapacity(), StaticPowerDataRegistry.getTier(tier).getCablePowerDelivery()));
	}

	@Override
	public void process() {

	}
}
