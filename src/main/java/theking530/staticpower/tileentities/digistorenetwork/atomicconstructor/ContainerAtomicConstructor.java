package theking530.staticpower.tileentities.digistorenetwork.atomicconstructor;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerAtomicConstructor extends StaticPowerTileEntityContainer<TileEntityAtomicConstructor> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerAtomicConstructor, GuiAtomicConstructor> TYPE = new ContainerTypeAllocator<>("digistore_atomic_constructor", ContainerAtomicConstructor::new,
			GuiAtomicConstructor::new);

	public ContainerAtomicConstructor(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityAtomicConstructor) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAtomicConstructor(int windowId, PlayerInventory playerInventory, TileEntityAtomicConstructor owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addSlotsInGrid(getTileEntity().patternInventory, 0, 89, 24, 9, (index, x, y) -> {
			return new StaticPowerContainerSlot(getTileEntity().patternInventory, index, x, y);
		});

		addPlayerHotbar(getPlayerInventory(), 8, 126);
		addPlayerInventory(getPlayerInventory(), 8, 68);
	}
}
