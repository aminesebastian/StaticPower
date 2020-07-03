package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.DigistoreSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.initialization.ModContainerTypes;

public class ContainerDigistoreManager extends StaticPowerTileEntityContainer<TileEntityDigistoreManager> {
	private DigistoreSimulatedItemStackHandler clientSimulatedInventory;

	public ContainerDigistoreManager(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityDigistoreManager) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistoreManager(int windowId, PlayerInventory playerInventory, TileEntityDigistoreManager owner) {
		super(ModContainerTypes.DIGISTORE_MANAGER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		if (!getTileEntity().getWorld().isRemote) {
			//ServerCable cable = CableNetworkManager.get(getTileEntity().getWorld()).getCable(getTileEntity().getPos());
			//DigistoreNetworkModule digistoreModule = cable.getNetwork().getModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
			//addSlotsInGrid(digistoreModule, 0, 88, 24, 9, 16, (index, x, y) -> new DigistoreSlot(digistoreModule, index, x, y));
		}

		addPlayerHotbar(getPlayerInventory(), 8, 226);
		addPlayerInventory(getPlayerInventory(), 8, 168);
	}

	@OnlyIn(Dist.CLIENT)
	public void setAll(List<ItemStack> items) {
		this.inventorySlots.clear();
		int digistoreItems = items.size() - 36;
		clientSimulatedInventory = new DigistoreSimulatedItemStackHandler(digistoreItems);
		addSlotsInGrid(clientSimulatedInventory, 0, 88, 24, 9, 16, (index, x, y) -> new DigistoreSlot(clientSimulatedInventory, index, x, y).setEnabledState(true));
		addPlayerHotbar(getPlayerInventory(), 8, 226);
		addPlayerInventory(getPlayerInventory(), 8, 168);

		super.setAll(items);
		
		for(int i=0; i<digistoreItems; i++) {
			StaticPowerContainerSlot spSlot = (StaticPowerContainerSlot)inventorySlots.get(i);
			spSlot.setEnabledState(spSlot.getHasStack());
		}
	}
	@Override
	public void putStackInSlot(int slotID, ItemStack stack) {
		
	}
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		Slot slot = inventorySlots.get(slotIndex);
		return slot.getStack();
	}
}
