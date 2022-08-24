package theking530.staticpower.tileentities.powered.refinery.heatvent;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerRefineryHeatVent extends StaticPowerTileEntityContainer<TileEntityRefineryHeatVent> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRefineryHeatVent, GuiRefineryHeatVent> TYPE = new ContainerTypeAllocator<>("machine_refinery_heavt_vent",
			ContainerRefineryHeatVent::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRefineryHeatVent::new);
		}
	}

	public ContainerRefineryHeatVent(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityRefineryHeatVent) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRefineryHeatVent(int windowId, Inventory playerInventory, TileEntityRefineryHeatVent owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
