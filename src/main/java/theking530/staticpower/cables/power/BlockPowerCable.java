package theking530.staticpower.cables.power;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.network.CableBoundsCache;

public class BlockPowerCable extends AbstractCableBlock {

	public BlockPowerCable(String name) {
		super(name, "power", new CableBoundsCache(2.0D, 3.0D, 2.0D));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityPowerCable();
	}
}
