package theking530.staticpower.tileentities.nonpowered.conveyors.straight;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;

public class TileEntityStraightConveyor extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStraightConveyor> TYPE = new BlockEntityTypeAllocator<>((type) -> new TileEntityStraightConveyor(), ModBlocks.StraightConveyor);

	protected final ConveyorMotionComponent conveyor;

	public TileEntityStraightConveyor() {
		super(TYPE);
		this.registerComponent(conveyor = new ConveyorMotionComponent("Conveyor", new Vector3D(0.075f, 0f, 0f)));
	}

	@Override
	protected void postInit(Level world, BlockPos pos, BlockState state) {
		super.postInit(world, pos, state);
		conveyor.setShouldAffectEntitiesAbove(false);
		conveyor.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.55, pos.getZ() + 1));
	}
}
