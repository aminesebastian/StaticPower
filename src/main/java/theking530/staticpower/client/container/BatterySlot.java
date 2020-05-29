package theking530.staticpower.client.container;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.api.container.StaticPowerContainerSlot;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class BatterySlot extends StaticPowerContainerSlot {

	public BatterySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(new ItemStack(ModItems.BasicPortableBattery), itemHandler, index, xPosition, yPosition);
	}
    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
		return EnergyHandlerItemStackUtilities.isEnergyContainer(stack);	          
    }

}
