package theking530.staticpower.data.crafting;

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryManager;

public class StaticPowerJsonParsingUtilities {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerJsonParsingUtilities.class);

	public static FluidStack parseFluidStack(JsonObject object) {
		try {
			// Get the fluid. If there is no defined fluid by that name, return null.
			Fluid fluid = RegistryManager.ACTIVE.getRegistry(Fluid.class).getValue(new ResourceLocation(object.get("fluid").getAsString()));
			if (fluid == null) {
				throw new RuntimeException("An invalid fluid name was supplied.");
			}

			// Get the amount (if provided).
			int amount = FluidAttributes.BUCKET_VOLUME;
			if (object.has("volume")) {
				amount = object.get("volume").getAsInt();
			}

			// Create the output fluid stack.
			FluidStack output = new FluidStack(fluid, amount);

			// If there is additional nbt provided, take that into consideration.
			if (object.has("nbt")) {
				try {
					output.setTag(TagParser.parseTag(object.get("nbt").getAsString()));
				} catch (Exception e) {
					throw new RuntimeException("An error occured when attempting to apply the nbt to fluid stack from a recipe.", e);
				}
			}

			// Return the parsed fluidstack.
			return output;
		} catch (Exception e) {
			throw new RuntimeException(String.format("An error occured when attempting to deserialize json object: %1$s to a FluidStack.", object), e);
		}
	}

	public static ItemStack parseItemWithNbt(JsonObject itemJson) {
		ItemStack item = ShapedRecipe.itemStackFromJson(itemJson);
		if (itemJson.has("nbt")) {
			if (!item.hasTag()) {
				item.setTag(new CompoundTag());
			}
			parseNbtFromJson(itemJson.getAsJsonObject("nbt"), item.getTag());
		}
		return item;
	}

	public static void parseNbtFromJson(JsonObject object, CompoundTag outputTag) {
		// Iterate through all the members and attempt to parse them.
		for (Entry<String, JsonElement> member : object.entrySet()) {
			// Only consider primitives for now.
			if (member.getValue().isJsonPrimitive()) {
				// Get the primitive.
				JsonPrimitive primitive = member.getValue().getAsJsonPrimitive();

				// Add it to the nbt.
				if (primitive.isNumber()) {
					Number number = primitive.getAsNumber();
					if (number instanceof Integer) {
						outputTag.putInt(member.getKey(), primitive.getAsInt());
					} else if (number instanceof Long) {
						outputTag.putLong(member.getKey(), primitive.getAsLong());
					} else if (number instanceof Double) {
						outputTag.putDouble(member.getKey(), primitive.getAsDouble());
					} else if (number instanceof Float) {
						outputTag.putFloat(member.getKey(), primitive.getAsFloat());
					} else if (number instanceof Short) {
						outputTag.putShort(member.getKey(), primitive.getAsShort());
					} else if (number instanceof Byte) {
						outputTag.putByte(member.getKey(), primitive.getAsByte());
					}

				} else if (primitive.isBoolean()) {
					outputTag.putBoolean(member.getKey(), primitive.getAsBoolean());
				} else if (primitive.isString()) {
					outputTag.putString(member.getKey(), primitive.getAsString());
				} else {
					LOGGER.warn(String.format("Encountered unsupported data type in JSON nbt: %1$s.", member.getKey()));
				}
			}
		}
	}
}
