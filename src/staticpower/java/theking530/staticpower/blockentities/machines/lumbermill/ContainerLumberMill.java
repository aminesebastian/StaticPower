package theking530.staticpower.blockentities.machines.lumbermill;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.OutputSlot;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerLumberMill extends StaticPowerTileEntityContainer<BlockEntityLumberMill> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerLumberMill, GuiLumberMill> TYPE = new ContainerTypeAllocator<>("machine_lumber_mill", ContainerLumberMill::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiLumberMill::new);
		}
	}

	public ContainerLumberMill(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityLumberMill) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerLumberMill(int windowId, Inventory playerInventory, BlockEntityLumberMill owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 36, 32));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().mainOutputInventory, 0, 90, 32));
		this.addSlot(new OutputSlot(getTileEntity().secondaryOutputInventory, 0, 120, 32));

		addAllPlayerSlots();
	}
}
