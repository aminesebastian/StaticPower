package theking530.staticpower.tileentities.nonpowered.placer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerAutomaticPlacer extends StaticPowerTileEntityContainer<TileEntityAutomaticPlacer> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerAutomaticPlacer, GuiAutomaticPlacer> TYPE = new ContainerTypeAllocator<>("placer", ContainerAutomaticPlacer::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiAutomaticPlacer::new);
		}
	}

	public ContainerAutomaticPlacer(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityAutomaticPlacer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAutomaticPlacer(int windowId, Inventory playerInventory, TileEntityAutomaticPlacer owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Upgrades
		this.addSlotsInGrid(getTileEntity().inventory, 0, 89, 20, 3, (index, x, y) -> {
			return new StaticPowerContainerSlot(getTileEntity().inventory, index, x, y);
		});

		addAllPlayerSlots();
	}
}
