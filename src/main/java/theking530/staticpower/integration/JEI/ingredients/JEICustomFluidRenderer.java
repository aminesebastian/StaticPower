package theking530.staticpower.integration.JEI.ingredients;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticpower.StaticPower;

public class JEICustomFluidRenderer implements IIngredientRenderer<FluidStack> {
	private final int width;
	private final int height;
	private final int capacity;

	public JEICustomFluidRenderer(int width, int height, int capacity) {
		this.width = width;
		this.height = height;
		this.capacity = capacity;
	}

	@Override
	public void render(PoseStack matrixStack, @Nullable FluidStack ingredient) {
		if (ingredient != null) {
			GuiFluidBarUtilities.drawFluidBar(matrixStack, ingredient, capacity, ingredient.getAmount(), 0, height, 0, width, height, true);
		}
	}

	@Override
	public List<Component> getTooltip(FluidStack ingredient, TooltipFlag tooltipFlag) {
		try {
			return GuiFluidBarUtilities.getTooltip(ingredient.getAmount(), ingredient);
		} catch (RuntimeException | LinkageError e) {
			StaticPower.LOGGER.error("Failed to get tooltip", e);
			List<Component> list = new ArrayList<>();
			MutableComponent crash = Component.translatable("jei.tooltip.error.crash");
			list.add(crash.withStyle(ChatFormatting.RED));
			return list;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}