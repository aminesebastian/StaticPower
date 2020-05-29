package theking530.staticpower.machines.chargingstation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.machines.TileEntityMachine;
import theking530.staticpower.tileentity.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.tileentity.tileentitycomponents.TileEntityItemInputServo;
import theking530.staticpower.tileentity.tileentitycomponents.TileEntityItemOutputServo;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityChargingStation extends TileEntityMachine {

	public TileEntityChargingStation() {
		super(ModTileEntityTypes.CHARGING_STATION);
		initializeSlots(1, 4, 4, 3);
		initializeBasicMachine(2, 0, 100000, 500, 2);
		energyStorage.setMaxExtract(512);

		registerComponent(new BatteryInteractionComponent("BatteryComponent", slotsInternal, 0, energyStorage));
		registerComponent(new TileEntityItemOutputServo(this, 1, slotsOutput, 0, 1, 2, 3));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0, 1, 2, 3));
	}

	@Override
	public void process() {
		if (energyStorage.getEnergyStored() > 0) {
			for (int i = 0; i < slotsInput.getSlots(); i++) {
				ItemStack stack = slotsInput.getStackInSlot(i);
				if (stack != ItemStack.EMPTY && EnergyHandlerItemStackUtilities.isEnergyContainer(stack)) {
					if (EnergyHandlerItemStackUtilities.getEnergyStored(stack) < EnergyHandlerItemStackUtilities.getEnergyStorageCapacity(stack)) {
						int maxOutput = 100; // this.getEnergyStorage().getCurrentMaximumPowerOutput();
						int charged = EnergyHandlerItemStackUtilities.addEnergyToItemstack(stack, maxOutput, false);
						getEnergyStorage().extractEnergy(charged, false);
					} else {
						moveChargedItemToOutputs(i);
					}
				}
			}
		}
	}

	/**
	 * Attempt to move the charged {@link ItemStack} from the fromSlot of the
	 * slotsInput into any open slot in the slotsOutput.
	 * 
	 * @param fromSlot The slot that contains the charged {@link ItemStack}.
	 */
	public void moveChargedItemToOutputs(int fromSlot) {
		// Iterate through each of the output slots.
		for (int i = 0; i < 4; i++) {
			// If we can place the charged item into an output slot, do so. There's no need
			// to check the result of the call to insertItem as we already know the result
			// is going to be empty.
			if (InventoryUtilities.canFullyInsertStackIntoSlot(slotsOutput, i, slotsInput.getStackInSlot(fromSlot))) {
				System.out.println(slotsInput.getStackInSlot(fromSlot));
				ItemStack stack = slotsInput.extractItem(fromSlot, 1, false);
				slotsOutput.insertItem(i, stack, false);
			}
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerChargingStation(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModBlocks.ChargingStation.getTranslationKey());
	}
}
