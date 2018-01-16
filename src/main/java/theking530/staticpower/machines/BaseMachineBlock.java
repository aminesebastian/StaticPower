package theking530.staticpower.machines;

import api.IWrenchable;
import api.RegularWrenchMode;
import api.SneakWrenchMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.BaseItemBlock;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.SideUtils;
import theking530.staticpower.utils.WorldUtilities;

public class BaseMachineBlock extends Block implements IWrenchable {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	protected BaseMachineBlock(String name) {
		super(Material.IRON);
		setCreativeTab(StaticPower.StaticPower);
		setRegistryName(name);
		setUnlocalizedName(name);	
		StaticPower.REGISTRY.PreRegisterItem(new BaseItemBlock(this, name));
		this.hasTileEntity = true;
		this.lightOpacity = 0;
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.north());
            IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            }
            else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.setDefaultFacing(worldIn, pos, state);
    }
    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if (stack.hasDisplayName())
        {
            if(worldIn.getTileEntity(pos) instanceof BaseMachine) {
    			BaseMachine tempMachine = (BaseMachine)worldIn.getTileEntity(pos);
    			if(placer.getHeldItemMainhand().hasTagCompound()) {
    				tempMachine.onMachinePlaced(placer.getHeldItemMainhand().getTagCompound());
    			}
    		}
    		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        }
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if(world.getTileEntity(pos) instanceof BaseMachine) {
			BaseMachine tempMachine = (BaseMachine)world.getTileEntity(pos);
			if(placer.getHeldItemMainhand().hasTagCompound()) {
				tempMachine.onMachinePlaced(placer.getHeldItemMainhand().getTagCompound());
			}
		}
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
    	if(worldIn.getTileEntity(pos) instanceof BaseTileEntity) {
    		 BaseTileEntity tileentity = (BaseTileEntity) worldIn.getTileEntity(pos);
    	        if(!tileentity.WRENCHED) {
    		        for(EnumFacing facing : EnumFacing.values()) {
    		        	if(tileentity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)) {
    		        		ItemStackHandler tempHandler = (ItemStackHandler) tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
    		        		if(tempHandler != null) {
    		        			for(int i=0; i<tempHandler.getSlots(); i++) {
    		        				if(tempHandler.getStackInSlot(i) != null) {
    		        					WorldUtilities.dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), tempHandler.getStackInSlot(i));
    		        				}
    		        			}
    		        		}
    		        	}   
    		        }
    	        }
    	}     
        super.breakBlock(worldIn, pos, state);
    }
    @Override
    public boolean hasTileEntity(IBlockState state) {
		return true;    	
    }
    @Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return null;
	}
	
	@Override
	public void wrenchBlock(EntityPlayer player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, EnumFacing facing, boolean returnDrops) {
		if(!world.isRemote) {
			if(mode == RegularWrenchMode.ROTATE) {
				if(facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
					System.out.println("Facing: " + facing);
					System.out.println("Facing Opposite: " + facing.getOpposite());
					if(facing != world.getBlockState(pos).getValue(FACING)) {
						world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, facing), 2);	
					}else{
						world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, facing.getOpposite()), 2);	
					};
				}	
			}else{
				int currentMeta = getMetaFromState(world.getBlockState(pos));
				BaseTileEntity TE = (BaseTileEntity) world.getTileEntity(pos);

				TE.incrementSide(SideUtils.getBlockSide(facing.getOpposite(), world.getBlockState(pos).getValue(BlockHorizontal.FACING)).ordinal());
				TE.updateBlock();
			}	
		}
		world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), world.getBlockState(pos), world.getBlockState(pos), 2);
	}	
	@Override
	public boolean canBeWrenched(EntityPlayer player, World world, BlockPos pos, EnumFacing facing){
		return true;
	}
	@Override
	public void sneakWrenchBlock(EntityPlayer player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, EnumFacing facing, boolean returnDrops){
		NBTTagCompound nbt = new NBTTagCompound();
		ItemStack machineStack = new ItemStack(Item.getItemFromBlock(this));
		if(world.getTileEntity(pos) instanceof BaseTileEntity) {
			BaseTileEntity tempMachine = (BaseTileEntity)world.getTileEntity(pos);
			tempMachine.WRENCHED = true;
			tempMachine.onMachineBroken(nbt);
			machineStack.setTagCompound(nbt);	
		}
		EntityItem droppedItem = new EntityItem(world, pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, machineStack);
		world.spawnEntity(droppedItem);
		world.playSound(player, pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 1.0f, 1.1f);
		world.setBlockToAir(pos);
		world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), world.getBlockState(pos), world.getBlockState(pos), 2);
	}

    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
}