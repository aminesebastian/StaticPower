package theking530.staticpower.tileentities.digistorenetwork.digistore;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.init.ModItems;

public class ContainerDigistore extends StaticPowerTileEntityContainer<TileEntityDigistore> {

	public ContainerDigistore(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityDigistore) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistore(int windowId, PlayerInventory playerInventory, TileEntityDigistore owner) {
		super(ModContainerTypes.DIGISTORE_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.BasicSingularDigistoreCard), 0.3f, getTileEntity().inventory, 0, 80, 45));

		addPlayerHotbar(getPlayerInventory(), 8, 126);
		addPlayerInventory(getPlayerInventory(), 8, 68);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		// Try to insert into inventory.
		ItemStack remaining = getTileEntity().insertItem(0, stack.copy(), false);

		// If any amount was inserted, modify the stack and return true. Otherwise,
		// resort to default behaviour.
		if (remaining.getCount() != remaining.getCount()) {
			stack.setCount(remaining.getCount());
			return true;
		} else {
			return super.playerItemShiftClicked(stack, player, slot, slotIndex);
		}
	}
}
