package theking530.staticpower.machines.centrifuge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.assists.utilities.InventoryUtilities;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
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
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, Mode.Input2, 1));
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

			if(!slotsInput.getStackInSlot(1).isEmpty() && multiplier < 1.0f) {
				slotsInput.extractItem(1, 1, false);
				multiplier += 0.01f;
			}
			if(energyStorage.getEnergyStored() >= 10) {
				rotationSpeed = Math.min(maxRotationSpeed, rotationSpeed+1);
				useEnergy(10);
			}else{
				rotationSpeed = Math.max(0, rotationSpeed-1);
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
			}else{
				moveTimer = 0;
			}
			if(isProcessing() && !isMoving()) {
				if(processingTimer < processingTime) {
					useEnergy(getProcessingCost() / processingTime);
					processingTimer++;
				}else{
					List<ItemStack> items = CentrifugeRecipeRegistry.Centrifuging().getOutputs(slotsInternal.getStackInSlot(0), rotationSpeed);
					if(InventoryUtilities.canInsertItemsIntoInventory(slotsOutput,items)) {
						for(int i=0; i<items.size(); i++) {
							InventoryUtilities.insertItemIntoInventory(slotsOutput, items.get(i).copy());
						}
						setInternalStack(0, ItemStack.EMPTY);
						moveTimer = 0;
						if(multiplier >= 0.01f) {
							multiplier -= 0.01;		
						}
						processingTimer=0;
						updateBlock();
					}
				}
			}
		}	
	}
    public boolean canInsertItem(ItemStackHandler itemHandler,  Mode sideMode, int slot, ItemStack stack) {
    	if(itemHandler == slotsInput && sideMode == Mode.Input2) {
    		
    	}
    	return true;
    }
    public boolean canExtractItem(ItemStackHandler itemHandler,  Mode sideMode, int slot, int amount) {
    	return true;
    }
    
	@Override
	public List<Mode> getValidSideConfigurations() {
		List<Mode> modes = new ArrayList<Mode>();
		modes.add(Mode.Input);
		modes.add(Mode.Input2);
		modes.add(Mode.Output);
		modes.add(Mode.Regular);
		modes.add(Mode.Disabled);
		return modes;
	}
}