package theking530.staticpower.blockentities.machines.refinery.condenser;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerRefineryCondenser extends StaticPowerTileEntityContainer<BlockEntityRefineryCondenser> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRefineryCondenser, GuiRefineryCondenser> TYPE = new ContainerTypeAllocator<>(
			"machine_refinery_condenser", ContainerRefineryCondenser::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRefineryCondenser::new);
		}
	}

	public ContainerRefineryCondenser(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityRefineryCondenser) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRefineryCondenser(int windowId, Inventory playerInventory, BlockEntityRefineryCondenser owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
