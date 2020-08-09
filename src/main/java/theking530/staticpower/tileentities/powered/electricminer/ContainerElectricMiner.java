package theking530.staticpower.tileentities.powered.electricminer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ContainerElectricMiner extends StaticPowerTileEntityContainer<TileEntityElectricMiner> {

	public ContainerElectricMiner(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityElectricMiner) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerElectricMiner(int windowId, PlayerInventory playerInventory, TileEntityElectricMiner owner) {
		super(ModContainerTypes.ELECTRIC_MINER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Drill Bit Slot
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.IronDrillBit), 0.3f, getTileEntity().drillBitInventory, 0, 142, 32));
		
		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));
		
		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 32));

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
