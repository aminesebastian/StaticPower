package theking530.staticcore.gui.text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import theking530.staticcore.utilities.MetricConverter;

/**
 * Utility class to format various values into display in the UI including
 * energy and fluid.
 * 
 * @author Amine Sebastian
 *
 */
public class GuiTextUtilities {
	/** Translation text component for millibuckets (mB). */
	public static final MutableComponent FLUID_UNIT_TRANSLATION = Component.translatable("gui.staticcore.fluid_unit");
	/** Translation text component for millibuckets Per Tick (mB/t). */
	public static final MutableComponent FLUID_RATE_TRANSLATION = Component
			.translatable("gui.staticcore.fluid_unit_per_tick");

	/** Translation text component for heat (H). */
	public static final MutableComponent HEAT_UNIT_TRANSLATION = Component.translatable("gui.staticcore.heat_unit");
	/** Translation text component for Heat Per Tick (H/t). */
	public static final MutableComponent HEAT_RATE_TRANSLATION = Component
			.translatable("gui.staticcore.heat_unit_per_second");
	/** Translation text component for Conductivity. */
	public static final MutableComponent HEAT_CONDUCTIVITY_TRANSLATION = Component
			.translatable("gui.staticcore.heat_conductivity_unit");

	/** Single instance of number formatter with two decimal places.. */
	private static final NumberFormat NUMBER_FORMATTER_TWO_DECIMAL;
	/** Single instance of number formatter with a single decimal place. */
	private static final NumberFormat NUMBER_FORMATTER_ONE_DECIMAL;
	/** Single instance of number formatter with no decimal place. */
	private static final NumberFormat NUMBER_FORMATTER_NO_DECIMAL;

	/**
	 * Static initializer for number formatter.
	 */
	static {
		NUMBER_FORMATTER_TWO_DECIMAL = NumberFormat.getInstance();
		NUMBER_FORMATTER_TWO_DECIMAL.setGroupingUsed(true);
		NUMBER_FORMATTER_TWO_DECIMAL.setMaximumFractionDigits(2);

		NUMBER_FORMATTER_ONE_DECIMAL = NumberFormat.getInstance();
		NUMBER_FORMATTER_ONE_DECIMAL.setGroupingUsed(true);
		NUMBER_FORMATTER_ONE_DECIMAL.setMaximumFractionDigits(1);

		NUMBER_FORMATTER_NO_DECIMAL = NumberFormat.getInstance();
		NUMBER_FORMATTER_NO_DECIMAL.setGroupingUsed(true);
		NUMBER_FORMATTER_NO_DECIMAL.setMaximumFractionDigits(0);
	}

	public static MutableComponent formatTicksToTimeUnit(int ticks) {
		return Component.translatable(NUMBER_FORMATTER_ONE_DECIMAL.format(ticks / 20))
				.append(Component.translatable("gui.staticcore.seconds.short"));
	}

	public static MutableComponent formatHeatToString(float currentHeat, float capacity) {
		return formatHeatToString(currentHeat, false, true).append("/").append(formatHeatToString(capacity));

	}

	public static MutableComponent formatHeatToString(float heat) {
		return formatHeatToString(heat, true, true);
	}

