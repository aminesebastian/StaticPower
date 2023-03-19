package theking530.staticpower.blockentities.machines.hydroponics.pod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainierHydroponicPod extends StaticPowerTileEntityContainer<BlockEntityHydroponicPod> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainierHydroponicPod, GuiHydroponicPod> TYPE = new ContainerTypeAllocator<>("hydroponic_pod", ContainierHydroponicPod::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiHydroponicPod::new);
		}
	}

	public ContainierHydroponicPod(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityHydroponicPod) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainierHydroponicPod(int windowId, Inventory playerInventory, BlockEntityHydroponicPod owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 80, 30));

		addAllPlayerSlots();
	}
}
