package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.items.upgrades.BaseUpgrade;

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
		if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 0, 3, false)) {
			return true;
		}
		return false;
	}
}
