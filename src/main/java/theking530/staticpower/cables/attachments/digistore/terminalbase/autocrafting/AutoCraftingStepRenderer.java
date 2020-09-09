package theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.GuiDrawItem;
import theking530.staticpower.cables.digistore.crafting.RequiredAutoCraftingMaterials.RequiredAutoCraftingMaterial;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

public class AutoCraftingStepRenderer {
	protected void drawStep(StaticPowerContainerGui<?> owningGui, RequiredAutoCraftingMaterial material, int xPos, int yPos, int mouseX, int mouseY, SpriteDrawable missingIngredientDrawable,
			int ingredientRenderIndex) {
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
				List<String> tooltips = owningGui.getTooltipFromItem(itemToDraw);
				owningGui.renderTooltip(tooltips, mouseX, mouseY);
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
}
