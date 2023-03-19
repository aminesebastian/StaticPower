package theking530.staticpower.integration.TOP;

import theking530.staticcore.utilities.SDColor;

public class BarTooltipRenderer {
	protected SDColor borderColor;
	protected SDColor mainColor;
	protected SDColor alternateColor;

	protected float width;
	protected float height;
	protected int border;
	protected int topOffset;

	public BarTooltipRenderer(float width, float height, SDColor mainColor, SDColor alternateColor, SDColor borderColor) {
		this.width = width;
		this.height = height;
		this.mainColor = mainColor;
		this.alternateColor = alternateColor;
		this.borderColor = borderColor;
		this.border = 1;
		this.topOffset = 0;
	}

//	@Override
//	public Dimension getSize(CompoundTag data, ICommonAccessor accessor) {
//		return new Dimension((int) (width + (border * 4)), (int) (height + (border * 2)));
//	}
//
//	@Override
//	public void draw(CompoundTag data, ICommonAccessor accessor, int x, int y) {
//		// If we're missing any values, return early.
//		if (!data.contains("value") || !data.contains("max")) {
//			return;
//		}
//
//		// Isolate the values.
//		double value = data.getDouble("value");
//		double max = data.getDouble("max");
//
//		// Triple check to make sure we don't overflow.
//		double percent = value / Math.max(value, max);
//		percent = SDMath.clamp(percent, 0, 1.0);
//
//		int segments = (int) (percent * width) - 2;
//
//		// Create the matrix stack.
//		MatrixStack stack = new MatrixStack();
//		stack.translate(x, y, 0);
//
//		// Render the border and the black filling.
//		GuiDrawUtilities.drawColoredRectangle(stack, 0, topOffset, width, height, 1.0f, borderColor);
//		GuiDrawUtilities.drawColoredRectangle(stack, border, topOffset + border, width - (2 * border), height - (2 * border), 1.0f, Color.BLACK);
//
//		// Render the segments.
//		for (int i = 0; i < segments; i++) {
//			if (i % 2 == 0) {
//				GuiDrawUtilities.drawColoredRectangle(stack, border + i, topOffset + border, 1, height - (2 * border), 2.0f, mainColor);
//			} else {
//				GuiDrawUtilities.drawColoredRectangle(stack, border + i, topOffset + border, 1, height - (2 * border), 2.0f, alternateColor);
//			}
//		}
//
//		// Get the description.
//		String description = getDescription(data);
//
//		// Render the description left justified.
//		int descriptionWidth = Minecraft.getInstance().fontRenderer.getStringWidth(description);
//		GuiDrawUtilities.drawStringWithSize(stack, description, (descriptionWidth * 0.75f) + (border * 2) + 1, height - (border * 2), 0.75f, Color.EIGHT_BIT_WHITE, true);
//	}
//
//	protected String getDescription(CompoundNBT data) {
//		return data.getString("description");
//	}
}
