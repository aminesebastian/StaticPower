package theking530.staticpower.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import net.minecraft.nbt.CompoundTag;
import theking530.staticpower.StaticPower;

public class FileUtilities {
	public static void writeDataToFile(Path dataPath, CompoundTag tag, String fileName) throws IOException {
		String formattedName = String.format("%1$s/%2$s_%3$s.json", dataPath.toAbsolutePath().toString(), StaticPower.MOD_ID, fileName);
		BufferedWriter writer = new BufferedWriter(new FileWriter(formattedName));
		writer.write(JsonUtilities.nbtToPrettyJson(tag));
		writer.close();
	}
}