	public static MutableComponent formatHeatToString(float heat, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) heat == Integer.MAX_VALUE) {
			output = Component.literal("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(heat);

			if (String.valueOf(metricRate.getValue()).length() > 2) {
				output = Component.literal(NUMBER_FORMATTER_NO_DECIMAL.format(metricRate.getValue()));
			} else {
				output = Component.literal(NUMBER_FORMATTER_ONE_DECIMAL.format(metricRate.getValue()));
			}

			if (includeMetricUnit) {
				output.append(metricRate.getSuffix());
			}
		}

		if (includeUnits) {
			output.append(" ").append(HEAT_UNIT_TRANSLATION);
		}
		return output;
	}

	public static MutableComponent formatHeatRateToString(double heatTransferRate) {
		// Allocate the text component.
		MutableComponent output;
		// If the value is equal to the integer max, make it infinite.
		if ((int) heatTransferRate == Integer.MAX_VALUE) {
			output = Component.literal("∞");
		} else {
			heatTransferRate *= 20.0f;
			MetricConverter metricRate = new MetricConverter(heatTransferRate);

			if (String.valueOf(metricRate.getValue()).length() > 2) {
				output = Component.literal(NUMBER_FORMATTER_NO_DECIMAL.format(metricRate.getValue()));
			} else {
				output = Component.literal(NUMBER_FORMATTER_ONE_DECIMAL.format(metricRate.getValue()));
			}
			output.append(metricRate.getSuffix());
		}

		return output.append(HEAT_RATE_TRANSLATION);
	}

	public static MutableComponent formatConductivityToString(double conductivity) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) conductivity == Integer.MAX_VALUE) {
			output = Component.literal("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(conductivity);
			output = Component.literal(NUMBER_FORMATTER_TWO_DECIMAL.format(metricRate.getValue()))
					.append(metricRate.getSuffix());
		}

		return output.append(" ").append(HEAT_CONDUCTIVITY_TRANSLATION);
	}

	public static MutableComponent formatFluidToString(double currentFluid, double capacity) {
		return formatFluidToString(currentFluid, false, true).append("/").append(formatFluidToString(capacity));

	}

	public static MutableComponent formatFluidToString(double fluidAmount) {
		return formatFluidToString(fluidAmount, true, true);
	}

	public static MutableComponent formatFluidToString(double fluid, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) fluid == Integer.MAX_VALUE) {
			output = Component.literal("∞");
		} else {
			MetricConverter metricFluid = new MetricConverter(fluid, -1);
			output = Component.literal(NUMBER_FORMATTER_ONE_DECIMAL.format(metricFluid.getValue()));

			if (includeMetricUnit) {
				output.append(metricFluid.getSuffix());
			}
		}

		// Append the units if requested.
		if (includeUnits) {
			output.append(FLUID_UNIT_TRANSLATION);
		}

		return output;
	}

	public static MutableComponent formatUnitRateToString(double rate, String unlocalizedUnit) {
		MutableComponent output = formatUnitRateToString(rate);
		output.append(Component.translatable(unlocalizedUnit));
		return output;
	}

	public static MutableComponent formatUnitRateToString(double rate) {
		MetricConverter metricPerUnit = new MetricConverter(rate);
		MutableComponent output = Component.literal(NUMBER_FORMATTER_ONE_DECIMAL.format(metricPerUnit.getValue()));
		output.append(metricPerUnit.getSuffix());
		return output;
	}

	public static MutableComponent formatFluidRateToString(double fluidRate) {
		return formatFluidRateToString(fluidRate, true);
	}

	public static MutableComponent formatFluidRateToString(double fluidRate, boolean includeSpace) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) fluidRate == Integer.MAX_VALUE) {
			output = Component.literal("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(fluidRate, -1);
			output = Component.literal(NUMBER_FORMATTER_NO_DECIMAL.format(metricRate.getValue()))
					.append(includeSpace ? " " : "").append(metricRate.getSuffix());
		}

		return output.append(FLUID_RATE_TRANSLATION);
	}

	public static MutableComponent formatNumberAsString(double number) {
		return Component.literal(NUMBER_FORMATTER_TWO_DECIMAL.format(number));
	}

	public static MutableComponent formatNumberAsStringNoDecimal(double number) {
		return Component.literal(NUMBER_FORMATTER_NO_DECIMAL.format(Double.isNaN(number) ? 0 : number));
	}

	public static MutableComponent formatNumberAsStringOneDecimal(double number) {
		return Component.literal(NUMBER_FORMATTER_ONE_DECIMAL.format(number));
	}

	public static MutableComponent formatNumberAsString(int number) {
		return Component.literal(NUMBER_FORMATTER_TWO_DECIMAL.format(number));
	}

	public static MutableComponent formatNumberAsPercentStringNoDecimal(double number) {
		return Component.literal(NUMBER_FORMATTER_NO_DECIMAL.format(Double.isNaN(number) ? 0 : number * 100))
				.append("%");
	}

	public static MutableComponent formatNumberAsPercentStringOneDecimal(double number) {
		return Component.literal(NUMBER_FORMATTER_ONE_DECIMAL.format(Double.isNaN(number) ? 0 : number * 100))
				.append("%");
	}

	public static void addColoredBulletTooltip(List<Component> tooltip, String key, ChatFormatting keyColor,
			String value) {
		addColoredBulletTooltip(tooltip, key, Style.EMPTY.withColor(keyColor), value,
				Style.EMPTY.withColor(ChatFormatting.WHITE));
	}

	public static void addColoredBulletTooltip(List<Component> tooltip, String key, ChatFormatting keyColor,
			String value, ChatFormatting valueColor) {
		addColoredBulletTooltip(tooltip, key, Style.EMPTY.withColor(keyColor), value,
				Style.EMPTY.withColor(valueColor));
	}

	public static void addColoredBulletTooltip(List<Component> tooltip, String key, Style keyStyle, String value,
			Style valueStyle) {
		tooltip.add(Component.translatable(key).setStyle(keyStyle));
		tooltip.add(Component.literal("• ").append(Component.translatable(value).setStyle(valueStyle)));
	}

	public static void addColoredBulletTooltip(List<Component> tooltip, String value) {
		addColoredBulletTooltip(tooltip, value, Style.EMPTY.withColor(ChatFormatting.WHITE));
	}

	public static void addColoredBulletTooltip(List<Component> tooltip, String value, ChatFormatting valueColor) {
		addColoredBulletTooltip(tooltip, value, Style.EMPTY.withColor(valueColor));
	}

	public static void addColoredBulletTooltip(List<Component> tooltip, String value, Style valueStyle) {
		tooltip.add(createColoredBulletTooltip(value, valueStyle));
	}

	public static List<Component> createColoredBulletTooltip(String key, ChatFormatting keyColor, String value) {
		return createColoredBulletTooltip(key, Style.EMPTY.withColor(keyColor), value,
				Style.EMPTY.withColor(ChatFormatting.WHITE));
	}

	public static List<Component> createColoredBulletTooltip(String key, ChatFormatting keyColor, String value,
			ChatFormatting valueColor) {
		return createColoredBulletTooltip(key, Style.EMPTY.withColor(keyColor), value,
				Style.EMPTY.withColor(valueColor));
	}

	public static List<Component> createColoredBulletTooltip(String key, Style keyStyle, String value,
			Style valueStyle) {
		List<Component> output = new ArrayList<Component>();
		addColoredBulletTooltip(output, key, keyStyle, value, valueStyle);
		return output;
	}

	public static Component createColoredBulletTooltip(String value) {
		return createColoredBulletTooltip(value, Style.EMPTY.withColor(ChatFormatting.WHITE));
	}

	public static Component createColoredBulletTooltip(String value, ChatFormatting valueColor) {
		return createColoredBulletTooltip(value, Style.EMPTY.withColor(valueColor));
	}

	public static Component createColoredBulletTooltip(String value, Style valueStyle) {
		return Component.literal("• ").append(Component.translatable(value)).setStyle(valueStyle);
	}
}
