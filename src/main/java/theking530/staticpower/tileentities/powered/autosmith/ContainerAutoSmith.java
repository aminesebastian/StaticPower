package theking530.staticpower.tileentities.powered.autosmith;

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
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerAutoSmith extends StaticPowerTileEntityContainer<TileEntityAutoSmith> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerAutoSmith, GuiAutoSmith> TYPE = new ContainerTypeAllocator<>("machine_auto_smith", ContainerAutoSmith::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiAutoSmith::new);
		}
	}

	public ContainerAutoSmith(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityAutoSmith) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAutoSmith(int windowId, PlayerInventory playerInventory, TileEntityAutoSmith owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 80, 18));
		
		// Modifier
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 1, 110, 38));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 60));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		return super.playerItemShiftClicked(stack, player, slot, slotIndex);
	}
}
