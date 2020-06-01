package theking530.staticpower.tileentities.components;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class BatteryComponent extends AbstractTileEntityComponent {

	private ItemStackHandler BATTERY_SLOT_HANDLER;
	private int BATTERY_SLOT;
	private EnergyStorageComponent ENERGY_STORAGE;

	public BatteryComponent(String name, ItemStackHandler batterySlotHandler, int batterySlot, EnergyStorageComponent energyHandler) {
		super(name);
		BATTERY_SLOT_HANDLER = batterySlotHandler;
		BATTERY_SLOT = batterySlot;
		ENERGY_STORAGE = energyHandler;
	}

	@Override
	public void preProcessUpdate() {
		if (BATTERY_SLOT < BATTERY_SLOT_HANDLER.getSlots()) {
			if (ENERGY_STORAGE.getEnergyStored() < ENERGY_STORAGE.getMaxEnergyStored()) {
				ItemStack candidate = BATTERY_SLOT_HANDLER.getStackInSlot(BATTERY_SLOT);
				if (candidate != null) {
					if (EnergyHandlerItemStackUtilities.isEnergyContainer(candidate)) {
						int maxInput = ENERGY_STORAGE.getCurrentMaximumPowerInput();
						int recieved = EnergyHandlerItemStackUtilities.useEnergyFromItemstack(candidate, maxInput, false);
						ENERGY_STORAGE.receiveEnergy(recieved, false);
					}
				}
			}
		}
	}
}
