package theking530.common.utilities;

public class Vector2D extends AbstractVector {

	public Vector2D(float x, float y) {
		super(2);
		setX(x);
		setY(y);
	}

	public Vector2D() {
		this(0.0f, 0.0f);
	}

	public float getX() {
		return values.get(0);
	}

	public int getXi() {
		return Math.round(values.get(0));
	}

	public void setX(float x) {
		values.set(0, x);
	}

	public float getY() {
		return values.get(1);
	}

	public int getYi() {
		return Math.round(values.get(1));
	}

	public void setY(float y) {
		values.set(1, y);
	}

	@Override
	public Vector2D clone() {
		return new Vector2D(values.get(0), values.get(1));
	}
}
