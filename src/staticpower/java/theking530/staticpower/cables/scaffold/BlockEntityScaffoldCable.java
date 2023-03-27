package theking530.staticpower.cables.scaffold;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityScaffoldCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityScaffoldCable> TYPE = new BlockEntityTypeAllocator<BlockEntityScaffoldCable>("cable_scaffold",
			(allocator, pos, state) -> new BlockEntityScaffoldCable(allocator, pos, state), ModBlocks.ScaffoldCable);

	public BlockEntityScaffoldCable(BlockEntityTypeAllocator<BlockEntityScaffoldCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(new ScaffoldCableComponent("ScaffoldCableComponent"));
	}
}
