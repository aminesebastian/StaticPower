package theking530.staticpower.tileentities.nonpowered.conveyors.rampdown;

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

public class TileEntityRampDownConveyor extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityRampDownConveyor> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityRampDownConveyor(), ModBlocks.RampDownConveyor);

	protected final ConveyorMotionComponent conveyor;

	public TileEntityRampDownConveyor() {
		super(TYPE);
		this.registerComponent(conveyor = new ConveyorMotionComponent("Conveyor", new Vector3D(0.1f, 0.0f, 0f), 0.2));
	}

	@Override
	protected void postInit(World world, BlockPos pos, BlockState state) {
		super.postInit(world, pos, state);
		conveyor.setBounds(new AxisAlignedBB(pos.getX(), pos.getY() - 0.1f, pos.getZ(), pos.getX() + 1, pos.getY() + 1.1, pos.getZ() + 1));
	}
}
