package theking530.staticpower.tileentities.powered.former;

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

public class ContainerFormer extends StaticPowerTileEntityContainer<TileEntityFormer> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerFormer, GuiFormer> TYPE = new ContainerTypeAllocator<>("machine_former", ContainerFormer::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiFormer::new);
		}
	}

	public ContainerFormer(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityFormer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFormer(int windowId, Inventory playerInventory, TileEntityFormer owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input Former
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 59, 34));

		// Input Mold
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.MoldBlank), 0.3f, getTileEntity().moldInventory, 0, 37, 34));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 118, 35));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 65));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 12));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 32));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
