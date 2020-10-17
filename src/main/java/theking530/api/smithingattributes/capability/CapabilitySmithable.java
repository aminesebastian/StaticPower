package theking530.api.smithingattributes.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilitySmithable {
	@CapabilityInject(ISmithable.class)
	public static Capability<ISmithable> SMITHABLE_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(ISmithable.class, new Capability.IStorage<ISmithable>() {
			@Override
			public INBT writeNBT(Capability<ISmithable> capability, ISmithable instance, Direction side) {
				if (!(instance instanceof SmithableHandler)) {
					throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
				}
				return ((SmithableHandler) instance).serializeNBT();
			}

			@Override
			public void readNBT(Capability<ISmithable> capability, ISmithable instance, Direction side, INBT base) {
				if (!(instance instanceof SmithableHandler)) {
					throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
				}
				((SmithableHandler) instance).deserializeNBT((CompoundNBT) base);
			}
		}, () -> new SmithableHandler("default"));
	}
}
