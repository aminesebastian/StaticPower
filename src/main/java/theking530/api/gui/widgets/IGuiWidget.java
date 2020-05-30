package theking530.api.gui.widgets;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import theking530.staticpower.client.gui.StaticPowerContainerGui;

public interface IGuiWidget {
	enum EInputResult {
		HANDLED, UNHANDLED
	}

	public void setOwningGui(StaticPowerContainerGui<?> owningGui);

	/* Misc */
	public boolean isVisible();

	public IGuiWidget setVisible(boolean isVisible);

	public IGuiWidget setPosition(int xPos, int yPos);

	public IGuiWidget setSize(int xSize, int ySize);

	/* Render Events */
	public void renderBackground(int mouseX, int mouseY, float partialTicks);

	public void renderForeground(int mouseX, int mouseY, float partialTicks);

	/* Tooltip */
	public boolean shouldDrawTooltip(int mouseX, int mouseY);

	public void getTooltips(List<ITextComponent> tooltips, boolean showAdvanced);

	/* Input Events */
	public default EInputResult mouseClick(int mouseX, int mouseY, int button) {
		return EInputResult.UNHANDLED;
	}

	public void mouseHover(int mouseX, int mouseY);
}
