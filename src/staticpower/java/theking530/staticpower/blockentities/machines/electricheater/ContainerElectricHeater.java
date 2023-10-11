package theking530.staticpower.blockentities.machines.electricheater;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.BatteryItemSlot;

public class ContainerElectricHeater extends StaticPowerTileEntityContainer<BlockEntityElectricHeater> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerElectricHeater, GuiElectricHeater> TYPE = new ContainerTypeAllocator<>(
			"machine_electric_heater", ContainerElectricHeater::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiElectricHeater::new);
		}
	}

	public ContainerElectricHeater(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityElectricHeater) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerElectricHeater(int windowId, Inventory playerInventory, BlockEntityElectricHeater owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addAllPlayerSlots();
	}
}
