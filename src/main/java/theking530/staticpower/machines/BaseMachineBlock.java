package theking530.staticpower.machines;

import java.util.ArrayList;

import api.IWrenchable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.BaseItemBlock;

public class BaseMachineBlock extends BlockContainer implements IWrenchable{

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	protected BaseMachineBlock(String name) {
		super(Material.IRON);
		this.setCreativeTab(StaticPower.StaticPower);
		setRegistryName(name);
		setUnlocalizedName(name);
		RegisterHelper.registerItem(new BaseItemBlock(this, name));
	}

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.setDefaultFacing(worldIn, pos, state);
    }
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state){
        if (!worldIn.isRemote) {
            IBlockState iblockstate = worldIn.getBlockState(pos.north());
            IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
                enumfacing = EnumFacing.SOUTH;
            }else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()){
                enumfacing = EnumFacing.NORTH;
            }else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()){
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()){
                enumfacing = EnumFacing.WEST;
            }
            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
      EnumFacing facing = EnumFacing.getHorizontal(meta);

      return this.getDefaultState().withProperty(FACING, facing);
    }
    @Override
    public int getMetaFromState(IBlockState state) {
      EnumFacing facing = (EnumFacing)state.getValue(FACING);

      int facingbits = facing.getHorizontalIndex();
      return facingbits;
    }
    @Override
    protected BlockStateContainer createBlockState(){
      return new BlockStateContainer(this, new IProperty[] {FACING});
    }
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if(world.getTileEntity(pos) instanceof BaseMachine) {
			BaseMachine tempMachine = (BaseMachine)world.getTileEntity(pos);
			if(placer.getHeldItemMainhand().hasTagCompound()) {
				tempMachine.onMachinePlaced(placer.getHeldItemMainhand().getTagCompound());
			}
		}
		EnumFacing enumfacing = (placer == null) ? EnumFacing.NORTH : EnumFacing.fromAngle(placer.rotationYaw);
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return null;
	}
	@Override
	public void wrenchBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {
	}
	@Override
	public boolean canBeWrenched(EntityPlayer player, World world, BlockPos pos){
		return true;
	}

	@Override
	public void sneakWrenchBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {
		ArrayList<ItemStack> items = new ArrayList();
		NBTTagCompound nbt = new NBTTagCompound();
		ItemStack machineStack = new ItemStack(Item.getItemFromBlock(this));
		if(world.getTileEntity(pos) instanceof BaseTileEntity) {
			BaseTileEntity tempMachine = (BaseTileEntity)world.getTileEntity(pos);
			tempMachine.writeToNBT(nbt);
			machineStack.setTagCompound(nbt);

			for(int i=0; i<tempMachine.slots.length; i++) {
				if(tempMachine.slots[i] != null) {
					items.add(tempMachine.slots[i].copy());
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
}
