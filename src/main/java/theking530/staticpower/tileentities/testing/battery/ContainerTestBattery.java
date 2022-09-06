package theking530.staticpower.tileentities.testing.battery;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModItems;

public class ContainerTestBattery extends StaticPowerTileEntityContainer<BlockEntityTestBattery> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerTestBattery, GuiTestBattery> TYPE = new ContainerTypeAllocator<>("battery_test", ContainerTestBattery::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiTestBattery::new);
		}
	}

	public ContainerTestBattery(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityTestBattery) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTestBattery(int windowId, Inventory playerInventory, BlockEntityTestBattery owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Battery
//		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Charging
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.BasicMiningDrill.get()), getTileEntity().chargingInventory, 0, 152, 64));

		addAllPlayerSlots();
	}
}
