package theking530.staticcore.gui.widgets.tabs;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.CurrentType;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.StaticCoreGuiTextures;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticcore.utilities.SDColor;

@OnlyIn(Dist.CLIENT)
public class GuiPowerInfoTab extends AbstractInfoTab {
	protected PowerStorageComponent energyStorage;
	private String inputLabel;
	private String maxInputLabel;

	private String outputLabel;
	private String maxOutputLabel;
	private int connectedPowerLineWidth;

	public GuiPowerInfoTab(PowerStorageComponent storage) {
		super("Power I/O", new SDColor(242, 0, 255), 105, new SDColor(0.6f, 0.1f, 1.0f), new SpriteDrawable(StaticCoreGuiTextures.POWER_TAB_ICON, 16, 16));
		this.energyStorage = storage;

		this.inputLabel = "gui.staticcore.power_tab.power_input";
		this.maxInputLabel = "gui.staticcore.power_tab.max_power_input";
		this.outputLabel = "gui.staticcore.power_tab.power_output";
		this.maxOutputLabel = "gui.staticcore.power_tab.max_power_output";
		this.connectedPowerLineWidth = 0;
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		super.tick();
		clear();

		if (isHovered()) {
			addKeyValueTwoLiner("Input", Component.translatable(maxInputLabel), PowerTextFormatting.formatPowerRateToString(energyStorage.getMaximumPowerInput()),
					ChatFormatting.YELLOW);
		} else {
			addKeyValueTwoLiner("Input", Component.translatable(inputLabel), PowerTextFormatting.formatPowerRateToString(energyStorage.getAveragePowerAddedPerTick()),
					ChatFormatting.GREEN);
		}

		if (isHovered()) {
			addKeyValueTwoLiner("Output", Component.translatable(maxOutputLabel), PowerTextFormatting.formatPowerRateToString(energyStorage.getMaximumPowerOutput()),
					ChatFormatting.LIGHT_PURPLE);
		} else {
			addKeyValueTwoLiner("Output", Component.translatable(outputLabel), PowerTextFormatting.formatPowerRateToString(energyStorage.getAveragePowerUsedPerTick()),
					ChatFormatting.RED);
		}
		MutableComponent connPowerLine = PowerTextFormatting.formatVoltageToString(energyStorage.getLastRecievedVoltage());
		connectedPowerLineWidth = Minecraft.getInstance().font.width(connPowerLine);
		addKeyValueTwoLiner("Connection", Component.translatable("gui.staticcore.power_tab.connected_power"), connPowerLine, ChatFormatting.AQUA);
	}

	@Override
	protected void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (isOpen()) {
			ResourceLocation currentTypeTexture = StaticCoreGuiTextures.DC_POWER_INDICATOR;
			if (energyStorage.getLastRecievedCurrentType() == CurrentType.ALTERNATING) {
				currentTypeTexture = StaticCoreGuiTextures.AC_POWER_INDICATOR;
			}

			GuiDrawUtilities.drawTexture(matrix, currentTypeTexture, 12, 12, connectedPowerLineWidth + 17, 75.5f, MinecraftColor.WHITE.getColor());
		}
	}

	public void setInputLabel(String inputLabel) {
		this.inputLabel = inputLabel;
	}

	public void setMaxInputLabel(String maxInputLabel) {
		this.maxInputLabel = maxInputLabel;
	}

	public void setOutputLabel(String outputLabel) {
		this.outputLabel = outputLabel;
	}

	public void setMaxOutputLabel(String maxOutputLabel) {
		this.maxOutputLabel = maxOutputLabel;
	}
}
