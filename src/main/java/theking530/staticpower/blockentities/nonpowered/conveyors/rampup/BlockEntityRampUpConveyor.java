package theking530.staticpower.blockentities.nonpowered.conveyors.rampup;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlockEntity;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRampUpConveyor extends AbstractConveyorBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRampUpConveyor> TYPE= new BlockEntityTypeAllocator<>("conveyor_ramp_up",
			(type, pos, state) -> new BlockEntityRampUpConveyor(type, pos, state), ModBlocks.ConveyorsRampUp.values());


	public BlockEntityRampUpConveyor(BlockEntityTypeAllocator<BlockEntityRampUpConveyor> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void configureConveyorComponent(ConveyorMotionComponent component, StaticPowerTier tier, Level world, BlockPos pos, BlockState state) {
		component.setVelocity(new Vector3D((float) (0.1f * tier.conveyorSpeedMultiplier.get()), (float) (0.05f * tier.conveyorSpeedMultiplier.get()), 0f));
		component.updateBounds(new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.5, pos.getZ() + 1.0));
	}
}
