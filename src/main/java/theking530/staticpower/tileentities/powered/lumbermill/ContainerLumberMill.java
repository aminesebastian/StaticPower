package theking530.staticpower.tileentities.powered.lumbermill;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatterySlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.client.container.slots.UpgradeSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ContainerLumberMill extends StaticPowerTileEntityContainer<TileEntityLumberMill> {

	public ContainerLumberMill(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityLumberMill) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerLumberMill(int windowId, PlayerInventory playerInventory, TileEntityLumberMill owner) {
		super(ModContainerTypes.LUMBER_MILL_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 36, 32));

		// Battery
		this.addSlot(new BatterySlot(getTileEntity().batteryInventory, 0, 8, 60));

		// FluidContainerSlots
//		this.addSlot(new FluidContainerSlot(teLumberMill.slotsInternal, 2, -24, 11));
//		this.addSlot(new OutputSlot(teLumberMill.slotsInternal, 3, -24, 43));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 90, 32));
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 1, 120, 32));

		// Upgrades
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventory, 0, -24, 76));
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventory, 1, -24, 94));
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventory, 2, -24, 112));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
		if (!mergeItemStack(stack, 0, 1, false)) {
			return true;
		}
		if (EnergyHandlerItemStackUtilities.isEnergyContainer(stack) && !mergeItemStack(stack, 1, 2, false)) {
			return true;
		}
		return false;
	}
}
