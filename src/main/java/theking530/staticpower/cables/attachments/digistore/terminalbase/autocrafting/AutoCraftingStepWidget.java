package theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.GuiDrawItem;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.cables.digistore.crafting.RequiredAutoCraftingMaterials.RequiredAutoCraftingMaterial;
import theking530.staticpower.client.StaticPowerSprites;

public class AutoCraftingStepWidget extends AbstractGuiWidget {
	public static final int TICKS_PER_INGREDIENT = 20;
	public static final SpriteDrawable MISSING_INGREDIENT_RENDERABLE;
	public static final GuiDrawItem ITEM_RENDERER;
	private RequiredAutoCraftingMaterial material;
	private CraftingRequestResponse sourceRequest;
	private boolean isCurrentStep;
	private int ingredientCycleTimer;
	private int ingredientRenderIndex;

	static {
		MISSING_INGREDIENT_RENDERABLE = new SpriteDrawable(StaticPowerSprites.ERROR, 16, 16);
		ITEM_RENDERER = new GuiDrawItem();
	}

	public AutoCraftingStepWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
	}

	public AutoCraftingStepWidget setStepData(CraftingRequestResponse request, RequiredAutoCraftingMaterial material, boolean isCurrentStep) {
		this.material = material;
		this.sourceRequest = request;
		this.isCurrentStep = isCurrentStep;
		return this;
	}

	@Override
	public void updateData() {
		ingredientCycleTimer++;
		if (ingredientCycleTimer > TICKS_PER_INGREDIENT) {
			ingredientCycleTimer = 0;
			ingredientRenderIndex++;
		}
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		if (material == null) {
			return;
		}

		// Get the ingredient.
		Ingredient ing = material.getItem();
		// If it has items, draw the item (rotate through all the itemstacks in the
		// ingredient).
		if (!ing.isEmpty()) {
			// Get the screen space position.
			Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(getLastRenderMatrix(), getPosition());

			// Get all the tooltips and add them to the tooltips list.
			if (mousePosition.getX() >= screenSpacePosition.getXi() && mousePosition.getX() <= screenSpacePosition.getXi() + 18 && mousePosition.getY() >= screenSpacePosition.getYi()
					&& mousePosition.getY() <= screenSpacePosition.getYi() + 18) {
				tooltips.addAll(getCurrentIndexItemStack().getTooltipLines(null, Default.NORMAL));
			} else if (this.isPointInsideBounds(mousePosition)) {
				// If this is a blocking step, add the blocker as a tooltip.
				if (isBlockingStep()) {
					tooltips.add(sourceRequest.getBlockerMessage());
				}
			}
		}
	}

	@Override
	public void renderForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (material == null) {
			return;
		}

		// Get the screen space position.
		Vector2D screenSpacePosition = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());
		Vector2D localPosition = getPosition();

		// Get the ingredient.
		Ingredient ing = material.getItem();

		// If it has items, draw the item (rotate through all the itemstacks in the
		// ingredient).
		if (!ing.isEmpty()) {
			ITEM_RENDERER.drawItem(getCurrentIndexItemStack(), 0, 0, screenSpacePosition.getXi() + 2, screenSpacePosition.getYi() + 3, 1.0f);
		}

		// If we're missing any items, render the missing scenario. If we have to craft,
		// render the crafting scenario. If we have all the items, render the we have
		// all
		// the items scenario.
		Color textColor = isBlockingStep() ? Color.EIGHT_BIT_RED : Color.EIGHT_BIT_WHITE;
		if (material.getMissingAmount() > 0) {
			GuiDrawUtilities.drawStringWithSize(matrix, "Required: " + material.getAmountRequired(), localPosition.getXi() + 53, localPosition.getYi() + 7, 0.5f, textColor, true);
			GuiDrawUtilities.drawStringWithSize(matrix, "Stored: " + material.getAmountStored(), localPosition.getXi() + 53, localPosition.getYi() + 12, 0.5f, textColor, true);
			GuiDrawUtilities.drawColoredRectangle(matrix, localPosition.getXi() + 21, localPosition.getYi() + 14, 35.0f, 0.5f, 1.0f, Color.GREY);
			GuiDrawUtilities.drawStringWithSize(matrix, "Missing: " + material.getMissingAmount(), localPosition.getXi() + 53.5f, localPosition.getYi() + 19.5f, 0.5f,
					new Color(75.0f, 25.0f, 0.0f, 255.0f), false);
			GuiDrawUtilities.drawStringWithSize(matrix, "Missing: " + material.getMissingAmount(), localPosition.getXi() + 53, localPosition.getYi() + 19, 0.5f,
					new Color(255.0f, 150.0f, 50.0f, 255.0f), false);
			MISSING_INGREDIENT_RENDERABLE.draw(screenSpacePosition.getXi() + 2, screenSpacePosition.getYi() + 3);
		} else if (material.getAmountToCraft() > 0) {
			GuiDrawUtilities.drawStringWithSize(matrix, "Required: " + material.getAmountRequired(), localPosition.getXi() + 53, localPosition.getYi() + 7, 0.5f, textColor, true);
			GuiDrawUtilities.drawStringWithSize(matrix, "Stored: " + material.getAmountStored(), localPosition.getXi() + 53, localPosition.getYi() + 12, 0.5f, textColor, true);
			GuiDrawUtilities.drawColoredRectangle(matrix, localPosition.getXi() + 21, localPosition.getYi() + 14, 35.0f, 0.5f, 1.0f, Color.GREY);
			GuiDrawUtilities.drawStringWithSize(matrix, "To Craft: " + material.getAmountToCraft(), localPosition.getXi() + 53, localPosition.getYi() + 19, 0.5f, textColor, true);
		} else {
			GuiDrawUtilities.drawStringWithSize(matrix, "Stored: " + material.getAmountRequired(), localPosition.getXi() + 53, localPosition.getYi() + 11, 0.5f, textColor, true);
		}

		if (isBlockingStep()) {
			MISSING_INGREDIENT_RENDERABLE.draw(screenSpacePosition.getXi() + 2, screenSpacePosition.getYi() + 3);
		}

		// Draw the bottom divider.
		GuiDrawUtilities.drawColoredRectangle(matrix, localPosition.getXi(), localPosition.getYi() + getSize().getY(), getSize().getX(), 0.75f, 1.0f, Color.GREY);
	}

	protected boolean isBlockingStep() {
		return isCurrentStep && sourceRequest.isBlocked();
	}

	protected ItemStack getCurrentIndexItemStack() {
		return material.getItem().getItems()[getCurrentIngredientIndex()];
	}

	protected int getCurrentIngredientIndex() {
		return ingredientRenderIndex % material.getItem().getItems().length;
	}
}
