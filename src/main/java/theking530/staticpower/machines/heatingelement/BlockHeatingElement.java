package theking530.staticpower.machines.heatingelement;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.machines.BaseMachineBlock;

public class BlockHeatingElement extends BaseMachineBlock{
	
	private static boolean keepInventory = false;
	
	public BlockHeatingElement() {
		super("HeatingElement");
		setHardness(3.5f);
	    setResistance(5.0f);
		setCreativeTab(StaticPower.StaticPower);
	}
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityHeatingElement();
	}
}
