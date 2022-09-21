package theking530.staticpower.blockentities.power.heatsink;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerHeatSink extends StaticPowerTileEntityContainer<BlockEntityHeatSink> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerHeatSink, GuiHeatSink> TYPE = new ContainerTypeAllocator<>("heat_sink", ContainerHeatSink::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiHeatSink::new);
		}
	}

	public ContainerHeatSink(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityHeatSink) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerHeatSink(int windowId, Inventory playerInventory, BlockEntityHeatSink owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
