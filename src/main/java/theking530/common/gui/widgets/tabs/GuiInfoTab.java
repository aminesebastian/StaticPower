package theking530.common.gui.widgets.tabs;

import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import theking530.common.gui.GuiTextures;
import theking530.common.gui.drawables.ItemDrawable;
import theking530.common.utilities.Color;

public class GuiInfoTab extends AbstractInfoTab {
	public GuiInfoTab(ITextComponent title, int width) {
		this(title.getFormattedText(), width);
	}

	public GuiInfoTab(int width) {
		this("Info", width);
	}

	public GuiInfoTab(String title, int width) {
		super(title, width, GuiTextures.GREEN_TAB, new ItemDrawable(Items.PAPER), new Color(0, 242, 255));
	}
}
