package theking530.staticpower.data;

import java.io.IOException;
import java.nio.file.Path;

import net.minecraft.nbt.CompoundTag;

public class StaticPowerGameData {
	private final String name;

	public StaticPowerGameData(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void tick() {

	}

	public void load(CompoundTag tag) {
	}

	public CompoundTag serialize(CompoundTag tag) {

		return tag;
	}

	public void saveToDisk(Path dataPath) throws IOException {
		FileUtilities.writeDataToFile(dataPath, serialize(new CompoundTag()), name);
	}
}
