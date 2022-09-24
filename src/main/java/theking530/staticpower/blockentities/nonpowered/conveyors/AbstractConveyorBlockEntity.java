package theking530.staticpower.blockentities.nonpowered.conveyors;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.data.StaticPowerTier;

public abstract class AbstractConveyorBlockEntity extends BlockEntityConfigurable {
	protected final ConveyorMotionComponent conveyor;
	protected final ResourceLocation tier;

	public AbstractConveyorBlockEntity(BlockEntityTypeAllocator<? extends BlockEntityConfigurable> type, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(type, pos, state);
		this.tier = tier;
		this.registerComponent(conveyor = new ConveyorMotionComponent("Conveyor"));
	}

	@Override
	protected void onLoadedInWorld(Level world, BlockPos pos, BlockState state) {
		super.onLoadedInWorld(world, pos, state);
		configureConveyorComponent(conveyor, StaticPowerConfig.getTier(tier), world, pos, state);
	}

	protected abstract void configureConveyorComponent(ConveyorMotionComponent component, StaticPowerTier tier, Level world, BlockPos pos, BlockState state);
}
