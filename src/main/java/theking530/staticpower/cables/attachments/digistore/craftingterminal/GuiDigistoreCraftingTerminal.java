package theking530.staticpower.cables.attachments.digistore.craftingterminal;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.EntityRenderWidget;
import theking530.staticcore.gui.widgets.GuiIslandWidget;
import theking530.staticcore.gui.widgets.button.FakeSlotButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.button.TextButton;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminalHistory.DigistoreCraftingTerminalHistoryEntry;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractGuiDigistoreTerminal;
import theking530.staticpower.entities.player.datacapability.CapabilityStaticPowerPlayerData;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiDigistoreCraftingTerminal extends AbstractGuiDigistoreTerminal<ContainerDigistoreCraftingTerminal, DigistoreCraftingTerminal> {
	private ArrowProgressBar progressBar;
	private EntityRenderWidget entityRenderer;
	private TextButton clearCurrentRecipe;
	private List<FakeSlotButton> previousRecipes;

	public GuiDigistoreCraftingTerminal(ContainerDigistoreCraftingTerminal container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 270);
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		this.previousRecipes = new ArrayList<FakeSlotButton>();
		registerWidget(progressBar = (ArrowProgressBar) new ArrowProgressBar(118, 138).disableProgressTooltip());
		registerWidget(entityRenderer = new EntityRenderWidget(10, 115, 42, 58, this.minecraft.player));
		searchBar.setSize(70, 12);
		searchBar.setPosition(98, 6);

		GuiInfoTab tab = new GuiInfoTab(100);
		tab.setTabSide(TabSide.RIGHT);
		getTabManager().setPosition(0, -5);
		getTabManager().registerTab(tab);

		// Add island for previous crafting recipes.
		registerWidget(new GuiIslandWidget(-24, 190, 30, 80));

		// Create the widegts for the previous crafts and register them.
		previousRecipes.add(new FakeSlotButton(ItemStack.EMPTY, -18, 195, this::onPreviousRecipeButtonPressed));
		previousRecipes.add(new FakeSlotButton(ItemStack.EMPTY, -18, 213, this::onPreviousRecipeButtonPressed));
		previousRecipes.add(new FakeSlotButton(ItemStack.EMPTY, -18, 231, this::onPreviousRecipeButtonPressed));
		previousRecipes.add(new FakeSlotButton(ItemStack.EMPTY, -18, 249, this::onPreviousRecipeButtonPressed));
		for (FakeSlotButton fakeSlot : previousRecipes) {
			registerWidget(fakeSlot);
		}

		// Add clear button.
		registerWidget(clearCurrentRecipe = new TextButton(118, 119, 8, 8, "x", (button, mouseButton) -> {
			this.getContainer().clearCraftingSlots(playerInventory.player);
		}));
	}

	protected void onPreviousRecipeButtonPressed(StandardButton button, MouseButton mouseButton) {
		DigistoreCraftingTerminalHistoryEntry entry = button.<DigistoreCraftingTerminalHistoryEntry>getData();
		if (entry != null) {
			// Send the packet to the server to update the crafting grid.
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new PacketRestorePreviousCraftingRecipe(container.windowId, entry.recipe));
		}
	}

	@Override
	protected Vector2D getContainerLabelDrawLocation() {
		return new Vector2D(8, 8);
	}

	@Override
	protected boolean shouldDrawInventoryLabel() {
		return true;
	}

	@Override
	protected Vector2D getInventoryLabelDrawLocation() {
		return new Vector2D(8, 176);
	}

	@Override
	protected boolean switchToCraftingStatusView() {
		if (super.switchToCraftingStatusView()) {
			this.entityRenderer.setVisible(false);
			this.clearCurrentRecipe.setVisible(false);
			return true;
		}
		return false;
	}

	@Override
	protected boolean switchToDefaultView() {
		if (super.switchToDefaultView()) {
			this.entityRenderer.setVisible(true);
			this.clearCurrentRecipe.setVisible(true);
			return true;
		}
		return false;
	}

	@Override
	public void updateData() {
		super.updateData();

		// Handle the manager present state.
		if (getCableComponent().isManagerPresent()) {
			progressBar.setErrorState(false);
		} else {
			progressBar.setErrorState(true).setErrorMessage("Digistore Manager not present or out of power!");
		}

		// Keep the last crafting recipes updated.
		playerInventory.player.getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).ifPresent((data) -> {
			List<DigistoreCraftingTerminalHistoryEntry> history = data.getCraftingHistory();
			for (int i = 0; i < previousRecipes.size(); i++) {
				FakeSlotButton fakeSlot = previousRecipes.get(i);
				if (i < history.size()) {
					fakeSlot.setContainedData(history.get(i));
					fakeSlot.setItemStack(history.get(i).output);
					fakeSlot.setEnabled(true);
				} else {
					fakeSlot.setContainedData(null);
					fakeSlot.setItemStack(ItemStack.EMPTY);
					fakeSlot.setEnabled(false);
				}
			}
		});
	}

	@Override
	protected void drawBackgroundExtras(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);

	}
}
