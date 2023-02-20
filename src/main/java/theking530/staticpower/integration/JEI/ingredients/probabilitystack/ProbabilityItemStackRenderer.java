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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.integration.JEI.JEIErrorUtilSnippet;

public class ProbabilityItemStackRenderer implements IIngredientRenderer<StaticPowerOutputItem> {

	@Override
	public void render(PoseStack matrixStack, @Nullable StaticPowerOutputItem ingredient) {
		if (ingredient != null) {
			RenderSystem.enableDepthTest();
			Lighting.setupForFlatItems();
			Minecraft minecraft = Minecraft.getInstance();
			Font font = getFontRenderer(minecraft, ingredient);

			GuiDrawUtilities.drawItem(matrixStack, ingredient.getItemStack(), 0, 0, 10.0f);

			// Draw the percentage string manually.
			if (ingredient.getOutputChance() != 1.0f) {
				String percentageString = GuiTextUtilities.formatNumberAsStringOneDecimal(ingredient.getOutputChance() * 100).getString() + "%";
				int width = font.width(percentageString);
				GuiDrawUtilities.drawString(matrixStack, percentageString, -1.5f + (width / 2), 2, 0.0f, 0.5f, SDColor.EIGHT_BIT_YELLOW, true);
			} else if (ingredient.getAdditionalBonus() > 0) {
				GuiDrawUtilities.drawString(matrixStack, "*", 3, 6, 0.0f, 1.0f, SDColor.EIGHT_BIT_YELLOW, true);
			}

			if (ingredient.getItemStack().getCount() > 1) {
				GuiDrawUtilities.drawString(matrixStack, String.valueOf(ingredient.getItemStack().getCount()), 17, 16, 100.0f, 1.0f, SDColor.EIGHT_BIT_WHITE, true);
			}

			RenderSystem.disableBlend();
		}
	}

	@Override
	public List<Component> getTooltip(StaticPowerOutputItem ingredient, TooltipFlag tooltipFlag) {
		try {
			// Get the original item tooltip but remove the last line (that should be the
			// mod name).
			@SuppressWarnings("resource")
			List<Component> tooltip = ingredient.getItemStack().getTooltipLines(Minecraft.getInstance().player, tooltipFlag);
			if (tooltip.size() > 0) {
				tooltip.remove(tooltip.size() - 1);
			}

			// Formulate the output percentage tooltip and then add it.
			if (ingredient.getOutputChance() != 1.0f) {
				Component outputPercentage = Component.translatable("gui.staticpower.output_chance").append(": ")
						.append(ChatFormatting.GREEN.toString() + String.valueOf((int) (ingredient.getOutputChance() * 100)) + "%");
				tooltip.add(outputPercentage);
			}

			// Add the tooltip for the bonus output.
			if (ingredient.getAdditionalBonus() > 0) {
				Component bonus = Component.translatable("gui.staticpower.bonus_output").withStyle(ChatFormatting.GREEN).append(": ")
						.append(ChatFormatting.GOLD.toString() + String.valueOf(ingredient.getAdditionalBonus()) + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString()
								+ " (" + GuiTextUtilities.formatNumberAsStringOneDecimal(ingredient.getBonusChance() * 100).getString() + "%)");
				tooltip.add(bonus);
			}

			return tooltip;
		} catch (RuntimeException | LinkageError e) {
			String itemStackInfo = JEIErrorUtilSnippet.getItemStackInfo(ingredient.getItemStack());
			StaticPower.LOGGER.error("Failed to get tooltip: {}", itemStackInfo, e);
			List<Component> list = new ArrayList<>();
			MutableComponent crash = Component.translatable("jei.tooltip.error.crash");
			list.add(crash.withStyle(ChatFormatting.RED));
			return list;
		}
	}

	@Override
	public Font getFontRenderer(Minecraft minecraft, StaticPowerOutputItem ingredient) {
		Font fontRenderer = null; // To-DO: =
		// ingredient.getItem().getItem().getFontRenderer(ingredient.getItem());
		if (fontRenderer == null) {
			fontRenderer = minecraft.font;
		}
		return fontRenderer;
	}
}
