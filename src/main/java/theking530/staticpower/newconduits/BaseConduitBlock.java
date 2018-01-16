package theking530.staticpower.newconduits;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.BaseItemBlock;

public class BaseConduitBlock extends BlockContainer {

	public BaseConduitBlock(Material materialIn, String name) {
		super(materialIn);
		setCreativeTab(StaticPower.StaticPower);
		setRegistryName(name);
		setUnlocalizedName(name);
		//RegisterHelper.registerItem(new BaseItemBlock(this, name));
	}
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){ 
    	if(world.getTileEntity(pos) != null) {
    		BaseConduitTileEntity te = (BaseConduitTileEntity)world.getTileEntity(pos);
    		te.onNeighborUpdate(world, pos, neighbor);
    	}
    }
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new BaseConduitTileEntity();
	}
}
