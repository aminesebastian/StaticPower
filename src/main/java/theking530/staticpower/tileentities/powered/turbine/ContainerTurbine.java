package theking530.staticpower.tileentities.powered.turbine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerTurbine extends StaticPowerTileEntityContainer<TileEntityTurbine> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerTurbine, GuiTurbine> TYPE = new ContainerTypeAllocator<>("machine_turbine", ContainerTurbine::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiTurbine::new);
		}
	}

	public ContainerTurbine(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityTurbine) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTurbine(int windowId, PlayerInventory playerInventory, TileEntityTurbine owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().turbineBladeInventory, 0, 80, 32));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		if (!mergeItemStack(stack, 0, 1, false)) {
			return true;
		}
		return false;
	}
}
