package theking530.staticpower.blockentities.nonpowered.randomitem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerRandomItemGenerator extends StaticPowerTileEntityContainer<BlockEntityRandomItemGenerator> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerRandomItemGenerator, GuiRandomItemGenerator> TYPE = new ContainerTypeAllocator<>("random_item_generator", ContainerRandomItemGenerator::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiRandomItemGenerator::new);
		}
	}

	public ContainerRandomItemGenerator(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityRandomItemGenerator) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerRandomItemGenerator(int windowId, Inventory playerInventory, BlockEntityRandomItemGenerator owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, x + y * 9, 8 + x * 18, 20 + y * 18));
			}
		}

		addAllPlayerSlots();
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
}
