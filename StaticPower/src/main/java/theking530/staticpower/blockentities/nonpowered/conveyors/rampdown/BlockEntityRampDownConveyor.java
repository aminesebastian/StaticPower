package theking530.staticpower.blockentities.nonpowered.conveyors.rampdown;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blockentities.components.ConveyorMotionComponent;
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlockEntity;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRampDownConveyor extends AbstractConveyorBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRampDownConveyor> TYPE = new BlockEntityTypeAllocator<>("conveyor_ramp_down",
			(type, pos, state) -> new BlockEntityRampDownConveyor(type, pos, state), ModBlocks.ConveyorsRampDown.values());


	public BlockEntityRampDownConveyor(BlockEntityTypeAllocator<BlockEntityRampDownConveyor> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void configureConveyorComponent(ConveyorMotionComponent component, StaticCoreTier tier, Level world, BlockPos pos, BlockState state) {
		component.setVelocity(new Vector3D((float) (0.05f * tier.conveyorSpeedMultiplier.get()), -(float) (0.05f * tier.conveyorSpeedMultiplier.get()), 0f));
		component.updateBounds(new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.5, pos.getZ() + 1.0));
	}
}
