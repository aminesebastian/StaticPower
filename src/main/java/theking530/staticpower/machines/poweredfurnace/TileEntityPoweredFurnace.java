package theking530.staticpower.machines.poweredfurnace;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.machines.machinecomponents.FillFromBatteryComponent;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityPoweredFurnace extends BaseMachine {

	private FillFromBatteryComponent BATTERY_COMPONENT;
	
	public TileEntityPoweredFurnace() {
		initializeBasicMachine(2, 1000, 100000, 80, 150, 1, 2, 1);
		BATTERY_COMPONENT = new FillFromBatteryComponent("BatteryComponent", slotsInput, 1, this, STORAGE);
	}
	@Override
	public String getName() {
		return "Powered Furnace";		
	}			
	
	//Functionality
	@Override
   	public ItemStack getResult(ItemStack itemStack) {
		if(itemStack!= ItemStack.EMPTY) {
			return FurnaceRecipes.instance().getSmeltingResult(itemStack);
		}else{
			return ItemStack.EMPTY;
		}
	}
	public boolean hasResult(ItemStack itemStack) {
		if(itemStack != null) {
			if(FurnaceRecipes.instance().getSmeltingResult(itemStack) != ItemStack.EMPTY) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean canProcess(ItemStack itemStack) {
		if(hasResult(itemStack)) {
			if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(itemStack)) && STORAGE.getEnergyStored() >= getProcessingCost()) {
				return true;
			}
		}
		return false;
	}
	public void process() {
		if(!getWorld().isRemote) {
			BATTERY_COMPONENT.update();
			if(!isProcessing() && !isMoving() && canProcess(getInputStack(0))) {
				MOVE_TIMER++;
			}
			if(!isProcessing() && isMoving() && canProcess(getInputStack(0))) {
				if(MOVE_TIMER < MOVE_SPEED) {
					MOVE_TIMER++;
				}else{
					moveItem(slotsInput, 0, slotsInternal, 0);
					PROCESSING_TIMER = 1;
					MOVE_TIMER = 0;
				}
			}
			if(isProcessing() && !isMoving()) {
				if(PROCESSING_TIMER < PROCESSING_TIME) {
					useEnergy(getProcessingCost() / PROCESSING_TIME);
					PROCESSING_TIMER++;
				}else{
					PROCESSING_TIMER=0;
					updateBlock();
					if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(getInternalStack(0)))) {
						InventoryUtilities.insertItemIntoInventory(slotsOutput, getResult(getInternalStack(0)), 0, 0);
						setInternalStack(0, ItemStack.EMPTY);
						MOVE_TIMER = 0;
					}
				}
			}
		}
		
	}
}


	
