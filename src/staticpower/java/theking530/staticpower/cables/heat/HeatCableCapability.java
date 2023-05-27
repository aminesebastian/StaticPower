package theking530.staticpower.cables.heat;

import net.minecraft.nbt.CompoundTag;
import theking530.api.heat.HeatStorage;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapability;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;

public class HeatCableCapability extends ServerCableCapability {
	private HeatStorage heatStorage;

	protected HeatCableCapability(ServerCableCapabilityType<?> type, Cable owningCable) {
		super(type, owningCable);
		heatStorage = new HeatStorage(0, 0, 0, 0, 0);
	}

	public void initialize(float mass, float specificHeat, float conductivity, float maximumTemperature) {
		heatStorage.setMass(mass);
		heatStorage.setSpecificHeat(specificHeat);
		heatStorage.setConductivity(conductivity);
		heatStorage.setMaximumHeat(maximumTemperature);
	}

	public IHeatStorage getStorage() {
		return heatStorage;
	}

	@Override
	public void preWorldTick() {
		heatStorage.getTicker().tick(getOwningCable().getLevel(), getPos(), true);
		heatStorage.setConductivity(80);
	}

	@Override
	public void save(CompoundTag tag) {
		tag.put("heat", heatStorage.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		heatStorage.deserializeNBT(tag.getCompound("heat"));
	}

	public static class HeatCableCapabilityType extends ServerCableCapabilityType<HeatCableCapability> {
		@Override
		public HeatCableCapability create(Cable owningCable) {
			return new HeatCableCapability(this, owningCable);
		}
	}
}
