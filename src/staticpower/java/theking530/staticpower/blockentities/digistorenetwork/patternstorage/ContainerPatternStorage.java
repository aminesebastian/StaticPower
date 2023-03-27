package theking530.staticpower.blockentities.digistorenetwork.patternstorage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerPatternStorage extends StaticPowerTileEntityContainer<BlockEntityPatternStorage> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPatternStorage, GuiPatternStorage> TYPE = new ContainerTypeAllocator<>("digistore_atomic_constructor", ContainerPatternStorage::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPatternStorage::new);
		}
	}

	public ContainerPatternStorage(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityPatternStorage) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPatternStorage(int windowId, Inventory playerInventory, BlockEntityPatternStorage owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addSlotsInGrid(getTileEntity().patternInventory, 0, 89, 24, 9, (index, x, y) -> {
			return new StaticPowerContainerSlot(getTileEntity().patternInventory, index, x, y);
		});

		addAllPlayerSlots();
	}
}
