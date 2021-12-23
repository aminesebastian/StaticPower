package theking530.staticpower.cables.redstone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.INBTSerializable;

public class RedstoneCableConfiguration implements INBTSerializable<CompoundTag> {
	public final RedstoneCableSideConfiguration[] sides;

	public RedstoneCableConfiguration() {
		this.sides = new RedstoneCableSideConfiguration[6];
		for (int i = 0; i < 6; i++) {
			sides[i] = new RedstoneCableSideConfiguration();
		}
	}

	public RedstoneCableSideConfiguration getSideConfig(Direction side) {
		return sides[side.ordinal()];
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();

		// Serialize the side configs.
		for (int i = 0; i < 6; i++) {
			output.put("side" + i, sides[i].serializeNBT());
		}

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// Deserialize the side configs.
		for (int i = 0; i < 6; i++) {
			RedstoneCableSideConfiguration sideConfig = new RedstoneCableSideConfiguration();
			sideConfig.deserializeNBT(nbt.getCompound("side" + i));
			sides[i] = sideConfig;
		}
	}
}
