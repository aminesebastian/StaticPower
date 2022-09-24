package theking530.staticpower.cables.scaffold;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.init.ModBlocks;

public class TileEntityScaffoldCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityScaffoldCable> TYPE = new BlockEntityTypeAllocator<TileEntityScaffoldCable>(
			(allocator, pos, state) -> new TileEntityScaffoldCable(allocator, pos, state), ModBlocks.ScaffoldCable);

	public TileEntityScaffoldCable(BlockEntityTypeAllocator<TileEntityScaffoldCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(new ScaffoldCableComponent("ScaffoldCableComponent"));
	}
}
