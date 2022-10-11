package theking530.staticcore.gui.widgets.tabs.slottabs;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.blockentities.components.fluids.PacketFluidContainerComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.slots.FluidContainerSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.network.StaticPowerMessageHandler;

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
		super("Fluid Containers", SDColor.EIGHT_BIT_WHITE, 26, 73, new SDColor(1, 0, 1, 1), Items.BUCKET);
		this.container = container;
		this.fluidConatinerInventoryIndecies = new ArrayList<Integer>();
		this.fluidContainerComponent = fluidContainerComponent;
		this.emptyBucketPreview = emptyBucketPreview;
		this.filledBucketPreview = filledBucketPreview;
		this.drawTitle = false;

		// Initialize the button.
		if (fluidContainerComponent.getFluidInteractionMode() == FluidContainerInteractionMode.DRAIN) {
			registerWidget(fillDirectionButton = new SpriteButton(9, 40, 10, 10, StaticPowerSprites.IMPORT, StaticPowerSprites.IMPORT, this::buttonPressed));
			this.topSlotPreview = filledBucketPreview;
			this.bottomSlotPreview = emptyBucketPreview;
			fillDirectionButton.setTooltip(Component.literal("Fill Machine with Container Contents"));
		} else {
			registerWidget(fillDirectionButton = new SpriteButton(9, 40, 10, 10, StaticPowerSprites.EXPORT, StaticPowerSprites.EXPORT, this::buttonPressed));
			this.topSlotPreview = emptyBucketPreview;
			this.bottomSlotPreview = filledBucketPreview;
			fillDirectionButton.setTooltip(Component.literal("Fill Container with Machine Contents"));
		}

		// Disable the default button background rendering.
		fillDirectionButton.setShouldDrawButtonBackground(false);
	}

	@Override
	protected void initialized() {
		// Add the slots.
		topSlot = container.addSlotGeneric(new FluidContainerSlot(fluidContainerComponent, topSlotPreview, 0, (int) (4 + getXPosition()), 0));
		fluidConatinerInventoryIndecies.add(container.slots.size() - 1);

		bottomSlot = container.addSlotGeneric(new FluidContainerSlot(fluidContainerComponent, bottomSlotPreview, 1, (int) (4 + getXPosition()), 0));
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
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBehindItems(matrix, mouseX, mouseY, partialTicks);
		topSlot.y = (int) (this.getYPosition() + 24);
		bottomSlot.y = (int) (this.getYPosition() + 51);
		topSlot.x = (int) (getXPosition() + 6);
		bottomSlot.x = (int) (getXPosition() + 6);
		
		if(!topSlot.getItem().isEmpty() || !bottomSlot.getItem().isEmpty()) {
			showNotificationBadge = true;
		}else {
			showNotificationBadge = false;
		}
	}

	@Override
	protected void onTabOpened() {
		for (int index : fluidConatinerInventoryIndecies) {
			StaticPowerContainerSlot slot = (StaticPowerContainerSlot) container.slots.get(index);
			topSlot.y = (int) (this.getYPosition() + 24);
			bottomSlot.y = (int) (this.getYPosition() + 60);
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
			fillDirectionButton.setRegularTexture(StaticPowerSprites.IMPORT);
			fillDirectionButton.setHoveredTexture(StaticPowerSprites.IMPORT);
			fillDirectionButton.setTooltip(Component.literal("Fill Machine with Container Contents"));
			topSlot.setPreviewItem(new ItemStack(filledBucketPreview));
			bottomSlot.setPreviewItem(new ItemStack(emptyBucketPreview));
		} else {
			fillDirectionButton.setRegularTexture(StaticPowerSprites.EXPORT);
			fillDirectionButton.setHoveredTexture(StaticPowerSprites.EXPORT);
			fillDirectionButton.setTooltip(Component.literal("Fill Container with Machine Contents"));
			topSlot.setPreviewItem(new ItemStack(emptyBucketPreview));
			bottomSlot.setPreviewItem(new ItemStack(filledBucketPreview));
		}
	}
}
