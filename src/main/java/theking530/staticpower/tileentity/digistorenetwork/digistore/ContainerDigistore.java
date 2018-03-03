package theking530.staticpower.tileentity.digistorenetwork.digistore;

import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerDigistore extends BaseContainer {
	
	private TileEntityDigistore barrel;

	public ContainerDigistore(InventoryPlayer invPlayer, TileEntityDigistore teBarrel) {
		barrel = teBarrel;

		addSlotToContainer(new UpgradeSlot(barrel.slotsUpgrades, 0, 152, 9));
		addSlotToContainer(new UpgradeSlot(barrel.slotsUpgrades, 1, 152, 27));
		addSlotToContainer(new UpgradeSlot(barrel.slotsUpgrades, 2, 152, 45));
		
		addPlayerHotbar(invPlayer, 8, 126);
		addPlayerInventory(invPlayer, 8, 68);

	}
}
	

