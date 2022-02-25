package theking530.staticpower.tileentities.nonpowered.chest;

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

public class ContainerStaticChest extends StaticPowerTileEntityContainer<TileEntityStaticChest> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerStaticChest, GuiStaticChest> TYPE = new ContainerTypeAllocator<>("chest_static", ContainerStaticChest::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiStaticChest::new);
		}
	}

	public ContainerStaticChest(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityStaticChest) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerStaticChest(int windowId, Inventory playerInventory, TileEntityStaticChest owner) {
		super(TYPE, windowId, playerInventory, owner);
		owner.addOpenCount();
	}

	@Override
	public void initializeContainer() {
		int rows = TileEntityStaticChest.getSlotsPerRow(getTileEntity());
		Vector2D size = TileEntityStaticChest.getInventorySize(getTileEntity());

		// Add slots.
		this.addSlotsInGrid(getTileEntity().inventory, 0, (int) (size.getXi() / 2), 20, rows, (index, x, y) -> {
			return new StaticPowerContainerSlot(getTileEntity().inventory, index, x, y);
		});

		this.addPlayerInventory(getPlayerInventory(), 8, size.getYi() - 82);
		this.addPlayerHotbar(getPlayerInventory(), 8, size.getYi() - 24);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		getTileEntity().removeOpenCount();
	}
}
