package theking530.staticpower.blockentities.machines.hydroponicpod;

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.widgets.AbstractGuiWidget;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HydroponicPodProgressWidget extends AbstractGuiWidget<GuiPowerBarFromStorage> {

	private BlockEntityHydroponicPod currentPod;

	public HydroponicPodProgressWidget(float xPosition, float yPosition, float width, float height) {
		super(xPosition, yPosition, width, height);
	}

	public void setPod(BlockEntityHydroponicPod pod) {
		this.currentPod = pod;
	}

	public void clearPod() {
		this.currentPod = null;
	}

	@SuppressWarnings("resource")
	@Override
	public void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		float growthPercentage = currentPod != null ? currentPod.getGrowthPercentage() : 0;

		GuiDrawUtilities.drawGenericBackground(matrix, this.getWidth(), getHeight(), 0, 0);

		matrix.pushPose();
		matrix.scale(1, 0.85f, 1);
		float barXPos = 5;
		float barYPos = (getHeight() - 8);
		float width = getWidth() - 10;
		GuiDrawUtilities.drawGenericBackground(matrix, width, 7, barXPos, barYPos, 1, new SDColor(0.4f, 0.4f, 0.4f, 1.0f));
		if (growthPercentage > 0) {
			GuiDrawUtilities.drawGenericBackground(matrix, Math.max(7, width * growthPercentage), 7, barXPos, barYPos, 1, new SDColor(0.2f, 0.5f, 1.4f, 1.0f));
		}
		matrix.popPose();

		if (currentPod == null) {
			GuiDrawUtilities.drawStringCentered(matrix, "Missing Pod", getWidth() / 2.0f, 10, 1, 0.5f, SDColor.EIGHT_BIT_DARK_GREY, false);
			return;
		}

		Optional<BlockState> state = currentPod.getPlantBlockStateForHarvest();
		if (state.isEmpty()) {
			GuiDrawUtilities.drawStringCentered(matrix, "Empty", getWidth() / 2.0f, 10, 1, 0.5f, SDColor.EIGHT_BIT_DARK_GREY, false);
			return;
		}

		long gameTime = getMinecraft().level.getGameTime();
		GuiDrawUtilities.drawBlockState(matrix, state.get(), BlockPos.ZERO, EmptyModelData.INSTANCE, new Vector3D(5.5f, 5.5f, 1), new Vector3D(15, gameTime + partialTicks, 180),
				new Vector3D(8, 8, -1));
		GuiDrawUtilities.drawStringLeftAligned(matrix, state.get().getBlock().getName().getString(), 15.5f, 10, 1, 0.5f, SDColor.EIGHT_BIT_DARK_GREY, false);
	}

	@Override
	protected void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		if (currentPod == null) {
			tooltips.add(new TextComponent("Missing Pod!"));
			return;
		}

		Optional<BlockState> state = currentPod.getPlantBlockStateForHarvest();
		if (state.isEmpty()) {
			tooltips.add(new TextComponent("Pod not currently active!"));
			return;
		}

		tooltips.add(new TextComponent("Currently growing:").withStyle(ChatFormatting.AQUA));
		tooltips.add(new TextComponent("• ").append(state.get().getBlock().getName()));
		tooltips.add(new TextComponent("Growth Progress: ").withStyle(ChatFormatting.GREEN));
		tooltips.add(new TextComponent("• ").append(GuiTextUtilities.formatNumberAsPercentStringNoDecimal(currentPod.getGrowthPercentage())));
	}

}
