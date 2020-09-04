package theking530.staticpower.tileentities.powered.chargingstation;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.PlayerArmorItemSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ContainerChargingStation extends StaticPowerTileEntityContainer<TileEntityChargingStation> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerChargingStation, GuiChargingStation> TYPE = new ContainerTypeAllocator<>("machine_charging_station",
			ContainerChargingStation::new, GuiChargingStation::new);

	public ContainerChargingStation(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityChargingStation) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerChargingStation(int windowId, PlayerInventory playerInventory, TileEntityChargingStation owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 0, 53, 24) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return EnergyHandlerItemStackUtilities.isEnergyContainer(itemStack);
			}
		});
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 1, 72, 24) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return EnergyHandlerItemStackUtilities.isEnergyContainer(itemStack);
			}
		});
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 2, 91, 24) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return EnergyHandlerItemStackUtilities.isEnergyContainer(itemStack);
			}
		});
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 3, 110, 24) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return EnergyHandlerItemStackUtilities.isEnergyContainer(itemStack);
			}
		});

		// Output
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 0, 48, 52));
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 1, 71, 52));
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 2, 94, 52));
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 3, 117, 52));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 54));

		// Armor
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 39, 152, 8, EquipmentSlotType.HEAD));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 38, 152, 26, EquipmentSlotType.CHEST));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 37, 152, 44, EquipmentSlotType.LEGS));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 36, 152, 62, EquipmentSlotType.FEET));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
