package theking530.staticpower.utilities;

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
		iterable.forEach(player -> {
			CompoundTag newTag = new CompoundTag();
			callback.accept(player, newTag);
			output.add(newTag);
		});
		return output;
	}

	public static <T> ListTag serialize(Iterable<T> iterable, Function<T, CompoundTag> callback) {
		ListTag output = new ListTag();
		iterable.forEach(player -> {
			CompoundTag newTag = callback.apply(player);
			output.add(newTag);
		});
		return output;
	}

	public static <T> List<T> deserialize(ListTag tagList, Function<CompoundTag, T> callback) {
		List<T> output = new ArrayList<T>();
		for (Tag tag : tagList) {
			CompoundTag tagCompound = (CompoundTag) tag;
			output.add(callback.apply(tagCompound));
		}

		return output;
	}
}
