package theking530.staticpower.cables.redstone.bundled;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.init.ModBlocks;

public class TileEntityBundledRedstoneCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBundledRedstoneCable> TYPE = new BlockEntityTypeAllocator<TileEntityBundledRedstoneCable>(
			(allocator, pos, state) -> new TileEntityBundledRedstoneCable(allocator, pos, state), ModBlocks.BundledRedstoneCable);

	public final BundledRedstoneCableComponent cableComponent;

	public TileEntityBundledRedstoneCable(BlockEntityTypeAllocator<TileEntityBundledRedstoneCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(cableComponent = new BundledRedstoneCableComponent("RedstoneCableComponent"));
	}
}
