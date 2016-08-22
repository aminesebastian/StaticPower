package theking530.staticpower.machines.basicfarmer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import theking530.staticpower.machines.BaseMachineBlock;

public class BlockBasicFarmer extends BaseMachineBlock{

	public BlockBasicFarmer() {
		super("AdvancedFarmer");
	}
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityBasicFarmer();
	}
}
