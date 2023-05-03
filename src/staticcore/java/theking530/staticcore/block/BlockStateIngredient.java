package theking530.staticcore.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.utilities.JsonUtilities;

public class BlockStateIngredient implements Predicate<BlockState> {
	public static final Codec<BlockStateIngredient> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
		try {
			BlockStateIngredient ingredient = BlockStateIngredient
					.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue());
			return DataResult.success(ingredient);
		} catch (Exception e) {
			return DataResult.error(e.getMessage());
		}
	}, ingredient -> new Dynamic<JsonElement>(JsonOps.INSTANCE, ingredient.toJson()));

	// Because Mojang caches things... we need to invalidate them.. so... here we
	// go..
	private static final java.util.concurrent.atomic.AtomicInteger INVALIDATION_COUNTER = new java.util.concurrent.atomic.AtomicInteger();
	public static final BlockStateIngredient EMPTY = new BlockStateIngredient(Stream.empty());

	public static void invalidateAll() {
		INVALIDATION_COUNTER.incrementAndGet();
	}

	private final BlockStateIngredient.Value[] values;
	@Nullable
	private BlockState[] blockstates;

	protected BlockStateIngredient(Stream<? extends BlockStateIngredient.Value> stream) {
		this.values = stream.toArray((value) -> {
			return new BlockStateIngredient.Value[value];
		});
	}

	public static BlockStateIngredient of() {
		return EMPTY;
	}

	public static BlockStateIngredient of(Block... blocks) {
		return of(Arrays.stream(blocks).map((block) -> block.defaultBlockState()));
	}

	public static BlockStateIngredient of(BlockState... blockStates) {
		return of(Arrays.stream(blockStates));
	}

	public static BlockStateIngredient of(Stream<BlockState> blockStates) {
		return fromValues(blockStates.filter((p_43944_) -> {
			return !p_43944_.isAir();
		}).map((blockState) -> new BlockStateIngredient.BlockStateValue(blockState)));
	}

	public static BlockStateIngredient of(TagKey<Block> blockTag) {
		return fromValues(Stream.of(new BlockStateIngredient.TagValue(blockTag)));
	}

	public BlockState[] getBlockStates() {
		this.dissolve();
		return this.blockstates;
	}

	public boolean isEmpty() {
		this.dissolve();
		return values.length == 0;
	}

	public boolean test(Block block) {
		return test(block.defaultBlockState());
	}

	@Override
	public boolean test(BlockState blockState) {
		if (blockState == null) {
			return false;
		} else {
			this.dissolve();
			if (this.blockstates.length == 0) {
				return blockState.isAir();
			} else {
				for (BlockState state : this.blockstates) {
					if (state.equals(blockState)) {
						return true;
					}
				}
				return false;
			}
		}
	}

	private void dissolve() {
		if (this.blockstates == null) {
			this.blockstates = Arrays.stream(values).flatMap((value) -> {
				return value.getBlocks().stream();
			}).distinct().toArray((size) -> {
				return new BlockState[size];
			});
		}
	}

	public static class BlockStateValue implements BlockStateIngredient.Value {
		private final BlockState blockState;

		public BlockStateValue(BlockState blockState) {
			this.blockState = blockState;
		}

		public Collection<BlockState> getBlocks() {
			return Collections.singleton(blockState);
		}

		public JsonObject serialize() {
			return JsonUtilities.blockStateToJson(blockState).getAsJsonObject();
		}

		public static BlockStateValue fromBuffer(FriendlyByteBuf buffer) {
			JsonObject json = JsonUtilities.nbtToJsonObject(buffer.readNbt());
			BlockState state = JsonUtilities.blockStateFromJson(json);
			if (state == null) {
				throw new RuntimeException("Unable to parse blockstate from bytebuffer.");
			}

			return new BlockStateValue(state);
		}

		@Override
		public void toBuffer(FriendlyByteBuf buffer) {
			JsonElement element = JsonUtilities.blockStateToJson(blockState);
			CompoundTag tag = JsonUtilities.jsonToNbt(element);
			buffer.writeNbt(tag);
		}
	}

	public static class TagValue implements BlockStateIngredient.Value {
		private final TagKey<Block> tag;

		public TagValue(TagKey<Block> blockTags) {
			this.tag = blockTags;
		}

		public Collection<BlockState> getBlocks() {
			List<BlockState> list = Lists.newArrayList();

			for (Block holder : ForgeRegistries.BLOCKS.tags().getTag(tag).stream().toList()) {
				list.add(holder.defaultBlockState());
			}

			if (list.size() == 0) {
				list.add(Blocks.AIR.defaultBlockState());
			}

			return list;
		}

		public JsonObject serialize() {
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("tag", this.tag.location().toString());
			return jsonobject;
		}

		public static TagValue fromBuffer(FriendlyByteBuf buffer) {
			String encodedTag = buffer.readUtf();
			return new TagValue(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(encodedTag)));
		}

		@Override
		public void toBuffer(FriendlyByteBuf buffer) {
			buffer.writeUtf(tag.location().toString());
		}
	}

	public void writeToBuffer(FriendlyByteBuf buffer) {
		buffer.writeInt(values.length);
		for (BlockStateIngredient.Value value : values) {
			if (value instanceof BlockStateValue) {
				buffer.writeChar('f');
			} else if (value instanceof TagValue) {
				buffer.writeChar('t');
			}
			value.toBuffer(buffer);
		}
	}

	public static BlockStateIngredient readFromBuffer(FriendlyByteBuf buffer) {
		int valueCount = buffer.readInt();
		List<BlockStateIngredient.Value> values = new ArrayList<>();
		for (int i = 0; i < valueCount; i++) {
			char type = buffer.readChar();
			if (type == 'f') {
				values.add(BlockStateValue.fromBuffer(buffer));
			} else if (type == 't') {
				values.add(TagValue.fromBuffer(buffer));
			}
		}

		return fromValues(values.stream());
	}

	public static BlockStateIngredient fromValues(Stream<? extends BlockStateIngredient.Value> values) {
		BlockStateIngredient ingredient = new BlockStateIngredient(values);
		return ingredient.values.length == 0 ? EMPTY : ingredient;
	}

	public JsonElement toJson() {
		JsonObject output = new JsonObject();

		if (this.values.length == 1) {
			output.add("block", this.values[0].serialize());
		} else {
			JsonArray jsonarray = new JsonArray();
			for (BlockStateIngredient.Value ingredient$value : this.values) {
				jsonarray.add(ingredient$value.serialize());
			}
			output.add("block", jsonarray);
		}
		return output;
	}

	public static BlockStateIngredient fromJson(JsonElement jsonElement) {
		if (!jsonElement.isJsonObject()) {
			throw new RuntimeException(
					String.format("BlockStateIngredient cannot be deserialized from non-object json: %1$s.", jsonElement));
		}

		JsonObject json = jsonElement.getAsJsonObject();
		JsonElement blockJson = json.get("block");
		if (blockJson != null && !blockJson.isJsonNull()) {
			if (blockJson.isJsonObject()) {
				return fromValues(Stream.of(valueFromJson(blockJson.getAsJsonObject())));
			} else if (blockJson.isJsonArray()) {
				JsonArray jsonarray = blockJson.getAsJsonArray();
				if (jsonarray.size() == 0) {
					throw new JsonSyntaxException(
							"BlockState array cannot be empty, at least one item must be defined");
				} else {
					return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((p_151264_) -> {
						return valueFromJson(GsonHelper.convertToJsonObject(p_151264_, "item"));
					}));
				}
			} else {
				throw new JsonSyntaxException("Expected item to be object or array of objects");
			}
		} else {
			throw new JsonSyntaxException("Block cannot be null");
		}
	}

	public static BlockStateIngredient.Value valueFromJson(JsonObject json) {
		if (json.has("Name") && json.has("tag")) {
			throw new JsonParseException("An ingredient entry is either a tag or a blockstate, not both");
		} else if (json.has("Name")) {
			BlockState blockState = JsonUtilities.blockStateFromJson(json);
			return new BlockStateIngredient.BlockStateValue(blockState);
		} else if (json.has("tag")) {
			ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
			TagKey<Block> tagkey = TagKey.create(Registry.BLOCK_REGISTRY, resourcelocation);
			return new BlockStateIngredient.TagValue(tagkey);
		} else {
			throw new JsonParseException("An ingredient entry needs either a tag or a blockstate");
		}
	}

	public interface Value {
		Collection<BlockState> getBlocks();

		JsonObject serialize();

		void toBuffer(FriendlyByteBuf buffer);
	}
}