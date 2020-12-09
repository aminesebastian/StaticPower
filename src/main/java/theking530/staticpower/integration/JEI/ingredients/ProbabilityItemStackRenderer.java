package theking530.staticpower.integration.JEI.ingredients;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.integration.JEI.JEIErrorUtilSnippet;

public class ProbabilityItemStackRenderer implements IIngredientRenderer<ProbabilityItemStackOutput> {

	@Override
	@SuppressWarnings("deprecation")
	public void render(MatrixStack matrixStack, int xPosition, int yPosition, @Nullable ProbabilityItemStackOutput ingredient) {
		if (ingredient != null) {
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
			RenderSystem.enableDepthTest();
			RenderHelper.enableStandardItemLighting();
			Minecraft minecraft = Minecraft.getInstance();
			FontRenderer font = getFontRenderer(minecraft, ingredient);
			ItemRenderer itemRenderer = minecraft.getItemRenderer();
			itemRenderer.renderItemAndEffectIntoGUI(null, ingredient.getItem(), xPosition, yPosition);
			itemRenderer.renderItemOverlayIntoGUI(font, ingredient.getItem(), xPosition, yPosition, null);

			// Draw the percentage string manually.
			if (ingredient.getOutputChance() != 1.0f) {
				String percentageString = (ingredient.getOutputChance() * 100) + "%";
				int width = Minecraft.getInstance().fontRenderer.getStringWidth(percentageString);
				GuiDrawUtilities.drawStringWithSize(matrixStack, percentageString, xPosition - 1.5f + (width / 2), yPosition + 2, 0.5f, Color.EIGHT_BIT_YELLOW, true);
			} else if (ingredient.getAdditionalBonus() > 0) {
				GuiDrawUtilities.drawStringWithSize(matrixStack, "*", xPosition + 3, yPosition + 6, 1.0f, Color.EIGHT_BIT_YELLOW, true);
			}

			RenderSystem.disableBlend();
			RenderHelper.disableStandardItemLighting();
			RenderSystem.popMatrix();
		}
	}

	@Override
	public List<ITextComponent> getTooltip(ProbabilityItemStackOutput ingredient, ITooltipFlag tooltipFlag) {
		Minecraft minecraft = Minecraft.getInstance();
		PlayerEntity player = minecraft.player;
		try {
			List<ITextComponent> tooltip = ingredient.getItem().getTooltip(player, tooltipFlag);

			// Formulate the output percentage tooltip and then add it.
			if (ingredient.getOutputChance() != 1.0f) {
				ITextComponent outputPercentage = new TranslationTextComponent("gui.staticpower.output_chance").appendString(": ")
						.appendString(TextFormatting.GREEN.toString() + String.valueOf((int) (ingredient.getOutputChance() * 100)) + "%");
				tooltip.add(outputPercentage);
			}

			// Add the tooltip for the bonus output.
			if (ingredient.getAdditionalBonus() > 0) {
				ITextComponent bonus = new TranslationTextComponent("gui.staticpower.bonus_output").mergeStyle(TextFormatting.GREEN).appendString(": ")
						.appendString(TextFormatting.GOLD.toString() + String.valueOf(ingredient.getAdditionalBonus()) + TextFormatting.GRAY.toString() + TextFormatting.ITALIC.toString()
								+ " (" + String.valueOf((int) (ingredient.getBonusChance() * 100)) + "%)");
				tooltip.add(bonus);
			}

			return tooltip;
		} catch (RuntimeException | LinkageError e) {
			String itemStackInfo = JEIErrorUtilSnippet.getItemStackInfo(ingredient.getItem());
			StaticPower.LOGGER.error("Failed to get tooltip: {}", itemStackInfo, e);
			List<ITextComponent> list = new ArrayList<>();
			TranslationTextComponent crash = new TranslationTextComponent("jei.tooltip.error.crash");
			list.add(crash.mergeStyle(TextFormatting.RED));
			return list;
		}
	}

	@Override
	public FontRenderer getFontRenderer(Minecraft minecraft, ProbabilityItemStackOutput ingredient) {
		FontRenderer fontRenderer = ingredient.getItem().getItem().getFontRenderer(ingredient.getItem());
		if (fontRenderer == null) {
			fontRenderer = minecraft.fontRenderer;
		}
		return fontRenderer;
	}
}
