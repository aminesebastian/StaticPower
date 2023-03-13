package theking530.staticcore.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ItemStackMultiCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
	protected final HashMap<Capability<?>, HashMap<Direction, IItemMultiCapability>> capabilityMap;
	protected final Set<IItemMultiCapability> flatCapabilityList;
	protected final ItemStack stack;
	protected final CompoundTag nbt;

	public ItemStackMultiCapabilityProvider(ItemStack stack, CompoundTag nbt) {
		this.stack = stack;
		this.nbt = nbt;
		capabilityMap = new HashMap<Capability<?>, HashMap<Direction, IItemMultiCapability>>();
		flatCapabilityList = new HashSet<IItemMultiCapability>();
	}

	/**
	 * Adds the instance in for all sides for this capability including null. This
	 * will NOT override any existing sides or null if they have already been used.
	 * Meaning if you add two capabilities, one for UP and one for EAST, calling
	 * this will populate WEST, DOWN, NORTH, SOUTH and NULL.
	 * 
	 * @param instance The instance to add.
	 * @return
	 */
	public ItemStackMultiCapabilityProvider addCapability(IItemMultiCapability instance) {
		return addCapability(instance, Direction.values());
	}

	/**
	 * Adds the instance in for the provided side for this capability. It will not
	 * override any existing sides.
	 * 
	 * @param instance The instance to add.
	 * @return
	 */
	public ItemStackMultiCapabilityProvider addCapability(IItemMultiCapability instance, Direction... sides) {
		// Do nothing if the instance or capability type are null.
		if (instance == null || instance.getCapabilityTypes().length == 0) {
			return this;
		}

		// Set the owning provider.
		instance.setOwningProvider(this);

		// Register this instance for all the capabilities it exposes.
		for (Capability<?> capability : instance.getCapabilityTypes()) {
			if (!capabilityMap.containsKey(capability)) {
				capabilityMap.put(capability, new HashMap<Direction, IItemMultiCapability>());
			}

			// Add the capability to the flat set.
			flatCapabilityList.add(instance);

			// If there were sides provided, add an entry for each side.
			// Otherwise, add an entry for a null side.
			if (sides.length > 0) {
				for (Direction side : sides) {
					if (!capabilityMap.get(capability).containsKey(side)) {
						capabilityMap.get(capability).put(side, instance);
					}
				}
			}

			// Add null if its not used.
			if (!capabilityMap.get(capability).containsKey(null)) {
				capabilityMap.get(capability).put(null, instance);
			}

		}

		// Return an instance of this for daisy chaining.
		return this;
	}

	@Override
	public CompoundTag serializeNBT() {
		// Create the output nbt.
		CompoundTag output = new CompoundTag();

		// Serialize all the capabilities.
		for (IItemMultiCapability cap : flatCapabilityList) {
			output.put(cap.getName(), cap.serializeNBT());
		}

		// Return the output.
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// De-serialize all the capabilities.
		for (IItemMultiCapability cap : flatCapabilityList) {
			cap.deserializeNBT(nbt.getCompound(cap.getName()));
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		// Check if we have the capability.
		if (cap != null && capabilityMap.containsKey(cap)) {
			// Get the sub map for that capability type.
			HashMap<Direction, IItemMultiCapability> multiMap = capabilityMap.get(cap);

			// If the map contains an entry for that side, return it.
			if (multiMap.containsKey(side)) {
				return LazyOptional.of(() -> multiMap.get(side)).cast();
			}
		}

		// Return the lazy optional.
		return LazyOptional.empty();
	}
}
