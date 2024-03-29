package theking530.staticpower.blockentities.machines.refinery.controller;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerRefineryController extends StaticPowerTileEntityContainer<BlockEntityRefineryController> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRefineryController, GuiRefineryController> TYPE = new ContainerTypeAllocator<>("machine_refinery_controller", ContainerRefineryController::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRefineryController::new);
		}
	}

	public ContainerRefineryController(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityRefineryController) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRefineryController(int windowId, Inventory playerInventory, BlockEntityRefineryController owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().catalystInventory, 0, 83, 22));

		addAllPlayerSlots();
	}
}
