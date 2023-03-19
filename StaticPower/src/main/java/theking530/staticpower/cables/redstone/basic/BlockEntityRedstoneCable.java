package theking530.staticpower.cables.redstone.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRedstoneCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_NAKED = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_naked",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state), ModBlocks.BasicRedstoneCableNaked);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state), ModBlocks.RedstoneCables.values());

	public final RedstoneCableComponent cableComponent;

	public BlockEntityRedstoneCable(BlockEntityTypeAllocator<BlockEntityRedstoneCable> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(cableComponent = new RedstoneCableComponent("RedstoneCableComponent", getColor()));
	}

	public MinecraftColor getColor() {
		Block block = this.getBlockState().getBlock();
		if (block instanceof BlockRedstoneCable) {
			BlockRedstoneCable cableBlock = (BlockRedstoneCable) block;
			return cableBlock.getColor();
		}
		throw new RuntimeException("Encountered a redstone cable whose block does not inherit from BlockRedstoneCable.");
	}
}
