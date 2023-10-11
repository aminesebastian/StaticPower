package theking530.staticcore.utilities;

import java.util.function.Supplier;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import theking530.staticpower.init.tags.ModBlockTags;
import theking530.staticpower.init.tags.ModItemTags;

/**
 * Special thanks to the u/Metroite for breaking down the initial color values.
 * https://www.reddit.com/r/MinecraftCommands/comments/9tdvfc/almost_perfect_minecraft_rgb_colors_in_percent/
 * 
 * @author amine
 *
 */
public enum MinecraftColor {
	// @formatter:off
	WHITE("gui.staticcore.white", new SDColor(1.0f, 1.0f, 1.0f), DyeColor.WHITE, ()->Items.WHITE_DYE, ()->Blocks.WHITE_WOOL, ()->Blocks.WHITE_BED), 
	ORANGE("gui.staticcore.orange", new SDColor(0.9176f, 0.6784f, 0.3255f), DyeColor.ORANGE, ()->Items.ORANGE_DYE, ()->Blocks.ORANGE_WOOL, ()->Blocks.ORANGE_BED),
	MAGENTA("gui.staticcore.magenta", new SDColor(0.8824f, 0.5608f, 0.8549f), DyeColor.MAGENTA, ()->Items.MAGENTA_DYE, ()->Blocks.MAGENTA_WOOL, ()->Blocks.MAGENTA_BED), 
	LIGHT_BLUE("gui.staticcore.light_blue", new SDColor(0.5882f, 0.7255f, 0.9176f), DyeColor.LIGHT_BLUE, ()->Items.LIGHT_BLUE_DYE, ()->Blocks.LIGHT_BLUE_WOOL, ()->Blocks.LIGHT_BLUE_BED),
	YELLOW("gui.staticcore.yellow", new SDColor(0.9176f, 0.8549f, 0.2549f), DyeColor.YELLOW, ()->Items.YELLOW_DYE, ()->Blocks.YELLOW_WOOL, ()->Blocks.YELLOW_BED), 
	LIME("gui.staticcore.lime", new SDColor(0.5843f, 0.8549f, 0.2549f), DyeColor.LIME, ()->Items.LIME_DYE, ()->Blocks.LIME_WOOL, ()->Blocks.LIME_BED),
	PINK("gui.staticcore.pink", new SDColor(0.9412f, 0.7098f, 0.8275f), DyeColor.PINK, ()->Items.PINK_DYE, ()->Blocks.PINK_WOOL, ()->Blocks.PINK_BED), 
	GRAY("gui.staticcore.gray", new SDColor(0.5412f, 0.5412f, 0.5412f), DyeColor.GRAY, ()->Items.GRAY_DYE, ()->Blocks.GRAY_WOOL, ()->Blocks.GRAY_BED),
	LIGHT_GRAY("gui.staticcore.light_gray", new SDColor(0.8157f, 0.8157f, 0.8157f), DyeColor.LIGHT_GRAY, ()->Items.LIGHT_GRAY_DYE, ()->Blocks.LIGHT_GRAY_WOOL, ()->Blocks.LIGHT_GRAY_BED), 
	CYAN("gui.staticcore.cyan", new SDColor(0.2235f, 0.4824f, 0.5843f), DyeColor.CYAN, ()->Items.CYAN_DYE, ()->Blocks.CYAN_WOOL, ()->Blocks.CYAN_BED),
	PURPLE("gui.staticcore.purple", new SDColor(0.7412f, 0.4862f, 0.8667f), DyeColor.PURPLE, ()->Items.PURPLE_DYE, ()->Blocks.PURPLE_WOOL, ()->Blocks.PURPLE_BED), 
	BLUE("gui.staticcore.blue", new SDColor(0.2353f, 0.4275f, 0.7098f), DyeColor.BLUE, ()->Items.BLUE_DYE, ()->Blocks.BLUE_WOOL, ()->Blocks.BLUE_BED),
	BROWN("gui.staticcore.brown", new SDColor(0.6118f, 0.4431f, 0.3255f), DyeColor.BROWN, ()->Items.BROWN_DYE, ()->Blocks.BROWN_WOOL, ()->Blocks.BROWN_BED), 
	GREEN("gui.staticcore.green", new SDColor(0.3922f, 0.5059f, 0.2196f), DyeColor.GREEN, ()->Items.GREEN_DYE, ()->Blocks.GREEN_WOOL, ()->Blocks.GREEN_BED),
	RED("gui.staticcore.red", new SDColor(0.7098f, 0.2510f, 0.2510f), DyeColor.RED, ()->Items.RED_DYE, ()->Blocks.RED_WOOL, ()->Blocks.RED_BED), 
	BLACK("gui.staticcore.black", new SDColor(0.1f, 0.1f, 0.1f), DyeColor.BLACK, ()->Items.BLACK_DYE, ()->Blocks.BLACK_WOOL, ()->Blocks.BLACK_BED);
	// @formatter:on

	private final String name;
	private final String unlocalizedName;
	private final SDColor color;
	private final DyeColor dyeColor;
	private final Supplier<Item> dyeItem;
	private final Supplier<Block> coloredWool;
	private final Supplier<Block> coloredBed;

	private final TagKey<Block> blockWoolTag;
	private final TagKey<Item> itemWoolTag;

	private final TagKey<Block> blockConcreteTag;
	private final TagKey<Item> itemConcreteTag;

	private final TagKey<Item> dyeTag;

	private MinecraftColor(String name, SDColor color, DyeColor dyeColor, Supplier<Item> dyeItem,
			Supplier<Block> coloredWool, Supplier<Block> coloredBed) {
		this.name = name.split("\\.")[2];
		this.unlocalizedName = name;
		this.color = color;
		this.dyeColor = dyeColor;
		this.dyeItem = dyeItem;
		this.coloredWool = coloredWool;
		this.coloredBed = coloredBed;

		this.blockWoolTag = ModBlockTags.createForgeTag("wool/" + getName());
		this.itemWoolTag = ModItemTags.createForgeTag("wool/" + getName());

		this.blockConcreteTag = ModBlockTags.createForgeTag("concrete/" + getName());
		this.itemConcreteTag = ModItemTags.createForgeTag("concrete/" + getName());

		this.dyeTag = ModItemTags.createForgeTag("dyes/" + getName());
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

	public TagKey<Item> getDyeTag() {
		return dyeTag;
	}

	public Supplier<Item> getDyeItem() {
		return dyeItem;
	}

	public Supplier<Block> getColoredWool() {
		return coloredWool;
	}

	public Supplier<Block> getColoredBed() {
		return coloredBed;
	}

	public TagKey<Block> getBlockWoolTag() {
		return blockWoolTag;
	}

	public TagKey<Item> getItemWoolTag() {
		return itemWoolTag;
	}

	public TagKey<Block> getBlockConcreteTag() {
		return blockConcreteTag;
	}

	public TagKey<Item> getItemConcreteTag() {
		return itemConcreteTag;
	}

	
}