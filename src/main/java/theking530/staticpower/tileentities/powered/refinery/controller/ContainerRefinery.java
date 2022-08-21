package theking530.staticpower.tileentities.powered.refinery.controller;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerRefinery extends StaticPowerTileEntityContainer<TileEntityRefinery> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRefinery, GuiRefinery> TYPE = new ContainerTypeAllocator<>("machine_refinery_controller", ContainerRefinery::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRefinery::new);
		}
	}

	public ContainerRefinery(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityRefinery) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRefinery(int windowId, Inventory playerInventory, TileEntityRefinery owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().catalystInventory, 0, 79, 22));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addAllPlayerSlots();
	}
}
