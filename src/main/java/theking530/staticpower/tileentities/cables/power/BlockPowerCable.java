package theking530.staticpower.tileentities.cables.power;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import theking530.staticpower.tileentities.cables.AbstractCableBlock;

public class BlockPowerCable extends AbstractCableBlock {

	public BlockPowerCable(String name) {
		super(name, "power");
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityPowerCable();
	}
}
