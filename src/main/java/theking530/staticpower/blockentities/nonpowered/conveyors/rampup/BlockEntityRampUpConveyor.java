package theking530.staticpower.blockentities.nonpowered.conveyors.rampup;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlockEntity;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRampUpConveyor extends AbstractConveyorBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRampUpConveyor> TYPE_BASIC = new BlockEntityTypeAllocator<>("conveyor_ramp_up_basic",
			(type, pos, state) -> new BlockEntityRampUpConveyor(type, pos, state, StaticPowerTiers.BASIC), ModBlocks.RampUpConveyorBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRampUpConveyor> TYPE_ADVANCED = new BlockEntityTypeAllocator<>("conveyor_ramp_up_advanced",
			(type, pos, state) -> new BlockEntityRampUpConveyor(type, pos, state, StaticPowerTiers.ADVANCED), ModBlocks.RampUpConveyorAdvanced);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRampUpConveyor> TYPE_STATIC = new BlockEntityTypeAllocator<>("conveyor_ramp_up_static",
			(type, pos, state) -> new BlockEntityRampUpConveyor(type, pos, state, StaticPowerTiers.STATIC), ModBlocks.RampUpConveyorStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRampUpConveyor> TYPE_ENERGIZED = new BlockEntityTypeAllocator<>("conveyor_ramp_up_energized",
			(type, pos, state) -> new BlockEntityRampUpConveyor(type, pos, state, StaticPowerTiers.ENERGIZED), ModBlocks.RampUpConveyorEnergized);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRampUpConveyor> TYPE_LUMUM = new BlockEntityTypeAllocator<>("conveyor_ramp_up_lumum",
			(type, pos, state) -> new BlockEntityRampUpConveyor(type, pos, state, StaticPowerTiers.LUMUM), ModBlocks.RampUpConveyorLumum);

	public BlockEntityRampUpConveyor(BlockEntityTypeAllocator<BlockEntityRampUpConveyor> type, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(type, pos, state, tier);
	}

	@Override
	protected void configureConveyorComponent(ConveyorMotionComponent component, StaticPowerTier tier, Level world, BlockPos pos, BlockState state) {
		component.setVelocity(new Vector3D((float) (0.1f * tier.conveyorSpeedMultiplier.get()), (float) (0.05f * tier.conveyorSpeedMultiplier.get()), 0f));
		component.updateBounds(new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.5, pos.getZ() + 1.0));
	}
}
