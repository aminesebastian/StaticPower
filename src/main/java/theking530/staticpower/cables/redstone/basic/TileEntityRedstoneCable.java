package theking530.staticpower.cables.redstone.basic;

import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityRedstoneCable extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_NAKED = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "naked"), ModBlocks.BasicRedstoneCableNaked);

	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_BLACK = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "black"), ModBlocks.BasicRedstoneCableBlack);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_BLUE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "dark_blue"), ModBlocks.BasicRedstoneCableDarkBlue);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_GREEN = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "dark_green"), ModBlocks.BasicRedstoneCableDarkGreen);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_AQUA = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "dark_aqua"), ModBlocks.BasicRedstoneCableDarkAqua);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_RED = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "dark_red"), ModBlocks.BasicRedstoneCableDarkRed);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_PURPLE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "dark_purple"), ModBlocks.BasicRedstoneCableDarkPurple);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_GOLD = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "gold"), ModBlocks.BasicRedstoneCableGold);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_GRAY = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "gray"), ModBlocks.BasicRedstoneCableGray);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_GRAY = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "dark_gray"), ModBlocks.BasicRedstoneCableDarkGray);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_BLUE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "blue"), ModBlocks.BasicRedstoneCableBlue);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_GREEN = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "green"), ModBlocks.BasicRedstoneCableGreen);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_AQUA = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "aqua"), ModBlocks.BasicRedstoneCableAqua);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_RED = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "red"), ModBlocks.BasicRedstoneCableRed);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_LIGHT_PURPLE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "light_purple"), ModBlocks.BasicRedstoneCableLightPurple);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_YELLOW = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "yellow"), ModBlocks.BasicRedstoneCableYellow);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_WHITE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator) -> new TileEntityRedstoneCable(allocator, "white"), ModBlocks.BasicRedstoneCableWhite);

	public final RedstoneCableComponent cableComponent;

	public TileEntityRedstoneCable(BlockEntityTypeAllocator<TileEntityRedstoneCable> allocator, String selector) {
		super(allocator);
		registerComponent(cableComponent = new RedstoneCableComponent("RedstoneCableComponent", selector));
	}
}
