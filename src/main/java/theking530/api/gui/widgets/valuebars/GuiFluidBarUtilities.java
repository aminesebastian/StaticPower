package theking530.api.gui.widgets.valuebars;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.api.gui.GuiDrawUtilities;
import theking530.api.utilities.Color;
import theking530.staticpower.tileentities.utilities.SideModeList.Mode;

public class GuiFluidBarUtilities {

	public static void drawFluidBar(FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height, boolean drawOverlay) {
		drawFluidBar(fluid, capacity, amount, x, y, zLevel, width, height, null, drawOverlay);
	}

	public static void drawFluidBar(FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height, Mode mode, boolean drawOverlay) {
		if (mode != null) {
			GuiDrawUtilities.drawSlot((int) x, (int) (y - height), (int) width, (int) height, mode.getBorderColor());
		} else {
			GuiDrawUtilities.drawSlot((int) x, (int) (y - height), (int) width, (int) height);
		}

		if (fluid != null && fluid.getFluid() != null) {

			TextureAtlasSprite icon = getStillFluidSprite(fluid);
			if (icon != null) {
				float ratio = ((float) amount / (float) capacity);
				float renderAmount = ratio * (float) height;

				float segmentCapacity = capacity / ((float) height / 16);
				int segmentsUsed = (int) ((renderAmount + 16) / 16);

				float minU = icon.getMinU();
				float maxU = icon.getMaxU();
				float minV = icon.getMinV();
				float maxV = icon.getMaxV();
				float diffV = maxV - minV;

				GL11.glColor3f(1.0f, 1.0f, 1.0f);

				for (int i = 0; i < segmentsUsed; i++) {
					float currentSegmentCapacity = segmentCapacity * (i + 1);
					float fillRatio = (float) Math.min(1.0, amount / currentSegmentCapacity);

					float yMin = (i * 16);
					float yMax = ((i + 1) * 16) * fillRatio;
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder tes = tessellator.getBuffer();
					tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
					tes.pos(x + width, y - yMin, zLevel).color(0.0f, 0.2f, 1.0f, 1.0f).tex(maxU, minV).endVertex();
					tes.pos(x + width, y - yMax, zLevel).color(0.0f, 0.2f, 1.0f, 1.0f).tex(maxU, minV + (fillRatio * diffV)).endVertex();
					tes.pos(x, y - yMax, zLevel).color(0.0f, 0.2f, 1.0f, 1.0f).tex(minU, minV + (fillRatio * diffV)).endVertex();
					tes.pos(x, y - yMin, zLevel).color(0.0f, 0.2f, 1.0f, 1.0f).tex(minU, minV).endVertex();
					tessellator.draw();

				}
			}
		}
		if (drawOverlay) {
			Color linesColor = new Color(0.2f, 0.2f, 0.2f, 0.5f);
			for (int i = 0; i < height / 10; i++) {
				GuiDrawUtilities.drawColoredRectangle(x, y - height + 2 + (i * 10), width - 3, 1, zLevel, linesColor);
				GuiDrawUtilities.drawColoredRectangle(x, y - height + 7 + (i * 10), width - 7, 1, zLevel, linesColor);
			}
		}
	}

	public static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		FluidAttributes attributes = fluid.getAttributes();
		ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
		return Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStill);
	}

	public static List<ITextComponent> getTooltip(int fluidAmount, int maxCapacity, FluidStack fluid) {
		List<ITextComponent> tooltip = new ArrayList<ITextComponent>();

		if (fluid != null) {
			ITextComponent name = fluid.getDisplayName();
			tooltip.add(name);
			tooltip.add(new StringTextComponent(NumberFormat.getNumberInstance(Locale.US).format(fluidAmount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity) + "mB"));
			return tooltip;
		} else {
			tooltip.add(new TranslationTextComponent("staticpower.gui.empty"));
			return tooltip;
		}
	}
}
