package theking530.staticpower.cables.redstone.bundled;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityBundledRedstoneCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityBundledRedstoneCable> TYPE = new BlockEntityTypeAllocator<TileEntityBundledRedstoneCable>(
			(allocator, pos, state) -> new TileEntityBundledRedstoneCable(allocator, pos, state), ModBlocks.BundledRedstoneCable);

	public final BundledRedstoneCableComponent cableComponent;

	public TileEntityBundledRedstoneCable(BlockEntityTypeAllocator<TileEntityBundledRedstoneCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(cableComponent = new BundledRedstoneCableComponent("RedstoneCableComponent"));
	}
}
