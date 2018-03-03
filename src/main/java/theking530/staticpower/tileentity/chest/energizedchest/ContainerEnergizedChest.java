package theking530.staticpower.tileentity.chest.energizedchest;

import net.minecraft.entity.player.InventoryPlayer;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;

public class ContainerEnergizedChest extends BaseContainer {
	
	public ContainerEnergizedChest(InventoryPlayer invPlayer, TileEntityEnergizedChest teEnergizedChest) {
		int yOff = 48;	
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new StaticPowerContainerSlot(teEnergizedChest.slotsOutput, x + y * 9, 8 +  (x * 18), (19 + (y * 18))));
			}
		}
		this.addPlayerInventory(invPlayer, 8, yOff+123);
		this.addPlayerHotbar(invPlayer, 8, yOff+181);
	}
}
	

