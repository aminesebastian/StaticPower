package theking530.staticpower.items.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import theking530.api.multipartitem.AbstractMultiPartItem;
import theking530.staticpower.StaticPower;

public abstract class AbstractMultiHarvestTool extends AbstractMultiPartItem {
	protected float attackDamage;
	protected Multimap<Attribute, AttributeModifier> toolAttributes;
	protected List<TagKey<Block>> mineableTags;

	public AbstractMultiHarvestTool(Item.Properties properties, String name, float attackDamageIn, float attackSpeedIn, List<TagKey<Block>> tags) {
		super(name, properties.stacksTo(1));
		this.attackDamage = attackDamageIn + 2.0f;
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double) this.attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double) attackSpeedIn, AttributeModifier.Operation.ADDITION));
		this.toolAttributes = builder.build();
		mineableTags = tags;
	}

	/**
	 * How many blocks to the left and right of the targeted block should this tool
	 * mine?
	 * 
	 * @param stack
	 * @return
	 */
	public abstract int getHorizontalRadius(ItemStack stack);

	/**
	 * How many blocks to the top and bottom of the targeted block should this tool
	 * mine?
	 * 
	 * @param stack
	 * @return
	 */
	public abstract int getVerticalRadius(ItemStack stack);

	/**
	 * How many blocks deep should this tool mine?
	 * 
	 * @param stack
	 * @return
	 */
	public int getDepth(ItemStack stack) {
		return 1;
	}

	protected abstract float getEfficiency(ItemStack itemstack);

	public abstract Tier getMiningTier(ItemStack stack);

	public boolean isReadyToMine(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
		if (player == null) {
			return false;
		}

		ItemStack thisStack = player.getMainHandItem();
		if (thisStack.isEmpty()) {
			return false;
		}

		if (!isReadyToMine(thisStack)) {
			return false;
		}
		return true;
	}

	/**
	 * This method is only raised on the server.
	 */
	protected void onStartingBlockMining(ItemStack stack, List<BlockPos> blocksMined, Player player) {

	}

	/**
	 * This method is only raised on the server.
	 */
	protected void onAllBlocksMined(ItemStack stack, List<BlockPos> blocksMined, Player player) {

	}

	public List<BlockPos> getMineableExtraBlocks(ItemStack itemstack, BlockPos pos, Player player) {
		// Capture the list of all the blocks to mine.
		List<BlockPos> minableBlocks = new ArrayList<BlockPos>();

		// If we can't mine, just return the single block.
		if (!isReadyToMine(itemstack)) {
			minableBlocks.add(pos);
			return minableBlocks;
		}

		// If we can't harvest the targeted block at full speed, don't apply the bonus
		// radius.
		if (!isCorrectToolForDrops(itemstack, player.level.getBlockState(pos))) {
			minableBlocks.add(pos);
			return minableBlocks;
		}

		// Capture the harvest directions.
		MultiBlockHarvestDirections harvestDirections = getHarvestDirections(itemstack, pos, player);

		// If the harvest directions are not valid, do nothing.
		if (!harvestDirections.isValid() || !isReadyToMine(itemstack)) {
			return Collections.emptyList();
		}

		// If sneaking, only mine the targeted block. Otherwise get all the blocks in
		// the width and height.
		if (player.isShiftKeyDown()) {
			minableBlocks.add(pos);
		} else {
			for (int x = -harvestDirections.getHorizontalRadius(); x <= harvestDirections.getHorizontalRadius(); x++) {
				for (int y = -harvestDirections.getVerticalRadius(); y <= harvestDirections.getVerticalRadius(); y++) {
					for (int z = 0; z < harvestDirections.getDepth(); z++) {
						// Offset in both directions.
						BlockPos offsetPos = pos.relative(harvestDirections.getHeightDirection(), y);
						offsetPos = offsetPos.relative(harvestDirections.getWidthDirection(), x);
						offsetPos = offsetPos.relative(harvestDirections.getDeptDDirection(), z);

						try {
							// Get the state.
							BlockState state = player.getCommandSenderWorld().getBlockState(offsetPos);

							// Check the hardness.
							if (state.getDestroyProgress(player, player.getCommandSenderWorld(), pos) <= 0.0f) {
								continue;
							}

							// Check if we can harvest this block.
							if (canAttackBlock(state, player.getCommandSenderWorld(), offsetPos, player)) {
								minableBlocks.add(offsetPos);
							}
						} catch (Exception e) {
							StaticPower.LOGGER.warn(String.format("Unable to mine block at position: %1$s.", offsetPos.toString()), e);
						}
					}
				}
			}
		}

		// Return the list of mineable blocks.
		return minableBlocks;
	}

	public MultiBlockHarvestDirections getHarvestDirections(ItemStack itemstack, BlockPos pos, Player player) {
		return new MultiBlockHarvestDirections(getHorizontalRadius(itemstack), getVerticalRadius(itemstack), getDepth(itemstack), itemstack, pos, player);
	}

	protected boolean breakAllMultiHarvestBlocks(ItemStack itemstack, BlockPos pos, Player player) {
		// If we can't mine, do nothing.
		if (!isReadyToMine(itemstack)) {
			return false;
		}

		// Quick check to see if we're on the client. If we are, then just perform the
		// default behaviour.
		if (!(player instanceof ServerPlayer)) {
			return super.onBlockStartBreak(itemstack, pos, player);
		}

		// Capture the list of all the blocks to mine.
		List<BlockPos> minableBlocks = getMineableExtraBlocks(itemstack, pos, player);

		// Harvest all the additional blocks if any were captured.
		if (minableBlocks.size() > 0) {
			onStartingBlockMining(itemstack, minableBlocks, player);

			for (BlockPos extraPos : minableBlocks) {
				breakAndHarvestBlock(extraPos, (ServerPlayer) player);
			}

			// Raise on the harvested method.
			onAllBlocksMined(itemstack, minableBlocks, player);
			return true;
		} else {
			return false;
		}
	}

	protected boolean breakAndHarvestBlock(BlockPos pos, ServerPlayer player) {
		BlockState blockstate = player.getCommandSenderWorld().getBlockState(pos);
		int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(player.getCommandSenderWorld(), player.gameMode.getGameModeForPlayer(), player, pos);
		if (exp == -1) {
			return false;
		} else {
			BlockEntity tileentity = player.getCommandSenderWorld().getBlockEntity(pos);
			Block block = blockstate.getBlock();
			if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !player.canUseGameMasterBlocks()) {
				player.getCommandSenderWorld().sendBlockUpdated(pos, blockstate, blockstate, 3);
				return false;
			} else if (player.blockActionRestricted(player.getCommandSenderWorld(), pos, player.gameMode.getGameModeForPlayer())) {
				return false;
			} else if (player.isCreative()) {
				if (breakBlock(blockstate, block, pos, player, tileentity, player.getMainHandItem(), exp, true)) {
					harvestBlockDrops(blockstate, block, pos, player, tileentity, player.getMainHandItem(), exp, true);
				}
				return true;
			} else {
				if (breakBlock(blockstate, block, pos, player, tileentity, player.getMainHandItem(), exp, false)) {
					harvestBlockDrops(blockstate, block, pos, player, tileentity, player.getMainHandItem(), exp, false);
				}
				return true;
			}
		}
	}

	protected boolean breakBlock(BlockState state, Block block, BlockPos pos, ServerPlayer player, BlockEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {
		// Indicate to the held item that a block was destroyed using it.
		heldItem.mineBlock(player.getCommandSenderWorld(), state, pos, player);

		// If we are in survival mode, lets see if the item mining was destroyed.
		if (!isCreative) {
			// Get a copy of the held item to see if the real heldItem broke.
			ItemStack heldItemCopy = heldItem.copy();

			// If the held item is now empty but the copy is not, that means the held item
			// was made empty and has therefore broken.
			if (heldItem.isEmpty() && !heldItemCopy.isEmpty()) {
				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, heldItemCopy, InteractionHand.MAIN_HAND);
			}
		}

		// Check if we can harvest.
		boolean canHarvestWithDrops = state.canHarvestBlock(player.getCommandSenderWorld(), pos, player);

		// Remove the block.
		boolean removed = player.getCommandSenderWorld().removeBlock(pos, canHarvestWithDrops);
		if (removed) {
			// Indicate that the player is destroying the block.
			state.getBlock().destroy(player.getCommandSenderWorld(), pos, state);
		}

		// Return if we broke the block and should drop the contents.
		return removed && canHarvestWithDrops;
	}

	protected void harvestBlockDrops(BlockState state, Block block, BlockPos pos, ServerPlayer player, BlockEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {
		block.playerDestroy(player.getCommandSenderWorld(), player, pos, state, tileEntity, heldItem);
		if (experience > 0) {
			state.getBlock().popExperience((ServerLevel) player.getCommandSenderWorld(), pos, experience);
		}
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return isCorrectToolForDrops(stack, state) ? getEfficiency(stack) : isReadyToMine(stack) ? 1.0f : 0.0f;
	}

	/**
	 * Current implementations of this method in child classes do not use the entry
	 * argument beside ev. They just raise the damage on the stack.
	 */
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
		return breakAllMultiHarvestBlocks(itemstack, pos, player);
	}

	/**
	 * Gets a map of item attribute modifiers, used by ItemSword to increase hit
	 * damage.
	 */
	@SuppressWarnings("deprecation")
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.MAINHAND ? this.toolAttributes : super.getDefaultAttributeModifiers(equipmentSlot);
	}

	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		// Check to make sure one of the tags contains this.
		boolean mineable = false;
		for (TagKey<Block> tag : mineableTags) {
			if (state.is(tag)) {
				mineable = true;
				break;
			}
		}

		if (!mineable) {
			return false;
		}

		// Check the tier.
		return net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(getMiningTier(stack), state);
	}

	protected class MultiBlockHarvestDirections {
		private final Direction widthDirection;
		private final Direction heightDirection;
		private final Direction depthDirection;
		private final int width;
		private final int height;
		private final int depth;
		private final boolean isValid;

		public Direction getWidthDirection() {
			return widthDirection;
		}

		public Direction getHeightDirection() {
			return heightDirection;
		}

		public Direction getDeptDDirection() {
			return depthDirection;
		}

		public boolean isValid() {
			return isValid;
		}

		public int getHorizontalRadius() {
			return width;
		}

		public int getVerticalRadius() {
			return height;
		}

		public int getDepth() {
			return depth;
		}

		private MultiBlockHarvestDirections(int width, int height, int depth, ItemStack itemstack, BlockPos pos, Player player) {
			this.width = width;
			this.height = height;
			this.depth = depth;

			BlockHitResult traceResult = Item.getPlayerPOVHitResult(player.getCommandSenderWorld(), player, ClipContext.Fluid.ANY);
			if (traceResult == null || traceResult.getType() != HitResult.Type.BLOCK) {
				widthDirection = null;
				heightDirection = null;
				depthDirection = null;
				isValid = false;
				return;
			}

			isValid = true;
			depthDirection = traceResult.getDirection().getOpposite();
			if (traceResult.getDirection().getAxis() == Axis.X) {
				heightDirection = Direction.UP;
				widthDirection = Direction.NORTH;
			} else if (traceResult.getDirection().getAxis() == Axis.Z) {
				heightDirection = Direction.UP;
				widthDirection = Direction.EAST;
			} else {
				heightDirection = player.getMotionDirection();
				widthDirection = heightDirection.getAxis() == Axis.X ? Direction.NORTH : Direction.EAST;
			}
		}
	}
}