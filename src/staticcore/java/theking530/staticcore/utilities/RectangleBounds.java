package theking530.staticcore.utilities;

import net.minecraft.client.renderer.Rect2i;

public class RectangleBounds extends AbstractVector<RectangleBounds> {
	public static final RectangleBounds INFINITE_BOUNDS = new RectangleBounds(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);

	public RectangleBounds(int x, int y, int width, int height) {
		super((float) x, (float) y, (float) width, (float) height);
	}

	public RectangleBounds(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	public float getX() {
		return values[0];
	}

	public int getXi() {
		return Math.round(values[0]);
	}

	public void setX(float x) {
		values[0] = x;
	}

	public void addX(float x) {
		values[0] += x;
	}

	public float getY() {
		return values[1];
	}

	public int getYi() {
		return Math.round(values[1]);
	}

	public void setY(float y) {
		values[1] = y;
	}

	public void addY(float y) {
		values[1] += y;
	}

	public int getWidth() {
		return Math.round(values[2]);
	}

	public int getHeight() {
		return Math.round(values[3]);
	}

	public RectangleBounds intersectWith(RectangleBounds in) {
		float leftX = Math.max(getX(), in.getX());
		float rightX = Math.min(getX() + getWidth(), in.getX() + in.getWidth());
		float topY = Math.max(getY(), in.getY());
		float bottomY = Math.min(getY() + getHeight(), in.getY() + in.getHeight());

		return new RectangleBounds(leftX, topY, Math.max(0, rightX - leftX), Math.max(0, bottomY - topY));
	}

	public RectangleBounds setWidth(float value) {
		values[0] = value;
		return this;
	}

	public RectangleBounds setHeight(float value) {
		values[0] = value;
		return this;
	}

	public void update(float x, float y, float width, float height) {
		this.values[0] = x;
		this.values[1] = y;
		this.values[2] = width;
		this.values[3] = height;
	}

	public Rect2i toRectange2d() {
		return new Rect2i((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
	}

	public boolean isPointInBounds(Vector2D point) {
		return point.getX() >= getX() && point.getX() < getX() + getWidth() && point.getY() >= getY() && point.getY() < getY() + getHeight();
	}

	@Override
	public RectangleBounds copy() {
		return new RectangleBounds(values[0], values[1], values[2], values[3]);
	}
}
