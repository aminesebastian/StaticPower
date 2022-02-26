package theking530.staticpower.tileentities.powered.packager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;

public class ContainerPackager extends StaticPowerTileEntityContainer<TileEntityPackager> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPackager, GuiPackager> TYPE = new ContainerTypeAllocator<>("machine_packagere", ContainerPackager::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPackager::new);
		}
	}

	public ContainerPackager(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityPackager) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPackager(int windowId, Inventory playerInventory, TileEntityPackager owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 50, 35));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 109, 35));

		// Upgrades
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 16));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 34));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		addAllPlayerSlots();
	}
}
