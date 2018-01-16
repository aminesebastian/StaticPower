package theking530.staticpower.world.plants;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayerFactory;
import theking530.staticpower.items.ItemBase;

public class DepletedCrop extends ItemBase {

	public DepletedCrop(String name) {
		super(name);
	}
	@Override  
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("Strangly Fertilizing...");
    }
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.canPlayerEdit(pos.offset(facing), facing, stack)) {
            return EnumActionResult.FAIL;
        }else{
            if (applyBonemeal(stack, worldIn, pos, playerIn)){
                if (!worldIn.isRemote){
                    worldIn.playEvent(2005, pos, 0);
                }
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.PASS;
        }
    }
	public static boolean func_150919_a(ItemStack itemstack, World world, BlockPos pos) {
        if (world instanceof WorldServer)
            return applyBonemeal(itemstack, world, pos, FakePlayerFactory.getMinecraft((WorldServer)world));
        return false;
    }	
    public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target){
        if (worldIn instanceof net.minecraft.world.WorldServer)
            return applyBonemeal(stack, worldIn, target, net.minecraftforge.common.util.FakePlayerFactory.getMinecraft((net.minecraft.world.WorldServer)worldIn));
        return false;
    }
    public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target, EntityPlayer player) {
        IBlockState iblockstate = worldIn.getBlockState(target);

        int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, worldIn, target, iblockstate, stack, null);
        if (hook != 0) return hook > 0;

        if (iblockstate.getBlock() instanceof IGrowable){
            IGrowable igrowable = (IGrowable)iblockstate.getBlock();

            if (igrowable.canGrow(worldIn, target, iblockstate, worldIn.isRemote)) {
                if (!worldIn.isRemote){
                    if (igrowable.canUseBonemeal(worldIn, worldIn.rand, target, iblockstate)){
                        igrowable.grow(worldIn, worldIn.rand, target, iblockstate);
                    }
                    stack.setCount(stack.getCount()-1);
                }
                return true;
            }
        }else if(iblockstate.getBlock() instanceof IPlantable) {
            if (!worldIn.isRemote){
            	Block tempBlock = (Block)iblockstate.getBlock();
	        	Random rand = new Random();
	        	tempBlock.updateTick(worldIn, target, iblockstate, rand);
	            stack.setCount(stack.getCount()-1);
	            return true;
            }
        }
        return false;
    }
}


