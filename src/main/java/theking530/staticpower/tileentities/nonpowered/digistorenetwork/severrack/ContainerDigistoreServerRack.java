package theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.items.upgrades.BaseUpgrade;

public class ContainerDigistoreServerRack extends StaticPowerTileEntityContainer<TileEntityDigistoreServerRack> {

	public ContainerDigistoreServerRack(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityDigistoreServerRack) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistoreServerRack(int windowId, PlayerInventory playerInventory, TileEntityDigistoreServerRack owner) {
		super(ModContainerTypes.DIGISTORE_SERVER_RACK, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().cardInventory, 0, 65, 20));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().cardInventory, 1, 65, 40));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().cardInventory, 2, 65, 60));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().cardInventory, 3, 65, 80));
		
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().cardInventory, 4, 93, 20));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().cardInventory, 5, 93, 40));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().cardInventory, 6, 93, 60));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().cardInventory, 7, 93, 80));
		
		addPlayerHotbar(getPlayerInventory(), 8, 161);
		addPlayerInventory(getPlayerInventory(), 8, 103);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
		if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 0, 3, false)) {
			return true;
		}
		return false;
	}
}
