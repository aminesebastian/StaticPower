package theking530.staticpower.tileentity.digistorenetwork.networkwire;

import net.minecraft.block.state.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentity.digistorenetwork.BaseDigistoreBlock;

public class BlockDigistoreNetworkWire extends BaseDigistoreBlock {

	public BlockDigistoreNetworkWire(String name) {
		super(name);
		setHardness(3.5f);
	    setResistance(5.0f);
	}	
	public EnumBlockRenderType getRenderType(BlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	public boolean isOpaqueCube(BlockState state) {
		return true;
	}
	public boolean isFullCube(BlockState state) {
		return true;		
	}
    public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
    	this.shouldDropContents = false;
        super.breakBlock(worldIn, pos, state);    
    }
    @Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return null; //new TileEntityDigistoreWire();
	}
    @Override
    public boolean hasTileEntity(BlockState state) {
		return false;    	
    }
}
