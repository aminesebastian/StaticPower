package theking530.staticpower.cables.redstone.basic.gui;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.cables.redstone.RedstoneCableConfiguration;
import theking530.staticpower.cables.redstone.RedstoneCableSideConfiguration;
import theking530.staticpower.cables.redstone.basic.RedstoneCableComponent;
import theking530.staticpower.cables.redstone.basic.BlockEntityRedstoneCable;
import theking530.staticpower.container.StaticPowerTileEntityContainer;

public class ContainerBasicRedstoneIO extends StaticPowerTileEntityContainer<BlockEntityRedstoneCable> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerBasicRedstoneIO, GuiBasicRedstoneIO> TYPE = new ContainerTypeAllocator<>("cable_basic_redstone_io", ContainerBasicRedstoneIO::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiBasicRedstoneIO::new);
		}
	}

	private Direction hitSide;

	public ContainerBasicRedstoneIO(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityRedstoneCable) resolveTileEntityFromDataPacket(inv, data), Direction.values()[data.readInt()]);
	}

	public ContainerBasicRedstoneIO(int windowId, Inventory playerInventory, BlockEntityRedstoneCable cableTe, Direction sideHit) {
		super(TYPE, windowId, playerInventory, cableTe);
		this.hitSide = sideHit;
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}

	public Direction getHitSide() {
		return hitSide;
	}

	public RedstoneCableComponent getCableComponent() {
		return this.getTileEntity().cableComponent;
	}

	public RedstoneCableConfiguration getConfiguration() {
		return getCableComponent().getConfiguration();
	}

	public RedstoneCableSideConfiguration getSideConfiguration() {
		return getCableComponent().getConfiguration().getSideConfig(hitSide);
	}
}
