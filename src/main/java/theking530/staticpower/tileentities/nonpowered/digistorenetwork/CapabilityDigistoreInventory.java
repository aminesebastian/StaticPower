package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.IItemHandlerModifiable;

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
				if (!(instance instanceof IItemHandlerModifiable))
					throw new RuntimeException("IItemHandler instance does not implement IItemHandlerModifiable");
				instance.deserializeNBT((CompoundNBT) base);
			}
		}, () -> new DigistoreInventory(0, 0));
	}
}
