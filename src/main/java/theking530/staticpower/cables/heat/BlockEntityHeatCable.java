package theking530.staticpower.cables.heat;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityHeatCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHeatCable> TYPE_ALUMINUM = new BlockEntityTypeAllocator<BlockEntityHeatCable>("cable_heat_aluminium",
			(allocator, pos, state) -> new BlockEntityHeatCable(allocator, pos, state), ModBlocks.AluminumHeatCable);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHeatCable> TYPE_COPPER = new BlockEntityTypeAllocator<BlockEntityHeatCable>("cable_heat_copper",
			(allocator, pos, state) -> new BlockEntityHeatCable(allocator, pos, state), ModBlocks.CopperHeatCable);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHeatCable> TYPE_GOLD = new BlockEntityTypeAllocator<BlockEntityHeatCable>("cable_heat_gold",
			(allocator, pos, state) -> new BlockEntityHeatCable(allocator, pos, state), ModBlocks.GoldHeatCable);

	private final HeatCableComponent cableComponent;

	public BlockEntityHeatCable(BlockEntityTypeAllocator<BlockEntityHeatCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(cableComponent = new HeatCableComponent("HeatCableComponent", getTierObject().cableHeatConfiguration.heatCableCapacity.get(),
				getTierObject().cableHeatConfiguration.heatCableConductivity.get()));
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			cableComponent.getHeatNetworkModule().ifPresent(module -> {
				if (module.getHeatPerCable() >= 100.0f) {
					AABB aabb = new AABB(this.worldPosition.offset(0.0, 0, 0.0), this.worldPosition.offset(1.0, 1, 1.0));
					List<Entity> list = this.level.getEntitiesOfClass(Entity.class, aabb);
					for (Entity entity : list) {
						entity.hurt(DamageSource.HOT_FLOOR, 1.0f);
					}
				}
			});
		}
	}
}