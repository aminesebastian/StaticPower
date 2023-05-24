package theking530.staticpower.blockentities.nonpowered.heatexchanger;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;

public class ContainerHeatExchanger extends StaticPowerTileEntityContainer<BlockEntityHeatExchanger> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerHeatExchanger, GuiHeatExchanger> TYPE = new ContainerTypeAllocator<>(
			"machine_heat_exchanger", ContainerHeatExchanger::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiHeatExchanger::new);
		}
	}

	public ContainerHeatExchanger(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityHeatExchanger) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerHeatExchanger(int windowId, Inventory playerInventory, BlockEntityHeatExchanger owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		return false;
	}
}
