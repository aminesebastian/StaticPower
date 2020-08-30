package theking530.staticpower.tileentities.components.items;

import net.minecraft.item.ItemStack;
import theking530.api.power.StaticVoltHandler;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class BatteryInventoryComponent extends InventoryComponent {

	private StaticVoltHandler EnergyStorage;

	public BatteryInventoryComponent(String name, StaticVoltHandler energyStorage) {
		super(name, 1);
		EnergyStorage = energyStorage;
		setShiftClickEnabled(true);
		setShiftClickPriority(-1);
		setCapabilityExtractEnabled(false);
		setCapabilityInsertEnabled(false);
		this.setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return EnergyHandlerItemStackUtilities.isEnergyContainer(stack);
			}
		});
	}

	@Override
	public void preProcessUpdate() {
		if (!isEnabled()) {
			return;
		}

		if (!getWorld().isRemote) {
			if (EnergyStorage.getStoredPower() < EnergyStorage.getCapacity()) {
				ItemStack candidate = getStackInSlot(0);
				if (candidate != null) {
					if (EnergyHandlerItemStackUtilities.isEnergyContainer(candidate)) {
						int maxInput = EnergyStorage.getCurrentMaximumPowerInput();
						int recieved = EnergyHandlerItemStackUtilities.useEnergyFromItemstack(candidate, maxInput, false);
						EnergyStorage.receivePower(recieved, false);
					}
				}
			}
		}
	}
}
