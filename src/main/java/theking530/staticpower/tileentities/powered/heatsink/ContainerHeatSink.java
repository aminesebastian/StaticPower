package theking530.staticpower.tileentities.powered.heatsink;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerHeatSink extends StaticPowerTileEntityContainer<TileEntityHeatSink> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerHeatSink, GuiHeatSink> TYPE = new ContainerTypeAllocator<>("heat_sink", ContainerHeatSink::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiHeatSink::new);
		}
	}

	public ContainerHeatSink(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityHeatSink) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerHeatSink(int windowId, Inventory playerInventory, TileEntityHeatSink owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		this.addPlayerInventory(getPlayerInventory(), 8, 74);
		this.addPlayerHotbar(getPlayerInventory(), 8, 132);
	}
}
