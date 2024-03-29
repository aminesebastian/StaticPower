package theking530.staticpower.blockentities.machines.mixer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerMixer extends StaticPowerTileEntityContainer<BlockEntityMixer> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerMixer, GuiMixer> TYPE = new ContainerTypeAllocator<>("machine_mixer", ContainerMixer::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiMixer::new);
		}
	}

	public ContainerMixer(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityMixer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerMixer(int windowId, Inventory playerInventory, BlockEntityMixer owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().input1Inventory, 0, 60, 22));
		addSlot(new StaticPowerContainerSlot(getTileEntity().input2Inventory, 0, 60, 60));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addAllPlayerSlots();
	}
}
