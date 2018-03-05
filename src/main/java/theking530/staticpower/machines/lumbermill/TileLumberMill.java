package theking530.staticpower.machines.lumbermill;

import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.utilities.InventoryUtilities;
import theking530.staticpower.handlers.crafting.registries.LumberMillRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.LumberMillRecipeWrapper;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemInputServo;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemOutputServo;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent.FluidContainerInteractionMode;

public class TileLumberMill extends BaseMachineWithTank {

	public TileLumberMill() {
		initializeSlots(4, 1, 2);
		initializeBasicMachine(2, 1000, 100000, 80, 150);
		initializeTank(5000);
		registerComponent(new TileEntityItemOutputServo(this, 1, slotsOutput, 0, 1));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0));
		registerComponent(new BatteryInteractionComponent("BatteryComponent", slotsInternal, 1, energyStorage));
		
		FluidContainerComponent drainComponent;
		registerComponent(drainComponent = new FluidContainerComponent("BucketDrain", slotsInternal, 2, slotsInternal, 3, this, fluidTank));
		drainComponent.setMode(FluidContainerInteractionMode.DRAIN);
	}
	@Override
	public String getName() {
		return "container.LumberMill";		
	}			
	
	//Functionality
	public boolean hasResult(ItemStack itemStack) {
		return LumberMillRecipeRegistry.Milling().getMillingRecipe(itemStack) != null;
	}
	@Override
	public boolean canProcess(ItemStack itemStack) {
		if(hasResult(itemStack)) {
			LumberMillRecipeWrapper recipe = LumberMillRecipeRegistry.Milling().getMillingRecipe(itemStack);
			if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, recipe.getMainOutput()) && energyStorage.getEnergyStored() >= getProcessingCost()) {
				if(!recipe.getSecondaryOutput().isEmpty()) {
					if(!InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 1, recipe.getSecondaryOutput())) {
						return false;
					}
				}
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
					transferItemInternally(slotsInput, 0, slotsInternal, 0);
					processingTimer = 1;
					moveTimer = 0;
					updateBlock();
				}
			}
			if(isProcessing() && !isMoving()) {
				if(processingTimer < processingTime) {
					useEnergy(getProcessingCost() / processingTime);
					processingTimer++;
				}else{
					processingTimer=0;
					updateBlock();
					LumberMillRecipeWrapper recipe = LumberMillRecipeRegistry.Milling().getMillingRecipe(getInternalStack(0));
					if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, recipe.getMainOutput()) && InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 1, recipe.getSecondaryOutput())) {
						slotsOutput.insertItem(0, recipe.getMainOutput().copy(), false);
						slotsOutput.insertItem(1, recipe.getSecondaryOutput().copy(), false);
						setInternalStack(0, ItemStack.EMPTY);
						moveTimer = 0;
					}
				}
			}
		}	
	}
}


	
