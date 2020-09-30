package theking530.staticcore.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.IDrawable;
import theking530.staticcore.utilities.Color;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractInfoTab extends BaseGuiTab {
	public static final float LINE_HEIGHT = 10.5f;
	public static final float LINE_BREAK_HEIGHT = 8.0f;
	public static final float HEIGHT_PADDING = 9.0f;

	private List<ITextComponent> info;

	public AbstractInfoTab(String title, Color titleColor, int width, ResourceLocation tabBackground, IDrawable icon) {
		super(title, titleColor, width, 0, tabBackground, icon);
		info = new ArrayList<ITextComponent>();
	}

	public int addLine(ITextComponent value) {
		info.add(value);
		return info.size() - 1;
	}

	public int addLine(TextFormatting color, ITextComponent value) {
		info.add(new StringTextComponent(color.toString()).append(value));
		return info.size() - 1;
	}

	public void addLineBreak() {
		info.add(new StringTextComponent("\n"));
	}

	public int addKeyValueLine(ITextComponent key, ITextComponent value, TextFormatting keyColor) {
		info.add(new StringTextComponent(keyColor.toString()).append(key).appendString(": ").append(value));
		return info.size() - 1;
	}

	public int addKeyValueTwoLiner(ITextComponent key, ITextComponent value, TextFormatting keyColor) {
		info.add(new StringTextComponent(keyColor.toString()).append(key).appendString(": "));
		info.add(new StringTextComponent(" ").append(value).setStyle(Style.EMPTY.setFormatting(keyColor)));
		return info.size() - 1;
	}

	public void updateLineByIndex(int index, ITextComponent line) {
		info.set(index, line);
	}

	public void clear() {
		info.clear();
	}

	@Override
	public void renderBackground(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(matrix, mouseX, mouseY, partialTicks);
		if (isOpen()) {
			if (info != null) {
				drawDarkBackground(matrix, 10, 22, tabWidth + 5, tabHeight - 7);
				drawTextBG(matrix);
				drawInfo(matrix, false);
			}
		}
	}

	@Override
	public void updateData() {
		this.tabHeight = (int) (drawInfo(null, true) + HEIGHT_PADDING);
	}

	protected float drawInfo(@Nullable MatrixStack stack, boolean simulate) {
		// Scale offsets.
		float lineHeight = 0.0f;
		float height = 0;

		// Iterate through all the info lines.
		for (int i = 0; i < info.size(); i++) {
			// Format the text.
			ITextComponent formattedText = info.get(i);

			if (formattedText.getString().equals("\n")) {
				lineHeight += LINE_BREAK_HEIGHT;
				height += LINE_BREAK_HEIGHT;
				continue;
			}

			// Get the word wrapped result.
			List<IReorderingProcessor> wordWrappedText = fontRenderer.trimStringToWidth(formattedText, this.tabWidth);
			// Render the info text.
			for (IReorderingProcessor text : wordWrappedText) {
				if (!simulate) {
					fontRenderer.func_238407_a_(stack, text, 14, 25 + lineHeight, 16777215);
				}
				lineHeight += LINE_HEIGHT;
				height += LINE_HEIGHT;
			}
		}

		return height;
	}

	protected void drawTextBG(MatrixStack stack) {

	}
}