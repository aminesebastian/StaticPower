package theking530.staticpower.cables.heat;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityHeatCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatCable> TYPE_ALUMINUM = new BlockEntityTypeAllocator<TileEntityHeatCable>(
			(allocator, pos, state) -> new TileEntityHeatCable(allocator, pos, state, StaticPowerTiers.ALUMINUM),
			ModBlocks.AluminumHeatCable);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatCable> TYPE_COPPER = new BlockEntityTypeAllocator<TileEntityHeatCable>(
			(allocator, pos, state) -> new TileEntityHeatCable(allocator, pos, state, StaticPowerTiers.COPPER),
			ModBlocks.CopperHeatCable);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatCable> TYPE_TIN = new BlockEntityTypeAllocator<TileEntityHeatCable>(
			(allocator, pos, state) -> new TileEntityHeatCable(allocator, pos, state, StaticPowerTiers.TIN),
			ModBlocks.TinHeatCable);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatCable> TYPE_SILVER = new BlockEntityTypeAllocator<TileEntityHeatCable>(
			(allocator, pos, state) -> new TileEntityHeatCable(allocator, pos, state, StaticPowerTiers.SILVER),
			ModBlocks.SilverHeatCable);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityHeatCable> TYPE_GOLD = new BlockEntityTypeAllocator<TileEntityHeatCable>(
			(allocator, pos, state) -> new TileEntityHeatCable(allocator, pos, state, StaticPowerTiers.GOLD),
			ModBlocks.GoldHeatCable);

	private final HeatCableComponent cableComponent;

	public TileEntityHeatCable(BlockEntityTypeAllocator<TileEntityHeatCable> allocator, BlockPos pos, BlockState state,
			ResourceLocation tier) {
		super(allocator, pos, state);
		registerComponent(cableComponent = new HeatCableComponent("HeatCableComponent",
				StaticPowerConfig.getTier(tier).heatCableCapacity.get(),
				StaticPowerConfig.getTier(tier).heatCableConductivity.get()));
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			cableComponent.getHeatNetworkModule().ifPresent(module -> {
				if (module.getHeatPerCable() >= 100.0f) {
					AABB aabb = new AABB(this.worldPosition.offset(0.0, 0, 0.0),
							this.worldPosition.offset(1.0, 1, 1.0));
					List<Entity> list = this.level.getEntitiesOfClass(Entity.class, aabb);
					for (Entity entity : list) {
						entity.hurt(DamageSource.HOT_FLOOR, 1.0f);
					}
				}
			});
		}
	}
}
