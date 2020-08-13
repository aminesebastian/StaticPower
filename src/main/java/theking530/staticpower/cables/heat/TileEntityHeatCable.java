package theking530.staticpower.cables.heat;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityHeatCable extends TileEntityBase {
	private final HeatCableComponent cableComponent;

	public TileEntityHeatCable(TileEntityType<TileEntityHeatCable> type, ResourceLocation tier) {
		super(type);
		registerComponent(cableComponent = new HeatCableComponent("HeatCableComponent", StaticPowerDataRegistry.getTier(tier).getHeatCableCapacity(),
				StaticPowerDataRegistry.getTier(tier).getHeatCableConductivity()));
	}

	@Override
	public void process() {
		if (!world.isRemote) {
			cableComponent.getHeatNetworkModule().ifPresent(module -> {
				if (module.getHeatPerCable() >= 100.0f) {
					AxisAlignedBB aabb = new AxisAlignedBB(this.pos.add(0.0, 0, 0.0), this.pos.add(1.0, 1, 1.0));
					List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb);
					for (Entity entity : list) {
						entity.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0f);
					}
				}
			});
		}
	}
}
