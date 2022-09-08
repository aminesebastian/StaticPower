package theking530.staticpower.blockentities.powered.refinery.fluidio.output;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.blockentities.powered.refinery.fluidio.BlockEntityRefineryFluidIO;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerRefineryFluidOutput extends StaticPowerTileEntityContainer<BlockEntityRefineryFluidIO> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRefineryFluidOutput, GuiRefineryFluidOutput> TYPE = new ContainerTypeAllocator<>("machine_refinery_fluid_output",
			ContainerRefineryFluidOutput::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRefineryFluidOutput::new);
		}
	}

	public ContainerRefineryFluidOutput(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityRefineryFluidIO) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRefineryFluidOutput(int windowId, Inventory playerInventory, BlockEntityRefineryFluidIO owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
