package theking530.staticpower.teams.research;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;

public class ResearchHistoryWidget extends AbstractGuiWidget<ResearchHistoryWidget> {
	private final SimpleProgressBar progressBar;
	private Research research;
	private Color color;
	private boolean drawBackground;

	public ResearchHistoryWidget(Research research, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		this.research = research;
		registerWidget(progressBar = new SimpleProgressBar(0, 0, 86, 7).disableProgressTooltip());
		progressBar.setMaxProgress(100);
		progressBar.setCurrentProgress(100);
	}

	@Override
	protected void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// Draw background.
		if (drawBackground) {
			GuiDrawUtilities.drawRectangle(pose, getSize().getX(), getSize().getY(), 0, 0, 0.0f, color);

		}

		if (research != null) {
			int requirementCount = research.getRequirements().size();

			// Draw icon.
			GuiDrawUtilities.drawItem(pose, research.getItemIcon(), 2, 2, 110, 1.0f);
			GuiDrawUtilities.drawStringLeftAligned(pose, new TranslatableComponent(research.getTitle()).getString(), 21, 9, 0.0f, 0.75f, Color.EIGHT_BIT_WHITE, true);

			// Draw progress bar.
			progressBar.setPosition(20, 11);
			progressBar.setSize(getSize().getX() * 0.75f, 7);

			// Draw requirements.
			for (int i = 0; i < requirementCount; i++) {
				int xOffset = i * 12;
				StaticPowerIngredient requirement = research.getRequirements().get(i);
				GuiDrawUtilities.drawItem(pose, requirement.getIngredient().getItems()[0], getSize().getX() - 20 - xOffset, getSize().getY() - 16, 100, 0.6f, 0.6f);

				int remainingCount = requirement.getCount();
				GuiDrawUtilities.drawString(pose, Integer.toString(remainingCount), getSize().getX() - 9 - xOffset, getSize().getY() - 10, 1, 0.5f, Color.EIGHT_BIT_WHITE, true);
			}
		}
	}

	public void setResearch(Research research) {
		this.research = research;
	}

	public ResearchHistoryWidget setBackgroundColor(Color color) {
		this.color = color;
		return this;
	}

	public ResearchHistoryWidget setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
		return this;
	}

	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		super.getTooltips(mousePosition, tooltips, showAdvanced);
		if (this.research != null) {
			String desc = new TranslatableComponent(research.getDescription()).getString();
			List<String> description = GuiDrawUtilities.wrapString(desc, 100);
			for (String line : description) {
				tooltips.add(new TextComponent(line));
			}
		}
	}
}
