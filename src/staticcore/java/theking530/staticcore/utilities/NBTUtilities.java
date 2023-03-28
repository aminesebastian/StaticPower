package theking530.staticcore.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class NBTUtilities {
	public static <T> ListTag serialize(Iterable<T> iterable, BiConsumer<T, CompoundTag> callback) {
		ListTag output = new ListTag();
		iterable.forEach(thing -> {
			CompoundTag newTag = new CompoundTag();
			callback.accept(thing, newTag);
			output.add(newTag);
		});
		return output;
	}

	public static <T> ListTag serialize(Iterable<T> iterable, Function<T, Tag> callback) {
		ListTag output = new ListTag();
		iterable.forEach(thing -> {
			output.add(callback.apply(thing));
		});
		return output;
	}

	public static <T> List<T> deserialize(ListTag tagList, Function<Tag, T> callback) {
		return deserialize(tagList, false, callback);
	}
	public static <T> List<T> deserialize(ListTag tagList,  boolean skipNull, Function<Tag, T> callback) {
		List<T> output = new ArrayList<T>();
		for (Tag tag : tagList) {
			T result = callback.apply(tag);
			if(result == null && skipNull) {
				continue;
			}
			output.add(result);
		}

		return output;
	}
}
