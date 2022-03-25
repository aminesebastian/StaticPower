package theking530.staticpower.teams.research;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.crafting.Recipe;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchUnlock;
import theking530.staticpower.data.research.ResearchUnlockUtilities;
import theking530.staticpower.data.research.ResearchUnlock.ResearchUnlockType;

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

		Color borderColor = new Color(0, 0, 0, 0.3f);
		GuiDrawUtilities.drawRectangle(pose, getSize().getX(), 0.5f, 0, 0, 1.0f, borderColor);
		GuiDrawUtilities.drawRectangle(pose, getSize().getX(), 0.5f, 0, getSize().getY(), 1.0f, borderColor);

		if (research != null) {
			// Draw icon.
			GuiDrawUtilities.drawItem(pose, research.getIcon().getItemIcon(), 2, 4, 110, 1.0f);
			GuiDrawUtilities.drawStringLeftAligned(pose, new TranslatableComponent(research.getTitle()).getString(), 21, 9, 0.0f, 0.75f, Color.EIGHT_BIT_WHITE, true);

			// Draw progress bar.
			progressBar.setPosition(20, 11);
			progressBar.setSize(getSize().getX() * 0.75f, 7);

			// Draw requirements.
			int xOffset = 0;
			for (StaticPowerIngredient requirement : research.getRequirements()) {
				GuiDrawUtilities.drawItem(pose, requirement.getIngredient().getItems()[0], getSize().getX() - 17 - xOffset, getSize().getY() - 16, 100, 8f, 8f);

				int remainingCount = requirement.getCount();
				GuiDrawUtilities.drawStringCentered(pose, Integer.toString(remainingCount), getSize().getX() - 9f - xOffset, getSize().getY() - 10, 101, 0.5f, Color.EIGHT_BIT_WHITE, true);

				xOffset += 9;
			}

			List<ResearchUnlock> unlocks = ResearchUnlockUtilities.getCollapsedUnlocks(research);
			if (unlocks.size() > 0) {
				GuiDrawUtilities.drawRectangle(pose, 0.4f, 8, getSize().getX() - 6f - xOffset, getSize().getY() - 12, 10, new Color(0, 0, 0, 0.3f));

				// Draw the unlocks.
				for (ResearchUnlock unlock : unlocks) {
					if (unlock.getType() == ResearchUnlockType.CRAFTING) {
						Recipe<?> recipe = unlock.getAsRecipe();
						if (recipe != null) {
							GuiDrawUtilities.drawItem(pose, recipe.getResultItem(), getSize().getX() - 19.5f - xOffset, getSize().getY() - 16, 200, 8f, 8f);
							xOffset += 9;
						}
					}
				}

				GuiDrawUtilities.drawRectangle(pose, unlocks.size() * 10f, 9.5f, getSize().getX() - 7.5f - xOffset, getSize().getY() - 12.85f, 10, new Color(0, 0, 0, 0.3f));
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

	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		super.getWidgetTooltips(mousePosition, tooltips, showAdvanced);
		if (this.research != null) {
			String desc = new TranslatableComponent(research.getDescription()).getString();
			List<String> description = GuiDrawUtilities.wrapString(desc, 150);
			for (String line : description) {
				tooltips.add(new TextComponent(line));
			}
		}
	}
}
