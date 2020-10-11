package theking530.staticcore.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public interface IItemMultiCapability extends INBTSerializable<CompoundNBT> {
	public String getName();

	public ItemStackMultiCapabilityProvider getOwningProvider();

	public void setOwningProvider(ItemStackMultiCapabilityProvider owningProvider);

	public Capability<?>[] getCapabilityTypes();
}
