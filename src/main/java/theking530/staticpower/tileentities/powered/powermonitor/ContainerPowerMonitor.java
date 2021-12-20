package theking530.staticpower.tileentities.powered.powermonitor;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerPowerMonitor extends StaticPowerTileEntityContainer<TileEntityPowerMonitor> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPowerMonitor, GuiPowerMonitor> TYPE = new ContainerTypeAllocator<>("power_monitor", ContainerPowerMonitor::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPowerMonitor::new);
		}
	}

	public ContainerPowerMonitor(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityPowerMonitor) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPowerMonitor(int windowId, PlayerInventory playerInventory, TileEntityPowerMonitor owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
