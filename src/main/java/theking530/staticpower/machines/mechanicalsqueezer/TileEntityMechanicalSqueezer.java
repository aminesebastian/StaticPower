package theking530.staticpower.machines.mechanicalsqueezer;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityMechanicalSqueezer extends BaseTileEntity {

	private String customName;
	public FluidTank TANK;
	public int PROCESSING_TIMER = 0;
	public int PROCESSING_TIME = 20;
	public int MOVE_TIMER = 0;
	public int MOVE_SPEED = 4;
	public int FLUID_TO_CONTAINER_RATE = 100; //1 Bucket
	
	public DrainToBucketComponent DRAIN_COMPONENT;

	public TileEntityMechanicalSqueezer() {
		initializeBasicTileEntity(1, 2, 2);
		TANK = new FluidTank(1000);
		DRAIN_COMPONENT = new DrainToBucketComponent(SLOTS_INPUT, 1, SLOTS_OUTPUT, 1, this, TANK, FLUID_TO_CONTAINER_RATE);
	}
	@Override
	public String getName() {
		return "Mechanical Squeezer";		
	}		
	@Override  
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        TANK.readFromNBT(nbt);
		PROCESSING_TIMER = nbt.getInteger("P_TIMER");
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		TANK.writeToNBT(nbt);
		nbt.setInteger("P_TIMER", PROCESSING_TIMER);
    	return nbt;
	}
	
	
    //Process
	public ItemStack getResult(ItemStack itemStack) {
		return SqueezerRecipeRegistry.Squeezing().getSqueezingItemResult(itemStack);
	}
	@Override 
	public boolean hasResult(ItemStack itemstack) {
		if(itemstack != null && getResult(itemstack) != null) {
			return true;
		}
		return false;
	}
	public FluidStack getFluidResult(ItemStack itemStack) {
    	if(itemStack != null) {
    		return SqueezerRecipeRegistry.Squeezing().getSqueezingFluidResult(itemStack);
    	}
    	return null;
    }
	public boolean canProcess(ItemStack itemstack) {
		FluidStack fluidstack = SqueezerRecipeRegistry.Squeezing().getSqueezingFluidResult(itemstack);
		if(hasResult(itemstack) && fluidstack != null && canSlotAcceptItemstack(getResult(itemstack), SLOTS_OUTPUT.getStackInSlot(0))) {
			if(fluidstack.amount + TANK.getFluidAmount() > TANK.getCapacity()) {
				return false;
			}
			if (TANK.getFluid() != null && !fluidstack.isFluidEqual(TANK.getFluid())) {
				return false;
			}
			if(TANK.getFluid() == null) {
				return true;
			}
			if(TANK.getFluidAmount() + fluidstack.amount <= TANK.getCapacity()) {
				return true;
			}
			if(TANK.getFluidAmount() + fluidstack.amount > TANK.getCapacity()) {
				return false;
			}
			if (TANK.getFluid() != null && fluidstack.isFluidEqual(TANK.getFluid())) {
				return true;
			}				
		}
		return false;
	}
	public boolean isProcessing(){
		return PROCESSING_TIMER > 0;
	}
	public boolean isMoving(){
		return MOVE_TIMER > 0;
	}
	public int getProgressScaled(int i) {
		float ratio = (float)PROCESSING_TIMER/(float)PROCESSING_TIME;
		return (int) (ratio * i);
	}
	@Override
	public void process(){
		DRAIN_COMPONENT.drainToContainer();
	}
	public void rightClick() {
		this.sync();
		if(SLOTS_INTERNAL.getStackInSlot(0) == null){
			PROCESSING_TIMER = 0;
		}
		//Start Process
		if(!isProcessing() && !isMoving() && canProcess(SLOTS_INPUT.getStackInSlot(0))) {
			MOVE_TIMER = 1;
		}
		//Start Moving
		if(!isProcessing() && isMoving() && canProcess(SLOTS_INPUT.getStackInSlot(0))) {
			MOVE_TIMER++;
			if(MOVE_TIMER >= MOVE_SPEED) {
				MOVE_TIMER = 0;
				moveItem(SLOTS_INPUT, 0, SLOTS_INTERNAL, 0);
				PROCESSING_TIMER = 1;	
			}
		}else{
			MOVE_TIMER = 0;
		}
		//Start Processing
		if(isProcessing() && !isMoving() && canProcess(SLOTS_INTERNAL.getStackInSlot(0))) {
			if(PROCESSING_TIMER < PROCESSING_TIME) {
				PROCESSING_TIMER++;
				worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_SAND_STEP, SoundCategory.BLOCKS, 0.15f, 1, false);						
			}else{				
				if(InventoryUtilities.canFullyInsertItemIntoSlot(SLOTS_OUTPUT, 0, getResult(SLOTS_INTERNAL.getStackInSlot(0)))) {
					TANK.fill(getFluidResult(SLOTS_INTERNAL.getStackInSlot(0)), true);
					SLOTS_OUTPUT.insertItem(0, getResult(SLOTS_INTERNAL.getStackInSlot(0)).copy(), false);
					SLOTS_INTERNAL.setStackInSlot(0, null);
					PROCESSING_TIMER = 0;
					sync();
				}
			}
		}	
	}
	public float getFluidLevelScaled(int height) {
		int capacity = TANK.getCapacity();
		int volume = TANK.getFluidAmount();
		if(capacity != 0) {
			float percentage = (float)volume/(float)capacity;
			return percentage * height;
		}else{
			return 0;
		}
	}
}


	
