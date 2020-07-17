package theking530.staticpower.data.crafting.wrappers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class StaticPowerJsonParsingUtilities {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerJsonParsingUtilities.class);

	public static FluidStack parseFluidStack(JsonObject object) {

		// Get the fluid. If there is no defined fluid by that name, return null.
		Fluid fluid = GameRegistry.findRegistry(Fluid.class).getValue(new ResourceLocation(object.get("fluid").getAsString()));
		if (fluid == null) {
			return null;
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
				output.setTag(JsonToNBT.getTagFromJson(object.get("nbt").getAsString()));
			} catch (Exception e) {
				throw new RuntimeException("An error occured when attempting to apply the nbt to fluid stack from a recipe.", e);
			}
		}

		// Return the parsed fluidstack.
		return output;
	}
}
