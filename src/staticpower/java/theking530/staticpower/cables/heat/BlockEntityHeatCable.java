package theking530.staticpower.cables.heat;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityHeatCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityHeatCable> TYPE = new BlockEntityTypeAllocator<BlockEntityHeatCable>(
			"cable_heat", (allocator, pos, state) -> new BlockEntityHeatCable(allocator, pos, state),
			ModBlocks.HeatCables.values());

	private final HeatCableComponent cableComponent;

	public BlockEntityHeatCable(BlockEntityTypeAllocator<BlockEntityHeatCable> allocator, BlockPos pos,
			BlockState state) {
		super(allocator, pos, state);
		registerComponent(cableComponent = new HeatCableComponent("HeatCableComponent",
				getTierObject().cableHeatConfiguration.heatCableCapacity.get(),
				getTierObject().cableHeatConfiguration.heatCableConductivity.get()));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			cableComponent.getHeatNetworkModule().ifPresent(module -> {
				Optional<HeatCableCapability> capability = module.getHeatCableCapability(getBlockPos());
				if (capability.isPresent()) {
					float temperature = capability.get().getStorage().getCurrentTemperature();
					if (temperature >= IHeatStorage.WATER_BOILING_TEMPERATURE) {
						AABB aabb = new AABB(getBlockPos().offset(0.0, 0, 0.0), getBlockPos().offset(1.0, 1, 1.0));
						List<LivingEntity> list = getLevel().getEntitiesOfClass(LivingEntity.class, aabb);
						for (LivingEntity entity : list) {
							entity.hurt(DamageSource.HOT_FLOOR, temperature / IHeatStorage.WATER_BOILING_TEMPERATURE);
						}
					}
				}
			});
		}
	}
}
