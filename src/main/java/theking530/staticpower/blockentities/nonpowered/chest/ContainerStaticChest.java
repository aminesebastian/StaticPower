package theking530.staticpower.blockentities.nonpowered.chest;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerStaticChest extends StaticPowerTileEntityContainer<BlockEntityStaticChest> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerStaticChest, GuiStaticChest> TYPE = new ContainerTypeAllocator<>("chest_static", ContainerStaticChest::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiStaticChest::new);
		}
	}

	public ContainerStaticChest(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityStaticChest) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerStaticChest(int windowId, Inventory playerInventory, BlockEntityStaticChest owner) {
		super(TYPE, windowId, playerInventory, owner);
		owner.addOpenCount();
	}

	@Override
	public void initializeContainer() {
		int rows = BlockEntityStaticChest.getSlotsPerRow(getTileEntity());
		Vector2D size = BlockEntityStaticChest.getInventorySize(getTileEntity());

		// Add slots.
		this.addSlotsInGrid(getTileEntity().inventory, 0, (int) (size.getXi() / 2) + 1, 18, rows, (index, x, y) -> {
			return new StaticPowerContainerSlot(getTileEntity().inventory, index, x, y);
		});

		addAllPlayerSlots();
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		getTileEntity().removeOpenCount();
	}
}
