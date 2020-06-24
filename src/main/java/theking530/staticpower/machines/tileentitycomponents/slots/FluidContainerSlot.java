package theking530.staticpower.machines.tileentitycomponents.slots;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;

public class FluidContainerSlot extends StaticPowerContainerSlot {

	public FluidContainerSlot(IItemHandler itemHandler, Item item, int index, int xPosition, int yPosition) {
		super(new ItemStack(item), 0.3f, itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).isPresent();
	}
}
