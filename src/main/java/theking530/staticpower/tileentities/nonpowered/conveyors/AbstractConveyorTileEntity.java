package theking530.staticpower.tileentities.nonpowered.conveyors;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;

public abstract class AbstractConveyorTileEntity extends TileEntityConfigurable {
	protected final ConveyorMotionComponent conveyor;
	protected final ResourceLocation tier;

	public AbstractConveyorTileEntity(BlockEntityTypeAllocator<? extends TileEntityConfigurable> type, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(type, pos, state);
		this.tier = tier;
		this.registerComponent(conveyor = new ConveyorMotionComponent("Conveyor"));
	}

	@Override
	protected void postInit(Level world, BlockPos pos, BlockState state) {
		super.postInit(world, pos, state);
		configureConveyorComponent(conveyor, StaticPowerConfig.getTier(tier), world, pos, state);
	}

	protected abstract void configureConveyorComponent(ConveyorMotionComponent component, StaticPowerTier tier, Level world, BlockPos pos, BlockState state);
}
