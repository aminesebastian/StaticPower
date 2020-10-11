package theking530.api.power;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticcore.item.IItemMultiCapability;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;

public class ItemStackStaticVoltCapability extends StaticVoltHandler implements IItemMultiCapability, IEnergyStorage {
	protected static final String ITEM_ENERGY_TAG = "static_power_energy";
	protected final String name;
	protected final ItemStack container;
	protected final StaticVoltHandler handler;
	protected final PowerEnergyInterface energyInterface;
	protected ItemStackMultiCapabilityProvider owningProvider;

	public ItemStackStaticVoltCapability(String name, ItemStack container, int capacity, int maxInput, int maxOutput) {
		super(capacity, maxInput, maxOutput);
		this.name = name;
		this.container = container;
		this.handler = new StaticVoltHandler(capacity, maxInput, maxOutput);
		this.energyInterface = new PowerEnergyInterface((StaticVoltHandler) this);

		if (container.hasTag() && container.getTag().contains(getName())) {
			deserializeNBT(container.getTag().getCompound(getName()));
		}
	}

	@Override
	public String getName() {
		return name + "_" + ITEM_ENERGY_TAG;
	}

	@Override
	public Capability<?>[] getCapabilityTypes() {
		return new Capability<?>[] { CapabilityStaticVolt.STATIC_VOLT_CAPABILITY, CapabilityEnergy.ENERGY };
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return energyInterface.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return energyInterface.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return energyInterface.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return energyInterface.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return energyInterface.canExtract();
	}

	@Override
	public boolean canReceive() {
		return energyInterface.canReceive();
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
