package theking530.staticpower.blockentities.machines.poweredgrinder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.OutputSlot;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.container.slots.UpgradeItemSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerPoweredGrinder extends StaticPowerTileEntityContainer<BlockEntityPoweredGrinder> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPoweredGrinder, GuiPoweredGrinder> TYPE = new ContainerTypeAllocator<>("machine_powered_grinder", ContainerPoweredGrinder::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPoweredGrinder::new);
		}
	}

	public ContainerPoweredGrinder(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityPoweredGrinder) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPoweredGrinder(int windowId, Inventory playerInventory, BlockEntityPoweredGrinder owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 80, 18));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 60));
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 1, 106, 46));
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 2, 54, 46));

		// Upgrades
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 12));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 32));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		addAllPlayerSlots();
	}
}
