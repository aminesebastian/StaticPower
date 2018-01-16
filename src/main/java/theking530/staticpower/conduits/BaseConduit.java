package theking530.staticpower.conduits;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import api.IWrenchTool;
import api.IWrenchable;
import api.RegularWrenchMode;
import api.SneakWrenchMode;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.conduits.staticconduit.TileEntityStaticConduit;
import theking530.staticpower.tileentity.BaseTileEntity;

public class BaseConduit extends Block implements IWrenchable {

	private float pixel = 1F/16F;
	
	protected BaseConduit(String name) {
		super(Material.GLASS);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(StaticPower.StaticPower);
		this.lightOpacity = 0;
		//RegisterHelper.registerItem(new BaseItemBlock(this, name));
	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    	float max = 1-12*pixel/2;
    	float min = 12*pixel/2;

    	AxisAlignedBB base = new AxisAlignedBB(min, min, min, max, max, max);
    	
		TileEntityStaticConduit conduit = (TileEntityStaticConduit) source.getTileEntity(pos);
		if (conduit != null) {
			if (conduit.connections[0] != null) {
				base = base.union(new AxisAlignedBB(min, 1.0F, min, max, max, max));
			}
			if (conduit.connections[1] != null) {
				base = base.union(new AxisAlignedBB(min, min, min, max, 0.0F, max));
			}
			if (conduit.connections[2] != null) {
				base = base.union(new AxisAlignedBB(min, min, 0.0F, max, max, max));
			}
			if (conduit.connections[3] != null) {
				base = base.union(new AxisAlignedBB(min, min, min, max, max, 1.0F));
			}
			if (conduit.connections[4] != null) {
				base = base.union(new AxisAlignedBB(1.0f, min, min, max, max, max));
			}
			if (conduit.connections[5] != null) {
				base = base.union(new AxisAlignedBB(0.0, min, min, max, max, max));
			}
			
		}
		return base;
	}
	@Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean b) {
    	float max = 1-11*pixel/2;
    	float min = 11*pixel/2;

		AxisAlignedBB bb = new AxisAlignedBB(min, min, min, max, max, max);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
		TileEntityStaticConduit conduit = (TileEntityStaticConduit) world.getTileEntity(pos);

		if (conduit != null) {
			if (conduit.connections[0] != null) {
				bb = new AxisAlignedBB(min, 1.0F, min, max, max, max);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
			}
			if (conduit.connections[1] != null) {
				bb = new AxisAlignedBB(min, min, min, max, 0.0F, max);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
			}
			if (conduit.connections[2] != null) {
				bb = new AxisAlignedBB(min, min, 0.0F, max, max, max);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
			}
			if (conduit.connections[3] != null) {
				bb = new AxisAlignedBB(min, min, min, max, max, 1.0F);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
			}
			if (conduit.connections[4] != null) {
				bb = new AxisAlignedBB(1.0F, min, min, max, max, max);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
			}
			if (conduit.connections[5] != null) {
				bb = new AxisAlignedBB(0.0F, min, min, max, max, max);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);
			}
			
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
    		return true;
    	}else if (!player.isSneaking()) {
    		if(player.getHeldItemMainhand().getItem() instanceof IWrenchTool) {
    			TileEntityBaseConduit tempConduit = (TileEntityBaseConduit) world.getTileEntity(pos);
    			tempConduit.incrementSideMode(facing);
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
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public void wrenchBlock(EntityPlayer player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, EnumFacing facing, boolean returnDrops) {		
		TileEntity te = world.getTileEntity(pos);
		if(te != null) {
			if(te instanceof TileEntityBaseConduit) {
				((TileEntityBaseConduit)te).incrementSideMode(facing);
			}
		}
	}
	@Override
	public void sneakWrenchBlock(EntityPlayer player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, EnumFacing facing, boolean returnDrops) {
		if(!world.isRemote) {
			ItemStack conduitStack = new ItemStack(Item.getItemFromBlock(this));

			EntityItem droppedItem = new EntityItem(world, pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, conduitStack);
			world.spawnEntity(droppedItem);

			world.setBlockToAir(pos);
			world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), world.getBlockState(pos), world.getBlockState(pos), 2);
		}
	}

	@Override
	public boolean canBeWrenched(EntityPlayer player, World world, BlockPos pos, EnumFacing facing) {
		return true;
	}
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    	if(world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBaseConduit) {
    		((TileEntityBaseConduit)world.getTileEntity(pos)).onBlockPlacedBy(world, pos, state, placer, stack);
    	}
    }
	@Override
    public void observedNeighborChange(IBlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos){
		super.onNeighborChange(world, observerPos, changedBlockPos);
    	if(world.getTileEntity(observerPos) != null && world.getTileEntity(observerPos) instanceof TileEntityBaseConduit) {
    		((TileEntityBaseConduit)world.getTileEntity(observerPos)).onNeighborUpdated(observerState, world, changedBlock, changedBlockPos, world.getBlockState(changedBlockPos).getBlock());
    	}
    }
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return null;
	}


}
