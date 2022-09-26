package theking530.staticpower.blockentities.machines.hydroponicfarmer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

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
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 80, 25));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 40, 60));
		addAllPlayerSlots();
	}
}
