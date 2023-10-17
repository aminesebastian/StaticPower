package theking530.staticpower.blockentities.nonpowered.cokeoven;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.OutputSlot;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerCokeOven extends StaticPowerTileEntityContainer<BlockEntityCokeOven> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCokeOven, GuiCokeOven> TYPE = new ContainerTypeAllocator<>(
			"coke_oven", ContainerCokeOven::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiCokeOven::new);
		}
	}

	public ContainerCokeOven(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityCokeOven) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerCokeOven(int windowId, Inventory playerInventory, BlockEntityCokeOven owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 45, 40));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 110, 40)
				.shouldApplyExperience(getTileEntity().processingComponent));
		addAllPlayerSlots();
	}
}
