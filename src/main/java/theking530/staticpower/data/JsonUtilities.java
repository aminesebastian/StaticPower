package theking530.staticpower.data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;

public class JsonUtilities {
	public static final Codec<Ingredient> INGREDIENT_CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
		try {
			Ingredient ingredient = Ingredient.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue());
			return DataResult.success(ingredient);
		} catch (Exception e) {
			return DataResult.error(e.getMessage());
		}
	}, ingredient -> new Dynamic<JsonElement>(JsonOps.INSTANCE, ingredient.toJson()));

	public static final Codec<ItemStack> ITEMSTACK_CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(ForgeRegistries.ITEMS.getCodec().fieldOf("id").forGetter((stack) -> {
			return stack.getItem();
		}), Codec.INT.optionalFieldOf("count", 1).forGetter((stack) -> {
			return stack.getCount();
		}), CompoundTag.CODEC.optionalFieldOf("tag").forGetter((stack) -> {
			return Optional.ofNullable(stack.getTag());
		})).apply(instance, (item, count, tag) -> {
			return new ItemStack(item, count, tag.orElse(null));
		});
	});

	public static final Codec<FluidStack> FLUIDSTACK_CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ForgeRegistries.FLUIDS.getCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
					Codec.INT.fieldOf("amount").forGetter(FluidStack::getAmount), CompoundTag.CODEC.optionalFieldOf("tag").forGetter(stack -> Optional.ofNullable(stack.getTag())))
					.apply(instance, (fluid, amount, tag) -> {
						FluidStack stack = new FluidStack(fluid, amount);
						tag.ifPresent(stack::setTag);
						return stack;
					}));

	public static final PrimitiveCodec<Character> CHAR = new PrimitiveCodec<Character>() {
		@Override
		public <T> DataResult<Character> read(final DynamicOps<T> ops, final T input) {
			return ops.getStringValue(input).map((string) -> string.charAt(0));
		}

		@Override
		public <T> T write(final DynamicOps<T> ops, final Character value) {
			return ops.createString(value.toString());
		}

		@Override
		public String toString() {
			return "Character";
		}
	};

	public static JsonElement fluidStackToJson(FluidStack fluid) {
		DataResult<JsonElement> encodedResult = FLUIDSTACK_CODEC.encodeStart(JsonOps.INSTANCE, fluid);
		return encodedResult.result().get();
	}

	public static FluidStack fluidStackFromJson(JsonElement json) {
		DataResult<Pair<FluidStack, JsonElement>> encodedResult = FLUIDSTACK_CODEC.decode(JsonOps.INSTANCE, json);
		return encodedResult.result().get().getFirst();
	}

	public static JsonElement itemStackToJson(ItemStack stack) {
		DataResult<JsonElement> encodedResult = ITEMSTACK_CODEC.encodeStart(JsonOps.INSTANCE, stack);
		return encodedResult.result().get();
	}

	public static ItemStack itemStackFromJson(JsonElement json) {
		DataResult<Pair<ItemStack, JsonElement>> encodedResult = ITEMSTACK_CODEC.decode(JsonOps.INSTANCE, json);
		return encodedResult.result().get().getFirst();
	}

	public static String nbtToPrettyJson(CompoundTag tag) {
		return new StringTagVisitor().visit(tag);
	}

	public static JsonObject nbtToJsonObject(CompoundTag tag) {
		return JsonParser.parseString(nbtToPrettyJson(tag)).getAsJsonObject();
	}

	public static CompoundTag jsonToNbt(JsonObject json) {
		try {
			return TagParser.parseTag(json.toString());
		} catch (CommandSyntaxException e) {
			StaticPower.LOGGER.error(String.format("There was an error when attempting to parse Json: %1$s.", json.getAsString()), e);
		}
		return null;
	}

	public static class SDStringTagVisitor extends StringTagVisitor {
		private final StringBuilder builder = new StringBuilder();

		public String visit(Tag p_178188_) {
			p_178188_.accept(this);
			return this.builder.toString();
		}

		public void visitString(StringTag p_178186_) {
			this.builder.append(StringTag.quoteAndEscape(p_178186_.getAsString()));
		}

		public void visitByteArray(ByteArrayTag p_178162_) {
			this.builder.append("[");
			byte[] abyte = p_178162_.getAsByteArray();

			for (int i = 0; i < abyte.length; ++i) {
				if (i != 0) {
					this.builder.append(',');
				}

				this.builder.append((int) abyte[i]).append('B');
			}

			this.builder.append(']');
		}

		public void visitIntArray(IntArrayTag p_178174_) {
			this.builder.append("[");
			int[] aint = p_178174_.getAsIntArray();

			for (int i = 0; i < aint.length; ++i) {
				if (i != 0) {
					this.builder.append(',');
				}

				this.builder.append(aint[i]);
			}

			this.builder.append(']');
		}

		public void visitLongArray(LongArrayTag p_178180_) {
			this.builder.append("[");
			long[] along = p_178180_.getAsLongArray();

			for (int i = 0; i < along.length; ++i) {
				if (i != 0) {
					this.builder.append(',');
				}

				this.builder.append(along[i]).append('L');
			}

			this.builder.append(']');
		}

		public void visitList(ListTag p_178178_) {
			this.builder.append('[');

			for (int i = 0; i < p_178178_.size(); ++i) {
				if (i != 0) {
					this.builder.append(',');
				}

				this.builder.append((new SDStringTagVisitor()).visit(p_178178_.get(i)));
			}

			this.builder.append(']');
		}

		public void visitCompound(CompoundTag p_178166_) {
			this.builder.append('{');
			List<String> list = Lists.newArrayList(p_178166_.getAllKeys());
			Collections.sort(list);

			for (String s : list) {
				if (this.builder.length() != 1) {
					this.builder.append(',');
				}

				this.builder.append(handleEscape(s)).append(':').append((new SDStringTagVisitor()).visit(p_178166_.get(s)));
			}

			this.builder.append('}');
		}

		public void visitEnd(EndTag p_178170_) {
			this.builder.append("END");
		}
	}
}
