package theking530.staticpower.blockentities.components.items;

import net.minecraft.world.item.ItemStack;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;

public class BatteryInventoryComponent extends InventoryComponent {

	private IStaticPowerStorage powerStorage;

	public BatteryInventoryComponent(String name, IStaticPowerStorage powerStorage) {
		super(name, 1);
		this.powerStorage = powerStorage;
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

		if (getLevel().isClientSide()) {
			return;
		}

		ItemStack candidate = getStackInSlot(0);
		if (candidate == null || candidate.isEmpty() || !EnergyHandlerItemStackUtilities.isEnergyContainer(candidate)) {
			return;
		}

		double candidateVoltage = EnergyHandlerItemStackUtilities.getVoltageOutput(candidate);
		if (powerStorage.getInputVoltageRange().isVoltageInRange(candidateVoltage)) {
			double requiredPower = powerStorage.addPower(new PowerStack(candidateVoltage, Double.MAX_VALUE, CurrentType.DIRECT), true);
			PowerStack maxPowerToSupply = EnergyHandlerItemStackUtilities.usePower(candidate, requiredPower, false);
			powerStorage.addPower(maxPowerToSupply, false);
		}
	}
}
