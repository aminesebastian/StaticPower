package theking530.staticcore.gui.widgets;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.drawables.SpriteDrawable;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;

public class TimeOfDayDrawable extends AbstractGuiWidget {
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
	private World world;
	private BlockPos pos;

	public TimeOfDayDrawable(float xPosition, float yPosition, float size, World world, BlockPos pos) {
		super(xPosition, yPosition, size, size);
		this.world = world;
		this.pos = pos;
		this.day = new SpriteDrawable(GuiTextures.DAY_INDICATOR, size, size);
		this.night = new SpriteDrawable(GuiTextures.NIGHT_INDICATOR, size, size);
		this.sunset = new SpriteDrawable(GuiTextures.SUNSET_INDICATOR, size, size);
		this.sunrise = new SpriteDrawable(GuiTextures.SUNRISE_INDICATOR, size, size);
		this.rain = new SpriteDrawable(GuiTextures.RAIN_INDICATOR, size, size);
		this.snow = new SpriteDrawable(GuiTextures.SNOW_INDICATOR, size, size);
		this.clouds = new SpriteDrawable(GuiTextures.CLOUDS_INDICATOR, size, size);
		this.rainClouds = new SpriteDrawable(GuiTextures.RAIN_CLOUDS_INDICATOR, size, size);
	}

	@Override
	public void renderBehindItems(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		Vector2D adjustedLocation = GuiDrawUtilities.translatePositionByMatrix(matrix, getPosition());
		float xPos = adjustedLocation.getX();
		float yPos = adjustedLocation.getY();

		EStateOfDay dayState = getDayState();
		if (dayState == EStateOfDay.DAY) {
			day.draw(xPos, yPos);
		} else if (dayState == EStateOfDay.SUNSET) {
			sunset.draw(xPos, yPos);
		} else if (dayState == EStateOfDay.SUNRISE) {
			sunrise.draw(xPos, yPos);
		} else {
			night.draw(xPos, yPos);
		}

		if (isRaining()) {
			rain.draw(xPos, yPos);
			rainClouds.draw(xPos, yPos);
		} else if (isSnowing()) {
			snow.draw(xPos, yPos);
		} else {
			clouds.draw(xPos, yPos);
		}
	}

	@Override
	public void getTooltips(Vector2D mousePosition, List<ITextComponent> tooltips, boolean showAdvanced) {
		// Allocate the tooltip.
		IFormattableTextComponent output;

		// Add the state of the day.
		EStateOfDay dayState = getDayState();
		if (dayState == EStateOfDay.DAY) {
			output = new TranslationTextComponent("gui.staticpower.day");
		} else if (dayState == EStateOfDay.SUNSET) {
			output = new TranslationTextComponent("gui.staticpower.sunset");
		} else if (dayState == EStateOfDay.SUNRISE) {
			output = new TranslationTextComponent("gui.staticpower.sunrise");
		} else {
			output = new TranslationTextComponent("gui.staticpower.night");
		}

		// Open parenthesis for the weather report.
		output.appendString(" (");

		// Add the weather report.
		if (isRaining()) {
			output.append(new TranslationTextComponent("gui.staticpower.rainy"));
		} else if (isSnowing()) {
			output.append(new TranslationTextComponent("gui.staticpower.snowy"));
		} else {
			output.append(new TranslationTextComponent("gui.staticpower.clear"));
		}

		// Close parenthesis for the weather report.
		output.appendString(")");

		// Add the tooltip.
		tooltips.add(output);
	}

	protected boolean isSnowing() {
		if (world.isRaining()) {
			if (world.getBiome(pos).doesSnowGenerate(world, pos)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isRaining() {
		if (world.isRaining()) {
			if (!world.getBiome(pos).doesSnowGenerate(world, pos)) {
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
