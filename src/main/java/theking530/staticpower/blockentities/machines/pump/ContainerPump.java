package theking530.staticpower.blockentities.machines.pump;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerPump extends StaticPowerTileEntityContainer<BlockEntityPump> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPump, GuiPump> TYPE = new ContainerTypeAllocator<>("pump", ContainerPump::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPump::new);
		}
	}

	public ContainerPump(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityPump) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPump(int windowId, Inventory playerInventory, BlockEntityPump owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));
		addAllPlayerSlots();
	}
}
