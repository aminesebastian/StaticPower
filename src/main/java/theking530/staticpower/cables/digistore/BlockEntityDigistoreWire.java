package theking530.staticpower.cables.digistore;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityDigistoreWire extends BaseDigistoreTileEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityDigistoreWire> TYPE = new BlockEntityTypeAllocator<BlockEntityDigistoreWire>(
			(type, pos, state) -> new BlockEntityDigistoreWire(pos, state), ModBlocks.DigistoreWire);

	public BlockEntityDigistoreWire(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return false;
	}
}
