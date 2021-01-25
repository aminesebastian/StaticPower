package theking530.staticpower.tileentities.nonpowered.conveyors.straight;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
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
	public void onPlaced(BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onPlaced(state, placer, stack);

		// If we're in the top state, look for entities slightly above the conveyor,
		// otherwise check for entities in the same block as the conveyor.
		if (state.get(BlockStraightConveyor.IS_TOP)) {
			conveyor.setShouldAffectEntitiesAbove(true);
			conveyor.setBounds(new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1.35, pos.getZ() + 1));
		} else {
			conveyor.setShouldAffectEntitiesAbove(false);
			conveyor.setBounds(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 0.35, pos.getZ() + 1));
		}
	}
}
