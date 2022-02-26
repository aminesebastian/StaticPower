package theking530.staticpower.tileentities.powered.fermenter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerFermenter extends StaticPowerTileEntityContainer<TileEntityFermenter> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerFermenter, GuiFermenter> TYPE = new ContainerTypeAllocator<>("machine_fermenter", ContainerFermenter::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiFermenter::new);
		}
	}

	public ContainerFermenter(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityFermenter) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFermenter(int windowId, Inventory playerInventory, TileEntityFermenter owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, j + i * 3, 40 + j * 18, 21 + i * 18));
			}
		}
		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 115, 55));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		addAllPlayerSlots();
	}
}
