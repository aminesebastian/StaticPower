package theking530.staticpower.tileentities.nonpowered.miner;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.init.ModItems;

public class ContainerMiner extends StaticPowerTileEntityContainer<TileEntityMiner> {

	public ContainerMiner(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityMiner) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerMiner(int windowId, PlayerInventory playerInventory, TileEntityMiner owner) {
		super(ModContainerTypes.MINER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Fuel Slot
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(Items.COAL), 0.3f,getTileEntity().fuelInventory, 0, 18, 32));

		// Drill Bit Slot
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.IronDrillBit), 0.3f, getTileEntity().drillBitInventory, 0, 142, 32));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 32));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
