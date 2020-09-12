package theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.DrawableWidget;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.scrollbar.ScrollBarWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget;
import theking530.staticcore.gui.widgets.textinput.TextInputWidget.TextAlignment;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.StringUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.digistore.crafting.network.PacketMakeDigistoreCraftingRequest;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.container.PacketRevertToParentContainer;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiCraftingAmount extends StaticPowerContainerGui<ContainerCraftingAmount> {
	public static final int COLUMNS = 2;
	public static final int MAX_ROWS = 4;

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
	public AutoCraftingStepsWidget stepsWidget;

	public GuiCraftingAmount(ContainerCraftingAmount container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 165, 174);
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		guiTop -= 30;

		missingIngredientDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 16, 16);

		craftingStack = new ItemDrawable(getContainer().getCraftingResponse().getCraftingItem());
		registerWidget(craftingStackDrawableWidget = new DrawableWidget(75, 16, 16, 16, craftingStack));
		craftingStackDrawableWidget.setTooltip(getContainer().getCraftingResponse().getCraftingItem().getDisplayName());

		registerWidget(close = new SpriteButton(152, 4, 8, 8, StaticPowerSprites.CLOSE, null, (b, n) -> goBack()));
		registerWidget(minusTen = new TextButton(28, 152, 25, 16, "-10", (b, n) -> modifyCraftingAmmount(-10)));
		registerWidget(minusOne = new TextButton(6, 152, 20, 16, "-1", (b, n) -> modifyCraftingAmmount(-1)));
		registerWidget(plusOne = new TextButton(112, 152, 20, 16, "+1", (b, n) -> modifyCraftingAmmount(1)));
		registerWidget(plusTen = new TextButton(134, 152, 25, 16, "+10", (b, n) -> modifyCraftingAmmount(10)));
		registerWidget(confirm = new TextButton(61, 152, 45, 16, "Confirm", (b, n) -> confirmCraft()));
		registerWidget(scrollBar = new ScrollBarWidget(146, 53, 95));
		registerWidget(stepsWidget = new AutoCraftingStepsWidget(8, 53, 136, 95, MAX_ROWS, COLUMNS));

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
		// Update the request.
		stepsWidget.setRequest(getContainer().getCraftingResponse());

		// Update the scroll offset.
		scrollBar.setMaxScroll(stepsWidget.getMaxScrollPosition());
		stepsWidget.setScrollPosition(scrollBar.getScrollAmount());

		// Update the confirm state.
		confirm.setEnabled(!getContainer().getCraftingResponse().getBillOfMaterials().isMissingMaterials());
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
}
