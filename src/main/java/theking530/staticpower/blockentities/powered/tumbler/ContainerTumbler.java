package theking530.staticpower.blockentities.powered.tumbler;

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

public class ContainerTumbler extends StaticPowerTileEntityContainer<BlockEntityTumbler> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerTumbler, GuiTumbler> TYPE = new ContainerTypeAllocator<>("machine_tumbler", ContainerTumbler::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiTumbler::new);
		}
	}

	public ContainerTumbler(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityTumbler) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTumbler(int windowId, Inventory playerInventory, BlockEntityTumbler owner) {
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

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 17));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 35));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 53));

		addAllPlayerSlots();
	}
}
