package theking530.staticpower.cables.digistore;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.digistorenetwork.BlockEntityDigistoreBase;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityDigistoreWire extends BlockEntityDigistoreBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityDigistoreWire> TYPE = new BlockEntityTypeAllocator<BlockEntityDigistoreWire>("cable_digistore_wire",
			(type, pos, state) -> new BlockEntityDigistoreWire(pos, state), ModBlocks.DigistoreWire);

	public BlockEntityDigistoreWire(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return false;
	}
}
