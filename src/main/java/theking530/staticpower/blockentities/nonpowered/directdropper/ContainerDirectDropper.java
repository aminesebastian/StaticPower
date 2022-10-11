package theking530.staticpower.blockentities.nonpowered.directdropper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerDirectDropper extends StaticPowerTileEntityContainer<BlockEntityDirectDropper> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDirectDropper, GuiDirectDropper> TYPE = new ContainerTypeAllocator<>("direct_dropper", ContainerDirectDropper::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDirectDropper::new);
		}
	}

	public ContainerDirectDropper(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityDirectDropper) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDirectDropper(int windowId, Inventory playerInventory, BlockEntityDirectDropper owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Upgrades
		this.addSlotsInGrid(getTileEntity().inventory, 0, 89, 20, 3, (index, x, y) -> {
			return new StaticPowerContainerSlot(getTileEntity().inventory, index, x, y);
		});

		addAllPlayerSlots();
	}
}
