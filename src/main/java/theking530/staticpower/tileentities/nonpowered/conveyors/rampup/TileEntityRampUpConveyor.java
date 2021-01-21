package theking530.staticpower.tileentities.nonpowered.conveyors.rampup;

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
		this.registerComponent(conveyor = new ConveyorMotionComponent("Conveyor", new Vector3D(0.1f, 0.1f, 0f), 0.2).setShouldAffectEntitiesAbove(false));
	}

	@Override
	public void onInitializedInWorld(World world, BlockPos pos) {
		super.onInitializedInWorld(world, pos);
		conveyor.setBounds(new AxisAlignedBB(pos.getX(), pos.getY() - 1.0, pos.getZ()-0.1, pos.getX() + 1, pos.getY() + 1.1, pos.getZ() + 1));
	}
}
