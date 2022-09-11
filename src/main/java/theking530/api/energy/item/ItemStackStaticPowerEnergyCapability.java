package theking530.api.energy.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.StaticPowerStorage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.item.IItemMultiCapability;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;

public class ItemStackStaticPowerEnergyCapability extends StaticPowerStorage implements IItemMultiCapability {
	protected static final String ITEM_ENERGY_TAG = "static_power_energy";
	protected final String name;
	protected final ItemStack container;
	protected ItemStackMultiCapabilityProvider owningProvider;

	public ItemStackStaticPowerEnergyCapability(String name, ItemStack container, double capacity, StaticVoltageRange inputRange, double maximumInputCurrent, double voltageOutput,
			double maxOutputCurrent) {
		this(name, container, capacity, inputRange, maximumInputCurrent, new CurrentType[] { CurrentType.DIRECT }, voltageOutput, maxOutputCurrent, CurrentType.DIRECT);
	}

	public ItemStackStaticPowerEnergyCapability(String name, ItemStack container, double capacity, StaticVoltageRange inputVoltageRange, double maxInputCurrent,
			CurrentType[] acceptableCurrentTypes, double outputVoltage, double maxOutputCurrent, CurrentType outputCurrentType) {
		super(capacity, inputVoltageRange, maxInputCurrent, acceptableCurrentTypes, outputVoltage, maxOutputCurrent, outputCurrentType);
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
