package theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
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
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.cables.digistore.crafting.network.PacketMakeDigistoreCraftingRequest;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.container.PacketRevertToParentContainer;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiCraftingAmount extends StaticPowerContainerGui<ContainerCraftingAmount> {
	public static final int COLUMNS = 2;
	public static final int MAX_ROWS = 5;

	public TextInputWidget searchBar;
	public ItemDrawable craftingStack;

	public DrawableWidget<ItemDrawable> craftingStackDrawableWidget;
	public SpriteDrawable missingIngredientDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 8, 8);

	public TextButton plusOne;
	public TextButton plusTen;
	public TextButton minusOne;
	public TextButton minusTen;

	public TextButton leftRecipe;
	public TextButton rightRecipe;

	public TextButton confirm;
	public ScrollBarWidget scrollBar;
	public SpriteButton close;
	public AutoCraftingStepsWidget stepsWidget;
	private int bundleIndex;

	public GuiCraftingAmount(ContainerCraftingAmount container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 165, 198);
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		topPos -= 30;
		bundleIndex = 0;

		missingIngredientDrawable = new SpriteDrawable(StaticPowerSprites.ERROR, 16, 16);

		craftingStack = new ItemDrawable(getCurrentBundle().getOutput());
		registerWidget(craftingStackDrawableWidget = new DrawableWidget<ItemDrawable>(75, 16, 16, 16, craftingStack));
		craftingStackDrawableWidget.setTooltip(getCurrentBundle().getOutput().getHoverName());

		registerWidget(close = new SpriteButton(152, 4, 8, 8, StaticPowerSprites.CLOSE, null, (b, n) -> goBack()));
		registerWidget(minusTen = new TextButton(28, 176, 25, 16, "-10", (b, n) -> modifyCraftingAmmount(-10)));
		registerWidget(minusOne = new TextButton(6, 176, 20, 16, "-1", (b, n) -> modifyCraftingAmmount(-1)));
		registerWidget(plusOne = new TextButton(112, 176, 20, 16, "+1", (b, n) -> modifyCraftingAmmount(1)));
		registerWidget(plusTen = new TextButton(134, 176, 25, 16, "+10", (b, n) -> modifyCraftingAmmount(10)));
		registerWidget(confirm = new TextButton(61, 176, 45, 16, "Confirm", (b, n) -> confirmCraft()));

		registerWidget(
				leftRecipe = (TextButton) new TextButton(60, 18, 14, 14, "<", (b, n) -> modifyBundleIndex(-1)).setTooltip(new TranslatableComponent("gui.staticpower.previous_recipe")));
		registerWidget(
				rightRecipe = (TextButton) new TextButton(92, 18, 14, 14, ">", (b, n) -> modifyBundleIndex(1)).setTooltip(new TranslatableComponent("gui.staticpower.next_recipe")));

		registerWidget(scrollBar = new ScrollBarWidget(146, 53, 119));
		registerWidget(stepsWidget = new AutoCraftingStepsWidget(8, 53, 136, 95, MAX_ROWS, COLUMNS));

		// Since we start at 1 item, disable the ability to decrement until we have gone
		// up at least once.
		minusTen.setEnabled(false);
		minusOne.setEnabled(false);

		// Since we start on bundle 0, we can't go left. That should be disabled. If
		// there are more than one recipe, then the right button can be enabled. If
		// there is only one recipe, hide both buttons.
		if (getMenu().getBundleContainer().getBundles().size() > 1) {
			leftRecipe.setEnabled(false);
			rightRecipe.setEnabled(getMenu().getBundleContainer().getBundles().size() > 1);
		} else {
			leftRecipe.setVisible(false);
			rightRecipe.setVisible(false);
		}

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
				getMenu().updateCraftingResponse(getCurrentBundle().getOutput(), Integer.parseInt(text));
			}
		}));
	}

	private CraftingStepsBundle getCurrentBundle() {
		return getMenu().getBundleContainer().getBundle(bundleIndex);
	}

	@Override
	public void updateData() {
		// Update the request.
		CraftingStepsBundle currentBundle = getCurrentBundle();
		CraftingRequestResponse requestWrapper = new CraftingRequestResponse(-1, currentBundle.getCraftableAmount(), getCurrentBundle().getOutput(), currentBundle);
		stepsWidget.setRequest(requestWrapper);

		// Update the scroll offset.
		scrollBar.setMaxScroll(stepsWidget.getMaxScrollPosition());
		stepsWidget.setScrollPosition(scrollBar.getScrollAmount());

		// Update the confirm state.
		confirm.setEnabled(!getCurrentBundle().getBillOfMaterials().isMissingMaterials());
	}

	protected void confirmCraft() {
		PacketMakeDigistoreCraftingRequest request = new PacketMakeDigistoreCraftingRequest(getMenu().containerId, getCurrentBundle());
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, request);
	}

	protected void modifyBundleIndex(int delta) {
		bundleIndex = SDMath.clamp(bundleIndex + delta, 0, getMenu().getBundleContainer().getBundles().size() - 1);
		leftRecipe.setEnabled(bundleIndex > 0);
		rightRecipe.setEnabled(bundleIndex < getMenu().getBundleContainer().getBundles().size() - 1);
	}

	protected void modifyCraftingAmmount(int delta) {
		if (searchBar.getText().isEmpty()) {
			searchBar.setText(String.valueOf(Math.min(0, delta)));
		} else {
			int currentAmount = Integer.parseInt(searchBar.getText());
			currentAmount = SDMath.clamp(currentAmount + delta, 0, Integer.MAX_VALUE);
			searchBar.setText(String.valueOf(currentAmount));
			minusTen.setEnabled(currentAmount > 10);
			minusOne.setEnabled(currentAmount > 1);
		}
	}

	protected void goBack() {
		PacketRevertToParentContainer request = new PacketRevertToParentContainer(getMenu().containerId);
		StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, request);
	}

	@Override
	protected void onScreenSizeChanged(Vector2D alpha) {
		super.onScreenSizeChanged(alpha);
		confirm.setPosition(61 - (alpha.getY() * 30), imageHeight - 22);
	}

	@Override
	protected void onScreenSizeChangeCompleted() {
		super.onScreenSizeChangeCompleted();
		scrollBar.setVisible(true);
	}
}
