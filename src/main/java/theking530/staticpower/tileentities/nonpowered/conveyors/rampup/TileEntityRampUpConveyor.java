package theking530.staticpower.tileentities.nonpowered.conveyors.rampup;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;

public class TileEntityRampUpConveyor extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityRampUpConveyor> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityRampUpConveyor(), ModBlocks.RampUpConveyor);

	protected final ConveyorMotionComponent conveyor;

	public TileEntityRampUpConveyor() {
		super(TYPE);
		this.registerComponent(conveyor = new ConveyorMotionComponent("Conveyor", new Vector3D(0.15f, 0.1f, 0f)).setShouldAffectEntitiesAbove(false));
	}

	@Override
	protected void postInit(World world, BlockPos pos, BlockState state) {
		super.postInit(world, pos, state);
		conveyor.updateBounds(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.5, pos.getZ() + 1.0));
	}
}
