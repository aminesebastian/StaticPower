package theking530.staticcore.gui.widgets.tabs.slottabs;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.gui.widgets.tabs.PacketGuiTabAddSlots;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.slots.FluidContainerSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.fluids.PacketFluidContainerComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

@OnlyIn(Dist.CLIENT)
public class GuiFluidContainerTab extends BaseGuiTab {
	private final FluidContainerInventoryComponent fluidContainerComponent;
	private final List<Integer> fluidConatinerInventoryIndecies;
	private final StaticPowerContainer container;
	private Item filledBucketPreview;
	private Item emptyBucketPreview;
	private Item topSlotPreview;
	private Item bottomSlotPreview;
	private SpriteButton fillDirectionButton;
	private StaticPowerContainerSlot topSlot;
	private StaticPowerContainerSlot bottomSlot;

	public GuiFluidContainerTab(StaticPowerContainer container, FluidContainerInventoryComponent fluidContainerComponent) {
		this(container, fluidContainerComponent, Items.BUCKET, Items.WATER_BUCKET);
	}

	public GuiFluidContainerTab(StaticPowerContainer container, FluidContainerInventoryComponent fluidContainerComponent, Item emptyBucketPreview, Item filledBucketPreview) {
		super("Fluid Containers", Color.EIGHT_BIT_WHITE, 0, 57, GuiTextures.MAGENTA_TAB, Items.BUCKET);
		this.container = container;
		this.fluidConatinerInventoryIndecies = new ArrayList<Integer>();
		this.fluidContainerComponent = fluidContainerComponent;
		this.emptyBucketPreview = emptyBucketPreview;
		this.filledBucketPreview = filledBucketPreview;
		this.drawTitle = false;

		// Initialize the button.
		if (fluidContainerComponent.getFluidInteractionMode() == FluidContainerInteractionMode.DRAIN) {
			this.widgetContainer.registerWidget(fillDirectionButton = new SpriteButton(2, 40, 20, 20, StaticPowerSprites.EMPTY_BUCKET, StaticPowerSprites.EMPTY_BUCKET_HOVERED, this::buttonPressed));
			this.topSlotPreview = filledBucketPreview;
			this.bottomSlotPreview = emptyBucketPreview;
			fillDirectionButton.setTooltip(new TextComponent("Fill Machine with Container Contents"));
		} else {
			this.widgetContainer.registerWidget(fillDirectionButton = new SpriteButton(2, 40, 20, 20, StaticPowerSprites.FILL_BUCKET, StaticPowerSprites.FILL_BUCKET_HOVERED, this::buttonPressed));
			this.topSlotPreview = emptyBucketPreview;
			this.bottomSlotPreview = filledBucketPreview;
			fillDirectionButton.setTooltip(new TextComponent("Fill Container with Machine Contents"));
		}

		// Disable the default button background rendering.
		fillDirectionButton.setShouldDrawButtonBackground(false);
	}

	@Override
	protected void initialized(int tabXPosition, int tabYPosition) {
		// Add the slots.
		topSlot = container.addSlotGeneric(new FluidContainerSlot(fluidContainerComponent, topSlotPreview, 0, 4 + xPosition, 0));
		fluidConatinerInventoryIndecies.add(container.slots.size() - 1);

		bottomSlot = container.addSlotGeneric(new FluidContainerSlot(fluidContainerComponent, bottomSlotPreview, 1, 4 + xPosition, 0));
		fluidConatinerInventoryIndecies.add(container.slots.size() - 1);

		PacketGuiTabAddSlots msg = new PacketGuiTabAddSlots(container.containerId);
		msg.addSlot((InventoryComponent) fluidContainerComponent, 0, 0, 0);
		msg.addSlot((InventoryComponent) fluidContainerComponent, 1, 0, 0);

		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);

		// Initialize as closed.
		onTabClosing();
	}

	@Override
	protected void renderBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBehindItems(matrix, mouseX, mouseY, partialTicks);
		topSlot.y = this.yPosition + 24;
		bottomSlot.y = this.yPosition + 60;
	}

	@Override
	protected void onTabOpened() {
		for (int index : fluidConatinerInventoryIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.slots.get(index);
			topSlot.y = this.yPosition + 24;
			bottomSlot.y = this.yPosition + 60;
			slot.setEnabledState(true);
		}
	}

	@Override
	protected void onTabClosing() {
		for (int index : fluidConatinerInventoryIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.slots.get(index);
			slot.setEnabledState(false);
		}
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		// Update the mode.
		fluidContainerComponent.setMode(fluidContainerComponent.getFluidInteractionMode().getInverseMode());

		// Create and send a packet to the server with the updated mode.
		PacketFluidContainerComponent packet = new PacketFluidContainerComponent(fluidContainerComponent, fluidContainerComponent.getFluidInteractionMode(), fluidContainerComponent.getPos());
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(packet);

		// Update the button and the slot icons.
		if (fluidContainerComponent.getFluidInteractionMode() == FluidContainerInteractionMode.DRAIN) {
			fillDirectionButton.setRegularTexture(StaticPowerSprites.EMPTY_BUCKET);
			fillDirectionButton.setHoveredTexture(StaticPowerSprites.EMPTY_BUCKET_HOVERED);
			fillDirectionButton.setTooltip(new TextComponent("Fill Machine with Container Contents"));
			topSlot.setPreviewItem(new ItemStack(filledBucketPreview));
			bottomSlot.setPreviewItem(new ItemStack(emptyBucketPreview));
		} else {
			fillDirectionButton.setRegularTexture(StaticPowerSprites.FILL_BUCKET);
			fillDirectionButton.setHoveredTexture(StaticPowerSprites.FILL_BUCKET_HOVERED);
			fillDirectionButton.setTooltip(new TextComponent("Fill Container with Machine Contents"));
			topSlot.setPreviewItem(new ItemStack(emptyBucketPreview));
			bottomSlot.setPreviewItem(new ItemStack(filledBucketPreview));
		}
	}
}
