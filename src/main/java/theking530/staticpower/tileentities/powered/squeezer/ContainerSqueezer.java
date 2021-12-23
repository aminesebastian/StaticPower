package theking530.staticpower.tileentities.powered.squeezer;

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
import theking530.staticpower.container.slots.UpgradeItemSlot;

public class ContainerSqueezer extends StaticPowerTileEntityContainer<TileEntitySqueezer> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerSqueezer, GuiSqueezer> TYPE = new ContainerTypeAllocator<>("machine_squeezer", ContainerSqueezer::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiSqueezer::new);
		}
	}

	public ContainerSqueezer(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntitySqueezer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerSqueezer(int windowId, Inventory playerInventory, TileEntitySqueezer owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 52, 28));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 52));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 17));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 35));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 53));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
