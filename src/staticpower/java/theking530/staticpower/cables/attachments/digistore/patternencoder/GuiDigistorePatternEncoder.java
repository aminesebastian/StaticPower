package theking530.staticpower.cables.attachments.digistore.patternencoder;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.button.SpriteButton;
import theking530.staticcore.gui.widgets.button.StandardButton;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.cables.attachments.digistore.patternencoder.DigistorePatternEncoder.RecipeEncodingType;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractGuiDigistoreTerminal;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class GuiDigistorePatternEncoder extends AbstractGuiDigistoreTerminal<ContainerDigistorePatternEncoder, DigistorePatternEncoder> {
	private ArrowProgressBar progressBar;
	public SpriteButton recipeTypeButton;
	public SpriteButton clearRecipeButton;
	public SpriteButton encodeButton;

	public GuiDigistorePatternEncoder(ContainerDigistorePatternEncoder container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 270);

		// Limit the view to only show 5 rows to make room for the crafting GUI.
		setMaxRows(5);
	}

	@Override
	public void initializeGui() {
		super.initializeGui();
		registerWidget(progressBar = (ArrowProgressBar) new ArrowProgressBar(64, 136).disableProgressTooltip());
		searchBar.setSize(70, 12);
		searchBar.setPosition(98, 6);

		// Add recipe type button.
		registerWidget(recipeTypeButton = new SpriteButton(67, 154, 16, 16, StaticPowerSprites.CRAFTING_TABLE_ICON, null, this::onRecipeTypeButtonPressed));
		recipeTypeButton.setTooltip(Component.literal("Crafting Table Recipe"));

		// Add clear recipe button.
		registerWidget(clearRecipeButton = new SpriteButton(65, 117, 8, 8, StaticPowerSprites.CLOSE, null, this::onClearRecipePressed));
		clearRecipeButton.setTooltip(Component.literal("Clear Recipe"));

		// Add encode button.
		registerWidget(encodeButton = new SpriteButton(145, 134, 16, 16, StaticPowerSprites.ARROW_DOWN, null, this::onEncodePressed));
		encodeButton.setTooltip(Component.literal("Encode Recipe"));
	}

	@Override
	protected boolean switchToCraftingStatusView() {
		if (super.switchToCraftingStatusView()) {
			this.recipeTypeButton.setVisible(false);
			this.clearRecipeButton.setVisible(false);
			this.encodeButton.setVisible(false);
			this.progressBar.setVisible(false);
			return true;
		}
		return false;
	}

	@Override
	protected boolean switchToDefaultView() {
		if (super.switchToDefaultView()) {
			this.recipeTypeButton.setVisible(true);
			this.clearRecipeButton.setVisible(true);
			this.encodeButton.setVisible(true);
			this.progressBar.setVisible(true);
			return true;
		}
		return false;
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
	public void updateData() {
		super.updateData();
		if (getCableComponent().isManagerPresent()) {
			progressBar.setErrorState(false);
		} else {
			progressBar.setErrorState(true).setErrorMessage(Component.translatable("gui.staticpower.alert.digistore_manager_missing"));
		}
	}

	protected void onEncodePressed(StandardButton button, MouseButton mouseButton) {
		// Send sync packet to the server.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new PacketPatternEncoderEncode(getMenu().containerId, getMenu().getCurrentRecipeType()));
	}

	protected void onClearRecipePressed(StandardButton button, MouseButton mouseButton) {
		getMenu().clearRecipe();
		// Send sync packet to the server.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new PacketPatternEncoderClearRecipe(getMenu().containerId));
	}

	protected void onRecipeTypeButtonPressed(StandardButton button, MouseButton mouseButton) {
		if (getMenu().getCurrentRecipeType() == RecipeEncodingType.CRAFTING_TABLE) {
			getMenu().setCurrentRecipeType(RecipeEncodingType.MACHINE);
			recipeTypeButton.setRegularTexture(StaticPowerSprites.FURNACE_ICON);
			recipeTypeButton.setTooltip(Component.literal("Free-form Recipe"));
		} else {
			getMenu().setCurrentRecipeType(RecipeEncodingType.CRAFTING_TABLE);
			recipeTypeButton.setRegularTexture(StaticPowerSprites.CRAFTING_TABLE_ICON);
			recipeTypeButton.setTooltip(Component.literal("Crafting Table Recipe"));
		}

		// Send sync packet to the server.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(new PacketPatternEncoderRecipeTypeChange(getMenu().containerId, getMenu().getCurrentRecipeType()));
	}

	@Override
	protected void drawBackgroundExtras(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		super.drawBackgroundExtras(stack, partialTicks, mouseX, mouseY);

	}
}
