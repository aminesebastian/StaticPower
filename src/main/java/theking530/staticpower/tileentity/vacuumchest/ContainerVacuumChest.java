package theking530.staticpower.tileentity.vacuumchest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.api.container.StaticPowerContainerSlot;
import theking530.staticpower.client.container.BaseTileEntityContainer;
import theking530.staticpower.client.container.FilterSlot;
import theking530.staticpower.client.container.UpgradeSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.items.upgrades.BaseUpgrade;

public class ContainerVacuumChest extends BaseTileEntityContainer<TileEntityVacuumChest> {

	public ContainerVacuumChest(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
		this(windowId, playerInventory, (TileEntityVacuumChest) getTileEntity(playerInventory, data));
	}

	public ContainerVacuumChest(final int windowId, PlayerInventory invPlayer, TileEntityVacuumChest teVChest) {
		super(windowId, ModContainerTypes.VACUUM_CHEST_CONTAINER, invPlayer, teVChest);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new StaticPowerContainerSlot(owningTileEntity.slotsOutput, x + y * 9, 8 + x * 18, 20 + y * 18));
			}
		}
		this.addSlot(new FilterSlot(owningTileEntity.slotsInternal, 0, 8, 78));

		this.addSlot(new UpgradeSlot(owningTileEntity.slotsUpgrades, 0, 116, 78));
		this.addSlot(new UpgradeSlot(owningTileEntity.slotsUpgrades, 1, 134, 78));
		this.addSlot(new UpgradeSlot(owningTileEntity.slotsUpgrades, 2, 152, 78));

		this.addPlayerInventory(invPlayer, 8, 103);
		this.addPlayerHotbar(invPlayer, 8, 161);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
//		if (stack.getItem() instanceof ItemFilter && !mergeItemStack(stack, 27)) {
//			return true;
//		}
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
