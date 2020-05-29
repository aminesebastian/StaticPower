package theking530.staticpower.tileentity.tileentitycomponents;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.energy.StaticEnergyStorage;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class BatteryInteractionComponent implements ITileEntityComponent {

	private String NAME;
	private boolean isEnabled;

	private ItemStackHandler BATTERY_SLOT_HANDLER;
	private int BATTERY_SLOT;
	private StaticEnergyStorage ENERGY_STORAGE;

	public BatteryInteractionComponent(String componentName, ItemStackHandler batterySlotHandler, int batterySlot, StaticEnergyStorage energyHandler) {
		NAME = componentName;
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

	@Override
	public String getComponentName() {
		return NAME;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
}
