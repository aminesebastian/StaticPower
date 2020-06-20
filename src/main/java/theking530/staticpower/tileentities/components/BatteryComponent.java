package theking530.staticpower.tileentities.components;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.energy.StaticPowerFEStorage;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class BatteryComponent extends AbstractTileEntityComponent {

	private IItemHandler BatterySlotHandler;
	private int BatterySlot;
	private StaticPowerFEStorage EnergyStorage;

	public BatteryComponent(String name, IItemHandler batterySlotHandler, int batterySlot, StaticPowerFEStorage energyStorage) {
		super(name);
		BatterySlotHandler = batterySlotHandler;
		BatterySlot = batterySlot;
		EnergyStorage = energyStorage;
	}

	@Override
	public void preProcessUpdate() {
		if (BatterySlot < BatterySlotHandler.getSlots()) {
			if (EnergyStorage.getEnergyStored() < EnergyStorage.getMaxEnergyStored()) {
				ItemStack candidate = BatterySlotHandler.getStackInSlot(BatterySlot);
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
