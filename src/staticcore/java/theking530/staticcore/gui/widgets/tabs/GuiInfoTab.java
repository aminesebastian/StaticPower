package theking530.staticcore.gui.widgets.tabs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.drawables.ItemDrawable;
import theking530.staticcore.utilities.SDColor;

@OnlyIn(Dist.CLIENT)
public class GuiInfoTab extends AbstractInfoTab {
	public GuiInfoTab(Component title, int width) {
		this(title.getString(), width);
	}

	public GuiInfoTab(int width) {
		this("Info", width);
	}

	public GuiInfoTab(String title, int width) {
		super(title, new SDColor(0, 242, 255), width,  new SDColor(0.1f, 0.6f, 0.1f, 1), new ItemDrawable(Items.PAPER));
	}
}
