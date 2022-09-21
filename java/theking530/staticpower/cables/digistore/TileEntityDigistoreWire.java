package theking530.staticpower.cables.digistore;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.init.ModBlocks;

public class TileEntityDigistoreWire extends BaseDigistoreTileEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityDigistoreWire> TYPE = new BlockEntityTypeAllocator<TileEntityDigistoreWire>(
			(type, pos, state) -> new TileEntityDigistoreWire(pos, state), ModBlocks.DigistoreWire);

	public TileEntityDigistoreWire(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return false;
	}
}
