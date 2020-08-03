package theking530.common.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.InventoryComponent;

public class PacketGuiTabAddSlots extends NetworkMessage {
	protected String inventoryComponentName;
	protected int windowId;
	protected int[] slotIndecies;
	protected int[] locations;
	protected int slotCount;

	private List<Integer> slotIndeciesList;
	private List<Integer> locationsList;

	public PacketGuiTabAddSlots() {
	}

	public PacketGuiTabAddSlots(InventoryComponent inventory, int windowId) {
		inventoryComponentName = inventory.getComponentName();
		this.windowId = windowId;
		slotIndeciesList = new ArrayList<Integer>();
		locationsList = new ArrayList<Integer>();
	}

	public PacketGuiTabAddSlots addSlot(int index, int xPos, int yPos) {
		slotCount++;
		slotIndeciesList.add(index);
		locationsList.add(xPos);
		locationsList.add(yPos);
		return this;
	}

	@Override
	public void decode(PacketBuffer buf) {
		inventoryComponentName = buf.readString();
		windowId = buf.readInt();
		slotCount = buf.readInt();
		slotIndecies = buf.readVarIntArray();
		locations = buf.readVarIntArray();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeString(inventoryComponentName);
		buf.writeInt(windowId);
		buf.writeInt(slotCount);

		slotIndecies = slotIndeciesList.stream().mapToInt(i -> i).toArray();
		locations = locationsList.stream().mapToInt(i -> i).toArray();

		buf.writeVarIntArray(slotIndecies);
		buf.writeVarIntArray(locations);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			// Make sure the server has them in the same conatiner.
			if (context.get().getSender().openContainer.windowId == windowId && context.get().getSender().openContainer instanceof StaticPowerTileEntityContainer) {
				// Get the container.
				StaticPowerTileEntityContainer<?> container = (StaticPowerTileEntityContainer<?>) context.get().getSender().openContainer;

				// Get the inventory component. If valid, add the slots.
				ComponentUtilities.getComponent(InventoryComponent.class, inventoryComponentName, container.getTileEntity()).ifPresent(comp -> {
					addSlotsToContainer(comp, container);
				});
			}
		});
	}

	protected void addSlotsToContainer(InventoryComponent inventoryComponent, StaticPowerTileEntityContainer<?> container) {
		for (int i = 0; i < slotCount; i++) {
			container.addSlot(new StaticPowerContainerSlot(inventoryComponent, slotIndecies[i], locations[i * 2], locations[(i * 2) + 1]));
		}
	}
}