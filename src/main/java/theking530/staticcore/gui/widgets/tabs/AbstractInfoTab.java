package theking530.staticcore.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.IDrawable;
import theking530.staticcore.utilities.Color;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractInfoTab extends BaseGuiTab {
	public static final float LINE_HEIGHT = 10.5f;
	public static final float LINE_BREAK_HEIGHT = 8.0f;
	public static final float HEIGHT_PADDING = 9.0f;

	private Map<String, List<Component>> info;
	private int lineBreakIndex;

	public AbstractInfoTab(String title, Color titleColor, int width, ResourceLocation tabBackground, IDrawable icon) {
		super(title, titleColor, width, 0, tabBackground, icon);
		info = new LinkedHashMap<String, List<Component>>();
		lineBreakIndex = 0;
	}

	public AbstractInfoTab addLine(String key, Component value) {
		List<Component> list = new ArrayList<Component>();
		list.add(value);
		info.put(key, list);
		return this;
	}

	public AbstractInfoTab addLine(String key, ChatFormatting color, Component value) {
		List<Component> list = new ArrayList<Component>();
		list.add(new TextComponent(color.toString()).append(value));
		info.put(key, list);
		return this;
	}

	public AbstractInfoTab addLineBreak() {
		List<Component> list = new ArrayList<Component>();
		list.add(new TextComponent("\n"));
		info.put("line_break_" + lineBreakIndex, list);
		lineBreakIndex++;
		return this;
	}

	public AbstractInfoTab addKeyValueLine(String key, Component label, Component value, ChatFormatting keyColor) {
		List<Component> list = new ArrayList<Component>();
		list.add(new TextComponent(keyColor.toString()).append(label).append(": ").append(value));
		info.put(key, list);
		return this;
	}

	public AbstractInfoTab addKeyValueTwoLiner(String key, Component label, Component value, ChatFormatting keyColor) {
		List<Component> list = new ArrayList<Component>();
		list.add(new TextComponent(keyColor.toString()).append(label).append(": "));
		list.add(new TextComponent(" ").append(value).setStyle(Style.EMPTY.withColor(keyColor)));
		info.put(key, list);
		return this;
	}

	public void clear() {
		info.clear();
	}

	@Override
	public void renderBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
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

	protected float drawInfo(@Nullable PoseStack stack, boolean simulate) {
		// Scale offsets.
		float lineHeight = 0.0f;
		float height = 0;

		// Iterate through all the info lines.
		for (List<Component> formattedTextList : info.values()) {
			for (Component formattedText : formattedTextList) {
				if (formattedText.getString().equals("\n")) {
					lineHeight += LINE_BREAK_HEIGHT;
					height += LINE_BREAK_HEIGHT;
					continue;
				}

				// Get the word wrapped result.
				List<FormattedCharSequence> wordWrappedText = fontRenderer.split(formattedText, this.tabWidth);
				// Render the info text.
				for (FormattedCharSequence text : wordWrappedText) {
					if (!simulate) {
						fontRenderer.drawShadow(stack, text, 14, 25 + lineHeight, 16777215);
					}
					lineHeight += LINE_HEIGHT;
					height += LINE_HEIGHT;
				}
			}
		}

		return height;
	}

	protected void drawTextBG(PoseStack stack) {

	}
}