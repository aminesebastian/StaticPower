package theking530.staticpower.cables.heat;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityHeatCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatCable> TYPE_ALUMINIUM = new TileEntityTypeAllocator<TileEntityHeatCable>(
			(allocator) -> new TileEntityHeatCable(allocator, StaticPowerTiers.ALUMINIUM), ModBlocks.AluminiumHeatCable);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatCable> TYPE_COPPER = new TileEntityTypeAllocator<TileEntityHeatCable>(
			(allocator) -> new TileEntityHeatCable(allocator, StaticPowerTiers.COPPER), ModBlocks.CopperHeatCable);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatCable> TYPE_TIN = new TileEntityTypeAllocator<TileEntityHeatCable>(
			(allocator) -> new TileEntityHeatCable(allocator, StaticPowerTiers.TIN), ModBlocks.TinHeatCable);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatCable> TYPE_SILVER = new TileEntityTypeAllocator<TileEntityHeatCable>(
			(allocator) -> new TileEntityHeatCable(allocator, StaticPowerTiers.SILVER), ModBlocks.SilverHeatCable);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatCable> TYPE_GOLD = new TileEntityTypeAllocator<TileEntityHeatCable>(
			(allocator) -> new TileEntityHeatCable(allocator, StaticPowerTiers.GOLD), ModBlocks.GoldHeatCable);

	private final HeatCableComponent cableComponent;

	public TileEntityHeatCable(TileEntityTypeAllocator<TileEntityHeatCable> allocator, ResourceLocation tier) {
		super(allocator);
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
