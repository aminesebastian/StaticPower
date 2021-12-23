package theking530.staticpower.tileentities.powered.pump;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.FluidContainerSlot;

public class ContainerPump extends StaticPowerTileEntityContainer<TileEntityPump> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPump, GuiPump> TYPE = new ContainerTypeAllocator<>("pump", ContainerPump::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPump::new);
		}
	}

	public ContainerPump(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityPump) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPump(int windowId, Inventory playerInventory, TileEntityPump owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// FluidContainerDrainSlots
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.BUCKET, 0, 133, 20));
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.WATER_BUCKET, 1, 133, 56));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 58));

		addPlayerInventory(getPlayerInventory(), 8, 78);
		addPlayerHotbar(getPlayerInventory(), 8, 136);
	}
}
