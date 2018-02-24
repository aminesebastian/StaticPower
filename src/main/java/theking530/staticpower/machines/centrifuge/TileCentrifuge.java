package theking530.staticpower.machines.centrifuge;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import theking530.staticpower.assists.utilities.InventoryUtilities;
import theking530.staticpower.handlers.crafting.registries.CentrifugeRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.CentrifugeRecipeWrapper;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemInputServo;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemOutputServo;

public class TileCentrifuge extends BaseMachine {
	public static final int DEFAULT_MAX_ROTATION_SPEED = 1000;
	private int rotationSpeed;
	private int maxRotationSpeed = DEFAULT_MAX_ROTATION_SPEED;
	private float multiplier;
	
	public TileCentrifuge() {
		multiplier = 0.0f;
		
		initializeSlots(2, 2, 3);
		initializeBasicMachine(2, 1000, 100000, 80, 100);
		registerComponent(new BatteryInteractionComponent("BatteryComponent", slotsInternal, 1, this, energyStorage));
		registerComponent(new TileEntityItemOutputServo(this, 2, slotsOutput, 0, 1, 2));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0, 1));
	}
		
	public int getRotationSpeed() {
		return rotationSpeed;
	}
	public int getMaxRotationSpeed() {
		return maxRotationSpeed;
	}
	public float getMultiplier() {
		return multiplier;
	}
	
	
	//IInventory				
	@Override
	public String getName() {
		return "container.Centrifuge";		
	}
	
   	public List<ItemStack> getResult() {
   		CentrifugeRecipeWrapper recipe = CentrifugeRecipeRegistry.Centrifuging().getRecipe(slotsInput.getStackInSlot(0), rotationSpeed);
		return recipe != null ? recipe.getOutputItems() : null;
	}
	public boolean hasResult() {
		return getResult() != null;
	}
	public boolean canProcess() {
		if(hasResult()) {
			if(energyStorage.getEnergyStored() >= getProcessingCost()) {
				List<ItemStack>items = getResult();
				if(items != null) {
					return InventoryUtilities.canInsertItemsIntoInventory(slotsOutput, items);
				}
			}
		}
		return false;
	}
	
	@Override
	public void deserializeData(NBTTagCompound nbt) {
		super.deserializeData(nbt);
		rotationSpeed = nbt.getInteger("speed");
		multiplier = nbt.getFloat("mult");
	}
	@Override
	public NBTTagCompound serializeData(NBTTagCompound nbt) {
		super.serializeData(nbt);
		nbt.setInteger("speed", rotationSpeed);
		nbt.setFloat("mult", multiplier);
		return nbt;
	}
	
	public void process() {
		if(!getWorld().isRemote) {
			useEnergy(10);
			if(!slotsInput.getStackInSlot(1).isEmpty() && multiplier < 1.0f) {
				slotsInput.extractItem(1, 1, false);
				multiplier += 0.01f;
			}
			if(energyStorage.getEnergyStored() > 10 && rotationSpeed < maxRotationSpeed) {
				rotationSpeed++;
			}
			if(!isProcessing() && !isMoving() && canProcess()) {
				moveTimer++;
			}
			if(!isProcessing() && isMoving() && canProcess()) {
				if(moveTimer < moveSpeed) {
					moveTimer++;
				}else{
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
					if(InventoryUtilities.canInsertItemsIntoInventory(slotsOutput, CentrifugeRecipeRegistry.Centrifuging().getOutputs(slotsInternal.getStackInSlot(0), rotationSpeed))) {
						List<ItemStack> items = CentrifugeRecipeRegistry.Centrifuging().getOutputs(slotsInternal.getStackInSlot(0), rotationSpeed);
						for(int i=0; i<items.size(); i++) {
							slotsOutput.insertItem(i, items.get(i).copy(), false);
						}
						setInternalStack(0, ItemStack.EMPTY);
						moveTimer = 0;
						if(multiplier >= 0.01f) {
							multiplier -= 0.01;		
						}
					}
				}
			}
		}	
	}
}