package theking530.staticpower.blockentities.power.transformer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerTransformer extends StaticPowerTileEntityContainer<BlockEntityTransformer> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerTransformer, GuiTransformer> TYPE = new ContainerTypeAllocator<>("transformer", ContainerTransformer::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiTransformer::new);
		}
	}

	public ContainerTransformer(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityTransformer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTransformer(int windowId, Inventory playerInventory, BlockEntityTransformer owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
