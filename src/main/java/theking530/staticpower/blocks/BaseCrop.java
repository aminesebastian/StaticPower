package theking530.staticpower.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.items.ModItems;

public class BaseCrop extends BlockCrops implements IGrowable {

	public Tier TIER;
	
    public BaseCrop(Tier tier, String name) {
        setTickRandomly(true);
        setHardness(0.0F);
        disableStats();
        setLightLevel(1.0F);
        TIER = tier;
		setUnlocalizedName(name);
		setRegistryName(name);
		//RegisterHelper.registerItem(new BaseItemBlock(this, name));
    }
	
	public int quantityDropped (Random random) {
		return 1;
	}
    protected Item getSeed(){
    	switch(TIER) {
    		case STATIC: return ModItems.StaticSeeds;
    		case ENERGIZED: return ModItems.EnergizedSeeds;
    		case LUMUM:return ModItems.LumumSeeds;
		default:
			break;
    	}
		return null;
    }
    protected Item getCrop(){
    	switch(TIER) {
		case STATIC: return ModItems.StaticCrop;
		case ENERGIZED: return ModItems.EnergizedCrop;
		case LUMUM:return ModItems.LumumCrop;
		default:
			break;
    	}
		return null;
    }
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(isMaxAge(state)) {
<<<<<<< HEAD
        	this.dropBlockAsItem(world, pos, state, 0); 
=======
        	dropBlockAsItem(world, pos, state, 0); 
>>>>>>> branch '1.10.2' of https://github.com/Theking5301/StaticPower.git
        	world.setBlockState(pos, withAge(0), 2);
    		return true;
        }
        return false;
    }
}