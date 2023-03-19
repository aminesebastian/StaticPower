package theking530.staticpower.integration.JEI.ingredients.power;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarUtilities;
import theking530.staticpower.StaticPower;

public class PowerIngredientRenderer implements IIngredientRenderer<MachineRecipeProcessingSection> {

	private final int width;
	private final int height;
	private final RecipeIngredientRole role;

	public PowerIngredientRenderer() {
		this(RecipeIngredientRole.INPUT, 16, 16);
	}

	public PowerIngredientRenderer(RecipeIngredientRole role, int width, int height) {
		this.width = width;
		this.height = height;
		this.role = role;
	}

	@SuppressWarnings("resource")
	@Override
	public void render(PoseStack matrixStack, @Nullable MachineRecipeProcessingSection ingredient) {
		if (ingredient != null) {
			float remainingTime = 100;
			float maxTime = 100;

			// If there was a processing time suplied, use it to animate the value.
			// Otherwise, keep it static.
			if (ingredient.getProcessingTime() > 0) {
				long currentGameTime = Minecraft.getInstance().level.getGameTime();
				maxTime = ingredient.getProcessingTime();
				remainingTime = currentGameTime % ingredient.getProcessingTime() + Minecraft.getInstance().getPartialTick();
				if (role == RecipeIngredientRole.INPUT) {
					remainingTime = ingredient.getProcessingTime() - remainingTime;
				}
			}

			GuiPowerBarUtilities.drawPowerBar(matrixStack, 0, 0, getWidth(), getHeight(), remainingTime, maxTime);
		}
	}

	@Override
	public List<Component> getTooltip(MachineRecipeProcessingSection ingredient, TooltipFlag tooltipFlag) {
		try {
			List<Component> tooltip = new ArrayList<>();

			MutableComponent component = null;
			if (role == RecipeIngredientRole.INPUT) {
				component = Component.translatable("gui.staticpower.usage");
			} else {
				component = Component.translatable("gui.staticpower.generation");

			}

			tooltip.add(component.append(": ").append(PowerTextFormatting.formatPowerToString(ingredient.getPower() * ingredient.getProcessingTime())).append(" (")
					.append(PowerTextFormatting.formatPowerRateToString(ingredient.getPower())).append(")"));

			return tooltip;
		} catch (RuntimeException | LinkageError e) {
			StaticPower.LOGGER.error("Failed to get tooltip", e);
			List<Component> list = new ArrayList<>();
			MutableComponent crash = Component.translatable("jei.tooltip.error.crash");
			list.add(crash.withStyle(ChatFormatting.RED));
			return list;
		}
	}

	@Override
	public Font getFontRenderer(Minecraft minecraft, MachineRecipeProcessingSection ingredient) {
		Font fontRenderer = null; // To-DO: =
		// ingredient.getItem().getItem().getFontRenderer(ingredient.getItem());
		if (fontRenderer == null) {
			fontRenderer = minecraft.font;
		}
		return fontRenderer;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
