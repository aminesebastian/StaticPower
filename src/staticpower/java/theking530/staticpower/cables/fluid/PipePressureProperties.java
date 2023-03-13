package theking530.staticpower.cables.fluid;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class PipePressureProperties {
	private float down;
	private float horizontal;
	private float up;

	public PipePressureProperties(CompoundTag tag) {
		deserialize(tag);
	}

	public PipePressureProperties(float down, float horizontal, float up) {
		this.down = down;
		this.horizontal = horizontal;
		this.up = up;
	}

	public float getPressureDeltaForToDirection(Direction direction) {
		if (direction == Direction.UP) {
			return up;
		}
		if (direction == Direction.DOWN) {
			return down;
		}
		return horizontal;
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putFloat("Down", down);
		tag.putFloat("Horizontal", horizontal);
		tag.putFloat("Up", up);
		return tag;
	}

	public void deserialize(CompoundTag tag) {
		down = tag.getFloat("Down");
		horizontal = tag.getFloat("Horizontal");
		up = tag.getFloat("Up");
	}
}
