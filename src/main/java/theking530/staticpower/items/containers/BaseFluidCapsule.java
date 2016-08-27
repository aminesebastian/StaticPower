package theking530.staticpower.items.containers;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import theking530.staticpower.items.ItemBase;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GUIUtilities;

public class BaseFluidCapsule extends ItemBase implements IItemColor  {

	public int CAPACITY;
	
	public BaseFluidCapsule(String name, int capacity) {
		super(name);
		CAPACITY = capacity;
	}
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
    	FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    	if(tempHandler.getFluid() != null) {
    		return super.getItemStackDisplayName(stack) + " (" + tempHandler.getFluid().getLocalizedName() + ")";
    	}else{
    		return super.getItemStackDisplayName(stack);
    	}
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, EntityPlayer player, EnumHand hand) {
    	FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) itemstack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        FluidStack fluidStack = tempHandler.getFluid();

        // clicked on a block?
        RayTraceResult mop = this.rayTrace(world, player, true);

        if(mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK){
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }

        BlockPos clickPos = mop.getBlockPos();
        if(!player.isSneaking()) {
	        if(world.getBlockState(clickPos).getBlock() instanceof IFluidBlock) {
	        	IFluidBlock baseBlock = (IFluidBlock) world.getBlockState(clickPos).getBlock();
	        	if(((Integer)world.getBlockState(clickPos).getValue(BlockLiquid.LEVEL)).intValue() == 0) {
	        		if(tempHandler.getFluid() == null) {
	        			tempHandler.fill(new FluidStack(baseBlock.getFluid(), 1000), true);
	        			player.playSound(baseBlock.getFluid().getFillSound(), 0.5F, (float) (0.5F + Math.random()*2.0));
	        			world.setBlockToAir(clickPos);
	                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
	        		}else if(tempHandler.getFluid().amount + 1000 <= CAPACITY  || player.isCreative()) {
	        			if(tempHandler.getFluid().getFluid().equals(baseBlock.getFluid())) {
		        			tempHandler.fill(new FluidStack(tempHandler.getFluid().getFluid(), tempHandler.getFluid().amount), true);
		        			player.playSound(baseBlock.getFluid().getFillSound(), 0.5F, (float) (1.0F));
		        			world.setBlockToAir(clickPos);
		                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);	
	        			}
	        		}
	        	}
	        }
	        if(world.getBlockState(clickPos).getBlock() == Blocks.WATER) {
        		if(tempHandler.getFluid() == null) {
        			tempHandler.fill(new FluidStack(FluidRegistry.WATER, 1000), true);
        			player.playSound(FluidRegistry.WATER.getFillSound(), 0.5F, (float) (0.5F + Math.random()*2.0));
        			world.setBlockToAir(clickPos);
                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
        		}else if(tempHandler.getFluid().amount + 1000 <= CAPACITY  || player.isCreative()) {
        			if(tempHandler.getFluid().getFluid().equals(FluidRegistry.WATER)) {
	        			tempHandler.fill(new FluidStack(tempHandler.getFluid().getFluid(), tempHandler.getFluid().amount), true);
	        			player.playSound(FluidRegistry.WATER.getFillSound(), 0.5F, (float) (1.0F));
	        			world.setBlockToAir(clickPos);
	                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);	
        			}
        		}	
	        }
	        if(world.getBlockState(clickPos).getBlock() == Blocks.LAVA) {
        		if(tempHandler.getFluid() == null) {
        			tempHandler.fill(new FluidStack(FluidRegistry.LAVA, 1000), true);
        			player.playSound(FluidRegistry.LAVA.getFillSound(), 0.5F, (float) (0.5F + Math.random()*2.0));
        			world.setBlockToAir(clickPos);
                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
        		}else if(tempHandler.getFluid().amount + 1000 <= CAPACITY  || player.isCreative()) {
        			if(tempHandler.getFluid().getFluid().equals(FluidRegistry.LAVA)) {
	        			tempHandler.fill(new FluidStack(tempHandler.getFluid().getFluid(), tempHandler.getFluid().amount), true);
	        			player.playSound(FluidRegistry.LAVA.getFillSound(), 0.5F, (float) (1.0F));
	        			world.setBlockToAir(clickPos);
	                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);	
        			}
        		}	
	        }
        }else{
        	 // empty bucket shouldn't exist, do nothing since it should be handled by the bucket event
            if (fluidStack == null || fluidStack.amount < 1000){
                return ActionResult.newResult(EnumActionResult.PASS, itemstack);
            }
            // can we place liquid there?
            if (world.isBlockModifiable(player, clickPos)){
                // the block adjacent to the side we clicked on
                BlockPos targetPos = clickPos.offset(mop.sideHit);

                // can the player place there?
                if (player.canPlayerEdit(targetPos, mop.sideHit, itemstack)){
                    // try placing liquid
                    if (FluidUtil.tryPlaceFluid(player, player.getEntityWorld(), fluidStack, targetPos) && !player.capabilities.isCreativeMode){
                        // success!
                        player.addStat(StatList.getObjectUseStats(this));
                        tempHandler.drain(1000, true);
                            return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
                    }
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
    }
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
    	if(stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
    		FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    	    if(tempHandler.getFluid() != null) {
        		list.add(NumberFormat.getNumberInstance(Locale.US).format(tempHandler.getFluid().amount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(CAPACITY) + " mB");
    	    }else{
        		list.add(0 + "/" + NumberFormat.getNumberInstance(Locale.US).format(CAPACITY) + " mB");
    	    }
    	    list.add(EnumTextFormatting.ITALIC + "Shift Right-Click to Place.");
    	}
    }
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt){
    	return new FluidHandlerItemStack(stack, CAPACITY);
    }
	@Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		if(tintIndex == 0) {
			return -1;
		}else{
			if(stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
	    		FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
	    		if(tempHandler.getFluid() != null) {
	    			if(tempHandler.getFluid().getFluid().getBlock() != null) {
	    				return tempHandler.getFluid().getFluid().getBlock().getMaterial(tempHandler.getFluid().getFluid().getBlock().getDefaultState()).getMaterialMapColor().colorValue;	
	    			}else{
	    				return -1;
	    			}
	    		}else{
	    			return GUIUtilities.getColor(50, 50, 50);
	    		}
	    	}
			return 1;
		}
	}
}
