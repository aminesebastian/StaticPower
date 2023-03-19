package theking530.staticpower.blockentities.power.solarpanels;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerSolarPanel extends StaticPowerTileEntityContainer<BlockEntitySolarPanel> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerSolarPanel, GuiSolarPanel> TYPE = new ContainerTypeAllocator<>("solar_panel", ContainerSolarPanel::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiSolarPanel::new);
		}
	}

	public ContainerSolarPanel(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntitySolarPanel) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerSolarPanel(int windowId, Inventory playerInventory, BlockEntitySolarPanel owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {

	}
}
