package theking530.staticpower.tileentities.nonpowered.conveyors.hopper;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.FilterItemSlot;

public class ContainerFilteredConveyorHopper extends StaticPowerTileEntityContainer<TileEntityConveyorHopper> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerFilteredConveyorHopper, GuiFilteredConveyorHopper> TYPE = new ContainerTypeAllocator<>("conveyor_filtered_hopper",
			ContainerFilteredConveyorHopper::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiFilteredConveyorHopper::new);
		}
	}

	public ContainerFilteredConveyorHopper(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityConveyorHopper) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFilteredConveyorHopper(int windowId, PlayerInventory playerInventory, TileEntityConveyorHopper owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addSlot(new FilterItemSlot(getTileEntity().filterInventory, 0, 80, 20));
		addPlayerInventory(getPlayerInventory(), 8, 42);
		addPlayerHotbar(getPlayerInventory(), 8, 100);
	}
}
