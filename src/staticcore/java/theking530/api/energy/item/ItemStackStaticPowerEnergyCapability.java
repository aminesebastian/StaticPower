package theking530.api.energy.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.StaticPowerStorage;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.item.ISPItemCapabilityProvider;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;

public class ItemStackStaticPowerEnergyCapability extends StaticPowerStorage implements ISPItemCapabilityProvider {
	protected static final String ITEM_ENERGY_TAG = "static_power_energy";
	protected final String name;
	protected final ItemStack container;
	protected ItemStackMultiCapabilityProvider owningProvider;

	public ItemStackStaticPowerEnergyCapability(String name, ItemStack container, double capacity, StaticVoltageRange inputRange, double maximumInputPower,
			StaticPowerVoltage voltageOutput, double maximumOutputPower, boolean canAcceptExternalPower, boolean canOutputExternalPower) {
		this(name, container, capacity, inputRange, maximumInputPower, new CurrentType[] { CurrentType.DIRECT }, voltageOutput, maximumOutputPower, CurrentType.DIRECT,
				canAcceptExternalPower, canOutputExternalPower);
	}

	public ItemStackStaticPowerEnergyCapability(String name, ItemStack container, double capacity, StaticVoltageRange inputVoltageRange, double maximumInputPower,
			CurrentType[] acceptableCurrentTypes, StaticPowerVoltage outputVoltage, double maximumOutputPower, CurrentType outputCurrentType,
			boolean canAcceptExternalPower, boolean canOutputExternalPower) {
		super(capacity, inputVoltageRange, maximumInputPower, acceptableCurrentTypes, outputVoltage, maximumOutputPower, outputCurrentType, canAcceptExternalPower,
				canOutputExternalPower, false);
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
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityStaticPower.STATIC_VOLT_CAPABILITY) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}
}
