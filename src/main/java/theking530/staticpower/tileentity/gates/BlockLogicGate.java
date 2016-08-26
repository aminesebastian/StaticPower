package theking530.staticpower.tileentity.gates;

import java.util.ArrayList;

import javax.annotation.Nullable;

import api.IWrenchable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.BaseItemBlock;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.SideModeList.Mode;

public class BlockLogicGate extends BlockContainer implements IWrenchable{

	static float PIXEL = 1F/16F;
    private static final AxisAlignedBB AA_BB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2*PIXEL, 1.0D);
    private int GUI_ID;
    
    protected BlockLogicGate(String name, int guiID) {
		super(Material.ROCK);
		setCreativeTab(StaticPower.StaticPower);
		setRegistryName(name);
		setUnlocalizedName(name);
		RegisterHelper.registerItem(new ItemLogicGate(this, name));
		GUI_ID = guiID;
	}
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
    		return true;
    	}else if (!player.isSneaking()) {
    		TileEntityBaseLogicGate entity = (TileEntityBaseLogicGate) world.getTileEntity(pos);
    		if(entity != null) {
    			if(hitX > .25 && hitX < .75 && hitZ > .25 && hitZ < .75) {
    				if(GUI_ID != 0) {
                		FMLNetworkHandler.openGui(player, StaticPower.staticpower, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
                		return true;					
    				}
        		}else{
        			int metadata = getMetaFromState(world.getBlockState(pos));
        			if(hitX > .75) {
        				entity.sideRightClicked(EnumFacing.EAST);	     
        			}else if(hitX < .25) {
        				entity.sideRightClicked(EnumFacing.WEST);	
        			}else if(hitZ > .75) {
        				entity.sideRightClicked(EnumFacing.SOUTH);
        			}else if(hitZ < .25) {
        				entity.sideRightClicked(EnumFacing.NORTH);	  
        			}
        			return true;
        		}	
    		} 	
    		return false;
    	}else{
    		return false;
    	}
	}
    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
    	TileEntityBaseLogicGate tempGate = (TileEntityBaseLogicGate) blockAccess.getTileEntity(pos);
    	return tempGate.OUTPUT_SIGNALS[side.getOpposite().ordinal()];
    }
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
    	TileEntityBaseLogicGate tempGate = (TileEntityBaseLogicGate) blockAccess.getTileEntity(pos);
        return tempGate.OUTPUT_SIGNALS[side.getOpposite().ordinal()];
    }
    @Override
    public boolean canProvidePower(IBlockState state){
        return true;
    }
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return AA_BB;
    }
    @Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    	TileEntityBaseLogicGate tempGate = (TileEntityBaseLogicGate) world.getTileEntity(pos);
    	if(tempGate != null && tempGate.SIDE_MODES[side.getOpposite().ordinal()] != Mode.Disabled) {
    		return true;
    	}
    	return false;
    }
	
	@Override
	public boolean canBeWrenched(EntityPlayer player, World world, BlockPos pos, EnumFacing facing){
		return true;
	}
	@Override
	public void sneakWrenchBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, boolean returnDrops) {
		ArrayList<ItemStack> items = new ArrayList();
		NBTTagCompound nbt = new NBTTagCompound();
		ItemStack machineStack = new ItemStack(Item.getItemFromBlock(this));
		if(world.getTileEntity(pos) instanceof BaseTileEntity) {
			BaseTileEntity tempMachine = (BaseTileEntity)world.getTileEntity(pos);
			tempMachine.writeToNBT(nbt);
			machineStack.setTagCompound(nbt);

			for(int i=0; i<tempMachine.SLOTS_INPUT.getSlots(); i++) {
				if(tempMachine.SLOTS_INPUT.getStackInSlot(i) != null) {
					items.add(tempMachine.SLOTS_INPUT.getStackInSlot(i).copy());
				}
			}
			for(int i=0; i<tempMachine.SLOTS_OUTPUT.getSlots(); i++) {
				if(tempMachine.SLOTS_OUTPUT.getStackInSlot(i) != null) {
					items.add(tempMachine.SLOTS_OUTPUT.getStackInSlot(i).copy());
				}
			}
			for(int i=0; i<tempMachine.SLOTS_INTERNAL.getSlots(); i++) {
				if(tempMachine.SLOTS_INTERNAL.getStackInSlot(i) != null) {
					items.add(tempMachine.SLOTS_INTERNAL.getStackInSlot(i).copy());
				}
			}
			for(int i=0; i<tempMachine.SLOTS_UPGRADES.getSlots(); i++) {
				if(tempMachine.SLOTS_UPGRADES.getStackInSlot(i) != null) {
					items.add(tempMachine.SLOTS_UPGRADES.getStackInSlot(i).copy());
				}
			}

		}
		items.add(machineStack);
		
		if(items != null) {
			for(int i=0; i<items.size(); i++) {
				EntityItem droppedItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(),items.get(i));
				world.spawnEntityInWorld(droppedItem);
			}
			//breakBlock(world, x, y, z, this, world.getBlockMetadata(x, y, z));
			world.setBlockToAir(pos);
		}		
	}
	public String getDescrption(){
		return null;	
	}
	public String getInputDescrption(){
		return null;
	}
	public String getOutputDescrption(){
		return null;
	}
	public String getExtraDescrption(){
		return null;
	}
    @Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return null;
	}
	@Override
	public void wrenchBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, boolean returnDrops) {
	}
}
