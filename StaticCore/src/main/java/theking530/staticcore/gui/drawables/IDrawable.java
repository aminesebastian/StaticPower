package theking530.staticcore.gui.drawables;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.math.Vector2D;

@OnlyIn(Dist.CLIENT)
public interface IDrawable {
	default void draw(PoseStack pose) {
		draw(pose, 0, 0, 0);
	}

	default void draw(PoseStack pose, float x, float y) {
		draw(pose, x, y, 0.0f);
	}

	public void draw(PoseStack pose, float x, float y, float z);

	public void setSize(float width, float height);

	public Vector2D getSize();
}
