package theking530.staticpower.items.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
import net.minecraft.item.ItemTier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.items.StaticPowerItem;

public abstract class AbstractMultiHarvestTool extends StaticPowerItem {
	protected final Set<Block> effectiveOn;
	protected final float efficiency;
	protected final float attackDamage;
	protected final Multimap<Attribute, AttributeModifier> toolAttributes;

	public AbstractMultiHarvestTool(String name, float attackDamageIn, float attackSpeedIn) {
		super(name, new Item.Properties().addToolType(ToolType.PICKAXE, ItemTier.DIAMOND.getHarvestLevel()).addToolType(ToolType.SHOVEL, ItemTier.DIAMOND.getHarvestLevel()));
		this.effectiveOn = getEffectiveBlocks();
		this.efficiency = 5.0f;
		this.attackDamage = attackDamageIn + 2.0f;
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double) this.attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double) attackSpeedIn, AttributeModifier.Operation.ADDITION));
		this.toolAttributes = builder.build();

	}

	public abstract Set<Block> getEffectiveBlocks();

	public abstract int getWidth();

	public abstract int getHeight();

	public boolean isEffectiveOnBlock(ItemStack stack, BlockState state) {
		if (getToolTypes(stack).stream().anyMatch(e -> state.isToolEffective(e))) {
			return true;
		}
		return effectiveOn.contains(state.getBlock()) ? true : false;
	}

	public List<BlockPos> getMineableExtraBlocks(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		// Capture the harvest directions.
		MultiBlockHarvestDirections harvestDirections = getHarvestDirections(itemstack, pos, player);

		// If the harvest directions are not valid, do nothing.
		if (!harvestDirections.isValid()) {
			return Collections.emptyList();
		}

		// Capture the list of all the blocks to mind.
		List<BlockPos> minableBlocks = new ArrayList<BlockPos>();
		for (int x = -getWidth(); x <= getWidth(); x++) {
			for (int y = -getHeight(); y <= getHeight(); y++) {
				// Offset in both directions.
				BlockPos offsetPos = pos.offset(harvestDirections.getHeightDirection(), y);
				offsetPos = offsetPos.offset(harvestDirections.getWidthDirection(), x);

				// Check if we can harvest this block.
				if (isEffectiveOnBlock(itemstack, player.getEntityWorld().getBlockState(offsetPos))) {
					minableBlocks.add(offsetPos);
				}
			}
		}

		// Return the list of mineable blocks.
		return minableBlocks;
	}

	public MultiBlockHarvestDirections getHarvestDirections(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		return new MultiBlockHarvestDirections(getWidth(), getHeight(), itemstack, pos, player);
	}

	protected boolean breakAllMultiHarvestBlocks(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		// Quick check to see if we're on the client. If we are, then just perform the
		// default behaviour.
		if (!(player instanceof ServerPlayerEntity)) {
			return super.onBlockStartBreak(itemstack, pos, player);
		}

		// Capture the list of all the blocks to mine.
		List<BlockPos> minableBlocks = getMineableExtraBlocks(itemstack, pos, player);

		// Harvest all the additional blocks if any were captured.
		if (minableBlocks.size() > 0) {
			for (BlockPos extraPos : minableBlocks) {
				harvestExtraBlock(extraPos, (ServerPlayerEntity) player);
			}

			return true;
		} else {
			return false;
		}
	}

	protected boolean harvestExtraBlock(BlockPos pos, ServerPlayerEntity player) {
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
			} else {
				if (player.isCreative()) {
					removeExtraBlock(pos, false, player);
					return true;
				} else {
					ItemStack itemstack = player.getHeldItemMainhand();
					ItemStack itemstack1 = itemstack.copy();
					boolean flag1 = blockstate.canHarvestBlock(player.getEntityWorld(), pos, player); // previously player.func_234569_d_(blockstate)
					itemstack.onBlockDestroyed(player.getEntityWorld(), blockstate, pos, player);
					if (itemstack.isEmpty() && !itemstack1.isEmpty())
						net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack1, Hand.MAIN_HAND);
					boolean flag = removeExtraBlock(pos, flag1, player);

					if (flag && flag1) {
						block.harvestBlock(player.getEntityWorld(), player, pos, blockstate, tileentity, itemstack1);
					}

					if (flag && exp > 0)
						blockstate.getBlock().dropXpOnBlockBreak((ServerWorld) player.getEntityWorld(), pos, exp);

					return true;
				}
			}
		}
	}

	protected boolean removeExtraBlock(BlockPos p_180235_1_, boolean canHarvest, ServerPlayerEntity player) {
		BlockState state = player.getEntityWorld().getBlockState(p_180235_1_);
		boolean removed = state.removedByPlayer(player.getEntityWorld(), p_180235_1_, player, canHarvest, player.getEntityWorld().getFluidState(p_180235_1_));
		if (removed) {
			state.getBlock().onPlayerDestroy(player.getEntityWorld(), p_180235_1_, state);
		}
		return removed;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return isEffectiveOnBlock(stack, state) ? this.efficiency : 1.0F;
	}

	/**
	 * Current implementations of this method in child classes do not use the entry
	 * argument beside ev. They just raise the damage on the stack.
	 */
	@Override
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damageItem(2, attacker, (entity) -> {
			entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
		});
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		return breakAllMultiHarvestBlocks(itemstack, pos, player);
	}

	/**
	 * Called when a Block is destroyed using this Item. Return true to trigger the
	 * "Use Item" statistic.
	 */
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
			stack.damageItem(1, entityLiving, (entity) -> {
				entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		}

		return true;
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