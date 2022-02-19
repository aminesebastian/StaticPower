package theking530.staticpower.teams.research;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

public class ResearchNodeWidget extends AbstractGuiWidget {
	private final Research research;
	private final ItemDrawable itemRenderer;
	private final SimpleProgressBar progressBar;
	private float hoveredScale;

	public ResearchNodeWidget(Research research, float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
		registerWidget(progressBar = new SimpleProgressBar(26, 20, 86, 7).setMaxProgress(100));
		progressBar.setVisible(false);
		this.research = research;
		itemRenderer = new ItemDrawable(ItemStack.EMPTY);
	}

	public void updateBeforeRender(PoseStack matrixStack, Vector2D ownerSize, float partialTicks, int mouseX, int mouseY) {
		super.updateBeforeRender(matrixStack, ownerSize, partialTicks, mouseX, mouseY);

		if (isHovered()) {
			hoveredScale = Math.min(1, hoveredScale + partialTicks * 0.45f);
		} else {
			hoveredScale = Math.max(0, hoveredScale - partialTicks * 0.45f);
		}

		setPosition(getInitialPosition().getX(), this.getInitialPosition().getY() - (hoveredScale * getSize().getY() / 4));
	}

	@Override
	public void renderWidgetBackground(PoseStack pose, int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		DEBUG_HOVER = false;

		Team team = TeamManager.get().getTeamForPlayer(Minecraft.getInstance().player).orElse(null);
		boolean hovered = this.isPointInsideBounds(new Vector2D(mouseX, mouseY));
		String translatedTitle = new TranslatableComponent(research.getTitle()).getString();

		int miniWidth = 24;
		int miniHeight = 24;

		int lerpedWidth = (int) (miniWidth + ((Minecraft.getInstance().font.width(translatedTitle) + 4 - miniWidth) * hoveredScale));
		int lerpedHeight = (int) (miniHeight + ((48 - miniHeight) * hoveredScale));
		this.setSize(lerpedWidth, lerpedHeight);

		float width = getSize().getX();
		float height = getSize().getY();

		Color drawColor;
		if (hovered) {
			drawColor = new Color(0.75f, 0.75f, 1.0f, 0.95f);
		} else {
			drawColor = new Color(0.75f, 0.75f, 0.75f, 0.95f);
		}

		GuiDrawUtilities.drawGenericBackground(pose, (int) width, (int) height, 4, 4, 0, drawColor);
		GuiDrawUtilities.drawGenericBackground(pose, (int) 24, (int) 24, 0, 0, 0, new Color(0.6f, 0.4f, 1.0f, 1.0f));

		itemRenderer.setItemStack(research.getItemIcon());
		itemRenderer.setSize(1.0f, 1.0f);
		itemRenderer.draw(pose, 4, 4, 100);

		if (isHovered()) {
			GuiDrawUtilities.drawStringLeftAligned(pose, translatedTitle, 28, 16, 255.0f, 0.75f, Color.EIGHT_BIT_WHITE, true);
			progressBar.setVisible(true);
			for (int i = 0; i < research.getRequirements().size(); i++) {
				int xOffset = i * 20;
				StaticPowerIngredient requirement = research.getRequirements().get(i);
				itemRenderer.setItemStack(requirement.getIngredient().getItems()[0]);
				itemRenderer.setSize(0.75f, 0.75f);
				itemRenderer.draw(pose, getSize().getX() - 19 - xOffset, 28, 1.0f);

				GuiDrawUtilities.drawRectangle(pose, 14, 14, 11 + getSize().getX() - 29 - xOffset, 29, 0, new Color(0.0f, 0.0f, 0.0f, 0.5f));
				if (team != null && team.isResearching(research.getId())) {
					GuiDrawUtilities.drawStringCentered(pose, Integer.toString(requirement.getCount() - team.getResearchProgress(research.getId()).getRequirementFullfillment(i)),
							12 + getSize().getX() - 22 - xOffset, 47, 0, 0.5f, Color.EIGHT_BIT_YELLOW, true);
				} else {
					GuiDrawUtilities.drawStringCentered(pose, Integer.toString(requirement.getCount()), 12 + getSize().getX() - 22 - xOffset, 47, 0, 0.5f, Color.EIGHT_BIT_WHITE, true);
				}

			}
			if (team != null) {
				progressBar.setSize(width - 28, 7);
				if (team.isResearching(research.getId())) {
					progressBar.setCurrentProgress((int) (team.getResearchProgress(research.getId()).getFullfillmentPercentage() * 100));
				} else {
					progressBar.setCurrentProgress(0);
				}
			}
		} else {
			progressBar.setVisible(false);
		}
	}

	@Override
	public void renderWidgetForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
	}

	public void getTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		// tooltips.add(new TranslatableComponent(research.getDescription()));
	}
}
