package theking530.staticpower.blockentities.machines.crucible;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.OutputSlot;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.container.slots.UpgradeItemSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerCrucible extends StaticPowerTileEntityContainer<BlockEntityCrucible> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCrucible, GuiCrucible> TYPE = new ContainerTypeAllocator<>("machine_crucible", ContainerCrucible::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiCrucible::new);
		}
	}

	public ContainerCrucible(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityCrucible) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerCrucible(int windowId, Inventory playerInventory, BlockEntityCrucible owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 52, 42));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 79, 24));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 17));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 35));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 53));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addAllPlayerSlots();
	}
}
