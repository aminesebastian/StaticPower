package theking530.staticpower.tileentities.powered.fluidgenerator;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.init.ModContainerTypes;

public class ContainerFluidGenerator extends StaticPowerTileEntityContainer<TileEntityFluidGenerator> {

	public ContainerFluidGenerator(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityFluidGenerator) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFluidGenerator(int windowId, PlayerInventory playerInventory, TileEntityFluidGenerator owner) {
		super(ModContainerTypes.FLUID_GENERATOR_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
