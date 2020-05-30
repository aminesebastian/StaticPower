package theking530.staticpower.tileentities.nonpowered.vacuumchest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.api.container.StaticPowerContainerSlot;
import theking530.staticpower.client.container.FilterSlot;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.UpgradeSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.upgrades.BaseUpgrade;

public class ContainerVacuumChest extends StaticPowerTileEntityContainer<TileEntityVacuumChest> {

	public ContainerVacuumChest(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityVacuumChest) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerVacuumChest(int windowId, PlayerInventory playerInventory, TileEntityVacuumChest owner) {
		super(ModContainerTypes.VACUUM_CHEST_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, x + y * 9, 8 + x * 18, 20 + y * 18));
			}
		}
		this.addSlot(new FilterSlot(getTileEntity().filterSlot, 0, 8, 78));

		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventoryComponent, 0, 116, 78));
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventoryComponent, 1, 134, 78));
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventoryComponent, 2, 152, 78));

		this.addPlayerInventory(getPlayerInventory(), 8, 103);
		this.addPlayerHotbar(getPlayerInventory(), 8, 161);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
		if (stack.getItem() instanceof ItemFilter && !mergeItemStack(stack, 27)) {
			return true;
		}
		if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 28, 31, false)) {
			return true;
		}
		if (!mergeItemStack(stack, 0, 27, false)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
}
