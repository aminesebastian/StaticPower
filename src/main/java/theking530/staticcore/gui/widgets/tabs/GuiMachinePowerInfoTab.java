package theking530.staticcore.gui.widgets.tabs;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.CurrentType;
import theking530.api.energy.utilities.StaticPowerEnergyTextUtilities;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class GuiMachinePowerInfoTab extends AbstractInfoTab {

	private PowerStorageComponent energyStorage;

	public GuiMachinePowerInfoTab(PowerStorageComponent storage) {
		super("Power I/O", new Color(242, 0, 255), 110, new Color(0.6f, 0.1f, 1.0f), new SpriteDrawable(GuiTextures.POWER_TAB_ICON, 16, 16));
		this.energyStorage = storage;
	}

	@Override
	public void tick() {
		super.tick();
		clear();
		addKeyValueTwoLiner("Input", new TextComponent("Power Input"), StaticPowerEnergyTextUtilities.formatPowerRateToString(energyStorage.getAveragePowerAddedPerTick()),
				ChatFormatting.GREEN);
		addKeyValueTwoLiner("Usage", new TextComponent("Power Usage"), StaticPowerEnergyTextUtilities.formatPowerRateToString(energyStorage.getAveragePowerUsedPerTick()),
				ChatFormatting.RED);
		addKeyValueTwoLiner("Connection", new TextComponent("Conn. Power"), StaticPowerEnergyTextUtilities.formatVoltageToString(energyStorage.getLastRecievedVoltage()),
				ChatFormatting.AQUA);
	}

	@SuppressWarnings("resource")
	@Override
	protected void renderWidgetBehindItems(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (isOpen()) {
			ResourceLocation currentTypeTexture = GuiTextures.DC_POWER_INDICATOR;
			if (energyStorage.getLastRecievedCurrentType() == CurrentType.ALTERNATING) {
				currentTypeTexture = GuiTextures.AC_POWER_INDICATOR;
			}

			String connPowerLine = StaticPowerEnergyTextUtilities.formatVoltageToString(energyStorage.getLastRecievedVoltage()).getString();
			int offset = Minecraft.getInstance().font.width(connPowerLine);
			GuiDrawUtilities.drawTexture(matrix, currentTypeTexture, 12, 12, 18 + offset, 75.5f, MinecraftColor.WHITE.getColor());
		}
	}

	@Override
	protected void renderWidgetForeground(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	protected void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
	}
}
