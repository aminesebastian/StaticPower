package theking530.staticpower.tileentities.powered.poweredfurnace;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatterySlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.client.container.slots.UpgradeSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ContainerPoweredFurnace extends StaticPowerTileEntityContainer<TileEntityPoweredFurnace> {

	public ContainerPoweredFurnace(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityPoweredFurnace) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPoweredFurnace(int windowId, PlayerInventory playerInventory, TileEntityPoweredFurnace owner) {
		super(ModContainerTypes.MACHINE_POWERED_FURNACE_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory.getInventory(), 0, 50, 28) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return getTileEntity().getRecipe(itemStack).isPresent();
			}
		});

		// Battery
		this.addSlot(new BatterySlot(getTileEntity().batteryInventory.getInventory(), 0, 8, 65));

		// Output
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().outputInventory.getInventory(), 0, 109, 32));

		// Upgrades
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventory.getInventory(), 0, 152, 12));
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventory.getInventory(), 1, 152, 32));
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventory.getInventory(), 2, 152, 52));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
		if (getTileEntity().getRecipe(stack).isPresent() && !mergeItemStack(stack, 0)) {
			return true;
		}
		if (EnergyHandlerItemStackUtilities.isEnergyContainer(stack) && !mergeItemStack(stack, 1)) {
			return true;
		}
		if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 3, 6, false)) {
			return true;
		}
		return false;
	}
}
