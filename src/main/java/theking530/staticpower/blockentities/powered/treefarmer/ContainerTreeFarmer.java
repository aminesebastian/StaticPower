package theking530.staticpower.blockentities.powered.treefarmer;

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

public class ContainerTreeFarmer extends StaticPowerTileEntityContainer<BlockEntityTreeFarm> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerTreeFarmer, GuiTreeFarmer> TYPE = new ContainerTypeAllocator<>("machine_tree_farmer", ContainerTreeFarmer::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiTreeFarmer::new);
		}
	}

	public ContainerTreeFarmer(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityTreeFarm) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTreeFarmer(int windowId, Inventory playerInventory, BlockEntityTreeFarm owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Inputs
		for (int l = 0; l < 3; ++l) {
			for (int i1 = 0; i1 < 3; ++i1) {
				addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 1 + i1 + l * 3, 30 + i1 * 18, 20 + l * 18));
			}
		}

		// Output
		for (int l = 0; l < 3; ++l) {
			for (int i1 = 0; i1 < 3; ++i1) {
				addSlot(new OutputSlot(getTileEntity().outputInventory, i1 + l * 3, 90 + i1 * 18, 20 + l * 18));
			}
		}

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		addAllPlayerSlots();
	}
}
