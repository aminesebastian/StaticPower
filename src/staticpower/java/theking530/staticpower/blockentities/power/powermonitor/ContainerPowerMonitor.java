package theking530.staticpower.blockentities.power.powermonitor;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerPowerMonitor extends StaticPowerTileEntityContainer<BlockEntityPowerMonitor> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPowerMonitor, GuiPowerMonitor> TYPE = new ContainerTypeAllocator<>("power_monitor", ContainerPowerMonitor::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPowerMonitor::new);
		}
	}

	public ContainerPowerMonitor(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityPowerMonitor) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPowerMonitor(int windowId, Inventory playerInventory, BlockEntityPowerMonitor owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
