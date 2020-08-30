package theking530.staticcore.utilities;

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

	public RectangleBounds setX(float value) {
		this.values.set(0, value);
		return this;
	}

	public RectangleBounds setY(float value) {
		this.values.set(1, value);
		return this;
	}

	public RectangleBounds setWidth(float value) {
		this.values.set(2, value);
		return this;
	}

	public RectangleBounds setHeight(float value) {
		this.values.set(3, value);
		return this;
	}

	public void update(float x, float y, float width, float height) {
		this.values.set(0, x);
		this.values.set(1, y);
		this.values.set(2, width);
		this.values.set(3, height);
	}

	public Rectangle2d toRectange2d() {
		return new Rectangle2d((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
	}

	public boolean isPointInBounds(Vector2D point) {
		return point.getX() >= getX() && point.getX() < getX() + getWidth() && point.getY() >= getY() && point.getY() < getY() + getHeight();
	}
}
