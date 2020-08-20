package theking530.staticpower.tileentities.components.items;

import net.minecraft.item.ItemStack;
import theking530.staticpower.energy.StaticPowerFEStorage;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;

public class BatteryInventoryComponent extends InventoryComponent {

	private StaticPowerFEStorage EnergyStorage;

	public BatteryInventoryComponent(String name, StaticPowerFEStorage energyStorage) {
		super(name, 1);
		EnergyStorage = energyStorage;
		setShiftClickEnabled(true);
		setShiftClickPriority(-1);
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
			if (EnergyStorage.getEnergyStored() < EnergyStorage.getMaxEnergyStored()) {
				ItemStack candidate = getStackInSlot(0);
				if (candidate != null) {
					if (EnergyHandlerItemStackUtilities.isEnergyContainer(candidate)) {
						int maxInput = EnergyStorage.getCurrentMaximumPowerInput();
						int recieved = EnergyHandlerItemStackUtilities.useEnergyFromItemstack(candidate, maxInput, false);
						EnergyStorage.receiveEnergy(recieved, false);
					}
				}
			}
		}
	}
}
