package theking530.staticpower.items.tools;

import api.IWrenchTool;
import api.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;

public class StaticWrench extends Item implements IWrenchTool{
	
	public StaticWrench() {
		setMaxStackSize(1);
		this.setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName("StaticWrench");
		setRegistryName("StaticWrench");	
	}		
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			if(stack != null) {
				if(player.isSneaking()) {
					player.swingArm(EnumHand.MAIN_HAND);
					if(world.getBlockState(pos).getBlock() instanceof IWrenchable) {
						IWrenchable block = (IWrenchable)world.getBlockState(pos).getBlock();
						if(block.canBeWrenched(player, world, pos)){	
							block.sneakWrenchBlock(player, world, pos, true);
							playWrenchSound(world, player, pos);
							return EnumActionResult.SUCCESS;
						}
					}	
				}else{
					player.swingArm(EnumHand.MAIN_HAND);
					if(world.getBlockState(pos).getBlock() instanceof IWrenchable) {
						IWrenchable block = (IWrenchable)world.getBlockState(pos).getBlock();
						if(block.canBeWrenched(player, world, pos)){	
							playWrenchSound(world, player, pos);
							return EnumActionResult.SUCCESS;
						}
					}	
				}
			}
		}
        return EnumActionResult.PASS;
    }
	public void playWrenchSound(World world, EntityPlayer player, BlockPos pos) {
		player.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 0.3F, (float) (0.5F + Math.random()*2.0));
	}
}
