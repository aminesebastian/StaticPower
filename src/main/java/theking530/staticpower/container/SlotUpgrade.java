package theking530.staticpower.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.tileentity.IUpgradeable;

public class SlotUpgrade extends SlotItemHandler {
	
	private IUpgradeable upgradeable;
	
	public SlotUpgrade(IUpgradeable upgradeable, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.upgradeable = upgradeable;
	}
	@Override
    public boolean isItemValid(ItemStack itemStack) {
		return !itemStack.isEmpty() && upgradeable.canAcceptUpgrade(itemStack);	         
    }
}
