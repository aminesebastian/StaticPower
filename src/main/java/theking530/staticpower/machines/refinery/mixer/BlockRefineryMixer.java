package theking530.staticpower.machines.refinery.mixer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import theking530.staticpower.machines.refinery.BaseRefineryBlock;

public class BlockRefineryMixer extends BaseRefineryBlock {

	public BlockRefineryMixer(String name) {
		super(name);
		setHardness(3.5f);
	    setResistance(5.0f);
	}	
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

    @Override
    public boolean hasTileEntity(IBlockState state) {
		return true;    	
    }
	@Override
	public TileEntity createTileEntity(World world, IBlockState state){
		return new TileEntityRefineryMixer();
	}
}
