package theking530.staticpower.blockentities.power.rectifier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerRectifier extends StaticPowerTileEntityContainer<BlockEntityRectifier> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRectifier, GuiRectifier> TYPE = new ContainerTypeAllocator<>("rectifier", ContainerRectifier::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRectifier::new);
		}
	}

	public ContainerRectifier(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityRectifier) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRectifier(int windowId, Inventory playerInventory, BlockEntityRectifier owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
