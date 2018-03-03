package theking530.staticpower.tileentity.chest.lumumchest;

import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;

public class ContainerLumumChest extends BaseContainer {
	
	public ContainerLumumChest(InventoryPlayer invPlayer, TileEntityLumumChest teStaticChest) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 12; x++) {
				this.addSlotToContainer(new StaticPowerContainerSlot(teStaticChest.slotsOutput, x + y * 12, 8 + (x * 18), (19 + (y * 18))));
			}
		}
		this.addPlayerInventory(invPlayer, 8, 172);
		this.addPlayerHotbar(invPlayer, 8, 230);
	}
}
	

