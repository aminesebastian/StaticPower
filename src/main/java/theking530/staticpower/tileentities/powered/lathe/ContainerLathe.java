package theking530.staticpower.tileentities.powered.lathe;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerLathe extends StaticPowerTileEntityContainer<TileEntityLathe> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerLathe, GuiLathe> TYPE = new ContainerTypeAllocator<>("machine_lathe", ContainerLathe::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiLathe::new);
		}
	}

	public ContainerLathe(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityLathe) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerLathe(int windowId, Inventory playerInventory, TileEntityLathe owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlotsInGrid(getTileEntity().inputInventory, 0, 60, 18, 3, (index, x, y) -> {
			return new StaticPowerContainerSlot(getTileEntity().inputInventory, index, x, y);
		});

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().mainOutputInventory, 0, 120, 20));
		this.addSlot(new OutputSlot(getTileEntity().secondaryOutputInventory, 0, 120, 52));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
