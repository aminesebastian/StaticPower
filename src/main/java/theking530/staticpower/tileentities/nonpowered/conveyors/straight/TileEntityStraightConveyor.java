package theking530.staticpower.tileentities.nonpowered.conveyors.straight;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;

public class TileEntityStraightConveyor extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityStraightConveyor> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityStraightConveyor(), ModBlocks.StraightConveyor);

	protected final ConveyorMotionComponent conveyor;

	public TileEntityStraightConveyor() {
		super(TYPE);
		this.registerComponent(conveyor = new ConveyorMotionComponent("Conveyor", new Vector3D(0.1f, 0f, 0f), 0.2));
	}

	@Override
	public void onInitializedInWorld(World world, BlockPos pos) {
		super.onInitializedInWorld(world, pos);
		conveyor.setBounds(new AxisAlignedBB(pos.getX(), pos.getY() + 1.0, pos.getZ(), pos.getX() + 1, pos.getY() + 1.1, pos.getZ() + 1));
	}
}
