package theking530.staticpower.tileentities.powered.lumbermill;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.FluidContainerSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.client.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModContainerTypes;
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
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().mainOutputInventory, 0, 90, 32));
		this.addSlot(new OutputSlot(getTileEntity().secondaryOutputInventory, 0, 120, 32));

		// FluidContainerDrainSlots
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.BUCKET, 0, -24, 11));
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.WATER_BUCKET, 1, -24, 43));

		// Upgrades
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, -24, 76));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, -24, 94));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, -24, 112));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		if (!mergeItemStack(stack, 0, 1, false)) {
			return true;
		}
		if (EnergyHandlerItemStackUtilities.isEnergyContainer(stack) && !mergeItemStack(stack, 1, 2, false)) {
			return true;
		}
		return false;
	}
}
