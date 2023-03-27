package theking530.staticcore.gui.widgets.tabs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.IDrawable;
import theking530.staticcore.utilities.SDColor;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractInfoTab extends BaseGuiTab {
	public static final float LINE_HEIGHT = 10.5f;
	public static final float LINE_BREAK_HEIGHT = 8.0f;
	public static final float HEIGHT_PADDING = 2.0f;

	private Map<String, List<Component>> info;
	private int lineBreakIndex;

	public AbstractInfoTab(String title, SDColor titleColor, int width, SDColor color, IDrawable icon) {
		super(title, titleColor, width, 100, color, icon);
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
		list.add(Component.literal(color.toString()).append(value));
		info.put(key, list);
		return this;
	}

	public AbstractInfoTab addLineBreak() {
		List<Component> list = new ArrayList<Component>();
		list.add(Component.literal("\n"));
		info.put("line_break_" + lineBreakIndex, list);
		lineBreakIndex++;
		return this;
	}

	public AbstractInfoTab addKeyValueLine(String key, Component label, Component value, ChatFormatting keyColor) {
		List<Component> list = new ArrayList<Component>();
		list.add(Component.literal(keyColor.toString()).append(label).append(": ").append(value));
		info.put(key, list);
		return this;
	}

	public AbstractInfoTab addKeyValueTwoLiner(String key, Component label, Component value, ChatFormatting keyColor) {
		List<Component> list = new ArrayList<Component>();
		list.add(Component.literal(keyColor.toString()).append(label).append(": "));
		list.add(Component.literal(" ").append(value).setStyle(Style.EMPTY.withColor(keyColor)));
		info.put(key, list);
		return this;
	}

	public void clear() {
		info.clear();
	}

	@Override
	public void renderWidgetBackground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderWidgetBackground(matrix, mouseX, mouseY, partialTicks);
		if (isOpen()) {
			if (info != null) {
				int padding = 9;

				drawDarkBackground(matrix, padding, 23, getSize().getXi() - (2 * padding), getSize().getYi() - (2 * padding) - 14);
				drawInfo(matrix, false);
			}
		}
	}

	@Override
	public void tick() {
		this.setExpandedHeight(drawInfo(null, true) + HEIGHT_PADDING);
	}

	protected float drawInfo(@Nullable PoseStack stack, boolean simulate) {
		// Scale offsets.
		float lineHeight = 32.0f;

		// Iterate through all the info lines.
		for (List<Component> formattedTextList : info.values()) {
			for (Component formattedText : formattedTextList) {
				String formattedString = formattedText.getString();
				if (formattedString.equals("\n") || formattedString.isEmpty()) {
					lineHeight += LINE_BREAK_HEIGHT;
					continue;
				}

				// Get the word wrapped result.
				List<String> lines = GuiDrawUtilities.wrapString(formattedString, getExpandedSize().getX() - 30);
				// Render the info text.
				for (String line : lines) {
					if (!simulate) {
						GuiDrawUtilities.drawStringLeftAligned(stack, line, 12, lineHeight + 1, 0, 1, SDColor.EIGHT_BIT_WHITE, true);
					}
					lineHeight += LINE_HEIGHT;
				}
			}
		}

		return lineHeight;
	}

	protected void drawTextBG(PoseStack stack) {

	}
}