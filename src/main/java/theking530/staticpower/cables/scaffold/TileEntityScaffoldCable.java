package theking530.staticpower.cables.scaffold;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityScaffoldCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityScaffoldCable> TYPE = new BlockEntityTypeAllocator<TileEntityScaffoldCable>(
			(allocator, pos, state) -> new TileEntityScaffoldCable(allocator, pos, state), ModBlocks.ScaffoldCable);

	public TileEntityScaffoldCable(BlockEntityTypeAllocator<TileEntityScaffoldCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(new ScaffoldCableComponent("ScaffoldCableComponent"));
	}
}
