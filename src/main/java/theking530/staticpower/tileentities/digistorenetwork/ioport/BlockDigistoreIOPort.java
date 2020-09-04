package theking530.staticpower.tileentities.digistorenetwork.ioport;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreBlock;

public class BlockDigistoreIOPort extends BaseDigistoreBlock {

	public BlockDigistoreIOPort(String name) {
		super(name);
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityDigistoreIOPort.TYPE.create();
	}
}
