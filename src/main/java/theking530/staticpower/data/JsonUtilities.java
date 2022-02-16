package theking530.staticpower.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.nbt.CompoundTag;

public class JsonUtilities {
	public static String nbtToPrettyJson(CompoundTag tag) {
		JsonObject jsonObject = JsonParser.parseString(tag.toString()).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(jsonObject);
	}
}
