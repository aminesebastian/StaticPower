package theking530.staticpower.cables.attachments.digistore.terminal.autocrafting;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.widgets.DrawableWidget;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.scrollbar.ScrollBarWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget.TextAlignment;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.GuiDrawItem;
import theking530.staticcore.utilities.StringUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.digistore.crafting.RequiredAutoCraftingMaterials.RequiredAutoCraftingMaterial;
import theking530.staticpower.cables.digistore.crafting.network.PacketMakeDigistoreCraftingRequest;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiCraftingAmount extends StaticPowerContainerGui<ContainerCraftingAmount> {
	public TextInputWidget searchBar;
	public ItemDrawable craftingStack;
	public DrawableWidget craftingStackDrawableWidget;

	public TextButton plusOne;
	public TextButton plusTen;
	public TextButton minusOne;
	public TextButton minusTen;
	public TextButton previewConfirm;
	public TextButton cancel;
	public ScrollBarWidget scrollBar;
	public SpriteButton close;

	public int ingredientCycleTimer;
	public int ingredientRenderIndex;
	public boolean isOnConfirmStep;

	public GuiCraftingAmount(ContainerCraftingAmount container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 165, 80);
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		isOnConfirmStep = false;
		ingredientCycleTimer = 0;
		ingredientRenderIndex = 0;
		this.guiTop -= 75;
		craftingStack = new ItemDrawable(getContainer().getCraftingResponse().getCraftingItem());
		registerWidget(craftingStackDrawableWidget = new DrawableWidget(75, 16, 16, 16, craftingStack));

		registerWidget(close = new SpriteButton(152, 4, 8, 8, StaticPowerSprites.CLOSE, null, this::buttonPressed));

		registerWidget(minusOne = new TextButton(6, 55, 20, 16, "-1", this::buttonPressed));
		registerWidget(minusTen = new TextButton(28, 55, 25, 16, "-10", this::buttonPressed));
		registerWidget(previewConfirm = new TextButton(61, 55, 45, 16, "Preview", this::buttonPressed));
		registerWidget(cancel = new TextButton(61, 55, 45, 16, "Cancel", this::buttonPressed));
		registerWidget(plusOne = new TextButton(112, 55, 20, 16, "+1", this::buttonPressed));
		registerWidget(plusTen = new TextButton(134, 55, 25, 16, "+10", this::buttonPressed));

		registerWidget(scrollBar = new ScrollBarWidget(146, 40, 85));

		// Disable the cancel button and scroll bar at first.
		cancel.setVisible(false);
		scrollBar.setVisible(false);

		// Set the initial request amount to 1. Don't allow any non-numerics and do not
		// let the number go higher than the max craftable.
		registerWidget(searchBar = new TextInputWidget("1", 63, 36, 40, 12).setAlignment(TextAlignment.CENTER).setTextFilter((text) -> {
			if (!text.isEmpty() && !StringUtilities.isNumber(text)) {
				return false;
			}
			return true;
		}).setTypedCallback((widget, text) -> {
			if (StringUtilities.isNumber(text)) {
				if (Integer.parseInt(text) > getContainer().getMaxCraftableAmount()) {
					widget.setText(String.valueOf(getContainer().getMaxCraftableAmount()));
				}
				getContainer().updateCraftingResponse(getContainer().getCraftingResponse().getCraftingItem(), Integer.parseInt(text));
			}
		}));

		// Set the craftable to 1.
		getContainer().updateCraftingResponse(getContainer().getCraftingResponse().getCraftingItem(), 1);
	}

	@Override
	public void updateData() {
		ingredientCycleTimer++;
		if (ingredientCycleTimer > 20) {
			ingredientCycleTimer = 0;
			ingredientRenderIndex++;
		}
	}

	protected void buttonPressed(StandardButton button, MouseButton mouseButton) {
		if (button == previewConfirm) {
			if (!isOnConfirmStep) {
				setGuiSizeTarget(165, 150);
				cancel.setVisible(true);
				plusOne.setVisible(false);
				plusTen.setVisible(false);
				minusOne.setVisible(false);
				minusTen.setVisible(false);
				searchBar.setVisible(false);
				previewConfirm.setText("Confirm");
			} else {
				PacketMakeDigistoreCraftingRequest request = new PacketMakeDigistoreCraftingRequest(getContainer().windowId, getContainer().getCraftingResponse().getCraftingItem(),
						getContainer().getCraftingResponse().getCraftableAmount());
				StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, request);
			}

		} else if (button == cancel) {
			setDesieredGuiSize(165, 80);
			cancel.setVisible(false);
			plusOne.setVisible(true);
			plusTen.setVisible(true);
			minusOne.setVisible(true);
			minusTen.setVisible(true);
			isOnConfirmStep = false;
			previewConfirm.setPosition(61, 55);
			scrollBar.setVisible(false);
			searchBar.setVisible(true);
			previewConfirm.setText("Preview");
		} else if (button == close) {
			Minecraft.getInstance().displayGuiScreen(null);
		}
	}

	protected void step(RequiredAutoCraftingMaterial material, int xPos, int yPos) {
		Ingredient ing = material.getItem();
		if (!ing.hasNoMatchingItems()) {
			int index = ingredientRenderIndex % ing.getMatchingStacks().length;
			ItemStack itemToDraw = ing.getMatchingStacks()[index];
			GuiDrawItem drawerer = new GuiDrawItem();
			drawerer.drawItem(itemToDraw, 0, 0, xPos + 2, yPos - 2, 1.0f);
		}

		if (material.getAmountToCraft() > 0) {
			GuiDrawUtilities.drawStringWithSize("Required: " + material.getAmountRequired(), xPos + 53, yPos + 3, 0.5f, Color.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawStringWithSize("Stored: " + (material.getAmountRequired() - material.getAmountToCraft()), xPos + 53, yPos + 8, 0.5f, Color.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawColoredRectangle(xPos + 21, yPos + 10, 35.0f, 0.5f, 1.0f, Color.GREY);
			GuiDrawUtilities.drawStringWithSize("To Craft: " + material.getAmountToCraft(), xPos + 53, yPos + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
		} else {
			GuiDrawUtilities.drawStringWithSize("Required: " + material.getAmountRequired(), xPos + 53, yPos + 8, 0.5f, Color.EIGHT_BIT_WHITE, true);
		}

		GuiDrawUtilities.drawColoredRectangle(xPos, yPos + 18, 67.0f, 0.75f, 1.0f, Color.GREY);
	}

	@Override
	protected void onScreenSizeChanged(Vector2D alpha) {
		super.onScreenSizeChanged(alpha);
		previewConfirm.setPosition(61 - (alpha.getY() * 30), ySize - 22);
		cancel.setPosition(61 + (alpha.getY() * 30), ySize - 22);
	}

	@Override
	protected void onScreenSizeChangeCompleted() {
		super.onScreenSizeChangeCompleted();
		isOnConfirmStep = true;
		scrollBar.setVisible(true);
	}

	@Override
	protected void drawForegroundExtras(float partialTicks, int mouseX, int mouseY) {
		super.drawForegroundExtras(partialTicks, mouseX, mouseY);

		if (!isOnConfirmStep) {
			return;
		}

		GuiDrawUtilities.drawSlot(guiLeft + 8, guiTop + 40, 136, 85);
		GuiDrawUtilities.drawColoredRectangle(guiLeft + 75, guiTop + 40, 1.0f, 85, 1.0f, Color.GREY);

		for (int i = 0; i < getContainer().getCraftingResponse().getBillOfMaterials().getMaterials().size(); i++) {
			int column = Math.floorMod(i, 2);
			int row = i / 2;
			RequiredAutoCraftingMaterial material = getContainer().getCraftingResponse().getBillOfMaterials().getMaterials().get(i);
			step(material, guiLeft + 8 + (column * 68), guiTop + 45 + (row * 24));
		}
	}
}
