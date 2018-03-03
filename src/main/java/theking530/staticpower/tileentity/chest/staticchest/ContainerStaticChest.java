package theking530.staticpower.tileentity.chest.staticchest;

import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;

public class ContainerStaticChest extends BaseContainer {
	
	public ContainerStaticChest(InventoryPlayer invPlayer, TileEntityStaticChest teStaticChest) {
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new StaticPowerContainerSlot(teStaticChest.slotsOutput, x + y * 9, 8 + x * 18, 19 + y * 18));
			}
		}
		this.addPlayerInventory(invPlayer, 8, 123);
		this.addPlayerHotbar(invPlayer, 8, 181);
	}
}
	

