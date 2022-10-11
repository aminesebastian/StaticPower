package theking530.staticpower.blockentities.digistorenetwork.manager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerDigistoreManager extends StaticPowerTileEntityContainer<TileEntityDigistoreManager> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreManager, GuiDigistoreManager> TYPE = new ContainerTypeAllocator<>("digistore_manager", ContainerDigistoreManager::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistoreManager::new);
		}
	}

	public ContainerDigistoreManager(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityDigistoreManager) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistoreManager(int windowId, Inventory playerInventory, TileEntityDigistoreManager owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addAllPlayerSlots();
	}
}
