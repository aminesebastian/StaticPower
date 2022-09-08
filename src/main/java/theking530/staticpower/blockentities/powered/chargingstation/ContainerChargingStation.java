package theking530.staticpower.blockentities.powered.chargingstation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.PlayerArmorItemSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerChargingStation extends StaticPowerTileEntityContainer<BlockEntityChargingStation> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerChargingStation, GuiChargingStation> TYPE = new ContainerTypeAllocator<>("machine_charging_station", ContainerChargingStation::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiChargingStation::new);
		}
	}

	public ContainerChargingStation(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityChargingStation) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerChargingStation(int windowId, Inventory playerInventory, BlockEntityChargingStation owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 0, 53, 24));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 1, 72, 24));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 2, 91, 24));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().unchargedInventory, 3, 110, 24));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 0, 48, 52));
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 1, 71, 52));
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 2, 94, 52));
		this.addSlot(new OutputSlot(getTileEntity().chargedInventory, 3, 117, 52));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 62));

		// Armor
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 39, 152, 8, EquipmentSlot.HEAD));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 38, 152, 26, EquipmentSlot.CHEST));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 37, 152, 44, EquipmentSlot.LEGS));
		this.addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 36, 152, 62, EquipmentSlot.FEET));

		addAllPlayerSlots();
	}
}
