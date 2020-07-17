package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityDigistoreInventory {
	@CapabilityInject(IDigistoreInventory.class)
	public static Capability<IDigistoreInventory> DIGISTORE_INVENTORY_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IDigistoreInventory.class, new Capability.IStorage<IDigistoreInventory>() {
			@Override
			public INBT writeNBT(Capability<IDigistoreInventory> capability, IDigistoreInventory instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<IDigistoreInventory> capability, IDigistoreInventory instance, Direction side, INBT base) {
				instance.deserializeNBT((CompoundNBT) base);
			}
		}, () -> new DigistoreInventory(0, 0));
	}
}
