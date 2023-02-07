package theking530.staticpower.blockentities.machines.fluid_pump;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerFluidPump extends StaticPowerTileEntityContainer<BlockEntityFluidPump> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerFluidPump, GuiFluidPump> TYPE = new ContainerTypeAllocator<>("fluid_pump", ContainerFluidPump::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiFluidPump::new);
		}
	}

	public ContainerFluidPump(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityFluidPump) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFluidPump(int windowId, Inventory playerInventory, BlockEntityFluidPump owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
