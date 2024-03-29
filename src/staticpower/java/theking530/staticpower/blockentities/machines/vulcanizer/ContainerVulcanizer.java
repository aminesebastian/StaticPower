package theking530.staticpower.blockentities.machines.vulcanizer;

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

public class ContainerVulcanizer extends StaticPowerTileEntityContainer<BlockEntityVulcanizer> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerVulcanizer, GuiVulcanizer> TYPE = new ContainerTypeAllocator<>("machine_vulcanizer", ContainerVulcanizer::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiVulcanizer::new);
		}
	}

	public ContainerVulcanizer(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityVulcanizer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerVulcanizer(int windowId, Inventory playerInventory, BlockEntityVulcanizer owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 77, 22));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 116, 40));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 12));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 32));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addAllPlayerSlots();
	}
}
