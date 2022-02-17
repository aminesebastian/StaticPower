package theking530.staticpower.teams;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.gui.widgets.progressbars.SimpleProgressBar;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerHUDElement;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.Research.ResearchInstance;

public class ActiveResearchHUD extends StaticPowerHUDElement {
	private final ItemDrawable itemRenderer;
	private final SimpleProgressBar progressBar;

	public ActiveResearchHUD() {
		super(0, 0);
		itemRenderer = new ItemDrawable(ItemStack.EMPTY);
		registerWidget(progressBar = new SimpleProgressBar(0, 0, 86, 7));
	}

	@Override
	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		int screenRight = getWindow().getGuiScaledWidth();
		GuiDrawUtilities.drawGenericBackground(115, 45, screenRight - 118, 3, 0, new Color(1, 0.5f, 1, 0.085f));

		Team team = TeamManager.get().getTeamForPlayer(Minecraft.getInstance().player).orElse(null);
		if (team != null) {
			ResearchInstance current = team.getCurrentResearch();
			Research research = current.getTrackedResearch();
			itemRenderer.setItemStack(current.getTrackedResearch().getItemIcon());
			itemRenderer.draw(screenRight - 113, 7);
			GuiDrawUtilities.drawStringWithSizeLeftAligned(pose, new TranslatableComponent(research.getTitle()).getString(), screenRight - 93, 15, 0.75f, Color.EIGHT_BIT_WHITE, true);

			progressBar.setPosition(screenRight - 96, 19);
			progressBar.setCurrentProgress(73);
			progressBar.setMaxProgress(100);

			for (int i = 0; i < research.getRequirements().size(); i++) {
				int xOffset = i * 20;
				StaticPowerIngredient requirement = research.getRequirements().get(i);
				itemRenderer.setItemStack(requirement.getIngredient().getItems()[0]);
				itemRenderer.draw(screenRight - 25 - xOffset, 27);

				pose.pushPose();
				pose.translate(0, 0, 255);
				//GuiDrawUtilities.drawStringWithSize(pose, Integer.toString(requirement.getCount() - current.getRequirementFullfillment(i)), screenRight - 8 - xOffset, 42, 0.75f, Color.EIGHT_BIT_WHITE,
				//		true);
				pose.popPose();
			}
		}

	}

	@Override
	protected void drawBehindItems(PoseStack pose, float partialTicks, int mouseX, int mouseY) {

	}

	@Override
	protected void drawForegroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
	}
}
