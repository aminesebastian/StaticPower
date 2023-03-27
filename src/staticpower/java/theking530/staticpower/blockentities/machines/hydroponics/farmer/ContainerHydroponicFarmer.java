package theking530.staticpower.blockentities.machines.hydroponics.farmer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerHydroponicFarmer extends StaticPowerTileEntityContainer<BlockEntityHydroponicFarmer> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerHydroponicFarmer, GuiHydroponicFarmer> TYPE = new ContainerTypeAllocator<>("hydroponic_farmer",
			ContainerHydroponicFarmer::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiHydroponicFarmer::new);
		}
	}

	public ContainerHydroponicFarmer(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityHydroponicFarmer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerHydroponicFarmer(int windowId, Inventory playerInventory, BlockEntityHydroponicFarmer owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addAllPlayerSlots();
	}
}
