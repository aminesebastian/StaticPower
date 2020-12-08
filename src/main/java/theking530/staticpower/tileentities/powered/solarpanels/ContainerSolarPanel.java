package theking530.staticpower.tileentities.powered.solarpanels;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerSolarPanel extends StaticPowerTileEntityContainer<TileEntitySolarPanel> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerSolarPanel, GuiSolarPanel> TYPE = new ContainerTypeAllocator<>("solar_panel", ContainerSolarPanel::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiSolarPanel::new);
		}
	}

	public ContainerSolarPanel(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntitySolarPanel) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerSolarPanel(int windowId, PlayerInventory playerInventory, TileEntitySolarPanel owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {

	}
}
