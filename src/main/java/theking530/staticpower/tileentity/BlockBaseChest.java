package theking530.staticpower.tileentity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import theking530.staticpower.machines.BaseMachineBlock;

public class BlockBaseChest extends BaseMachineBlock{

	//private final Random rand = new Random();
	
	public BlockBaseChest(String name) {
		super(name);
		setHardness(3.5f);
	    setResistance(5.0f);
	}
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
    public int getRenderType() {
        return -1;
    }
}
