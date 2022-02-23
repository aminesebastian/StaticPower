package theking530.staticcore.utilities;

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

	public Vector3D promote() {
		return new Vector3D(getX(), getY(), 0.0f);
	}

	public Vector2D add(float x, float y) {
		add(new Vector2D(x, y));
		return this;
	}

	public Vector2D subtract(float x, float y) {
		subtract(new Vector2D(x, y));
		return this;
	}

	public Vector2D multiply(float x, float y) {
		values.set(0, values.get(0) * x);
		values.set(1, values.get(1) * y);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Vector2D copy() {
		return new Vector2D(values.get(0), values.get(1));
	}
}
