package theking530.staticpower.tileentities.powered.fluidinfuser;

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

public class ContainerFluidInfuser extends StaticPowerTileEntityContainer<TileEntityFluidInfuser> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerFluidInfuser, GuiFluidInfuser> TYPE = new ContainerTypeAllocator<>("machine_fluid_infuser", ContainerFluidInfuser::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiFluidInfuser::new);
		}
	}

	public ContainerFluidInfuser(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityFluidInfuser) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFluidInfuser(int windowId, Inventory playerInventory, TileEntityFluidInfuser owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 31, 30));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 127, 30));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 12));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 32));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
