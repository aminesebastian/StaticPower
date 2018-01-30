package theking530.staticpower.items.containers;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.block.BlockDispenser;
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
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.items.ItemBase;

public class BaseFluidCapsule extends ItemBase { 

	public int CAPACITY;
	public int DAMAGE_DIVISOR;
	
	public BaseFluidCapsule(String name, int capacity, int damageDivisor) {
		super(name);
		CAPACITY = capacity;
	    BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DispenseFluidContainer.getInstance());
		setMaxDamage(capacity/damageDivisor);
		setMaxStackSize(1);
		setNoRepair();
		DAMAGE_DIVISOR = damageDivisor;
	}
	@Override
    public boolean showDurabilityBar(ItemStack stack) {
    	FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        FluidStack fluidStack = tempHandler.getFluid();
    	if(fluidStack != null) {
            return true;
    	}
		return false;
    }
    public double getDurabilityForDisplay(ItemStack stack) {
    	FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        FluidStack fluidStack = tempHandler.getFluid();
    	if(fluidStack != null) {
            return 1-((double)fluidStack.amount / (double)CAPACITY);
    	}
		return getMaxDamage(stack);
    }

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
    	
    	if(tempHandler != null && tempHandler.getFluid() != null) {
    		return tempString + tempHandler.getFluid().getLocalizedName() + " " + super.getItemStackDisplayName(stack);
    	}else{
    		return tempString + super.getItemStackDisplayName(stack);
    	}
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
    	FluidHandlerItemStack tempHandler = (FluidHandlerItemStack) itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

        RayTraceResult rayTrace = this.rayTrace(world, player, true);

        if(rayTrace == null || rayTrace.typeOfHit != RayTraceResult.Type.BLOCK){
            return ActionResult.newResult(EnumActionResult.PASS, itemStack);
        }

        BlockPos clickPos = rayTrace.getBlockPos();
        if(!player.isSneaking()) {
        	Fluid fluid = null;
        	
	        if(world.getBlockState(clickPos).getBlock() instanceof IFluidBlock) {
	        	IFluidBlock baseBlock = (IFluidBlock) world.getBlockState(clickPos).getBlock();
	        	fluid = baseBlock.getFluid();	     
	        }else if(world.getBlockState(clickPos).getBlock() == Blocks.WATER) {
	        	fluid = FluidRegistry.WATER;
	        }else if(world.getBlockState(clickPos).getBlock() == Blocks.LAVA) {
        		fluid = FluidRegistry.LAVA;
	        }
	        if(fluid != null) {
				int filled = tempHandler.fill(new FluidStack(fluid, 1000), false);
				if(filled == 1000) {
					tempHandler.fill(new FluidStack(fluid, 1000), true);
	      			world.setBlockToAir(clickPos);
	        		player.playSound(fluid.getFillSound(tempHandler.getFluid()), 0.5F, (float) (1.0F));
	                return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);	
				}
	        }
        }else{
            FluidStack fluidStack = tempHandler.getFluid();
            if (fluidStack == null || fluidStack.amount < 1000){
                return ActionResult.newResult(EnumActionResult.PASS, itemStack);
            }
            if (world.isBlockModifiable(player, clickPos)){
                BlockPos targetPos = clickPos.offset(rayTrace.sideHit);
                if (player.canPlayerEdit(targetPos, rayTrace.sideHit, itemStack)){
                    if (FluidUtil.tryPlaceFluid(player, world, targetPos, itemStack, fluidStack) != FluidActionResult.FAILURE){
                        player.addStat(StatList.getObjectUseStats(this));
                		player.playSound(fluidStack.getFluid().getEmptySound(fluidStack), 0.5F, (float) (1.0F));
                        if(!player.capabilities.isCreativeMode) {
                            tempHandler.drain(1000, true);
                        }
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
    	return new SPItemStackFluidHandler(stack, CAPACITY);
    }
}
