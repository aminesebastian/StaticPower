package theking530.staticpower.tileentities.powered.chargingstation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.TileEntityBatteryComponent;
import theking530.staticpower.tileentities.components.TileEntityInputServoComponent;
import theking530.staticpower.tileentities.components.TileEntityInventoryComponent;
import theking530.staticpower.tileentities.components.TileEntityOutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityChargingStation extends TileEntityMachine {
	public final TileEntityInventoryComponent unchargedInventory;
	public final TileEntityInventoryComponent chargedInventory;
	public final TileEntityInventoryComponent batterySlot;

	public TileEntityChargingStation() {
		super(ModTileEntityTypes.CHARGING_STATION, 3);
		initializeBasicMachine(2, 0, 100000, 500, 2);
		energyStorage.setMaxExtract(512);

		registerComponent(unchargedInventory = new TileEntityInventoryComponent("unchargedInventory", 4, MachineSideMode.Input));
		registerComponent(chargedInventory = new TileEntityInventoryComponent("chargedInventory", 4, MachineSideMode.Output));
		registerComponent(batterySlot = new TileEntityInventoryComponent("batterySlot", 1, MachineSideMode.Never));

		registerComponent(new TileEntityBatteryComponent("BatteryComponent", batterySlot, 0, energyStorage));
		registerComponent(new TileEntityOutputServoComponent(this, 1, chargedInventory, 0, 1, 2, 3));
		registerComponent(new TileEntityInputServoComponent(this, 2, unchargedInventory, 0, 1, 2, 3));
	}

	@Override
	public void process() {
		if (energyStorage.getEnergyStored() > 0) {
			for (int i = 0; i < unchargedInventory.getSlots(); i++) {
				ItemStack stack = unchargedInventory.getStackInSlot(i);
				if (stack != ItemStack.EMPTY && EnergyHandlerItemStackUtilities.isEnergyContainer(stack)) {
					if (EnergyHandlerItemStackUtilities.getEnergyStored(stack) < EnergyHandlerItemStackUtilities.getEnergyStorageCapacity(stack)) {
						int maxOutput = this.getEnergyStorage().getCurrentMaximumPowerOutput();
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
	 * unchargedInventory into any open slot in the chargedInventory.
	 * 
	 * @param fromSlot The slot that contains the charged {@link ItemStack}.
	 */
	public void moveChargedItemToOutputs(int fromSlot) {
		// Iterate through each of the output slots.
		for (int i = 0; i < 4; i++) {
			// If we can place the charged item into an output slot, do so. There's no need
			// to check the result of the call to insertItem as we already know the result
			// is going to be empty.
			if (InventoryUtilities.canFullyInsertStackIntoSlot(chargedInventory, i, unchargedInventory.getStackInSlot(fromSlot))) {
				System.out.println(unchargedInventory.getStackInSlot(fromSlot));
				ItemStack stack = unchargedInventory.extractItem(fromSlot, 1, false);
				chargedInventory.insertItem(i, stack, false);
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
