package theking530.common.utilities;

import net.minecraft.client.renderer.Rectangle2d;

public class RectangleBounds extends AbstractVector {
	public RectangleBounds(int x, int y, int width, int height) {
		super((float) x, (float) y, (float) width, (float) height);
	}

	public RectangleBounds(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	public int getX() {
		return Math.round(values.get(0));
	}

	public int getY() {
		return Math.round(values.get(1));
	}

	public int getWidth() {
		return Math.round(values.get(2));
	}

	public int getHeight() {
		return Math.round(values.get(3));
	}

	public Rectangle2d toRectange2d() {
		return new Rectangle2d((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
	}
}
