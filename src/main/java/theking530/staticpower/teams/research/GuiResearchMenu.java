package theking530.staticpower.teams.research;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerDetatchedGui;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchLevels;
import theking530.staticpower.data.research.ResearchLevels.ResearchLevel;

public class GuiResearchMenu extends StaticPowerDetatchedGui {

	public GuiResearchMenu() {
		super(400, 400);
		setDrawDefaultDarkBackground(false);
	}

	@Override
	public void initializeGui() {
		ResearchLevels levels = ResearchLevels.getAllResearchLevels();

		for (int y = 0; y < levels.getLevels().size(); y++) {
			ResearchLevel level = levels.getLevels().get(y);

			for (int i = 0; i < level.getResearch().size(); i++) {
				Research research = level.getResearch().get(i);
				this.registerWidget(new ResearchWidget(research, 110 + (i * 200), 40 + y * 80, 125, 40));
			}
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// We have to do this as Screens don't usually have this called.
		this.renderBackground(pose);
		super.render(pose, mouseX, mouseY, partialTicks);
	}

	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		GuiDrawUtilities.drawRectangle(pose, getMinecraft().screen.width, getMinecraft().screen.height, 0, 0, -0.1f, new Color(0, 0, 0, 0.5f));
		GuiDrawUtilities.drawGenericBackground(pose, 100, this.getMinecraft().screen.height + 8, -5, -4, 1.0f, new Color(0.75f, 0.5f, 1.0f, 0.85f));

		for (int i = 0; i < Math.ceil(getMinecraft().screen.height / 80) + 1; i++) {
			float tint = i % 2 == 0 ? 0.1f : 0.0f;
			GuiDrawUtilities.drawRectangle(pose, getMinecraft().screen.width, 80, 94, i * 80 + 15, -0.1f, new Color(tint, tint, tint, 0.75f));
		}
	}

	public static void tryOpen() {
		if (Minecraft.getInstance().screen == null) {
			Minecraft.getInstance().setScreen(new GuiResearchMenu());
		}
	}
}
