package theking530.staticpower.cables.redstone.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.init.ModBlocks;

public class TileEntityRedstoneCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_NAKED = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, null), ModBlocks.BasicRedstoneCableNaked);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_BLACK = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.BLACK), ModBlocks.BasicRedstoneCableBlack);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_BLUE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.BLUE ), ModBlocks.BasicRedstoneCableDarkBlue);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_GREEN = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.GREEN), ModBlocks.BasicRedstoneCableDarkGreen);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_AQUA = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.BROWN), ModBlocks.BasicRedstoneCableDarkAqua);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_RED = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.RED), ModBlocks.BasicRedstoneCableDarkRed);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_PURPLE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.PURPLE), ModBlocks.BasicRedstoneCableDarkPurple);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_GOLD = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.ORANGE), ModBlocks.BasicRedstoneCableGold);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_GRAY = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.LIGHT_GRAY), ModBlocks.BasicRedstoneCableGray);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_DARK_GRAY = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.GRAY), ModBlocks.BasicRedstoneCableDarkGray);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_BLUE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.LIGHT_BLUE), ModBlocks.BasicRedstoneCableBlue);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_GREEN = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.LIME), ModBlocks.BasicRedstoneCableGreen);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_AQUA = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.CYAN), ModBlocks.BasicRedstoneCableAqua);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_RED = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.PINK), ModBlocks.BasicRedstoneCableRed);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_LIGHT_PURPLE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.MAGENTA), ModBlocks.BasicRedstoneCableLightPurple);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_YELLOW = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.YELLOW), ModBlocks.BasicRedstoneCableYellow);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRedstoneCable> TYPE_BASIC_WHITE = new BlockEntityTypeAllocator<TileEntityRedstoneCable>(
			(allocator, pos, state) -> new TileEntityRedstoneCable(allocator, pos, state, MinecraftColor.WHITE), ModBlocks.BasicRedstoneCableWhite);

	public final RedstoneCableComponent cableComponent;

	public TileEntityRedstoneCable(BlockEntityTypeAllocator<TileEntityRedstoneCable> allocator, BlockPos pos, BlockState state, MinecraftColor color) {
		super(allocator, pos, state);
		registerComponent(cableComponent = new RedstoneCableComponent("RedstoneCableComponent", color));
	}
}
