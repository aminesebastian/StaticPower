package theking530.staticpower.cables.redstone.bundled;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityBundledRedstoneCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityBundledRedstoneCable> TYPE = new BlockEntityTypeAllocator<BlockEntityBundledRedstoneCable>(
			(allocator, pos, state) -> new BlockEntityBundledRedstoneCable(allocator, pos, state), ModBlocks.BundledRedstoneCable);

	public final BundledRedstoneCableComponent cableComponent;

	public BlockEntityBundledRedstoneCable(BlockEntityTypeAllocator<BlockEntityBundledRedstoneCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(cableComponent = new BundledRedstoneCableComponent("RedstoneCableComponent"));
	}
}
