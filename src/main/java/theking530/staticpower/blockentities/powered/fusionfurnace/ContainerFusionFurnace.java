package theking530.staticpower.blockentities.powered.fusionfurnace;

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
import theking530.staticpower.container.slots.UpgradeItemSlot;

public class ContainerFusionFurnace extends StaticPowerTileEntityContainer<BlockEntityFusionFurnace> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerFusionFurnace, GuiFusionFurnace> TYPE = new ContainerTypeAllocator<>("machine_fusion_furnace", ContainerFusionFurnace::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiFusionFurnace::new);
		}
	}

	public ContainerFusionFurnace(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityFusionFurnace) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFusionFurnace(int windowId, Inventory playerInventory, BlockEntityFusionFurnace owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 36, 40));
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 1, 58, 28));
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 2, 80, 17));
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 3, 102, 28));
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 4, 124, 40));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 59));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 14));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 34));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 54));

		addAllPlayerSlots();
	}
}
