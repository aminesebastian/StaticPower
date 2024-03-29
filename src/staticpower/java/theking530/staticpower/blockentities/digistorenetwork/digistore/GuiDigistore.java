package theking530.staticpower.blockentities.digistorenetwork.digistore;

import java.util.List;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag.Default;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.utilities.MetricConverter;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiDigistore extends StaticCoreBlockEntityScreen<ContainerDigistore, BlockEntityDigistore> {

	private GuiInfoTab infoTab;
	private TextButton lockedButton;
	private final IDigistoreInventory inventory;

	public GuiDigistore(ContainerDigistore container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 154);
		inventory = getTileEntity().inventory;
	}

	@Override
	public void initializeGui() {
		registerWidget(lockedButton = new TextButton(10, 23, 50, 20, "Locked", this::buttonPressed));
		lockedButton.setToggleable(true);
		lockedButton.setText(getTileEntity().isLocked() ? "Locked" : "Unlocked");
		lockedButton.setToggled(getTileEntity().isLocked());

		getTabManager().registerTab(infoTab = new GuiInfoTab(getTileEntity().getDisplayName().getString(), 100));
	}

	public void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (getTileEntity() != null) {
			getTileEntity().setLocked(!getTileEntity().isLocked());
			button.setToggled(getTileEntity().isLocked());
			lockedButton.setText(getTileEntity().isLocked() ? "Locked" : "Unlocked");

			NetworkMessage msg = new PacketLockDigistore(getTileEntity().isLocked(), getTileEntity().getBlockPos());
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
		}
	}

	@Override
	public void updateData() {
		// Update the info tab.
		infoTab.clear();
		infoTab.addLine("desc", Component.literal("Stores a large amount of a single item."));
		infoTab.addLineBreak();

		// Pass the itemstack count through the metric converter.
		MetricConverter count = new MetricConverter(inventory.getItemCapacity());
		infoTab.addKeyValueLine("max", Component.literal("Max Items"), Component.literal(ChatFormatting.WHITE.toString()).append(Component.literal(count.getValueAsString(true))), ChatFormatting.RED);
	}

	@Override
	protected void drawForegroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawForegroundExtras(stack, partialTicks, mouseX, mouseY);
		if (inventory.getItemCapacity() > 0) {
			if (mouseX >= leftPos + 76 && mouseX <= leftPos + 100 && mouseY >= topPos + 21 && mouseY <= topPos + 45) {
				GuiDrawUtilities.drawRectangle(stack, 18, 18, 79, 19, 1.0f, new SDColor(200, 200, 200, 200).fromEightBitToFloat());
			}
		}
	}

	@Override
	protected void getExtraTooltips(List<Component> tooltips, PoseStack stack, int mouseX, int mouseY) {
		super.getExtraTooltips(tooltips, stack, mouseX, mouseY);
		if (inventory.getItemCapacity() > 0) {
			if (mouseX >= leftPos + 76 && mouseX <= leftPos + 100 && mouseY >= topPos + 21 && mouseY <= topPos + 45) {
				tooltips.addAll(inventory.getDigistoreStack(0).getStoredItem().getTooltipLines(Minecraft.getInstance().player, Default.NORMAL));
			}
		}
	}

	@Override
	protected void drawBackgroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);

		// Draw the massive digistore slot.
		drawEmptySlot(stack, 78, 18, 20, 20);

		// Draw the item.
		if (inventory.getCurrentUniqueItemTypeCount() > 0) {
			Lighting.setupForFlatItems();
			GuiDrawUtilities.drawItem(stack, inventory.getDigistoreStack(0).getStoredItem(), 80, 21, 1.0f);

			// Pass the itemstack count through the metric converter.
			MetricConverter count = new MetricConverter(inventory.getTotalContainedCount());

			// Draw the item count string.
			GuiDrawUtilities.drawString(stack, count.getValueAsString(true), 98, 37, 0.0f, 0.5f, SDColor.EIGHT_BIT_WHITE, true);
		}
	}
}
