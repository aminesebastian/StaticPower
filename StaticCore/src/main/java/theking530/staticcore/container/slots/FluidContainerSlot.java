package theking530.staticcore.container.slots;

import javax.annotation.Nonnull;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class FluidContainerSlot extends StaticPowerContainerSlot {

	public FluidContainerSlot(IItemHandler itemHandler, Item item, int index, int xPosition, int yPosition) {
		super(new ItemStack(item), 0.3f, itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).isPresent();
	}
}
