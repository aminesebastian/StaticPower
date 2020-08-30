package theking530.staticpower.tileentities.powered.fusionfurnace;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerFusionFurnace extends StaticPowerTileEntityContainer<TileEntityFusionFurnace> {

	public ContainerFusionFurnace(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityFusionFurnace) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFusionFurnace(int windowId, PlayerInventory playerInventory, TileEntityFusionFurnace owner) {
		super(ModContainerTypes.FUSION_FURNACE_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 36, 40));
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 1, 58, 28));
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 2, 80, 17));
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 3, 102, 28));
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 4, 124, 40));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 59));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 14));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 34));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 54));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
