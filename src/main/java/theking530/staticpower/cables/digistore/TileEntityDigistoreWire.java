package theking530.staticpower.cables.digistore;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityDigistoreWire extends BaseDigistoreTileEntity {
	@TileEntityTypePopulator()
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
