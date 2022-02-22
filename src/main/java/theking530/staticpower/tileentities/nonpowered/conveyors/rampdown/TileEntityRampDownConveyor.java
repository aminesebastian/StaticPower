package theking530.staticpower.tileentities.nonpowered.conveyors.rampdown;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;

public class TileEntityRampDownConveyor extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRampDownConveyor> TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityRampDownConveyor(pos, state), ModBlocks.RampDownConveyor);

	protected final ConveyorMotionComponent conveyor;

	public TileEntityRampDownConveyor(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		this.registerComponent(conveyor = new ConveyorMotionComponent("Conveyor", new Vector3D(0.1f, -0.1f, 0f)));
	}

	@Override
	protected void postInit(Level world, BlockPos pos, BlockState state) {
		super.postInit(world, pos, state);
		conveyor.updateBounds(
				new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.5, pos.getZ() + 1.0));
	}
}
