package theking530.staticpower.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.RegisterHelper;

public class AdvancedEarth extends BlockFarmland{

    protected AdvancedEarth() {
        setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, Integer.valueOf(7)));
        setTickRandomly(true);
        setLightOpacity(255);
		setRegistryName("AdvancedEarth");
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName("AdvancedEarth");
		RegisterHelper.registerItem(new BaseItemBlock(this, "AdvancedEarth"));
    }
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        entityIn.fall(fallDistance, 1.0F);
    }
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        worldIn.setBlockState(pos, state.withProperty(MOISTURE, Integer.valueOf(7)), 2);
        if(worldIn.getBlockState(pos.up()) != null && worldIn.getBlockState(pos.up()).getBlock() instanceof IGrowable) {
        	IGrowable tempCrop = (IGrowable) worldIn.getBlockState(pos.up()).getBlock();
        	if(tempCrop.canGrow(worldIn, pos.up(), worldIn.getBlockState(pos.up()), true)) {
        		worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() + 0.5D, pos.getY() + 1.0D, 
        				pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]);
        		tempCrop.grow(worldIn, rand, pos.up(), worldIn.getBlockState(pos.up()));
        	}
        }
    }
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn){}
    @Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.AdvancedEarth);
    }
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    	IBlockState state = getStateFromMeta(meta);
    	return state.withProperty(MOISTURE, Integer.valueOf(7));
    }
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(ModBlocks.AdvancedEarth));
    }	
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable) {
    	return true;
    }
}
