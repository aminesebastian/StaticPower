package theking530.staticpower.conduits;

import java.util.ArrayList;

import javax.annotation.Nullable;

import api.IWrenchTool;
import api.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.BaseItemBlock;

public class BaseConduit extends Block implements ITileEntityProvider, IWrenchable {

	protected BaseConduit(String name) {
		super(Material.GLASS);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(StaticPower.StaticPower);
		RegisterHelper.registerItem(new BaseItemBlock(this, name));
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
			if (world.isRemote) {
	    		return true;
	    	}else if (!player.isSneaking()) {
	    		if(player.getHeldItemMainhand().getItem() instanceof IWrenchTool) {
	    			TileEntityBaseConduit tempConduit = (TileEntityBaseConduit) world.getTileEntity(pos);
	    			tempConduit.incrementSideMode(side);
	    		}
	    	}
			return false;
	}
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	public boolean renderAsNormalBlock(IBlockState state) {
		return false;
	}
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return null;
	}

	@Override
	public void wrenchBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {		
	}
	@Override
	public void sneakWrenchBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {
		ArrayList<ItemStack> items = new ArrayList();
		ItemStack machineStack = new ItemStack(Item.getItemFromBlock(this));
		items.add(machineStack);
		
		if(items != null) {
			//breakBlock(world, x, y, z, this, world.getBlockMetadata(x, y, z));
			world.setBlockToAir(pos);
			//world.playSoundEffect((double)x, (double)y, (double)z, "pop", .5f, .5f);
		}	
	}

	@Override
	public boolean canBeWrenched(EntityPlayer player, World world, BlockPos pos) {
		return true;
	}

}
