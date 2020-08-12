package theking530.staticpower.cables.heat;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityHeatCable extends TileEntityBase {

	public TileEntityHeatCable(TileEntityType<TileEntityHeatCable> type, ResourceLocation tier) {
		super(type);
		registerComponent(new HeatCableComponent("HeatCableComponent", StaticPowerDataRegistry.getTier(tier).getHeatCableCapacity(), StaticPowerDataRegistry.getTier(tier).getHeatCableConductivity()));
	}

	@Override
	public void process() {

	}
}
