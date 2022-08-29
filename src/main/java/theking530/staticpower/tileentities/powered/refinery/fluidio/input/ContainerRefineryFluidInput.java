package theking530.staticpower.tileentities.powered.refinery.fluidio.input;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.tileentities.powered.refinery.fluidio.TileEntityRefineryFluidIO;

public class ContainerRefineryFluidInput extends StaticPowerTileEntityContainer<TileEntityRefineryFluidIO> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRefineryFluidInput, GuiRefineryFluidInput> TYPE = new ContainerTypeAllocator<>("machine_refinery_fluid_input", ContainerRefineryFluidInput::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRefineryFluidInput::new);
		}
	}

	public ContainerRefineryFluidInput(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityRefineryFluidIO) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRefineryFluidInput(int windowId, Inventory playerInventory, TileEntityRefineryFluidIO owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}