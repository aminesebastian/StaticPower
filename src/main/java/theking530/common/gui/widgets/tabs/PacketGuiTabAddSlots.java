package theking530.common.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

public class PacketGuiTabAddSlots extends NetworkMessage {
	public static final Logger LOGGER = LogManager.getLogger(PacketGuiTabAddSlots.class);

	protected int windowId;
	protected int[] slotIndecies;
	protected int[] locations;
	protected String[] inventoryComponentNames;
	protected int slotCount;

	private List<Integer> slotIndeciesList;
	private List<Integer> locationsList;
	private List<String> inventoryComponentList;

	public PacketGuiTabAddSlots() {
	}

	public PacketGuiTabAddSlots(int windowId) {
		this.windowId = windowId;
		slotIndeciesList = new ArrayList<Integer>();
		locationsList = new ArrayList<Integer>();
		inventoryComponentList = new ArrayList<String>();
	}

	public PacketGuiTabAddSlots addSlot(InventoryComponent inventoryComponent, int index, int xPos, int yPos) {
		slotCount++;
		slotIndeciesList.add(index);
		locationsList.add(xPos);
		locationsList.add(yPos);
		inventoryComponentList.add(inventoryComponent.getComponentName());
		return this;
	}

	@Override
	public void decode(PacketBuffer buf) {
		windowId = buf.readInt();
		slotCount = buf.readInt();
		slotIndecies = buf.readVarIntArray();
		locations = buf.readVarIntArray();

		// Split out the inventory names.
		String inventories = buf.readString();
		inventoryComponentNames = inventories.split("\\$");
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(windowId);
		buf.writeInt(slotCount);

		slotIndecies = slotIndeciesList.stream().mapToInt(i -> i).toArray();
		locations = locationsList.stream().mapToInt(i -> i).toArray();

		buf.writeVarIntArray(slotIndecies);
		buf.writeVarIntArray(locations);

		String inventories = inventoryComponentList.stream().collect(Collectors.joining("$"));
		buf.writeString(inventories);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			// Make sure the server has them in the same conatiner.
			if (context.get().getSender().openContainer.windowId == windowId && context.get().getSender().openContainer instanceof StaticPowerTileEntityContainer) {
				// Get the container.
				StaticPowerTileEntityContainer<?> container = (StaticPowerTileEntityContainer<?>) context.get().getSender().openContainer;

				// Get the inventory component. If valid, add the slots.
				for (int i = 0; i < inventoryComponentNames.length; i++) {
					// Get the component name.
					String invComp = inventoryComponentNames[i];

					// Get the component.
					InventoryComponent comp = ComponentUtilities.getComponent(InventoryComponent.class, invComp, container.getTileEntity()).orElse(null);

					// If the component is valid, add the slot.
					if (comp != null) {
						addSlotToContainer(comp, container, slotIndecies[i * 1], locations[i * 2], locations[(i * 2) + 1]);
					} else {
						LOGGER.error(String.format("Encountered invalid inventory component with name: %1$s.", invComp));
					}
				}
			}
		});
	}

	protected void addSlotToContainer(InventoryComponent inventoryComponent, StaticPowerTileEntityContainer<?> container, int index, int xPos, int yPos) {
		container.addSlotGeneric(new StaticPowerContainerSlot(inventoryComponent, index, xPos, yPos));
	}
}