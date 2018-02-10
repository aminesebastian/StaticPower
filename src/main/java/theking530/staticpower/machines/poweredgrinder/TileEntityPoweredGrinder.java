package theking530.staticpower.machines.poweredgrinder;

import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.utilities.InventoryUtilities;
import theking530.staticpower.assists.utilities.TileEntityUtilities;
import theking530.staticpower.handlers.crafting.registries.GrinderRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.upgrades.BaseOutputMultiplierUpgrade;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemInputServo;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemOutputServo;

public class TileEntityPoweredGrinder extends BaseMachine {
	
	private float bonusOutputChance;
	
	public TileEntityPoweredGrinder() {
		initializeSlots(1, 2, 3);
		initializeBasicMachine(2, 1000, 100000, 80, 100);
		registerComponent(new BatteryInteractionComponent("BatteryComponent", slotsInput, 1, this, energyStorage));
		registerComponent(new TileEntityItemOutputServo(this, 2, slotsOutput, 0, 1, 2));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0));
		bonusOutputChance = 0.0f;
	}
		
	//IInventory				
	@Override
	public String getName() {
		return "Powered Grinder";		
	}
	
	//Process 
	public void process() {
		if(!getWorld().isRemote) {
			if(!isProcessing() && !isMoving() && canProcess(getInputStack(0))) {
				moveTimer = 1;
			}
			if(!isProcessing() && isMoving() && canProcess(getInputStack(0))) {
				if(moveTimer < moveSpeed) {
					moveTimer++;
				}else{
					transferItemInternally(slotsInput, 0, slotsInternal, 0);
					moveTimer=0;
					processingTimer = 1;
				}
			}
			if(isProcessing() && !isMoving()) {
				if(processingTimer < processingTime) {
					energyStorage.setMaxExtract(getProcessingCost() / processingTime);
					useEnergy(getProcessingCost() / processingTime);
					processingTimer++;
				}else{
					if(getGrindingResult(getInternalStack(0)) != null) {
						for(int j=0; j<getGrindingResult(getInternalStack(0)).getOutputItemCount(); j++) {
							ItemStack result = getGrindingResult(getInternalStack(0)).getOutputItems().get(j).getOutput();
							if(TileEntityUtilities.diceRoll(getGrindingResult(getInternalStack(0)).getOutputItems().get(j).getPercentage()+bonusOutputChance)) {
								boolean flag = false;
								int slot = -1;
								for(int i=0; i<3; i++) {
									if(ItemStack.areItemStacksEqual(slotsOutput.getStackInSlot(0), result) && InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, i, getGrindingResult(getInternalStack(0)).getOutputItems().get(j).getOutput())) {
										slot = i;
										flag = true;
										break;
									}	
								}
								if(!flag) {
									for(int i=0; i<3; i++) {
										if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, i, result)) {
											slot = i;
											break;
										}	
									}
								}
								if(slot != -1) {
									slotsOutput.insertItem(slot, result.copy(), false);
								}
							}						
						}
					}
					setInternalStack(0, ItemStack.EMPTY);
					updateBlock();
					processingTimer=0;
					moveTimer = 0;
				}	
			}
		}
	}
	@Override
	public void upgradeHandler(){
		super.upgradeHandler();
		outputUpgradeHandler();
	}
	private void outputUpgradeHandler() {
		if(hasUpgrade(ModItems.BasicOutputMultiplierUpgrade)) {
			BaseOutputMultiplierUpgrade tempUpgrade = (BaseOutputMultiplierUpgrade) getUpgrade(ModItems.BasicOutputMultiplierUpgrade).getItem();
			bonusOutputChance = tempUpgrade.getMultiplier(getUpgrade(ModItems.BasicOutputMultiplierUpgrade), 0);
		}else{
			bonusOutputChance = 0.0f;
		}
		processingEnergyMult = (processingEnergyMult * (1+bonusOutputChance/2.0f));
	}
	
	public float getBonusOutputChance() {
		return Math.min(1.0f, bonusOutputChance);
	}
	
	public GrinderOutputWrapper getGrindingResult(ItemStack stack) {
		if(stack != ItemStack.EMPTY) {
			return GrinderRecipeRegistry.Grinding().getGrindingResult(stack);
		}else{
			return null;
		}
		
	}
	public boolean hasResult(ItemStack stack) {
		if(stack != ItemStack.EMPTY && getGrindingResult(stack) != null) {
			return true;
		}
		return false;
	}
	@Override
	public boolean canProcess(ItemStack stack) {
		if(hasResult(stack) == true) {
			if(getGrindingResult(stack).getOutputItemCount() > 0) {
				boolean flag = true;
				boolean slot1 = false;
				boolean slot2 = false;
				boolean slot3 = false;
				GrinderOutputWrapper tempWrapper = getGrindingResult(stack);
				for(int i=0; i<tempWrapper.getOutputItemCount(); i++) {
					if(tempWrapper.getOutputItems().get(i).isValid()) {
						if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, tempWrapper.getOutputItems().get(i).getOutput()) && slot1 == false) {
							slot1 = true;
						}else if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 1, tempWrapper.getOutputItems().get(i).getOutput()) && slot2 == false) {
							slot2 = true;
						}else if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 2, tempWrapper.getOutputItems().get(i).getOutput()) && slot3 == false) {
							slot3 = true;
						}else{
							flag = false;
						}
					}

				}
				if(energyStorage.getEnergyStored() >= getProcessingCost() && flag == true) {
					return true;
				}
			}
		}
		return false;
	}
}