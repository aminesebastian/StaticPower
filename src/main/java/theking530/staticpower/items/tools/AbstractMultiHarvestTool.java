package theking530.staticpower.items.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.server.ServerWorld;
import theking530.staticpower.items.StaticPowerItem;

public abstract class AbstractMultiHarvestTool extends StaticPowerItem {
	protected float attackDamage;
	protected Multimap<Attribute, AttributeModifier> toolAttributes;

	public AbstractMultiHarvestTool(Item.Properties properties, String name, float attackDamageIn, float attackSpeedIn) {
		super(name, properties.maxStackSize(1));
		this.attackDamage = attackDamageIn + 2.0f;
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double) this.attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double) attackSpeedIn, AttributeModifier.Operation.ADDITION));
		this.toolAttributes = builder.build();

	}

	public abstract boolean canHarvestAtFullSpeed(ItemStack stack, BlockState state);

	protected abstract boolean canHarvestBlockInternal(ItemStack stack, BlockState state);

	public abstract int getWidth(ItemStack stack);

	public abstract int getHeight(ItemStack stack);

	protected abstract float getEfficiency(ItemStack itemstack);

	public boolean isReadyToMine(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(ItemStack stack, BlockState state) {
		if (!isReadyToMine(stack)) {
			return false;
		}
		return canHarvestBlockInternal(stack, state);
	}

	/**
	 * This method is only raised on the server.
	 */
	protected void onStartingBlockMining(ItemStack stack, List<BlockPos> blocksMined, PlayerEntity player) {

	}

	/**
	 * This method is only raised on the server.
	 */
	protected void onAllBlocksMined(ItemStack stack, List<BlockPos> blocksMined, PlayerEntity player) {

	}

	public List<BlockPos> getMineableExtraBlocks(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		// Capture the harvest directions.
		MultiBlockHarvestDirections harvestDirections = getHarvestDirections(itemstack, pos, player);

		// If the harvest directions are not valid, do nothing.
		if (!harvestDirections.isValid() || !isReadyToMine(itemstack)) {
			return Collections.emptyList();
		}

		// Capture the list of all the blocks to mind.
		List<BlockPos> minableBlocks = new ArrayList<BlockPos>();

		// If sneaking, only mine the targeted block. Otherwise get all the blocks in
		// the width and height.
		if (player.isSneaking()) {
			minableBlocks.add(pos);
		} else {
			for (int x = -getWidth(itemstack); x <= getWidth(itemstack); x++) {
				for (int y = -getHeight(itemstack); y <= getHeight(itemstack); y++) {
					// Offset in both directions.
					BlockPos offsetPos = pos.offset(harvestDirections.getHeightDirection(), y);
					offsetPos = offsetPos.offset(harvestDirections.getWidthDirection(), x);

					// Get the state.
					BlockState state = player.getEntityWorld().getBlockState(offsetPos);

					// Check the hardness.
					if (state.getPlayerRelativeBlockHardness(player, player.getEntityWorld(), pos) <= 0.0f) {
						continue;
					}

					// Check if we can harvest this block.
					if (canHarvestBlock(itemstack, player.getEntityWorld().getBlockState(offsetPos))) {
						minableBlocks.add(offsetPos);
					}
				}
			}
		}

		// Return the list of mineable blocks.
		return minableBlocks;
	}

	public MultiBlockHarvestDirections getHarvestDirections(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		return new MultiBlockHarvestDirections(getWidth(itemstack), getHeight(itemstack), itemstack, pos, player);
	}

	protected boolean breakAllMultiHarvestBlocks(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		// If we can't mine, do nothing.
		if (!isReadyToMine(itemstack)) {
			return false;
		}

		// Quick check to see if we're on the client. If we are, then just perform the
		// default behaviour.
		if (!(player instanceof ServerPlayerEntity)) {
			return super.onBlockStartBreak(itemstack, pos, player);
		}

		// Capture the list of all the blocks to mine.
		List<BlockPos> minableBlocks = getMineableExtraBlocks(itemstack, pos, player);

		// Harvest all the additional blocks if any were captured.
		if (minableBlocks.size() > 0) {
			onStartingBlockMining(itemstack, minableBlocks, player);

			for (BlockPos extraPos : minableBlocks) {
				breakAndHarvestBlock(extraPos, (ServerPlayerEntity) player);
			}

			// Raise on the harvested method.
			onAllBlocksMined(itemstack, minableBlocks, player);
			return true;
		} else {
			return false;
		}
	}

	protected boolean breakAndHarvestBlock(BlockPos pos, ServerPlayerEntity player) {
		BlockState blockstate = player.getEntityWorld().getBlockState(pos);
		int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(player.getEntityWorld(), player.interactionManager.getGameType(), player, pos);
		if (exp == -1) {
			return false;
		} else {
			TileEntity tileentity = player.getEntityWorld().getTileEntity(pos);
			Block block = blockstate.getBlock();
			if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !player.canUseCommandBlock()) {
				player.getEntityWorld().notifyBlockUpdate(pos, blockstate, blockstate, 3);
				return false;
			} else if (player.blockActionRestricted(player.getEntityWorld(), pos, player.interactionManager.getGameType())) {
				return false;
			} else if (player.isCreative()) {
				if (breakBlock(blockstate, block, pos, player, tileentity, player.getHeldItemMainhand(), exp, true)) {
					harvestBlockDrops(blockstate, block, pos, player, tileentity, player.getHeldItemMainhand(), exp, true);
				}
				return true;
			} else {
				if (breakBlock(blockstate, block, pos, player, tileentity, player.getHeldItemMainhand(), exp, false)) {
					harvestBlockDrops(blockstate, block, pos, player, tileentity, player.getHeldItemMainhand(), exp, false);
				}
				return true;
			}
		}
	}

	protected boolean breakBlock(BlockState state, Block block, BlockPos pos, ServerPlayerEntity player, TileEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {
		// Indicate to the held item that a block was destroyed using it.
		heldItem.onBlockDestroyed(player.getEntityWorld(), state, pos, player);

		// If we are in survival mode, lets see if the item mining was destroyed.
		if (!isCreative) {
			// Get a copy of the held item to see if the real heldItem broke.
			ItemStack heldItemCopy = heldItem.copy();

			// If the held item is now empty but the copy is not, that means the held item
			// was made empty and has therefore broken.
			if (heldItem.isEmpty() && !heldItemCopy.isEmpty()) {
				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, heldItemCopy, Hand.MAIN_HAND);
			}
		}

		// Check if we can harvest.
		boolean canHarvestWithDrops = state.canHarvestBlock(player.getEntityWorld(), pos, player);

		// Remove the block.
		boolean removed = state.removedByPlayer(player.getEntityWorld(), pos, player, canHarvestWithDrops, player.getEntityWorld().getFluidState(pos));
		if (removed) {
			// Indicate that the player is destroying the block.
			state.getBlock().onPlayerDestroy(player.getEntityWorld(), pos, state);
		}

		// Return if we broke the block and should drop the contents.
		return removed && canHarvestWithDrops;
	}

	protected void harvestBlockDrops(BlockState state, Block block, BlockPos pos, ServerPlayerEntity player, TileEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {
		block.harvestBlock(player.getEntityWorld(), player, pos, state, tileEntity, heldItem);
		if (experience > 0) {
			state.getBlock().dropXpOnBlockBreak((ServerWorld) player.getEntityWorld(), pos, experience);
		}
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return canHarvestAtFullSpeed(stack, state) ? this.getEfficiency(stack) : isReadyToMine(stack) ? 1.0f : 0.0f;
	}

	/**
	 * Current implementations of this method in child classes do not use the entry
	 * argument beside ev. They just raise the damage on the stack.
	 */
	@Override
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		return breakAllMultiHarvestBlocks(itemstack, pos, player);
	}

	/**
	 * Gets a map of item attribute modifiers, used by ItemSword to increase hit
	 * damage.
	 */
	@SuppressWarnings("deprecation")
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
		return equipmentSlot == EquipmentSlotType.MAINHAND ? this.toolAttributes : super.getAttributeModifiers(equipmentSlot);
	}

	public float getAttackDamage() {
		return this.attackDamage;
	}

	protected class MultiBlockHarvestDirections {
		private final Direction widthDirection;
		private final Direction heightDirection;
		private final int width;
		private final int height;
		private final boolean isValid;

		public Direction getWidthDirection() {
			return widthDirection;
		}

		public Direction getHeightDirection() {
			return heightDirection;
		}

		public boolean isValid() {
			return isValid;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		private MultiBlockHarvestDirections(int width, int height, ItemStack itemstack, BlockPos pos, PlayerEntity player) {
			this.width = width;
			this.height = height;

			BlockRayTraceResult traceResult = Item.rayTrace(player.getEntityWorld(), player, RayTraceContext.FluidMode.ANY);
			if (traceResult == null || traceResult.getType() != RayTraceResult.Type.BLOCK) {
				widthDirection = null;
				heightDirection = null;
				isValid = false;
				return;
			}

			isValid = true;
			if (traceResult.getFace().getAxis() == Axis.X) {
				heightDirection = Direction.UP;
				widthDirection = Direction.NORTH;
			} else if (traceResult.getFace().getAxis() == Axis.Z) {
				heightDirection = Direction.UP;
				widthDirection = Direction.EAST;
			} else {
				heightDirection = player.getAdjustedHorizontalFacing();
				widthDirection = heightDirection.getAxis() == Axis.X ? Direction.NORTH : Direction.EAST;
			}
		}
	}
}