package theking530.staticpower.tileentities.powered.lumbermill;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerLumberMill extends StaticPowerTileEntityContainer<TileEntityLumberMill> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerLumberMill, GuiLumberMill> TYPE = new ContainerTypeAllocator<>("machine_lumber_mill", ContainerLumberMill::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiLumberMill::new);
		}
	}

	public ContainerLumberMill(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityLumberMill) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerLumberMill(int windowId, PlayerInventory playerInventory, TileEntityLumberMill owner) {
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

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
