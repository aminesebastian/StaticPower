package theking530.staticpower.items.containers;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.DispenseFluidContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.items.ItemBase;
import theking530.staticpower.utils.EnumTextFormatting;

public class BaseFluidCapsule extends ItemBase { // implements IItemColor  {

	public int CAPACITY;
	
	public BaseFluidCapsule(String name, int capacity) {
		super(name);
		CAPACITY = capacity;
	    BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DispenseFluidContainer.getInstance());
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    	items.add(new ItemStack(this));
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
                // add all fluids that the bucket can be filled  with
                ItemStack stack = new ItemStack(this);
                FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY , null);
                FluidStack fs = new FluidStack(fluid, CAPACITY);
                if (tempHandler.fill(fs, true) == fs.amount) {
                	items.add(stack);
                }
        }
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
    	FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
    	if(tempHandler != null && tempHandler.getFluid() != null) {
    		return tempHandler.getFluid().getLocalizedName() + " " + super.getItemStackDisplayName(stack);
    	}else{
    		String tempString;
    		if(CAPACITY == 2000) {
    			tempString = "";
    		}else if(CAPACITY == 4000) {
    			tempString = "Static ";
    		}else if(CAPACITY == 8000) {
    			tempString = "Energized ";
    		}else{
    			tempString = "Lumum ";
    		}
    		return tempString + super.getItemStackDisplayName(stack);
    	}
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
    	FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        FluidStack fluidStack = tempHandler.getFluid();

        // clicked on a block?
        RayTraceResult mop = this.rayTrace(world, player, true);

        if(mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK){
            return ActionResult.newResult(EnumActionResult.PASS, itemStack);
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
	                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
	        		}else if(tempHandler.getFluid().amount + 1000 <= CAPACITY  || player.isCreative()) {
	        			if(tempHandler.getFluid().getFluid().equals(baseBlock.getFluid())) {
		        			tempHandler.fill(new FluidStack(tempHandler.getFluid().getFluid(), tempHandler.getFluid().amount), true);
		        			player.playSound(baseBlock.getFluid().getFillSound(), 0.5F, (float) (1.0F));
		        			world.setBlockToAir(clickPos);
		                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);	
	        			}
	        		}
	        	}
	        }
	        if(world.getBlockState(clickPos).getBlock() == Blocks.WATER) {
        		if(tempHandler.getFluid() == null) {
        			tempHandler.fill(new FluidStack(FluidRegistry.WATER, 1000), true);
        			player.playSound(FluidRegistry.WATER.getFillSound(), 0.5F, (float) (0.5F + Math.random()*2.0));
        			world.setBlockToAir(clickPos);
                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
        		}else if(tempHandler.getFluid().amount + 1000 <= CAPACITY  || player.isCreative()) {
        			if(tempHandler.getFluid().getFluid().equals(FluidRegistry.WATER)) {
	        			tempHandler.fill(new FluidStack(tempHandler.getFluid().getFluid(), tempHandler.getFluid().amount), true);
	        			player.playSound(FluidRegistry.WATER.getFillSound(), 0.5F, (float) (1.0F));
	        			world.setBlockToAir(clickPos);
	                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);	
        			}
        		}	
	        }
	        if(world.getBlockState(clickPos).getBlock() == Blocks.LAVA) {
        		if(tempHandler.getFluid() == null) {
        			tempHandler.fill(new FluidStack(FluidRegistry.LAVA, 1000), true);
        			player.playSound(FluidRegistry.LAVA.getFillSound(), 0.5F, (float) (0.5F + Math.random()*2.0));
        			world.setBlockToAir(clickPos);
                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
        		}else if(tempHandler.getFluid().amount + 1000 <= CAPACITY  || player.isCreative()) {
        			if(tempHandler.getFluid().getFluid().equals(FluidRegistry.LAVA)) {
	        			tempHandler.fill(new FluidStack(tempHandler.getFluid().getFluid(), tempHandler.getFluid().amount), true);
	        			player.playSound(FluidRegistry.LAVA.getFillSound(), 0.5F, (float) (1.0F));
	        			world.setBlockToAir(clickPos);
	                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);	
        			}
        		}	
	        }
        }else{
        	 // empty bucket shouldn't exist, do nothing since it should be handled by the bucket event
            if (fluidStack == null || fluidStack.amount < 1000){
                return ActionResult.newResult(EnumActionResult.PASS, itemStack);
            }
            // can we place liquid there?
            if (world.isBlockModifiable(player, clickPos)){
                // the block adjacent to the side we clicked on
                BlockPos targetPos = clickPos.offset(mop.sideHit);

                // can the player place there?
                if (player.canPlayerEdit(targetPos, mop.sideHit, itemStack)){
                    // try placing liquid
                    if (FluidUtil.tryPlaceFluid(player, world, targetPos, itemStack, fluidStack) != FluidActionResult.FAILURE && !player.capabilities.isCreativeMode){
                        // success!
                        player.addStat(StatList.getObjectUseStats(this));
                        tempHandler.drain(1000, true);
                            return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
                    }
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
    }
    @Override
    	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
    	if(stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)){
    		FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
    	    if(tempHandler.getFluid() != null) {
        		list.add(NumberFormat.getNumberInstance(Locale.US).format(tempHandler.getFluid().amount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(CAPACITY) + " mB");
    	    }else{
        		list.add(0 + "/" + NumberFormat.getNumberInstance(Locale.US).format(CAPACITY) + " mB");
    	    }
    	    if(showHiddenTooltips()) {
    	    	list.add("Right-Click to Pick Up.");
    	    	list.add("Shift Right-Click to Place.");
    	    }else{
    	    	list.add(EnumTextFormatting.ITALIC + "Hold Shift");
    	    }
    	}
    }
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt){
    	return new FluidHandlerItemStack(stack, CAPACITY);
    }
}
