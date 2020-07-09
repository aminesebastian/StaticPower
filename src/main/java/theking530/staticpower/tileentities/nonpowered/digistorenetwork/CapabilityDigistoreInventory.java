package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityDigistoreInventory implements Capability.IStorage<IDigistoreInventory> {
	@CapabilityInject(IDigistoreInventory.class)
	public static Capability<IDigistoreInventory> DIGISTORE_INVENTORY_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IDigistoreInventory.class, new DefaultDigistoreInventoryStorage<>(), () -> new DigistoreInventory(0, 0));
	}

	@Override
	public INBT writeNBT(Capability<IDigistoreInventory> capability, IDigistoreInventory instance, Direction side) {
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<IDigistoreInventory> capability, IDigistoreInventory instance, Direction side, INBT nbt) {
		instance.deserializeNBT((CompoundNBT) nbt);
	}

	private static class DefaultDigistoreInventoryStorage<T extends IDigistoreInventory> implements Capability.IStorage<T> {
		@Override
		public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
			if (!(instance instanceof DigistoreInventory)) {
				throw new RuntimeException("Cannot serialize to an instance that isn't the default implementation");
			}
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
			if (!(instance instanceof DigistoreInventory)) {
				throw new RuntimeException("Cannot deserialize to an instance that isn't the default implementation");
			}
			instance.deserializeNBT((CompoundNBT) nbt);
		}
	}
}
