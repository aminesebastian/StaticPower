package theking530.staticpower.items.tools;

import api.IWrenchTool;
import api.IWrenchable;
import api.RegularWrenchMode;
import api.SneakWrenchMode;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.machines.BaseMachineBlock;
import theking530.staticpower.utils.EnumTextFormatting;

public class StaticWrench extends Item implements IWrenchTool{
	
	public StaticWrench() {
		setMaxStackSize(1);
		this.setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName("StaticWrench");
		setRegistryName("StaticWrench");	
	}		
	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand){
		if(!worldIn.isRemote) {
			if(!playerIn.isSneaking()) {
				if(getWrenchMode(itemStackIn).ordinal() + 1 <= RegularWrenchMode.values().length-1) {
					setWrenchMode(itemStackIn, RegularWrenchMode.values()[getWrenchMode(itemStackIn).ordinal() + 1]);
					playerIn.addChatComponentMessage(new TextComponentString("Wrench Mode: " + getWrenchMode(itemStackIn).toString()));	
				}else{
					setWrenchMode(itemStackIn, RegularWrenchMode.values()[0]);	
					playerIn.addChatComponentMessage(new TextComponentString("Wrench Mode: " + getWrenchMode(itemStackIn).toString()));	
				}	
			}
		}
        return new ActionResult(EnumActionResult.PASS, itemStackIn);
    }
	@Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if(!world.isRemote) {
			if(stack != null) {
				if(!player.isSneaking()) {
					player.swingArm(EnumHand.MAIN_HAND);
					if(world.getBlockState(pos).getBlock() instanceof IWrenchable) {
						IWrenchable block = (IWrenchable) world.getBlockState(pos).getBlock();
						block.wrenchBlock(player, getWrenchMode(stack), stack, world, pos, side, true);
						playWrenchSound(world, player, pos);
						return EnumActionResult.SUCCESS;
					}	
				}
				if(player.isSneaking()) {
					player.swingArm(EnumHand.MAIN_HAND);
					if(world.getBlockState(pos).getBlock() instanceof IWrenchable) {
						IWrenchable block = (IWrenchable)world.getBlockState(pos).getBlock();
						if(block.canBeWrenched(player, world, pos, side)){	
							block.sneakWrenchBlock(player, getSneakWrenchMode(stack), stack, world, pos, side, true);
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
	@Override
	public RegularWrenchMode getWrenchMode(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return RegularWrenchMode.values()[stack.getTagCompound().getInteger("REGULAR")];
		}
		return RegularWrenchMode.TOGGLE_SIDES;
	}
	@Override
	public SneakWrenchMode getSneakWrenchMode(ItemStack stack) {
		return SneakWrenchMode.DISMANTE;
	}
	public void setWrenchMode(ItemStack stack, RegularWrenchMode mode) {
		if(stack.hasTagCompound()) {
			stack.getTagCompound().setInteger("REGULAR", mode.ordinal());
		}else{
			NBTTagCompound nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
			stack.getTagCompound().setInteger("REGULAR", mode.ordinal());
		}
	}
}
