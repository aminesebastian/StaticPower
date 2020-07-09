package theking530.staticpower.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import theking530.common.wrench.IWrenchable;
import theking530.common.wrench.RegularWrenchMode;
import theking530.common.wrench.SneakWrenchMode;
import theking530.staticpower.items.tools.StaticWrench;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.InventoryComponent;

/**
 * Basic implmentation of a static power block.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerBlock extends Block implements IItemBlockProvider, IBlockRenderLayerProvider, IWrenchable {
	/**
	 * Facing property used by blocks that require keeping track of the direction
	 * they face (does not have to be used).
	 */
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
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

	/**
	 * Basic constructor for a static power block with a specific material type.
	 * 
	 * @param name     The registry name of this block sans namespace.
	 * @param material The {@link Material} this block is made of.
	 */
	public StaticPowerBlock(String name, Material material) {
		this(name, material, ToolType.PICKAXE, 1, 1.0f);
	}

	/**
	 * Basic constructor for a static power block with a specific material type,
	 * tool type.
	 * 
	 * @param name     The registry name of this block sans namespace.
	 * @param material The {@link Material} this block is made of.
	 * @param tool     The {@link ToolType} this block should be harvested by.
	 */
	public StaticPowerBlock(String name, Material material, ToolType tool) {
		this(name, material, tool, 1, 1.0f);
	}

	/**
	 * Basic constructor for a static power block with a specific material type,
	 * tool type, harvest level.
	 * 
	 * @param name         The registry name of this block sans namespace.
	 * @param material     The {@link Material} this block is made of.
	 * @param tool         The {@link ToolType} this block should be harvested by.
	 * @param harvestLevel The harvest level of this block.
	 */
	public StaticPowerBlock(String name, Material material, ToolType tool, int harvestLevel) {
		this(name, material, tool, harvestLevel, 1.0f);
	}

	/**
	 * Basic constructor for a static power block with a specific material type,
	 * tool type, harvest level, and hardness/resistance.
	 * 
	 * @param name                  The registry name of this block sans namespace.
	 * @param material              The {@link Material} this block is made of.
	 * @param tool                  The {@link ToolType} this block should be
	 *                              harvested by.
	 * @param harvestLevel          The harvest level of this block.
	 * @param hardnessAndResistance The hardness and resistance of this block.
	 */
	public StaticPowerBlock(String name, Material material, ToolType tool, int harvestLevel, float hardnessAndResistance) {
		this(name, Block.Properties.create(material).harvestTool(tool).harvestLevel(harvestLevel).hardnessAndResistance(hardnessAndResistance));
	}

	/**
	 * This is a helper event that is raised when a block is harvested by the
	 * player. Once this call is completed, the vanilla code is still executed. This
	 * is useful in the event that the block would like to serialize its owned
	 * {@link TileEntity}'s inventory to NBT or drop the contents on the floor. The
	 * vanilla code then handles dropping this actual block or any other drops as
	 * defined in the datapack. If you would like to completely override vanilla
	 * Behavior, override
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
	public void onStaticPowerBlockHarvested(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {

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
	public ActionResultType onStaticPowerBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return ActionResultType.PASS;
	}

	public void onStaticPowerBlockPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

	}

	public void onStaticPowerBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {

	}

	public void onStaticPowerNeighborChanged(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {

	}

	public void onStaticPowerBlockReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving, boolean newBlock) {

	}

	public void onStaticPowerPostPlacement(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {

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
		return RenderType.getSolid();
	}

	/**
	 * Gets the basic tooltip that is displayed when hovered by the user.
	 * 
	 * @param stack   The item stack hovered by the user.
	 * @param worldIn The world the player was in when hovering the item.
	 * @param tooltip The list of {@link ITextComponent} to add to the tooltip.
	 */
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
	}

	/**
	 * Gets the advanced tooltip that is displayed when hovered by the user and they
	 * are holding shift.
	 * 
	 * @param stack   The item stack hovered by the user.
	 * @param worldIn The world the player was in when hovering the item.
	 * @param tooltip The list of {@link ITextComponent} to add to the tooltip.
	 */
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
	}

	/**
	 * Defines the behavior for when this block is regular right clicked by a
	 * wrench.
	 */
	@Override
	public ActionResultType wrenchBlock(PlayerEntity player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		return ActionResultType.PASS;
	}

	/**
	 * Defines the behavior for when this block is sneak right clicked by a wrench.
	 */
	@Override
	public ActionResultType sneakWrenchBlock(PlayerEntity player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		return ActionResultType.PASS;
	}

	@Override
	public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		// Raise the inheritor's method.
		onStaticPowerBlockHarvested(world, player, pos, state, te, stack);

		// Super call.
		super.harvestBlock(world, player, pos, state, te, stack);
	}

	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		// Raise the inheritor's method.
		onStaticPowerBlockReplaced(state, world, pos, newState, isMoving, state.getBlock() != newState.getBlock());

		spawnDrops(state, world, pos);

		if (state.getBlock() != newState.getBlock()) {
			// Raise the tile entity's broken method.
			if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBase) {
				((TileEntityBase) world.getTileEntity(pos)).onBlockBroken(state, newState, isMoving);
			}

			// Only call the super if the blocks are not equal.
			super.onReplaced(state, world, pos, newState, isMoving);
		} else {
			// Raise the tile entity's changed method.
			if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBase) {
				((TileEntityBase) world.getTileEntity(pos)).onBlockReplaced(state, newState, isMoving);
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
		// Return the super call if needed.
		@SuppressWarnings("deprecation")
		ActionResultType superResult = super.onBlockActivated(state, world, pos, player, hand, hit);
		if (superResult != ActionResultType.PASS) {
			return superResult;
		}

		// Raise the tile entity's activated method and return here if it does not
		// pass.
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBase) {
			ActionResultType teResult = ((TileEntityBase) world.getTileEntity(pos)).onBlockActivated(state, player, hand, hit);
			if (teResult != ActionResultType.PASS) {
				return teResult;
			}
		}

		// Stop here if the item is a wrench. The wrench methods will handle that.
		if (!player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() instanceof StaticWrench) {
			return ActionResultType.PASS;
		}

		// Pass through to inheritors.
		return onStaticPowerBlockActivated(state, world, pos, player, hand, hit);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBase) {
			((TileEntityBase) world.getTileEntity(pos)).onPlaced(state, placer, stack);
		}
		onStaticPowerBlockPlaced(world, pos, state, placer, stack);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		super.onBlockClicked(state, world, pos, player);
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBase) {
			((TileEntityBase) world.getTileEntity(pos)).onBlockLeftClicked(state, player);
		}
		onStaticPowerBlockClicked(state, world, pos, player);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		super.updatePostPlacement(state, dir, facingState, world, pos, facingPos);
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBase) {
			((TileEntityBase) world.getTileEntity(pos)).updatePostPlacement(state, dir, facingState, facingPos);
		}
		onStaticPowerPostPlacement(state, dir, facingState, world, pos, facingPos);
		return state;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, world, pos, block, fromPos, isMoving);
		super.onNeighborChange(state, world, pos, fromPos);
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBase) {
			((TileEntityBase) world.getTileEntity(pos)).onNeighborChanged(state, fromPos);
		}
		onStaticPowerNeighborChanged(state, world, pos, fromPos);
	}

	@Deprecated
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		// Call the super.
		List<ItemStack> superDrops = super.getDrops(state, builder);

		// Add all the items that are currently in an inventory.
		BlockPos pos = builder.get(LootParameters.POSITION);
		if (pos != null) {
			TileEntityBase baseTe = builder.getWorld().getTileEntity(pos) instanceof TileEntityBase ? (TileEntityBase) builder.getWorld().getTileEntity(pos) : null;
			if (baseTe != null) {
				for (InventoryComponent comp : baseTe.getComponents(InventoryComponent.class)) {
					for (int i = 0; i < comp.getSlots(); i++) {
						ItemStack extracted = comp.extractItem(i, Integer.MAX_VALUE, false);
						if (!extracted.isEmpty()) {
							superDrops.add(extracted);
						}
					}
				}
			}
		}

		return superDrops;
	}
}
