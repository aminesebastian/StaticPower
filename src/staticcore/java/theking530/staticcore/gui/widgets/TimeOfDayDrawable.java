package theking530.staticcore.gui.widgets;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import theking530.staticcore.gui.StaticCoreGuiTextures;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.math.Vector2D;

public class TimeOfDayDrawable extends AbstractGuiWidget<TimeOfDayDrawable> {
	public enum EStateOfDay {
		NIGHT, SUNRISE, DAY, SUNSET
	}

	private SpriteDrawable day;
	private SpriteDrawable night;
	private SpriteDrawable sunset;
	private SpriteDrawable sunrise;
	private SpriteDrawable rain;
	private SpriteDrawable snow;
	private SpriteDrawable clouds;
	private SpriteDrawable rainClouds;
	private Level world;
	private BlockPos pos;

	public TimeOfDayDrawable(float xPosition, float yPosition, float size, Level world, BlockPos pos) {
		super(xPosition, yPosition, size, size);
		this.world = world;
		this.pos = pos;
		this.day = new SpriteDrawable(StaticCoreGuiTextures.DAY_INDICATOR, size, size);
		this.night = new SpriteDrawable(StaticCoreGuiTextures.NIGHT_INDICATOR, size, size);
		this.sunset = new SpriteDrawable(StaticCoreGuiTextures.SUNSET_INDICATOR, size, size);
		this.sunrise = new SpriteDrawable(StaticCoreGuiTextures.SUNRISE_INDICATOR, size, size);
		this.rain = new SpriteDrawable(StaticCoreGuiTextures.RAIN_INDICATOR, size, size);
		this.snow = new SpriteDrawable(StaticCoreGuiTextures.SNOW_INDICATOR, size, size);
		this.clouds = new SpriteDrawable(StaticCoreGuiTextures.CLOUDS_INDICATOR, size, size);
		this.rainClouds = new SpriteDrawable(StaticCoreGuiTextures.RAIN_CLOUDS_INDICATOR, size, size);
	}

	@Override
	public void renderWidgetBehindItems(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		EStateOfDay dayState = getDayState();
		if (dayState == EStateOfDay.DAY) {
			day.draw(pose, getPosition().getX(), getPosition().getY());
		} else if (dayState == EStateOfDay.SUNSET) {
			sunset.draw(pose, getPosition().getX(), getPosition().getY());
		} else if (dayState == EStateOfDay.SUNRISE) {
			sunrise.draw(pose, getPosition().getX(), getPosition().getY());
		} else {
			night.draw(pose, getPosition().getX(), getPosition().getY());
		}

		if (isRaining()) {
			rain.draw(pose, getPosition().getX(), getPosition().getY());
			rainClouds.draw(pose);
		} else if (isSnowing()) {
			snow.draw(pose, getPosition().getX(), getPosition().getY());
		} else {
			clouds.draw(pose, getPosition().getX(), getPosition().getY());
		}
	}

	@Override
	public void getWidgetTooltips(Vector2D mousePosition, List<Component> tooltips, boolean showAdvanced) {
		// Allocate the tooltip.
		MutableComponent output;

		// Add the state of the day.
		EStateOfDay dayState = getDayState();
		if (dayState == EStateOfDay.DAY) {
			output = Component.translatable("gui.staticcore.day");
		} else if (dayState == EStateOfDay.SUNSET) {
			output = Component.translatable("gui.staticcore.sunset");
		} else if (dayState == EStateOfDay.SUNRISE) {
			output = Component.translatable("gui.staticcore.sunrise");
		} else {
			output = Component.translatable("gui.staticcore.night");
		}

		// Open parenthesis for the weather report.
		output.append(" (");

		// Add the weather report.
		if (isRaining()) {
			output.append(Component.translatable("gui.staticcore.rainy"));
		} else if (isSnowing()) {
			output.append(Component.translatable("gui.staticcore.snowy"));
		} else {
			output.append(Component.translatable("gui.staticcore.clear"));
		}

		// Close parenthesis for the weather report.
		output.append(")");

		// Add the tooltip.
		tooltips.add(output);
	}

	protected boolean isSnowing() {
		if (world.isRaining()) {
			if (world.getBiome(pos).value().shouldSnow(world, pos)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isRaining() {
		if (world.isRaining()) {
			if (!world.getBiome(pos).value().shouldSnow(world, pos)) {
				return true;
			}
		}
		return false;
	}

	protected EStateOfDay getDayState() {
		if (world.getDayTime() < 12542) {
			return EStateOfDay.DAY;
		} else if (world.getDayTime() < 13000) {
			return EStateOfDay.SUNSET;
		} else if (world.getDayTime() < 23000) {
			return EStateOfDay.NIGHT;
		} else {
			return EStateOfDay.SUNRISE;
		}
	}
}
