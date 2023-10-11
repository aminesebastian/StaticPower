package theking530.staticpower.blockentities.machines.refinery.fluidinput;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerRefineryFluidInput extends StaticPowerTileEntityContainer<BlockEntityRefineryFluidInput> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRefineryFluidInput, GuiRefineryFluidInput> TYPE = new ContainerTypeAllocator<>("machine_refinery_fluid_input", ContainerRefineryFluidInput::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRefineryFluidInput::new);
		}
	}

	public ContainerRefineryFluidInput(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityRefineryFluidInput) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRefineryFluidInput(int windowId, Inventory playerInventory, BlockEntityRefineryFluidInput owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
