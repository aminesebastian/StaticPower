package theking530.staticpower.blockentities.digistorenetwork.severrack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerDigistoreServerRack extends StaticPowerTileEntityContainer<BlockEntityDigistoreServerRack> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreServerRack, GuiDigistoreServerRack> TYPE = new ContainerTypeAllocator<>("digistore_server_rack", ContainerDigistoreServerRack::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistoreServerRack::new);
		}
	}

	public ContainerDigistoreServerRack(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityDigistoreServerRack) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistoreServerRack(int windowId, Inventory playerInventory, BlockEntityDigistoreServerRack owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 0, 65, 20));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 1, 65, 40));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 2, 65, 60));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 3, 65, 80));

		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 4, 93, 20));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 5, 93, 40));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 6, 93, 60));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, 7, 93, 80));

		addAllPlayerSlots();
	}
}
