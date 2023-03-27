package theking530.staticcore.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class StaticPowerJsonParsingUtilities {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerJsonParsingUtilities.class);

	public static FluidStack parseFluidStack(JsonObject object) {
		try {
			// Get the fluid. If there is no defined fluid by that name, return null.
			Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(object.get("fluid").getAsString()));
			if (fluid == null) {
				throw new RuntimeException("An invalid fluid name was supplied.");
			}

			// Get the amount (if provided).
			int amount = 1000;
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
}
