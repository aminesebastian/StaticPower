package theking530.staticpower.blockentities.machines.enchanter;

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

public class ContainerEnchanter extends StaticPowerTileEntityContainer<BlockEntityEnchanter> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerEnchanter, GuiEnchanter> TYPE = new ContainerTypeAllocator<>("machine_enchanter", ContainerEnchanter::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiEnchanter::new);
		}
	}

	public ContainerEnchanter(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityEnchanter) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerEnchanter(int windowId, Inventory playerInventory, BlockEntityEnchanter owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 32, 20));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 1, 32, 41));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 2, 32, 62));
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().enchantableInventory, 0, 100, 40));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 150, 40));

		addAllPlayerSlots();
	}
}
