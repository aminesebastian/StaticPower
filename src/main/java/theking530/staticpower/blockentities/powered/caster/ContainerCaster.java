package theking530.staticpower.blockentities.powered.caster;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.init.ModItems;

public class ContainerCaster extends StaticPowerTileEntityContainer<BlockEntityCaster> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCaster, GuiCaster> TYPE = new ContainerTypeAllocator<>("machine_caster", ContainerCaster::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiCaster::new);
		}
	}

	public ContainerCaster(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityCaster) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerCaster(int windowId, Inventory playerInventory, BlockEntityCaster owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input Mold
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.MoldBlank.get()), 0.3f, getTileEntity().inputInventory, 0, 86, 34));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 126, 52));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 65));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 17));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 35));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 53));

		addAllPlayerSlots();
	}
}
