package theking530.staticpower.tileentities.components.items;

import net.minecraft.world.item.ItemStack;
import theking530.api.power.IStaticVoltHandler;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class BatteryInventoryComponent extends InventoryComponent {

	private IStaticVoltHandler EnergyStorage;

	public BatteryInventoryComponent(String name, IStaticVoltHandler energyStorage) {
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

	@SuppressWarnings("resource")
	@Override
	public void preProcessUpdate() {
		if (!isEnabled()) {
			return;
		}

		if (!getLevel().isClientSide) {
			if (EnergyStorage.getStoredPower() < EnergyStorage.getCapacity()) {
				ItemStack candidate = getStackInSlot(0);
				if (candidate != null) {
					if (EnergyHandlerItemStackUtilities.isEnergyContainer(candidate)) {
						long maxInput = Math.min(EnergyStorage.getCapacity() - EnergyStorage.getStoredPower(), EnergyStorage.getMaxReceive());
						long recieved = EnergyHandlerItemStackUtilities.drainPower(candidate, maxInput, false);
						EnergyStorage.receivePower(recieved, false);
					}
				}
			}
		}
	}
}
