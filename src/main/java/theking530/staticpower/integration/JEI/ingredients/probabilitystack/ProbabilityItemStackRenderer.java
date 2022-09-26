package theking530.staticpower.integration.JEI.ingredients.probabilitystack;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.TooltipFlag;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.integration.JEI.JEIErrorUtilSnippet;

public class ProbabilityItemStackRenderer implements IIngredientRenderer<ProbabilityItemStackOutput> {

	@Override
	public void render(PoseStack matrixStack, int xPosition, int yPosition, @Nullable ProbabilityItemStackOutput ingredient) {
		if (ingredient != null) {
			RenderSystem.enableDepthTest();
			Lighting.setupForFlatItems();
			Minecraft minecraft = Minecraft.getInstance();
			Font font = getFontRenderer(minecraft, ingredient);

			GuiDrawUtilities.drawItem(matrixStack, ingredient.getItem(), xPosition, yPosition, 10.0f);

			// Draw the percentage string manually.
			if (ingredient.getOutputChance() != 1.0f) {
				String percentageString = GuiTextUtilities.formatNumberAsStringOneDecimal(ingredient.getOutputChance() * 100).getString() + "%";
				int width = font.width(percentageString);
				GuiDrawUtilities.drawString(matrixStack, percentageString, xPosition - 1.5f + (width / 2), yPosition + 2, 0.0f, 0.5f, Color.EIGHT_BIT_YELLOW, true);
			} else if (ingredient.getAdditionalBonus() > 0) {
				GuiDrawUtilities.drawString(matrixStack, "*", xPosition + 3, yPosition + 6, 0.0f, 1.0f, Color.EIGHT_BIT_YELLOW, true);
			}

			if (ingredient.getItem().getCount() > 1) {
				GuiDrawUtilities.drawString(matrixStack,
						String.valueOf(ingredient.getItem().getCount()), xPosition + 17, yPosition + 16, 100.0f, 1.0f, Color.EIGHT_BIT_WHITE,
						true);
			}

			RenderSystem.disableBlend();
		}
	}

	@Override
	public List<Component> getTooltip(ProbabilityItemStackOutput ingredient, TooltipFlag tooltipFlag) {
		try {
			// Get the original item tooltip but remove the last line (that should be the
			// mod name).
			@SuppressWarnings("resource")
			List<Component> tooltip = ingredient.getItem().getTooltipLines(Minecraft.getInstance().player, tooltipFlag);

			// Formulate the output percentage tooltip and then add it.
			if (ingredient.getOutputChance() != 1.0f) {
				Component outputPercentage = new TranslatableComponent("gui.staticpower.output_chance").append(": ")
						.append(ChatFormatting.GREEN.toString() + String.valueOf((int) (ingredient.getOutputChance() * 100)) + "%");
				tooltip.add(outputPercentage);
			}

			// Add the tooltip for the bonus output.
			if (ingredient.getAdditionalBonus() > 0) {
				Component bonus = new TranslatableComponent("gui.staticpower.bonus_output").withStyle(ChatFormatting.GREEN).append(": ")
						.append(ChatFormatting.GOLD.toString() + String.valueOf(ingredient.getAdditionalBonus()) + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString()
								+ " (" + GuiTextUtilities.formatNumberAsStringOneDecimal(ingredient.getBonusChance() * 100).getString() + "%)");
				tooltip.add(bonus);
			}

			return tooltip;
		} catch (RuntimeException | LinkageError e) {
			String itemStackInfo = JEIErrorUtilSnippet.getItemStackInfo(ingredient.getItem());
			StaticPower.LOGGER.error("Failed to get tooltip: {}", itemStackInfo, e);
			List<Component> list = new ArrayList<>();
			TranslatableComponent crash = new TranslatableComponent("jei.tooltip.error.crash");
			list.add(crash.withStyle(ChatFormatting.RED));
			return list;
		}
	}

	@Override
	public Font getFontRenderer(Minecraft minecraft, ProbabilityItemStackOutput ingredient) {
		Font fontRenderer = null; // To-DO: =
		// ingredient.getItem().getItem().getFontRenderer(ingredient.getItem());
		if (fontRenderer == null) {
			fontRenderer = minecraft.font;
		}
		return fontRenderer;
	}
}