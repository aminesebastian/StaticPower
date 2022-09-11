package theking530.staticpower.blockentities.machines.pumpjack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerPumpJack extends StaticPowerTileEntityContainer<BlockEntityPumpJack> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPumpJack, GuiPumpJack> TYPE = new ContainerTypeAllocator<>("pump_jack", ContainerPumpJack::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPumpJack::new);
		}
	}

	public ContainerPumpJack(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityPumpJack) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPumpJack(int windowId, Inventory playerInventory, BlockEntityPumpJack owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
//		// FluidContainerDrainSlots
//		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.BUCKET, 0, 133, 20));
//		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.WATER_BUCKET, 1, 133, 56));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 58));

		addAllPlayerSlots();
	}
}
