package theking530.staticcore.utilities;

import net.minecraft.world.item.DyeColor;

/**
 * Special thanks to the u/Metroite for breaking down the initial color values.
 * https://www.reddit.com/r/MinecraftCommands/comments/9tdvfc/almost_perfect_minecraft_rgb_colors_in_percent/
 * 
 * @author amine
 *
 */
public enum MinecraftColor {
	// @formatter:off
	WHITE("gui.staticcore.white", new SDColor(1.0f, 1.0f, 1.0f), DyeColor.WHITE), 
	ORANGE("gui.staticcore.orange", new SDColor(0.9176f, 0.6784f, 0.3255f), DyeColor.ORANGE),
	MAGENTA("gui.staticcore.magenta", new SDColor(0.8824f, 0.5608f, 0.8549f), DyeColor.MAGENTA), 
	LIGHT_BLUE("gui.staticcore.light_blue", new SDColor(0.5882f, 0.7255f, 0.9176f), DyeColor.LIGHT_BLUE),
	YELLOW("gui.staticcore.yellow", new SDColor(0.9176f, 0.8549f, 0.2549f), DyeColor.YELLOW), 
	LIME("gui.staticcore.lime", new SDColor(0.5843f, 0.8549f, 0.2549f), DyeColor.LIME),
	PINK("gui.staticcore.pink", new SDColor(0.9412f, 0.7098f, 0.8275f), DyeColor.PINK), 
	GRAY("gui.staticcore.gray", new SDColor(0.5412f, 0.5412f, 0.5412f), DyeColor.GRAY),
	LIGHT_GRAY("gui.staticcore.light_gray", new SDColor(0.8157f, 0.8157f, 0.8157f), DyeColor.LIGHT_GRAY), 
	CYAN("gui.staticcore.cyan", new SDColor(0.2235f, 0.4824f, 0.5843f), DyeColor.CYAN),
	PURPLE("gui.staticcore.purple", new SDColor(0.7412f, 0.4862f, 0.8667f), DyeColor.PURPLE), 
	BLUE("gui.staticcore.blue", new SDColor(0.2353f, 0.4275f, 0.7098f), DyeColor.BLUE),
	BROWN("gui.staticcore.brown", new SDColor(0.6118f, 0.4431f, 0.3255f), DyeColor.BROWN), 
	GREEN("gui.staticcore.green", new SDColor(0.3922f, 0.5059f, 0.2196f), DyeColor.GREEN),
	RED("gui.staticcore.red", new SDColor(0.7098f, 0.2510f, 0.2510f), DyeColor.RED), 
	BLACK("gui.staticcore.black", new SDColor(0.1f, 0.1f, 0.1f), DyeColor.BLACK);
	// @formatter:on

	private final String name;
	private final String unlocalizedName;
	private final SDColor color;
	private final DyeColor dyeColor;

	private MinecraftColor(String name, SDColor color, DyeColor dyeColor) {
		this.name = name.split("\\.")[2];
		this.unlocalizedName = name;
		this.color = color;
		this.dyeColor = dyeColor;
	}

	public String getId() {
		return name;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public SDColor getColor() {
		return color;
	}

	public String getName() {
		return dyeColor.getName();
	}

	public DyeColor getDyeColor() {
		return dyeColor;
	}
}