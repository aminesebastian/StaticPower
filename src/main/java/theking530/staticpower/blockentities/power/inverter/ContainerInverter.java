package theking530.staticpower.blockentities.power.inverter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerInverter extends StaticPowerTileEntityContainer<BlockEntityInverter> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerInverter, GuiInverter> TYPE = new ContainerTypeAllocator<>("inverter", ContainerInverter::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiInverter::new);
		}
	}

	public ContainerInverter(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityInverter) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerInverter(int windowId, Inventory playerInventory, BlockEntityInverter owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}
}
