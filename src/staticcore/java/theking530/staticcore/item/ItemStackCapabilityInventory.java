package theking530.staticcore.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackCapabilityInventory extends ItemStackHandler implements ISPItemCapabilityProvider {
	protected static final String ITEM_INVENTORY_TAG = "static_power_inventory";
	protected final ItemStack container;
	protected final String name;
	protected ItemStackMultiCapabilityProvider owningProvider;

	public ItemStackCapabilityInventory(String name, ItemStack container, int size) {
		super(size);
		this.container = container;
		this.name = name;

		if (container.hasTag() && container.getTag().contains(getName())) {
			deserializeNBT(container.getTag().getCompound(getName()));
		}
	}

	@Override
	public String getName() {
		return name + "_" + ITEM_INVENTORY_TAG;
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}
}
