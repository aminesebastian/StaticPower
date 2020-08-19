package theking530.common.gui.widgets.tabs.slottabs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.widgets.button.SpriteButton;
import theking530.common.gui.widgets.button.StandardButton;
import theking530.common.gui.widgets.button.StandardButton.MouseButton;
import theking530.common.gui.widgets.tabs.BaseGuiTab;
import theking530.common.gui.widgets.tabs.PacketGuiTabAddSlots;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.container.StaticPowerContainer;
import theking530.staticpower.client.container.slots.FluidContainerSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.fluids.FluidContainerComponent;
import theking530.staticpower.tileentities.components.fluids.PacketFluidContainerComponent;
import theking530.staticpower.tileentities.components.fluids.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

public class GuiFluidContainerTab extends BaseGuiTab {
	private final FluidContainerComponent fluidContainerComponent;
	private final List<Integer> fluidConatinerInventoryIndecies;
	private final StaticPowerContainer container;
	private Item filledBucketPreview;
	private Item emptyBucketPreview;
	private Item topSlotPreview;
	private Item bottomSlotPreview;
	private SpriteButton fillDirectionButton;
	private StaticPowerContainerSlot topSlot;
	private StaticPowerContainerSlot bottomSlot;

	public GuiFluidContainerTab(StaticPowerContainer container, FluidContainerComponent fluidContainerComponent) {
		this(container, fluidContainerComponent, Items.BUCKET, Items.WATER_BUCKET);
	}

	public GuiFluidContainerTab(StaticPowerContainer container, FluidContainerComponent fluidContainerComponent, Item emptyBucketPreview, Item filledBucketPreview) {
		super("Fluid Containers", 0, 57, GuiTextures.MAGENTA_TAB, Items.BUCKET);
		this.container = container;
		this.fluidConatinerInventoryIndecies = new ArrayList<Integer>();
		this.fluidContainerComponent = fluidContainerComponent;
		this.emptyBucketPreview = emptyBucketPreview;
		this.filledBucketPreview = filledBucketPreview;

		// Initialize the button.
		if (fluidContainerComponent.getMode() == FluidContainerInteractionMode.DRAIN) {
			this.widgetContainer.registerWidget(fillDirectionButton = new SpriteButton(3, 40, 20, 20, StaticPowerSprites.EMPTY_BUCKET, StaticPowerSprites.EMPTY_BUCKET_HOVERED, this::buttonPressed));
			this.topSlotPreview = filledBucketPreview;
			this.bottomSlotPreview = emptyBucketPreview;
			fillDirectionButton.setTooltip(new StringTextComponent("Fill Machine with Container Contents"));
		} else {
			this.widgetContainer.registerWidget(fillDirectionButton = new SpriteButton(3, 40, 20, 20, StaticPowerSprites.FILL_BUCKET, StaticPowerSprites.FILL_BUCKET_HOVERED, this::buttonPressed));
			this.topSlotPreview = emptyBucketPreview;
			this.bottomSlotPreview = filledBucketPreview;
			fillDirectionButton.setTooltip(new StringTextComponent("Fill Container with Machine Contents"));
		}

		// Disable the default button background rendering.
		fillDirectionButton.setShouldDrawButtonBackground(false);
	}

	@Override
	protected void initialized(int tabXPosition, int tabYPosition) {
		// Add the slots.
		topSlot = container.addSlotGeneric(new FluidContainerSlot(fluidContainerComponent.getPrimaryInventory(), topSlotPreview, fluidContainerComponent.getPrimarySlot(), -18, 23 + this.guiYOffset));
		fluidConatinerInventoryIndecies.add(container.inventorySlots.size() - 1);

		bottomSlot = container
				.addSlotGeneric(new FluidContainerSlot(fluidContainerComponent.getSecondaryInventory(), bottomSlotPreview, fluidContainerComponent.getSecondarySlot(), -18, 60 + this.guiYOffset));
		fluidConatinerInventoryIndecies.add(container.inventorySlots.size() - 1);

		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(container.windowId);
		msg.addSlot((InventoryComponent) fluidContainerComponent.getPrimaryInventory(), 0, -18, 23);
		msg.addSlot((InventoryComponent) fluidContainerComponent.getSecondaryInventory(), 1, -18, 45);

		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

		// Initialize as closed.
		onTabClosing();
	}

	@Override
	protected void onTabOpened() {
		for (int index : fluidConatinerInventoryIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(index);
			slot.setEnabledState(true);
		}
	}

	@Override
	protected void onTabClosing() {
		for (int index : fluidConatinerInventoryIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.inventorySlots.get(index);
			slot.setEnabledState(false);
		}
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		// Update the mode.
		fluidContainerComponent.setMode(fluidContainerComponent.getMode().getInverseMode());

		// Create and send a packet to the server with the updated mode.
		PacketFluidContainerComponent packet = new PacketFluidContainerComponent(fluidContainerComponent, fluidContainerComponent.getMode(), fluidContainerComponent.getPos());
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(packet);

		// Update the button and the slot icons.
		if (fluidContainerComponent.getMode() == FluidContainerInteractionMode.DRAIN) {
			fillDirectionButton.setRegularTexture(StaticPowerSprites.EMPTY_BUCKET);
			fillDirectionButton.setHoveredTexture(StaticPowerSprites.EMPTY_BUCKET_HOVERED);
			fillDirectionButton.setTooltip(new StringTextComponent("Fill Machine with Container Contents"));
			topSlot.setPreviewItem(new ItemStack(filledBucketPreview));
			bottomSlot.setPreviewItem(new ItemStack(emptyBucketPreview));
		} else {
			fillDirectionButton.setRegularTexture(StaticPowerSprites.FILL_BUCKET);
			fillDirectionButton.setHoveredTexture(StaticPowerSprites.FILL_BUCKET_HOVERED);
			fillDirectionButton.setTooltip(new StringTextComponent("Fill Container with Machine Contents"));
			topSlot.setPreviewItem(new ItemStack(emptyBucketPreview));
			bottomSlot.setPreviewItem(new ItemStack(filledBucketPreview));
		}
	}
}
