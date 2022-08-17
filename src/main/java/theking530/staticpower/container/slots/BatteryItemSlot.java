package theking530.staticpower.container.slots;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class BatteryItemSlot extends StaticPowerContainerSlot {
	public BatteryItemSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		this(itemHandler, new ItemStack(ModItems.BasicPortableBattery.get()), index, xPosition, yPosition);
	}

	public BatteryItemSlot(IItemHandler itemHandler, ItemStack preview, int index, int xPosition, int yPosition) {
		super(preview, 0.3f, itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return EnergyHandlerItemStackUtilities.isEnergyContainer(stack);
	}

}
