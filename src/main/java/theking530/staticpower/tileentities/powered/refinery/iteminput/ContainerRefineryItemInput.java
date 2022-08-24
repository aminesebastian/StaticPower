package theking530.staticpower.tileentities.powered.refinery.iteminput;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerRefineryItemInput extends StaticPowerTileEntityContainer<TileEntityRefineryItemInput> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRefineryItemInput, GuiRefineryItemInput> TYPE = new ContainerTypeAllocator<>("machine_refinery_item_input",
			ContainerRefineryItemInput::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRefineryItemInput::new);
		}
	}

	public ContainerRefineryItemInput(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityRefineryItemInput) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRefineryItemInput(int windowId, Inventory playerInventory, TileEntityRefineryItemInput owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
