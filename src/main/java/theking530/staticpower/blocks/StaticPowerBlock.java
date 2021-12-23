package theking530.staticpower.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.wrench.IWrenchable;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticcore.utilities.ITooltipProvider;
import theking530.staticpower.blocks.interfaces.IItemBlockProvider;
import theking530.staticpower.blocks.interfaces.IRenderLayerProvider;
import theking530.staticpower.items.tools.StaticWrench;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.interfaces.IBreakSerializeable;

/**
 * Basic implementation of a static power block.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerBlock extends Block
		implements IItemBlockProvider, IRenderLayerProvider, IWrenchable, ITooltipProvider {
	/**
	 * Facing property used by blocks that require keeping track of the direction
	 * they face (does not have to be used).
	 */
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	/**
	 * Rotation property used by blocks who don't use {@link #FACING} but still need
	 * the option to rotate to either face X, Y, or Z. (Does not have to be used).
	 */
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

	/**
	 * Constructor for a static power block.
	 * 
	 * @param name       The registry name of this block sans namespace.
	 * @param properties The block properties to be used when defining this block.
	 */
	public StaticPowerBlock(String name, Block.Properties properties) {
		super(properties);
		setRegistryName(name);
	}

	@Override
	public BlockItem getItemBlock() {
		return new StaticPowerItemBlock(this);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		return IBreakSerializeable.createItemDrop(this, player, world, pos);
	}

	public Component getDisplayName(ItemStack stack) {
		return new TranslatableComponent(getDescriptionId());
	}

	/**
	 * Basic constructor for a static power block with a specific material type.
	 * 
	 * @param name     The registry name of this block sans namespace.
	 * @param material The {@link Material} this block is made of.
	 */
	public StaticPowerBlock(String name, Material material) {
		this(name, material, 1.0f);
	}

	/**
	 * Basic constructor for a static power block with a specific material type,
	 * tool type, harvest level, and hardness/resistance.
	 * 
	 * @param name                  The registry name of this block sans namespace.
	 * @param material              The {@link Material} this block is made of.
	 * @param tool                  The {@link ToolType} this block should be
	 *                              harvested by.
	 * @param hardnessAndResistance The hardness and resistance of this block.
	 */
	public StaticPowerBlock(String name, Material material, float hardnessAndResistance) {
		this(name, Block.Properties.of(material).strength(hardnessAndResistance));
	}

	/**
	 * This is a helper event that is raised when a block is harvested by the
	 * player. It is only raised when the owning block is removed by a player that
	 * is not in creative mode. Once this call is completed, the vanilla code is
	 * still executed. This is useful in the event that the block would like to
	 * serialize its owned {@link TileEntity}'s inventory to NBT or drop the
	 * contents on the floor. The vanilla code then handles dropping this actual
	 * block or any other drops as defined in the datapack. If you would like to
	 * completely override vanilla Behavior, override
	 * {@link #harvestBlock(World, PlayerEntity, BlockPos, BlockState, TileEntity, ItemStack)}
	 * instead.
	 * 
	 * @param world  The world the block was harvested in.
	 * @param player The player that harvested the block.
	 * @param pos    The position that the block was at.
	 * @param state  The state of the block at the point of harvest.
	 * @param te     The {@link TileEntity} of the block (if one existed).
	 * @param stack  The {@link ItemStack} that the block was harvested by.
	 */
	public void onStaticPowerBlockHarvested(Level world, Player player, BlockPos pos, BlockState state,
			@Nullable BlockEntity te, ItemStack stack) {

	}

	/**
	 * This is a helper event that is raised when a block is activated by the
	 * player. It is NOT called when the player activates this block with a wrench
	 * in their hand. To override the behaviour when that occurs, use the
	 * {@link #wrenchBlock(PlayerEntity, RegularWrenchMode, ItemStack, World, BlockPos, Direction, boolean)}
	 * and
	 * {@link #sneakWrenchBlock(PlayerEntity, SneakWrenchMode, ItemStack, World, BlockPos, Direction, boolean)}
	 * methods.
	 * 
	 * @param state  The state of the block at the point of harvest.
	 * @param world  The world the block was harvested in.
	 * @param pos    The position that the block was at.
	 * @param player The player that harvested the block.
	 * @param hand   The hand the block was right-clicked with.
	 * @param hit    The hit result of the activation.
	 * @return The result of the activation.
	 */
	public InteractionResult onStaticPowerBlockActivated(BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.PASS;
	}

	public void onStaticPowerBlockPlaced(Level world, BlockPos pos, BlockState state, LivingEntity placer,
			ItemStack stack) {

	}

	public void onStaticPowerBlockClicked(BlockState state, Level world, BlockPos pos, Player player) {

	}

	public void onStaticPowerNeighborChanged(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor,
			boolean isMoving) {

	}

	public void onStaticPowerBlockReplaced(BlockState state, Level world, BlockPos pos, BlockState newState,
			boolean isMoving, boolean newBlock) {

	}

	public void onNeighborReplaced(BlockState state, Direction dir, BlockState facingState, LevelAccessor world,
			BlockPos pos, BlockPos facingPos) {

	}

	// ***********//
	// Overrides //
	// ***********//
	/**
	 * Gets the {@link RenderLayer} for this block. The default is Solid.
	 * 
	 * @return The render layer for this block.
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.solid();
	}

	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
			boolean isShowingAdvanced) {
	}

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
	}

	/**
	 * Defines the behavior for when this block is regular right clicked by a
	 * wrench.
	 */
	@Override
	public InteractionResult wrenchBlock(Player player, RegularWrenchMode mode, ItemStack wrench, Level world,
			BlockPos pos, Direction facing, boolean returnDrops) {
		return InteractionResult.PASS;
	}

	/**
	 * Defines the behavior for when this block is sneak right clicked by a wrench.
	 */
	@Override
	public InteractionResult sneakWrenchBlock(Player player, SneakWrenchMode mode, ItemStack wrench, Level world,
			BlockPos pos, Direction facing, boolean returnDrops) {
		return InteractionResult.PASS;
	}

	@Override
	public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te,
			ItemStack stack) {
		// Raise the inheritor's method.
		onStaticPowerBlockHarvested(world, player, pos, state, te, stack);

		// Super call.
		super.playerDestroy(world, player, pos, state, te, stack);
	}

	@Deprecated
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		// Raise the inheritor's method.
		onStaticPowerBlockReplaced(state, world, pos, newState, isMoving, state.getBlock() != newState.getBlock());

		if (state.getBlock() != newState.getBlock()) {
			// Raise the tile entity's broken method.
			if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof TileEntityBase) {
				((TileEntityBase) world.getBlockEntity(pos)).onBlockBroken(state, newState, isMoving);
			}

			// Only call the super if the blocks are not equal.
			super.onRemove(state, world, pos, newState, isMoving);
		} else {
			// Raise the tile entity's changed method.
			if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof TileEntityBase) {
				((TileEntityBase) world.getBlockEntity(pos)).onBlockReplaced(state, newState, isMoving);
			}
		}
	}

	@Override
	public InteractionResult use(final BlockState state, final Level world, final BlockPos pos, final Player player,
			final InteractionHand hand, final BlockHitResult hit) {
		// Return the super call if needed.
		@SuppressWarnings("deprecation")
		InteractionResult superResult = super.use(state, world, pos, player, hand, hit);
		if (superResult != InteractionResult.PASS) {
			return superResult;
		}

		// Raise the tile entity's activated method and return here if it does not
		// pass.
		if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof TileEntityBase) {
			InteractionResult teResult = ((TileEntityBase) world.getBlockEntity(pos)).onBlockActivated(state, player,
					hand, hit);
			if (teResult != InteractionResult.PASS) {
				return teResult;
			}
		}

		// Stop here if the item is a wrench. The wrench methods will handle that.
		if (!player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getItem() instanceof StaticWrench) {
			return InteractionResult.PASS;
		}

		// Pass through to inheritors.
		return onStaticPowerBlockActivated(state, world, pos, player, hand, hit);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);
		if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof TileEntityBase) {
			((TileEntityBase) world.getBlockEntity(pos)).onPlaced(state, placer, stack);
		}
		onStaticPowerBlockPlaced(world, pos, state, placer, stack);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player) {
		super.attack(state, world, pos, player);
		if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof TileEntityBase) {
			((TileEntityBase) world.getBlockEntity(pos)).onBlockLeftClicked(state, player);
		}
		onStaticPowerBlockClicked(state, world, pos, player);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, LevelAccessor world,
			BlockPos pos, BlockPos facingPos) {
		super.updateShape(state, dir, facingState, world, pos, facingPos);
		if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof TileEntityBase) {
			((TileEntityBase) world.getBlockEntity(pos)).onNeighborReplaced(state, dir, facingState, facingPos);
		}
		onNeighborReplaced(state, dir, facingState, world, pos, facingPos);
		return state;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos,
			boolean isMoving) {
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
		if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof TileEntityBase) {
			((TileEntityBase) world.getBlockEntity(pos)).onNeighborChanged(state, fromPos, isMoving);
		}
		onStaticPowerNeighborChanged(state, world, pos, fromPos, isMoving);
	}
}
