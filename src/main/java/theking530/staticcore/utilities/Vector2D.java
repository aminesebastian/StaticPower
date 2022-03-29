package theking530.staticcore.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;

public class Vector2D extends AbstractVector<Vector2D> {

	public Vector2D(double x, double y) {
		super((float) x, (float) y);
	}

	public Vector2D(float x, float y) {
		super(x, y);
	}

	public Vector2D() {
		this(0.0f, 0.0f);
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
		values[0] *= x;
		values[1] *= y;
		return this;
	}

	public static Vector2D fromBuffer(FriendlyByteBuf buff) {
		return new Vector2D(buff.readFloat(), buff.readFloat());
	}

	public static Vector2D fromJson(JsonElement element) {
		JsonArray obj = element.getAsJsonArray();
		return new Vector2D(obj.get(0).getAsFloat(), obj.get(1).getAsFloat());
	}

	@Override
	public Vector2D copy() {
		return new Vector2D(values[0], values[1]);
	}
}
