package theking530.api.energy;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.staticcore.item.IItemMultiCapability;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;

public class ItemStackStaticPowerEnergyCapability extends StaticPowerStorage implements IItemMultiCapability {
	protected static final String ITEM_ENERGY_TAG = "static_power_energy";
	protected final String name;
	protected final ItemStack container;
	protected ItemStackMultiCapabilityProvider owningProvider;

	public ItemStackStaticPowerEnergyCapability(String name, ItemStack container, double capacity, double inputVoltage, double maximumInputCurrent) {
		this(name, container, capacity, inputVoltage, inputVoltage, maximumInputCurrent);
	}

	public ItemStackStaticPowerEnergyCapability(String name, ItemStack container, double capacity, double minimumInput, double maximumInput, double maximumInputCurrent) {
		this(name, container, capacity, new StaticVoltageRange(minimumInput, maximumInput), maximumInputCurrent, 0, 0);
	}

	public ItemStackStaticPowerEnergyCapability(String name, ItemStack container, double capacity, double minimumInput, double maximumInput, double maximumInputCurrent,
			double voltageOutput, double maxOutputCurrent) {
		this(name, container, capacity, new StaticVoltageRange(minimumInput, maximumInput), maximumInputCurrent, voltageOutput, maxOutputCurrent);
	}

	public ItemStackStaticPowerEnergyCapability(String name, ItemStack container, double capacity, StaticVoltageRange inpuptRange, double maximumInputCurrent) {
		this(name, container, capacity, inpuptRange, maximumInputCurrent, 0, 0);
	}

	public ItemStackStaticPowerEnergyCapability(String name, ItemStack container, double capacity, StaticVoltageRange inputRange, double maximumInputCurrent, double voltageOutput,
			double maxOutputCurrent) {
		super(capacity, inputRange, maximumInputCurrent, voltageOutput, maxOutputCurrent);
		this.name = name;
		this.container = container;

		if (container.hasTag() && container.getTag().contains(getName())) {
			deserializeNBT(container.getTag().getCompound(getName()));
		}
	}

	public void setStoredPower(double power) {
		this.storedPower = Math.min(power, capacity);
	}

	@Override
	public String getName() {
		return name + "_" + ITEM_ENERGY_TAG;
	}

	@Override
	public Capability<?>[] getCapabilityTypes() {
		return new Capability<?>[] { CapabilityStaticPower.STATIC_VOLT_CAPABILITY };
	}

	@Override
	public ItemStackMultiCapabilityProvider getOwningProvider() {
		return owningProvider;
	}

	@Override
	public void setOwningProvider(ItemStackMultiCapabilityProvider owningProvider) {
		this.owningProvider = owningProvider;
	}
}
