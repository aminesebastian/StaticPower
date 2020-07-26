package theking530.staticpower.tileentities.nonpowered.digistorenetwork.ioport;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreBlock;

public class BlockDigistoreIOPort extends BaseDigistoreBlock {

	public BlockDigistoreIOPort(String name) {
		super(name);
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.DIGISTORE_IO_PORT.create();
	}
}
