package theking530.staticpower.blockentities.power.turbine;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerTurbine extends StaticPowerTileEntityContainer<BlockEntityTurbine> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerTurbine, GuiTurbine> TYPE = new ContainerTypeAllocator<>("machine_turbine", ContainerTurbine::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiTurbine::new);
		}
	}

	public ContainerTurbine(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityTurbine) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTurbine(int windowId, Inventory playerInventory, BlockEntityTurbine owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().turbineBladeInventory, 0, 80, 32));

		addAllPlayerSlots();
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		if (!moveItemStackTo(stack, 0, 1, false)) {
			return true;
		}
		return false;
	}
}
