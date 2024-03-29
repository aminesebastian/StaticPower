package theking530.staticpower.blockentities.nonpowered.tank;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.FluidContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerTank extends StaticPowerTileEntityContainer<BlockEntityTank> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerTank, GuiTank> TYPE = new ContainerTypeAllocator<>("tank", ContainerTank::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiTank::new);
		}
	}

	public ContainerTank(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityTank) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTank(int windowId, Inventory playerInventory, BlockEntityTank owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// FluidContainerDrainSlots
		addSlot(new FluidContainerSlot(getTileEntity().inputFluidContainerComponent, Items.WATER_BUCKET, 0, 30, 31));
		addSlot(new FluidContainerSlot(getTileEntity().inputFluidContainerComponent, Items.BUCKET, 1, 30, 63));

		// FluidContainerFillSlots
		addSlot(new FluidContainerSlot(getTileEntity().outputFluidContainerComponent, Items.BUCKET, 0, 130, 31));
		addSlot(new FluidContainerSlot(getTileEntity().outputFluidContainerComponent, Items.WATER_BUCKET, 1, 130, 63));

		addAllPlayerSlots();
	}
}
