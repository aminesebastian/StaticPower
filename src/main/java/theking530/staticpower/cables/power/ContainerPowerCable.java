package theking530.staticpower.cables.power;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerPowerCable extends StaticPowerTileEntityContainer<TileEntityPowerCable> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerPowerCable, GuiPowerCable> TYPE = new ContainerTypeAllocator<>("power_cable", ContainerPowerCable::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiPowerCable::new);
		}
	}

	public ContainerPowerCable(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityPowerCable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPowerCable(int windowId, PlayerInventory playerInventory, TileEntityPowerCable owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
	}
}
