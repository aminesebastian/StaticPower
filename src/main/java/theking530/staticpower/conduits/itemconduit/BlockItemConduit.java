package theking530.staticpower.conduits.itemconduit;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import theking530.staticpower.conduits.BaseConduit;

public class BlockItemConduit extends BaseConduit {

	public BlockItemConduit() {
		super("ItemConduit");
		this.useNeighborBrightness = true;
	}	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityItemConduit();
	}
}
