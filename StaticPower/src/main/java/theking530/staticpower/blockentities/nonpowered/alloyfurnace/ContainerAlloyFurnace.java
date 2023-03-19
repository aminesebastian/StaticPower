package theking530.staticpower.blockentities.nonpowered.alloyfurnace;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.OutputSlot;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerAlloyFurnace extends StaticPowerTileEntityContainer<BlockEntityAlloyFurnace> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerAlloyFurnace, GuiAlloyFurnace> TYPE = new ContainerTypeAllocator<>("alloy_furnace", ContainerAlloyFurnace::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiAlloyFurnace::new);
		}
	}

	public ContainerAlloyFurnace(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityAlloyFurnace) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAlloyFurnace(int windowId, Inventory playerInventory, BlockEntityAlloyFurnace owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 36, 22));
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 1, 58, 22));

		addSlot(new StaticPowerContainerSlot(getTileEntity().fuelInventory, 0, 47, 64));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 110, 40).shouldApplyExperience(getTileEntity().processingComponent));
		addAllPlayerSlots();
	}
}
