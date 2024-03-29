package theking530.staticcore.fluid;

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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidIngredient implements Predicate<FluidStack> {
	public static final Codec<FluidIngredient> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
		try {
			FluidIngredient ingredient = FluidIngredient.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue());
			return DataResult.success(ingredient);
		} catch (Exception e) {
			return DataResult.error(e.getMessage());
		}
	}, ingredient -> new Dynamic<JsonElement>(JsonOps.INSTANCE, ingredient.toJson()));

	// Because Mojang caches things... we need to invalidate them.. so... here we
	// go..
	private static final java.util.concurrent.atomic.AtomicInteger INVALIDATION_COUNTER = new java.util.concurrent.atomic.AtomicInteger();
	public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty(), 0);

	public static void invalidateAll() {
		INVALIDATION_COUNTER.incrementAndGet();
	}

	private final FluidIngredient.Value[] values;
	@Nullable
	private FluidStack[] fluidStacks;
	private int fluidAmount;

	protected FluidIngredient(Stream<? extends FluidIngredient.Value> stream, int amount) {
		this.values = stream.toArray((value) -> {
			return new FluidIngredient.Value[value];
		});
		this.fluidAmount = amount;
	}

	public static FluidIngredient of() {
		return EMPTY;
	}

	public static FluidIngredient of(Fluid... fluids) {
		return of(1, fluids);
	}

	public static FluidIngredient of(int amount, Fluid... fluids) {
		return of(Arrays.stream(fluids).map((fluid) -> new FluidStack(fluid, amount)), amount);
	}

	public static FluidIngredient of(int amount, FluidStack... fluids) {
		return of(Arrays.stream(fluids), amount);
	}

	public static FluidIngredient of(Stream<FluidStack> fluids, int amount) {
		return fromValues(fluids.filter((p_43944_) -> {
			return !p_43944_.isEmpty();
		}).map((fluidstack) -> new FluidIngredient.FluidValue(fluidstack.getFluid())), amount);
	}

	public static FluidIngredient of(TagKey<Fluid> fluidTag, int amount) {
		return fromValues(Stream.of(new FluidIngredient.TagValue(fluidTag)), amount);
	}

	public FluidStack[] getFluids() {
		this.dissolve();
		return this.fluidStacks;
	}

	public boolean isEmpty() {
		this.dissolve();
		return fluidAmount == 0 || values.length == 0;
	}

	public int getAmount() {
		return fluidAmount;
	}

	@Override
	public boolean test(FluidStack fluid) {
		if (fluid == null) {
			return false;
		} else {
			this.dissolve();
			if (this.fluidStacks.length == 0) {
				return fluid.isEmpty();
			} else {
				for (FluidStack fluidstack : this.fluidStacks) {
					if (fluidstack.isFluidEqual(fluid)) {
						return true;
					}
				}
				return false;
			}
		}
	}

	public boolean test(FluidStack fluid, boolean testAmount) {
		if (testAmount) {
			return testWithAmount(fluid);
		}
		return test(fluid);
	}

	public boolean testWithAmount(FluidStack fluid) {
		if (!test(fluid)) {
			return false;
		}

		return fluid.getAmount() >= this.fluidAmount;
	}

	private void dissolve() {
		if (this.fluidStacks == null) {
			this.fluidStacks = Arrays.stream(values).flatMap((value) -> {
				return value.getFluids().stream();
			}).distinct().map((fluid) -> new FluidStack(fluid, fluidAmount)).toArray((size) -> {
				return new FluidStack[size];
			});
		}

	}

	public static class FluidValue implements FluidIngredient.Value {
		private final Fluid fluid;

		public FluidValue(Fluid fluid) {
			this.fluid = fluid;
		}

		public Collection<Fluid> getFluids() {
			return Collections.singleton(fluid);
		}

		public JsonObject serialize() {
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(fluid).toString());
			return jsonobject;
		}

		public static FluidValue fromBuffer(FriendlyByteBuf buffer) {
			ResourceLocation fluidId = new ResourceLocation(buffer.readUtf());
			Fluid foundFluid = Fluids.EMPTY;
			if (ForgeRegistries.FLUIDS.containsKey(fluidId)) {
				foundFluid = ForgeRegistries.FLUIDS.getValue(fluidId);
			}

			return new FluidValue(foundFluid);
		}

		@Override
		public void toBuffer(FriendlyByteBuf buffer) {
			buffer.writeUtf(ForgeRegistries.FLUIDS.getKey(fluid).toString());
		}
	}

	public static class TagValue implements FluidIngredient.Value {
		private final TagKey<Fluid> tag;

		public TagValue(TagKey<Fluid> fluidTag) {
			this.tag = fluidTag;
		}

		public Collection<Fluid> getFluids() {
			List<Fluid> list = Lists.newArrayList();

			for (Fluid holder : ForgeRegistries.FLUIDS.tags().getTag(tag).stream().toList()) {
				list.add(holder);
			}

			if (list.size() == 0) {
				list.add(Fluids.EMPTY);
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
			return new TagValue(TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(encodedTag)));
		}

		@Override
		public void toBuffer(FriendlyByteBuf buffer) {
			buffer.writeUtf(tag.location().toString());
		}
	}

	public void writeToBuffer(FriendlyByteBuf buffer) {
		buffer.writeInt(fluidAmount);
		buffer.writeInt(values.length);
		for (FluidIngredient.Value value : values) {
			if (value instanceof FluidValue) {
				buffer.writeChar('f');
			} else if (value instanceof TagValue) {
				buffer.writeChar('t');
			}
			value.toBuffer(buffer);
		}
	}

	public static FluidIngredient readFromBuffer(FriendlyByteBuf buffer) {
		int amount = buffer.readInt();
		int valueCount = buffer.readInt();
		List<FluidIngredient.Value> values = new ArrayList<>();
		for (int i = 0; i < valueCount; i++) {
			char type = buffer.readChar();
			if (type == 'f') {
				values.add(FluidValue.fromBuffer(buffer));
			} else if (type == 't') {
				values.add(TagValue.fromBuffer(buffer));
			}
		}

		return fromValues(values.stream(), amount);
	}

	public static FluidIngredient fromValues(Stream<? extends FluidIngredient.Value> values, int amount) {
		FluidIngredient ingredient = new FluidIngredient(values, amount);
		return ingredient.values.length == 0 ? EMPTY : ingredient;
	}

	public JsonElement toJson() {
		JsonObject output = new JsonObject();
		output.addProperty("amount", fluidAmount);

		if (this.values.length == 1) {
			output.add("fluids", this.values[0].serialize());
		} else {
			JsonArray jsonarray = new JsonArray();
			for (FluidIngredient.Value ingredient$value : this.values) {
				jsonarray.add(ingredient$value.serialize());
			}
			output.add("fluids", jsonarray);
		}
		return output;
	}

	public static FluidIngredient fromJson(JsonElement jsonElement) {
		if (!jsonElement.isJsonObject()) {
			throw new RuntimeException(
					String.format("FluidIngredient cannot be deserialized from non-object json: %1$s.", jsonElement));
		}

		JsonObject json = jsonElement.getAsJsonObject();
		int amount = json.get("amount").getAsInt();
		JsonElement fluidJson = json.get("fluids");
		if (fluidJson != null && !fluidJson.isJsonNull()) {
			if (fluidJson.isJsonObject()) {
				return fromValues(Stream.of(valueFromJson(fluidJson.getAsJsonObject())), amount);
			} else if (fluidJson.isJsonArray()) {
				JsonArray jsonarray = fluidJson.getAsJsonArray();
				if (jsonarray.size() == 0) {
					throw new JsonSyntaxException(
							"Fluidstack array cannot be empty, at least one item must be defined");
				} else {
					return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((p_151264_) -> {
						return valueFromJson(GsonHelper.convertToJsonObject(p_151264_, "item"));
					}), amount);
				}
			} else {
				throw new JsonSyntaxException("Expected item to be object or array of objects");
			}
		} else {
			throw new JsonSyntaxException("Item cannot be null");
		}
	}

	public static FluidIngredient.Value valueFromJson(JsonObject json) {
		if (json.has("fluid") && json.has("tag")) {
			throw new JsonParseException("An ingredient entry is either a tag or a fluid, not both");
		} else if (json.has("fluid")) {
			ResourceLocation fluidName = new ResourceLocation(json.get("fluid").getAsString());
			Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
			return new FluidIngredient.FluidValue(fluid);
		} else if (json.has("tag")) {
			ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
			TagKey<Fluid> tagkey = TagKey.create(Registry.FLUID_REGISTRY, resourcelocation);
			return new FluidIngredient.TagValue(tagkey);
		} else {
			throw new JsonParseException("An ingredient entry needs either a tag or a fluid");
		}
	}

	public interface Value {
		Collection<Fluid> getFluids();

		JsonObject serialize();

		void toBuffer(FriendlyByteBuf buffer);
	}
}