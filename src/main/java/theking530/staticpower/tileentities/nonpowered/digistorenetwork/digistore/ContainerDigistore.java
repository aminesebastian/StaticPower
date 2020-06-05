package theking530.staticpower.tileentity.digistorenetwork.digistore;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerDigistore extends BaseContainer {
	
	private TileEntityDigistore barrel;

	public ContainerDigistore(PlayerInventory invPlayer, TileEntityDigistore teBarrel) {
		barrel = teBarrel;

		addSlot(new UpgradeSlot(barrel.slotsUpgrades, 0, 152, 9));
		addSlot(new UpgradeSlot(barrel.slotsUpgrades, 1, 152, 27));
		addSlot(new UpgradeSlot(barrel.slotsUpgrades, 2, 152, 45));
		
		addPlayerHotbar(invPlayer, 8, 126);
		addPlayerInventory(invPlayer, 8, 68);

	}
	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
        if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 0, 3, false)) {
        	return true;
        }
		return false;	
	}
}
	

