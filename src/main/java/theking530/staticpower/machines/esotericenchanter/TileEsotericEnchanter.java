package theking530.staticpower.machines.esotericenchanter;

import javax.annotation.Nonnull;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.utilities.InventoryUtilities;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.crafting.registries.EsotericEnchanterRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemInputServo;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemOutputServo;

public class TileEsotericEnchanter extends BaseMachineWithTank {

	private ItemStack nextOutputItemStack;
	private int fluidLeftToExtract;
	private int fluidRequired;
	
	private FluidContainerComponent fluidContainerComponent;
	
	public TileEsotericEnchanter() {
		this.initializeBasicMachine(1.0f, 5000, 100000, 120, 200);
		this.initializeSlots(6, 3, 1);
		this.initializeTank(5000);
		
		this.registerComponent(new BatteryInteractionComponent("Battery Component", slotsInternal, 3, this, getEnergyStorage()));
		
		nextOutputItemStack = ItemStack.EMPTY;
		fluidContainerComponent = new FluidContainerComponent("Bucket Component", slotsInternal, 4, slotsInternal, 5, this, fluidTank, 40);
		registerComponent(fluidContainerComponent);
		
		registerComponent(new TileEntityItemOutputServo(this, 1, slotsOutput, 0));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0, 1, 2));
	}
	
	@Override
	public void process() {
		if(!getWorld().isRemote) {
			if(!isProcessing() && !isMoving() && getOutputStack(0).isEmpty() && energyStorage.getEnergyStored() >= getProcessingCost() && EsotericEnchanterRecipeRegistry.Enchanting().getEnchantingResult(getInputStack(0), getInputStack(1), getInputStack(2), fluidTank.getFluid()) != null) {
				moveTimer = 1;
			}	
			if(!isProcessing() && isMoving() && EsotericEnchanterRecipeRegistry.Enchanting().getEnchantingResult(getInputStack(0), getInputStack(1), getInputStack(2), fluidTank.getFluid()) != null) {
				if(moveTimer < moveSpeed) {
					moveTimer++;
				}else{
					nextOutputItemStack = EsotericEnchanterRecipeRegistry.Enchanting().getEnchantingResult(getInputStack(0), getInputStack(1), getInputStack(2), fluidTank.getFluid()).getOutputItemStack().copy();
					fluidLeftToExtract = EsotericEnchanterRecipeRegistry.Enchanting().getEnchantingResult(getInputStack(0), getInputStack(1), getInputStack(2), fluidTank.getFluid()).getInputFluidStack().amount;
						
					transferItemInternally(slotsInput, 0, slotsInternal, 0);
					transferItemInternally(slotsInput, 1, slotsInternal, 1);
					transferItemInternally(slotsInput, 2, slotsInternal, 2);
					
					fluidRequired = fluidLeftToExtract;
					processingTimer = 1;
					moveTimer = 0;
				}
			}else{
				moveTimer = 0;
			}
			if(isProcessing() && !isMoving()) {
				if(processingTimer < processingTime) {
					useEnergy(getProcessingCost() / processingTime);
					int drainAmount = fluidRequired/processingTime;
					fluidLeftToExtract -= drainAmount;
					fluidTank.drain(drainAmount, true);
					processingTimer++;
					updateBlock();
				}else{
					processingTimer=0;
					fluidTank.drain(fluidLeftToExtract, true);
					updateBlock();
					if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, nextOutputItemStack)) {
						InventoryUtilities.insertItemIntoInventory(slotsOutput, nextOutputItemStack, 0, 0);
						setInternalStack(0, ItemStack.EMPTY);
						setInternalStack(1, ItemStack.EMPTY);
						setInternalStack(2, ItemStack.EMPTY);
						moveTimer = 0;
					}
				}
			}
		}
	}
	@Override
	public ItemStack getResult(ItemStack itemstack) {
		return null;	
	}	
	@Override
	public boolean canProcess(ItemStack itemstack) {
		return false;
	}
	@Override
    public String getName(){
    	return "container.EsotericEnchanter";
    }
	public FluidContainerComponent getFluidInteractionComponent() {
		return fluidContainerComponent;
	}

	public void deserializeData(NBTTagCompound nbt) {
		super.deserializeData(nbt);
		fluidLeftToExtract = nbt.getInteger("FLUID_LEFT");
		fluidRequired = nbt.getInteger("FLUID_REQ");
		nextOutputItemStack = new ItemStack(nbt.getCompoundTag("OUTPUT"));
    }
    public NBTTagCompound serializeData(NBTTagCompound nbt) {
    	super.serializeData(nbt);
    	nbt.setInteger("FLUID_REQ", fluidRequired);
    	nbt.setInteger("FLUID_LEFT", fluidLeftToExtract);
    	NBTTagCompound outItem = new NBTTagCompound();
    	nextOutputItemStack.writeToNBT(outItem);
    	nbt.setTag("OUTPUT", outItem);
		return nbt;	
    }
    
    @Nonnull
    public ItemStack insertItem(Mode sideMode, int slot, @Nonnull ItemStack stack, boolean simulate) {
    	if(sideMode == Mode.Output) {
    		return stack;
    	}
    	if(stack.getItem() != null && stack.getItem() == Items.BOOK) {
    		return slotsInput.insertItem(0, stack, simulate);
    	}
    	if(slot != 0) {
        	return slotsInput.insertItem(slot, stack, simulate);
    	}
    	return stack;
    }
    @Override
	public int fill(FluidStack resource, boolean doFill, EnumFacing facing) {
    	updateBlock();
		if(resource != null && resource.getFluid() == ModFluids.LiquidExperience) {
			return fluidTank.fill(resource, doFill);
		}
		return 0;
	}
}
