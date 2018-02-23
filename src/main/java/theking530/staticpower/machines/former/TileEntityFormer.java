package theking530.staticpower.machines.former;

import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.utilities.InventoryUtilities;
import theking530.staticpower.handlers.crafting.registries.FormerRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.FormerRecipeWrapper;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemInputServo;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemOutputServo;

public class TileEntityFormer extends BaseMachine {
	
	public TileEntityFormer() {
		initializeSlots(2, 2, 1);
		initializeBasicMachine(2, 1000, 100000, 80, 100);
		registerComponent(new BatteryInteractionComponent("BatteryComponent", slotsInternal, 1, this, energyStorage));
		registerComponent(new TileEntityItemOutputServo(this, 2, slotsOutput, 0));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0, 1));
	}
		
	//IInventory				
	@Override
	public String getName() {
		return "container.Former";		
	}
	
	@Override
   	public ItemStack getResult(ItemStack itemStack) {
		if(itemStack != ItemStack.EMPTY && slotsInput.getStackInSlot(1) != ItemStack.EMPTY) { //Null check needed
			FormerRecipeWrapper recipe = FormerRecipeRegistry.Forming().getFormingResult(itemStack, slotsInput.getStackInSlot(1));
			if(recipe != null) {
				return recipe.getOutputItemStack();	
			}
		}
		return ItemStack.EMPTY;
	}
	public boolean hasResult(ItemStack itemStack) {
		return getResult(itemStack) != ItemStack.EMPTY;
	}
	@Override
	public boolean canProcess(ItemStack itemStack) {
		if(hasResult(itemStack)) {
			if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(itemStack)) && energyStorage.getEnergyStored() >= getProcessingCost()) {
				return true;
			}
		}
		return false;
	}
	public void process() {
		if(!getWorld().isRemote) {
			if(!isProcessing() && !isMoving() && canProcess(getInputStack(0))) {
				moveTimer++;
			}
			if(!isProcessing() && isMoving() && canProcess(getInputStack(0))) {
				if(moveTimer < moveSpeed) {
					moveTimer++;
				}else{
					slotsInternal.setStackInSlot(0, getResult(getInputStack(0)).copy());
					transferItemInternally(slotsInput, 0, slotsInternal, 0);
					processingTimer = 1;
					moveTimer = 0;
				}
			}
			if(isProcessing() && !isMoving()) {
				if(processingTimer < processingTime) {
					useEnergy(getProcessingCost() / processingTime);
					processingTimer++;
				}else{
					processingTimer=0;
					updateBlock();
					if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, slotsInternal.getStackInSlot(0))) {
						InventoryUtilities.insertItemIntoInventory(slotsOutput, slotsInternal.getStackInSlot(0), 0, 0);
						setInternalStack(0, ItemStack.EMPTY);
						moveTimer = 0;
					}
				}
			}
		}	
	}
}