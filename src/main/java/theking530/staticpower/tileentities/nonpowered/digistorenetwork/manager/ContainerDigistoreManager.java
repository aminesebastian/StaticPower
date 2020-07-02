package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.initialization.ModContainerTypes;

public class ContainerDigistoreManager extends StaticPowerTileEntityContainer<TileEntityDigistoreManager> {

	public ContainerDigistoreManager(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityDigistoreManager) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistoreManager(int windowId, PlayerInventory playerInventory, TileEntityDigistoreManager owner) {
		super(ModContainerTypes.DIGISTORE_MANAGER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		if (!getTileEntity().getWorld().isRemote) {
			ServerCable cable = CableNetworkManager.get(getTileEntity().getWorld()).getCable(getTileEntity().getPos());
			DigistoreNetworkModule digistoreModule = cable.getNetwork().getModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
			addSlotsInGrid(digistoreModule, 0, 88, 24, 9, 16, (index, x, y) -> new StaticPowerContainerSlot(digistoreModule, index, x, y));
		} else {
			ItemStackHandler handler = new ItemStackHandler(13);
			addSlotsInGrid(handler, 0, 88, 24, 9, 16, (index, x, y) -> new StaticPowerContainerSlot(handler, index, x, y).setEnabledState(true));
		}

		addPlayerHotbar(getPlayerInventory(), 8, 126);
		addPlayerInventory(getPlayerInventory(), 8, 68);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		Slot slot = inventorySlots.get(slotIndex);
		return slot.getStack();
	}
}
