package theking530.staticcore.fluid;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import theking530.staticcore.gui.text.GuiTextUtilities;

public class StaticCoreFluidType extends FluidType {
	public static final String TEMPERATURE_TAG = "temperature";

	public StaticCoreFluidType(Properties properties) {
		super(properties);
	}

	public Component getDescription(FluidStack stack) {
		Component superCall = super.getDescription();
		if (!stack.hasTag() || stack.getTag().contains(TEMPERATURE_TAG)) {
			return superCall;
		}

		float temperature = stack.getTag().getFloat(TEMPERATURE_TAG);
		MutableComponent temperatureComponent = GuiTextUtilities.formatHeatToString(temperature);

		return superCall.copy().append(" (").append(temperatureComponent).append(")");
	}
}
