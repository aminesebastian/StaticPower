package theking530.staticcore.item;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ItemStackMultiCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
	protected final Set<ISPItemCapabilityProvider> flatCapabilityList;
	protected final ItemStack stack;
	protected final CompoundTag nbt;

	public ItemStackMultiCapabilityProvider(ItemStack stack, CompoundTag nbt) {
		this.stack = stack;
		this.nbt = nbt;
		flatCapabilityList = new HashSet<ISPItemCapabilityProvider>();
	}

	/**
	 * Adds the instance in for the provided side for this capability. It will not
	 * override any existing sides.
	 * 
	 * @param instance The instance to add.
	 * @return
	 */
	public ItemStackMultiCapabilityProvider addCapability(ISPItemCapabilityProvider instance) {
		// Do nothing if the instance or capability type are null.
		if (instance == null) {
			return this;
		}

		// Add the capability to the flat set.
		flatCapabilityList.add(instance);

		return this;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		for (ISPItemCapabilityProvider cap : flatCapabilityList) {
			output.put(cap.getName(), cap.serializeNBT());
		}
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		for (ISPItemCapabilityProvider cap : flatCapabilityList) {
			cap.deserializeNBT(nbt.getCompound(cap.getName()));
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap != null) {
			for (ISPItemCapabilityProvider provider : flatCapabilityList) {
				LazyOptional<T> testing = provider.getCapability(cap, side);
				if (!testing.isPresent()) {
					continue;
				}

				return testing;
			}
		}
		return LazyOptional.empty();
	}
}
