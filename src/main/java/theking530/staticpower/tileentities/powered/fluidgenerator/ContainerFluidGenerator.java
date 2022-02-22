package theking530.staticpower.tileentities.powered.fluidgenerator;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerFluidGenerator extends StaticPowerTileEntityContainer<TileEntityFluidGenerator> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerFluidGenerator, GuiFluidGenerator> TYPE = new ContainerTypeAllocator<>("machine_fluid_generator", ContainerFluidGenerator::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiFluidGenerator::new);
		}
	}

	public ContainerFluidGenerator(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityFluidGenerator) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFluidGenerator(int windowId, Inventory playerInventory, TileEntityFluidGenerator owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
