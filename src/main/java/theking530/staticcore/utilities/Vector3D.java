package theking530.staticcore.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class Vector3D extends AbstractVector<Vector3D> {
	public static final Vector3D ZERO = new Vector3D(0, 0, 0);

	public Vector3D(float x, float y, float z) {
		super(x, y, z);

	}

	public Vector3D(Vec3i pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}

	public Vector3D(Direction direction) {
		this(direction.getStepX(), direction.getStepY(), direction.getStepZ());
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

	public float getZ() {
		return values[2];
	}

	public void setZ(float z) {
		values[2] = z;
	}

	public int getZi() {
		return Math.round(values[2]);
	}

	public Vector3D add(float x, float y, float z) {
		add(new Vector3D(x, y, z));
		return this;
	}

	public Vector3D substract(float x, float y, float z) {
		subtract(new Vector3D(x, y, z));
		return this;
	}

	public Vector4D promote() {
		return new Vector4D(getX(), getY(), getZ(), 0.0f);
	}

	@Override
	public Vector3D copy() {
		return new Vector3D(values[0], values[1], values[2]);
	}
}
