package theking530.staticpower.cables.redstone.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRedstoneCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_NAKED = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_naked",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, null), ModBlocks.BasicRedstoneCableNaked);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_BLACK = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_black",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.BLACK), ModBlocks.BasicRedstoneCableBlack);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_DARK_BLUE = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_dark_blue",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.BLUE), ModBlocks.BasicRedstoneCableDarkBlue);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_DARK_GREEN = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>(
			"cable_redstone_dark_green", (allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.GREEN),
			ModBlocks.BasicRedstoneCableDarkGreen);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_DARK_AQUA = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_dark_aqua",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.BROWN), ModBlocks.BasicRedstoneCableDarkAqua);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_DARK_RED = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_dark_red",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.RED), ModBlocks.BasicRedstoneCableDarkRed);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_DARK_PURPLE = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_purple",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.PURPLE), ModBlocks.BasicRedstoneCableDarkPurple);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_GOLD = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_gold",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.ORANGE), ModBlocks.BasicRedstoneCableGold);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_GRAY = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_gray",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.LIGHT_GRAY), ModBlocks.BasicRedstoneCableGray);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_DARK_GRAY = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_dark_gray",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.GRAY), ModBlocks.BasicRedstoneCableDarkGray);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_BLUE = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_blue",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.LIGHT_BLUE), ModBlocks.BasicRedstoneCableBlue);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_GREEN = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_green",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.LIME), ModBlocks.BasicRedstoneCableGreen);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_AQUA = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_aqua",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.CYAN), ModBlocks.BasicRedstoneCableAqua);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_RED = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_red",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.PINK), ModBlocks.BasicRedstoneCableRed);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_LIGHT_PURPLE = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>(
			"cable_redstone_light_purple", (allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.MAGENTA),
			ModBlocks.BasicRedstoneCableLightPurple);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_YELLOW = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_yellow",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.YELLOW), ModBlocks.BasicRedstoneCableYellow);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRedstoneCable> TYPE_BASIC_WHITE = new BlockEntityTypeAllocator<BlockEntityRedstoneCable>("cable_redstone_white",
			(allocator, pos, state) -> new BlockEntityRedstoneCable(allocator, pos, state, MinecraftColor.WHITE), ModBlocks.BasicRedstoneCableWhite);

	public final RedstoneCableComponent cableComponent;

	public BlockEntityRedstoneCable(BlockEntityTypeAllocator<BlockEntityRedstoneCable> allocator, BlockPos pos, BlockState state, MinecraftColor color) {
		super(allocator, pos, state);
		registerComponent(cableComponent = new RedstoneCableComponent("RedstoneCableComponent", color));
	}
}
