package theking530.staticpower.teams.research;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.StaticPowerDetatchedGui;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchLevels;
import theking530.staticpower.data.research.ResearchLevels.ResearchLevel;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

@SuppressWarnings("resource")
public class GuiResearchMenu extends StaticPowerDetatchedGui {
	private int levelHeight;

	public GuiResearchMenu() {
		super(400, 400);
		setDrawDefaultDarkBackground(false);

		ActiveResearchWidget active = new ActiveResearchWidget(0, 0, 0, 0);
		// Keep the research updated.
		Team team = TeamManager.get().getTeamForPlayer(Minecraft.getInstance().player).orElse(null);
		if (team != null) {
			active.setTeam(team);
			if (team.isResearching()) {
				active.setResearch(team.getCurrentResearch());
			} else {
				active.setResearch(team.getLastCompletedResearch());
			}
			active.setDrawBackground(false);
		}
		this.registerWidget(active);
	}

	@Override
	public void initializeGui() {
		levelHeight = 80;
		ResearchLevels levels = ResearchLevels.getAllResearchLevels();

		int levelWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth() - 120;
		for (int y = 0; y < levels.getLevels().size(); y++) {
			ResearchLevel level = levels.getLevels().get(y);
			int distanceBetween = levelWidth / (level.getResearch().size() + 1);
			for (int i = 0; i < level.getResearch().size(); i++) {
				Research research = level.getResearch().get(i);
				this.registerWidget(new ResearchNodeWidget(research, 120 + ((i + 1) * distanceBetween), 25 + y * levelHeight, 125, 40));
			}
		}
	}

	@Override
	public void tick() {
		super.tick();

	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		// We have to do this as Screens don't usually have this called.
		this.renderBackground(pose);
		super.render(pose, mouseX, mouseY, partialTicks);
	}

	protected void drawBackgroundExtras(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.enableBlend();

		// Draw the sidebar and background bar.
		GuiDrawUtilities.drawRectangle(pose, getMinecraft().screen.width, getMinecraft().screen.height, 0, 0, -0.1f, new Color(0, 0, 0, 0.5f));
		GuiDrawUtilities.drawGenericBackground(pose, 120, getMinecraft().screen.height + 8, -5, -4, 0.0f, new Color(0.75f, 0.5f, 1.0f, 0.85f));
		GuiDrawUtilities.drawGenericBackground(pose, 123, 80, -4, -4, 0.0f, new Color(0.25f, 0.5f, 1.0f, 1.0f));

		// Draw the level backgrounds.
		for (int i = 0; i < Math.ceil(getMinecraft().screen.height / levelHeight) + 1; i++) {
			float tint = i % 2 == 0 ? 0.1f : 0.0f;
			GuiDrawUtilities.drawRectangle(pose, getMinecraft().screen.width, levelHeight, 115, i * levelHeight, -0.1f, new Color(tint, tint, tint, 0.75f));
		}

		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.enableBlend();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(pose.last().pose(), 394, 53, 1).color(1, 0, 0, 1.0f).endVertex();
		bufferbuilder.vertex(pose.last().pose(), 302, 105, 1).color(0, 1, 1, 1.0f).endVertex();
		bufferbuilder.vertex(pose.last().pose(), 305, 105, 1).color(0, 1, 1, 1.0f).endVertex();
		bufferbuilder.vertex(pose.last().pose(), 397, 53, 1).color(1, 0, 0, 1.0f).endVertex();
		tessellator.end();
	}

	public static void tryOpen() {
		if (Minecraft.getInstance().screen == null) {
			Minecraft.getInstance().setScreen(new GuiResearchMenu());
		} else if (Minecraft.getInstance().screen instanceof GuiResearchMenu) {
			Minecraft.getInstance().screen.onClose();
		}
	}
}
