package theking530.staticcore.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class Vector3D extends Vector2D {
	public static final Vector3D ZERO = new Vector3D(0, 0, 0);

	public Vector3D(float x, float y, float z) {
		super(x, y);
		values.add(z);

	}

	public Vector3D(BlockPos pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}

	public Vector3D(Direction direction) {
		this(direction.getStepX(), direction.getStepY(), direction.getStepZ());
	}

	public float getZ() {
		return values.get(2);
	}

	public void setZ(float z) {
		values.set(2, z);
	}

	public int getZi() {
		return Math.round(values.get(2));
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
		return new Vector3D(values.get(0), values.get(1), values.get(2));
	}
}
