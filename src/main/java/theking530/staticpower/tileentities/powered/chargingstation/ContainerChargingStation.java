package theking530.staticpower.tileentities.powered.chargingstation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.PlayerArmorItemSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.client.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ContainerChargingStation extends StaticPowerTileEntityContainer<TileEntityChargingStation> {

	public ContainerChargingStation(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityChargingStation) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerChargingStation(int windowId, PlayerInventory playerInventory, TileEntityChargingStation owner) {
		super(ModContainerTypes.CHARGING_STATION_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 0, 51, 24) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return EnergyHandlerItemStackUtilities.isEnergyContainer(itemStack);
			}
		});
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 1, 70, 24) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return EnergyHandlerItemStackUtilities.isEnergyContainer(itemStack);
			}
		});
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 2, 89, 24) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return EnergyHandlerItemStackUtilities.isEnergyContainer(itemStack);
			}
		});
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 3, 108, 24) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return EnergyHandlerItemStackUtilities.isEnergyContainer(itemStack);
			}
		});

		// Output
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 0, 46, 52));
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 1, 69, 52));
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 2, 92, 52));
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 3, 115, 52));

		// Upgrades
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 8));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 30));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batterySlot, 0, 8, 54));

		// Armor
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 39, -19, 14, EquipmentSlotType.HEAD));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 38, -19, 33, EquipmentSlotType.CHEST));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 37, -19, 52, EquipmentSlotType.LEGS));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 36, -19, 71, EquipmentSlotType.FEET));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		if (EnergyHandlerItemStackUtilities.isEnergyContainer(stack) && !mergeItemStack(stack, 0, 4, false)) {
			return true;
		}
		if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 8, 11, false)) {
			return true;
		}
		return false;
	}
}
