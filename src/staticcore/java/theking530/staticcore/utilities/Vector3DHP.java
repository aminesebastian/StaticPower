package theking530.staticcore.utilities;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class Vector3DHP extends AbstractVectorHP<Vector3DHP> {
	public static final Vector3DHP ZERO = new Vector3DHP(0, 0, 0);

	public Vector3DHP(double x, double y, double z) {
		super(x, y, z);
	}

	public Vector3DHP(Vector3D lpVector) {
		super(lpVector.getX(), lpVector.getY(), lpVector.getZ());
	}

	public Vector3DHP(Vec3i pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}

	public Vector3DHP(Direction direction) {
		this(direction.getStepX(), direction.getStepY(), direction.getStepZ());
	}

	public double getX() {
		return values[0];
	}

	public long getXi() {
		return Math.round(values[0]);
	}

	public void setX(float x) {
		values[0] = x;
	}

	public void addX(float x) {
		values[0] += x;
	}

	public double getY() {
		return values[1];
	}

	public long getYi() {
		return Math.round(values[1]);
	}

	public void setY(float y) {
		values[1] = y;
	}

	public void addY(float y) {
		values[1] += y;
	}

	public double getZ() {
		return values[2];
	}

	public void setZ(float z) {
		values[2] = z;
	}

	public long getZi() {
		return Math.round(values[2]);
	}

	public Vector3DHP add(float x, float y, float z) {
		add(new Vector3DHP(x, y, z));
		return this;
	}

	public Vector3DHP substract(float x, float y, float z) {
		subtract(new Vector3DHP(x, y, z));
		return this;
	}

	public Vector3DHP substract(Vector3DHP other) {
		subtract(other);
		return this;
	}

	@Override
	public Vector3DHP copy() {
		return new Vector3DHP(values[0], values[1], values[2]);
	}
}
