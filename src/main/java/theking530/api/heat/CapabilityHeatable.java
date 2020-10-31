package theking530.api.heat;

import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityHeatable {
	@CapabilityInject(IHeatStorage.class)
	public static Capability<IHeatStorage> HEAT_STORAGE_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IHeatStorage.class, new Capability.IStorage<IHeatStorage>() {
			@Override
			public INBT writeNBT(Capability<IHeatStorage> capability, IHeatStorage instance, Direction side) {
				return DoubleNBT.valueOf(instance.getCurrentHeat());
			}

			@Override
			public void readNBT(Capability<IHeatStorage> capability, IHeatStorage instance, Direction side, INBT base) {
				if (!(instance instanceof HeatStorage))
					throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
				((HeatStorage) instance).currentHeat = ((FloatNBT) base).getInt();
			}
		}, () -> new HeatStorage(0, 0));
	}
}
