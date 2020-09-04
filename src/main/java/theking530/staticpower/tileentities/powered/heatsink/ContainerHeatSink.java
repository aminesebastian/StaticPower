package theking530.staticpower.tileentities.powered.heatsink;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerHeatSink extends StaticPowerTileEntityContainer<TileEntityHeatSink> {
		@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerHeatSink, GuiHeatSink> TYPE = new ContainerTypeAllocator<>("heat_sink", ContainerHeatSink::new, GuiHeatSink::new);

	public ContainerHeatSink(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityHeatSink) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerHeatSink(int windowId, PlayerInventory playerInventory, TileEntityHeatSink owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		this.addPlayerInventory(getPlayerInventory(), 8, 64);
		this.addPlayerHotbar(getPlayerInventory(), 8, 122);
	}
}
