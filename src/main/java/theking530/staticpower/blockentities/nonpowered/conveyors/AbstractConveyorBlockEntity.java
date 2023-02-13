package theking530.staticpower.blockentities.nonpowered.conveyors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.data.StaticPowerTier;

public abstract class AbstractConveyorBlockEntity extends BlockEntityBase {
	protected final ConveyorMotionComponent conveyor;

	public AbstractConveyorBlockEntity(BlockEntityTypeAllocator<? extends BlockEntityBase> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.registerComponent(conveyor = new ConveyorMotionComponent("Conveyor"));
	}

	@Override
	protected void onLoadedInWorld(Level world, BlockPos pos, BlockState state) {
		super.onLoadedInWorld(world, pos, state);
		configureConveyorComponent(conveyor, getTierObject(), world, pos, state);
	}

	protected abstract void configureConveyorComponent(ConveyorMotionComponent component, StaticPowerTier tier, Level world, BlockPos pos, BlockState state);
}
