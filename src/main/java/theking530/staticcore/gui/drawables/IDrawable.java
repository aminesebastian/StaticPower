package theking530.staticcore.gui.drawables;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IDrawable {
	default void draw(float x, float y) {
		draw(x, y, 0.0f);
	}

	public void draw(float x, float y, float z);
}
