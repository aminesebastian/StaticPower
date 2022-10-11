package theking530.staticpower.blockentities.nonpowered.conveyors.hopper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.FilterItemSlot;

public class ContainerFilteredConveyorHopper extends StaticPowerTileEntityContainer<BlockEntityConveyorHopper> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerFilteredConveyorHopper, GuiFilteredConveyorHopper> TYPE = new ContainerTypeAllocator<>("conveyor_filtered_hopper",
			ContainerFilteredConveyorHopper::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiFilteredConveyorHopper::new);
		}
	}

	public ContainerFilteredConveyorHopper(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityConveyorHopper) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFilteredConveyorHopper(int windowId, Inventory playerInventory, BlockEntityConveyorHopper owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addSlot(new FilterItemSlot(getTileEntity().filterInventory, 0, 80, 20));
		addAllPlayerSlots();
	}
}
