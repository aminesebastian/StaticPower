package theking530.staticpower.blockentities.machines.refinery.powertap;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerRefineryPowerTap extends StaticPowerTileEntityContainer<BlockEntityRefineryPowerTap> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRefineryPowerTap, GuiRefineryPowerTap> TYPE = new ContainerTypeAllocator<>("machine_refinery_power_tap", ContainerRefineryPowerTap::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRefineryPowerTap::new);
		}
	}

	public ContainerRefineryPowerTap(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityRefineryPowerTap) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRefineryPowerTap(int windowId, Inventory playerInventory, BlockEntityRefineryPowerTap owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
