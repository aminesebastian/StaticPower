package theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.DrawableWidget;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.scrollbar.ScrollBarWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget.TextAlignment;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.GuiDrawItem;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.StringUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.digistore.crafting.RequiredAutoCraftingMaterials;
import theking530.staticpower.cables.digistore.crafting.RequiredAutoCraftingMaterials.RequiredAutoCraftingMaterial;
import theking530.staticpower.cables.digistore.crafting.network.PacketMakeDigistoreCraftingRequest;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.container.PacketRevertToParentContainer;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiCraftingAmount extends StaticPowerContainerGui<ContainerCraftingAmount> {
	public static final int COLUMNS = 2;
	public static final int MAX_ROWS = 5;

	public TextInputWidget searchBar;
	public ItemDrawable craftingStack;

	public DrawableWidget craftingStackDrawableWidget;
	public SpriteDrawable missingIngredientDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 8, 8);

	public TextButton plusOne;
	public TextButton plusTen;
	public TextButton minusOne;
	public TextButton minusTen;
	public TextButton confirm;
	public ScrollBarWidget scrollBar;
	public SpriteButton close;

	public int ingredientCycleTimer;
	public int ingredientRenderIndex;

	public GuiCraftingAmount(ContainerCraftingAmount container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 165, 185);
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		guiTop -= 40;

		ingredientCycleTimer = 0;
		ingredientRenderIndex = 0;

		missingIngredientDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 16, 16);

		craftingStack = new ItemDrawable(getContainer().getCraftingResponse().getCraftingItem());
		registerWidget(craftingStackDrawableWidget = new DrawableWidget(75, 16, 16, 16, craftingStack));
		craftingStackDrawableWidget.setTooltip(getContainer().getCraftingResponse().getCraftingItem().getDisplayName());

		registerWidget(close = new SpriteButton(152, 4, 8, 8, StaticPowerSprites.CLOSE, null, (b, n) -> goBack()));
		registerWidget(minusTen = new TextButton(28, 163, 25, 16, "-10", (b, n) -> modifyCraftingAmmount(-10)));
		registerWidget(minusOne = new TextButton(6, 163, 20, 16, "-1", (b, n) -> modifyCraftingAmmount(-1)));
		registerWidget(plusOne = new TextButton(112, 163, 20, 16, "+1", (b, n) -> modifyCraftingAmmount(1)));
		registerWidget(plusTen = new TextButton(134, 163, 25, 16, "+10", (b, n) -> modifyCraftingAmmount(10)));
		registerWidget(confirm = new TextButton(61, 163, 45, 16, "Confirm", (b, n) -> confirmCraft()));
		registerWidget(scrollBar = new ScrollBarWidget(146, 53, 105));

		// Set the initial request amount to 1. Don't allow any non-numerics and do not
		// let the number go higher than the max craftable.
		registerWidget(searchBar = new TextInputWidget("1", 63, 36, 40, 12).setAlignment(TextAlignment.CENTER).setTextFilter((text) -> {
			if (!text.isEmpty() && !StringUtilities.isNumber(text)) {
				return false;
			}
			return true;
		}).setTypedCallback((widget, text) -> {
			// If the input was a number.
			if (StringUtilities.isNumber(text)) {
				// Make sure we don't let the user put in 0 or fewer.
				if (Integer.parseInt(text) <= 0) {
					widget.setText("1");
				}

				// Recalculate the crafting response.
				getContainer().updateCraftingResponse(getContainer().getCraftingResponse().getCraftingItem(), Integer.parseInt(text));
			}
		}));
	}

	@Override
	public void updateData() {
		ingredientCycleTimer++;
		if (ingredientCycleTimer > 20) {
			ingredientCycleTimer = 0;
			ingredientRenderIndex++;
		}
	}

	protected void confirmCraft() {
		PacketMakeDigistoreCraftingRequest request = new PacketMakeDigistoreCraftingRequest(getContainer().windowId, getContainer().getCraftingResponse().getCraftingItem(),
				getContainer().getCraftingResponse().getCraftableAmount());
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, request);
	}

	protected void modifyCraftingAmmount(int delta) {
		if (searchBar.getText().isEmpty()) {
			searchBar.setText(String.valueOf(Math.min(0, delta)));
		} else {
			int currentAmount = Integer.parseInt(searchBar.getText());
			currentAmount = SDMath.clamp(currentAmount + delta, 0, Integer.MAX_VALUE);
			searchBar.setText(String.valueOf(currentAmount));
		}
	}

	protected void goBack() {
		PacketRevertToParentContainer request = new PacketRevertToParentContainer(getContainer().windowId);
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, request);
	}

	protected void drawStep(RequiredAutoCraftingMaterial material, int xPos, int yPos, int mouseX, int mouseY) {
		// Get the ingredient.
		Ingredient ing = material.getItem();

		// If it has items, draw the item (rotate through all the itemstacks in the
		// ingredient).
		if (!ing.hasNoMatchingItems()) {
			int index = ingredientRenderIndex % ing.getMatchingStacks().length;
			ItemStack itemToDraw = ing.getMatchingStacks()[index];
			GuiDrawItem drawerer = new GuiDrawItem();
			drawerer.drawItem(itemToDraw, 0, 0, xPos + 2, yPos - 2, 1.0f);

			// This is a bad way to do this, don't do it. I am being lazy and should fix
			// this later. Should use the tooltip method.
			if (mouseX >= xPos && mouseX <= xPos + 16 && mouseY >= yPos && mouseY <= yPos + 16) {
				renderTooltip(itemToDraw, mouseX, mouseY);
			}
		}

		// If we're missing any items, render the missing scenario. If we have to craft,
		// render the crafting scenario. If we have all the items, render the we have
		// all
		// the items scenario.
		if (material.getMissingAmount() > 0) {
			GuiDrawUtilities.drawStringWithSize("Required: " + material.getAmountRequired(), xPos + 53, yPos + 3, 0.5f, Color.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawStringWithSize("Stored: " + material.getAmountStored(), xPos + 53, yPos + 8, 0.5f, Color.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawColoredRectangle(xPos + 21, yPos + 10, 35.0f, 0.5f, 1.0f, Color.GREY);
			GuiDrawUtilities.drawStringWithSize("Missing: " + material.getMissingAmount(), xPos + 53.5f, yPos + 15.5f, 0.5f, new Color(75.0f, 25.0f, 0.0f, 255.0f), false);
			GuiDrawUtilities.drawStringWithSize("Missing: " + material.getMissingAmount(), xPos + 53, yPos + 15, 0.5f, new Color(255.0f, 150.0f, 50.0f, 255.0f), false);
			missingIngredientDrawable.draw(xPos + 2, yPos - 1.5f);
		} else if (material.getAmountToCraft() > 0) {
			GuiDrawUtilities.drawStringWithSize("Required: " + material.getAmountRequired(), xPos + 53, yPos + 3, 0.5f, Color.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawStringWithSize("Stored: " + material.getAmountStored(), xPos + 53, yPos + 8, 0.5f, Color.EIGHT_BIT_WHITE, true);
			GuiDrawUtilities.drawColoredRectangle(xPos + 21, yPos + 10, 35.0f, 0.5f, 1.0f, Color.GREY);
			GuiDrawUtilities.drawStringWithSize("To Craft: " + material.getAmountToCraft(), xPos + 53, yPos + 15, 0.5f, Color.EIGHT_BIT_WHITE, true);
		} else {
			GuiDrawUtilities.drawStringWithSize("Required: " + material.getAmountRequired(), xPos + 53, yPos + 7, 0.5f, Color.EIGHT_BIT_WHITE, true);
		}

		// Draw the bottom divider.
		GuiDrawUtilities.drawColoredRectangle(xPos, yPos + 18, 67.0f, 0.75f, 1.0f, Color.GREY);
	}

	@Override
	protected void onScreenSizeChanged(Vector2D alpha) {
		super.onScreenSizeChanged(alpha);
		confirm.setPosition(61 - (alpha.getY() * 30), ySize - 22);
	}

	@Override
	protected void onScreenSizeChangeCompleted() {
		super.onScreenSizeChangeCompleted();
		scrollBar.setVisible(true);
	}

	@Override
	protected void drawBehindItems(float partialTicks, int mouseX, int mouseY) {
		super.drawForegroundExtras(partialTicks, mouseX, mouseY);

		confirm.setEnabled(!getContainer().getCraftingResponse().getBillOfMaterials().isMissingMaterials());

		GuiDrawUtilities.drawSlot(guiLeft + 8, guiTop + 53, 136, 105);
		GuiDrawUtilities.drawColoredRectangle(guiLeft + 75, guiTop + 53, 1.0f, 105, 1.0f, Color.GREY);

		// Get the required materials.
		RequiredAutoCraftingMaterials materials = getContainer().getCraftingResponse().getBillOfMaterials();

		// Update the scroll offset.
		int rows = (int) Math.ceil((double) materials.getMaterials().size() / COLUMNS);
		scrollBar.setMaxScroll(Math.max(0, rows - MAX_ROWS));

		// Capture the min and max indicies.
		int start = 0 + (scrollBar.getScrollAmount() * COLUMNS);
		int end = start + 10;
		end = Math.min(end, materials.getMaterials().size());

		// Draw the steps.
		for (int i = start; i < end; i++) {
			int column = Math.floorMod(i - start, COLUMNS);
			int row = (i - start) / COLUMNS;
			RequiredAutoCraftingMaterial material = materials.getMaterials().get(i);
			drawStep(material, guiLeft + 8 + (column * 68), guiTop + 58 + (row * 24), mouseX, mouseY);
		}
	}
}
