package theking530.api.gui.widgets.valuebars;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
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
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class GuiFluidBarUtilities {

	public static void drawFluidBar(FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height, boolean drawOverlay) {
		drawFluidBar(fluid, capacity, amount, x, y, zLevel, width, height, null, drawOverlay);
	}

	@SuppressWarnings("deprecation")
	public static void drawFluidBar(FluidStack fluid, int capacity, int amount, float x, float y, float zLevel, float width, float height, MachineSideMode mode, boolean drawOverlay) {
		if (mode != null && mode != MachineSideMode.Regular && mode != MachineSideMode.Never) {
			GuiDrawUtilities.drawSlot((int) x, (int) (y - height), (int) width, (int) height, mode.getColor());
		} else {
			GuiDrawUtilities.drawSlot((int) x, (int) (y - height), (int) width, (int) height);
		}

		if (fluid != null && fluid.getFluid() != null) {

			FluidAttributes attributes = fluid.getFluid().getAttributes();
			int fluidColor = attributes.getColor(fluid);
			float r = (fluidColor >> 16 & 0xFF) / 255.0f;
			float g = (fluidColor >> 8 & 0xFF) / 255.0f;
			float b = (fluidColor & 0xFF) / 255.0f;
			float a = (fluidColor >> 24 & 0xFF) / 255.0f;

			TextureAtlasSprite icon = getStillFluidSprite(fluid);
			if (icon != null) {
				Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

				float ratio = ((float) amount / (float) capacity);
				float renderAmount = ratio * (float) height;

				float segmentCapacity = capacity / ((float) height / 16);
				int segmentsUsed = (int) ((renderAmount + 16) / 16);

				float diffV = icon.getMaxV() - icon.getMinV();

				for (int i = 0; i < segmentsUsed; i++) {
					float currentSegmentCapacity = segmentCapacity * (i + 1);
					float fillRatio = (float) Math.min(1.0, amount / currentSegmentCapacity);

					float yMin = (i * 16);
					float yMax = ((i + 1) * 16) * fillRatio;
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder tes = tessellator.getBuffer();
					tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
					tes.pos(x + width, y - yMin, zLevel).color(r, g, b, a).tex(icon.getMaxU(), icon.getMinV()).endVertex();
					tes.pos(x + width, y - yMax, zLevel).color(r, g, b, a).tex(icon.getMaxU(), icon.getMinV() + (fillRatio * diffV)).endVertex();
					tes.pos(x, y - yMax, zLevel).color(r, g, b, a).tex(icon.getMinU(), icon.getMinV() + (fillRatio * diffV)).endVertex();
					tes.pos(x, y - yMin, zLevel).color(r, g, b, a).tex(icon.getMinU(), icon.getMinV()).endVertex();
					tessellator.draw();
				}
			}
		}
		if (drawOverlay) {
			Color linesColor = new Color(0.2f, 0.2f, 0.2f, 0.5f);
			for (int i = 0; i < height / 10; i++) {
				if (y - height + 2 + (i * 10) < y) {
					GuiDrawUtilities.drawColoredRectangle(x, y - height + 2 + (i * 10), width - 3, 0.5f, zLevel, linesColor);
				}
				if (y - height + 7 + (i * 10) < y) {
					GuiDrawUtilities.drawColoredRectangle(x, y - height + 7 + (i * 10), width - 7, 0.5f, zLevel, linesColor);
				}
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

		if (fluid != null && !fluid.isEmpty()) {
			ITextComponent name = fluid.getDisplayName();
			tooltip.add(name);
			tooltip.add(new StringTextComponent(NumberFormat.getNumberInstance(Locale.US).format(fluidAmount) + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity))
					.appendSibling(new TranslationTextComponent("gui.staticpower.millbuckets")));
			return tooltip;
		} else {
			tooltip.add(new TranslationTextComponent("gui.staticpower.empty"));
			tooltip.add(new StringTextComponent("0/" + NumberFormat.getNumberInstance(Locale.US).format(maxCapacity)).appendSibling(new TranslationTextComponent("gui.staticpower.millbuckets")));
			return tooltip;
		}
	}
}
