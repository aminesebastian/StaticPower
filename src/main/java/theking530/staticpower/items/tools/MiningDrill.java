package theking530.staticpower.items.tools;

import java.lang.reflect.Field;
import java.util.Set;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.items.StaticPowerItem;

public class MiningDrill extends StaticPowerItem {
	private static final String IS_MINING_TAG = "is_mining";
	private static final String MINING_START_TIME_TAG = "mining_start_time";
	private static final String MINING_POS_TAG = "mining_position";
	private static final String MINING_SIDE_TAG = "mining_side";
	private static Field isDestroyingBlockField;
	private static Field remainingDurabilityField;

	private static final Set<Block> EFFECTIVE_ON = ImmutableSet.of(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL,
			Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK,
			Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE,
			Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB,
			Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB,
			Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB,
			Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB,
			Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX,
			Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX,
			Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.PISTON_HEAD, Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND,
			Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER,
			Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER,
			Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER,
			Blocks.SOUL_SOIL);
	protected final float efficiency;
	private final float attackDamage;
	private final Multimap<Attribute, AttributeModifier> toolAttributes;

	public MiningDrill(String name, float attackDamageIn, float attackSpeedIn) {
		super(name, new Item.Properties().addToolType(ToolType.PICKAXE, ItemTier.DIAMOND.getHarvestLevel()).addToolType(ToolType.SHOVEL, ItemTier.DIAMOND.getHarvestLevel()));
		this.efficiency = 5.0f;
		this.attackDamage = attackDamageIn + 2.0f;
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double) this.attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double) attackSpeedIn, AttributeModifier.Operation.ADDITION));
		this.toolAttributes = builder.build();
	}

	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (getToolTypes(stack).stream().anyMatch(e -> state.isToolEffective(e)))
			return 1.0f; // efficiency;
		return MiningDrill.EFFECTIVE_ON.contains(state.getBlock()) ? this.efficiency : 1.0F;
	}

	/**
	 * Current implementations of this method in child classes do not use the entry
	 * argument beside ev. They just raise the damage on the stack.
	 */
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damageItem(2, attacker, (entity) -> {
			entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
		});
		return true;
	}

	public void handleMiningProgress(PlayerEntity player, ItemStack stack, World worldIn, BlockState state, BlockPos pos, Direction facing) {
		if (!isAlreadyMining(stack)) {
			setMiningStarted(stack, player.ticksExisted, pos, facing);
		}
		System.out.println(this.isAlreadyMining(stack));
		if (!worldIn.isRemote && player instanceof ServerPlayerEntity) {
			if (isAlreadyMining(stack)) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
				serverPlayer.interactionManager.tryHarvestBlock(pos);
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
//		if (!worldIn.isRemote && entityIn instanceof ServerPlayerEntity) {
//			if (isAlreadyMining(stack)) {
//				ServerPlayerEntity player = (ServerPlayerEntity) entityIn;
//				int miningDelta = entityIn.ticksExisted - getMiningStartTime(stack);
//				BlockPos pos = getMiningPosition(stack);
//				player.interactionManager.tryHarvestBlock(pos);
//				Direction face = getMiningFace(stack);
//				BlockState state = worldIn.getBlockState(pos);
//				float hardness = state.getPlayerRelativeBlockHardness(player, worldIn, pos);
//				float progress = hardness * (float) (miningDelta + 1);
//				int scaledProgress = (int) (progress * 10.0F);
//
//				for (int i = -5; i < 5; i++) {
//					BlockPos offsetPos = pos.offset(Direction.EAST, i);
//					worldIn.sendBlockBreakProgress(entityIn.getEntityId(), offsetPos, scaledProgress);
//				}
//
//				System.out.println("Progress: " + progress + "   Scaled Progress: " + scaledProgress + "   Hardness: " + 1.0f / hardness);
//			}
//		}
	}

	private void setMiningStarted(ItemStack stack, int startGameTime, BlockPos miningBlock, Direction miningFace) {
		ensureNBTExists(stack);
		stack.getTag().putInt(MINING_START_TIME_TAG, startGameTime);
		stack.getTag().putBoolean(IS_MINING_TAG, true);
		stack.getTag().putLong(MINING_POS_TAG, miningBlock.toLong());
		stack.getTag().putInt(MINING_SIDE_TAG, miningFace.ordinal());
	}

	private void setMiningCompleted(ItemStack stack) {
		stack.getTag().putBoolean(IS_MINING_TAG, false);
	}

	private int getMiningStartTime(ItemStack stack) {
		ensureNBTExists(stack);
		return stack.getTag().getInt(MINING_START_TIME_TAG);
	}

	private BlockPos getMiningPosition(ItemStack stack) {
		ensureNBTExists(stack);
		return BlockPos.fromLong(stack.getTag().getLong(MINING_POS_TAG));
	}

	private Direction getMiningFace(ItemStack stack) {
		ensureNBTExists(stack);
		return Direction.values()[(stack.getTag().getInt(MINING_SIDE_TAG))];
	}

	private boolean isAlreadyMining(ItemStack stack) {
		ensureNBTExists(stack);
		return stack.getTag().getBoolean(IS_MINING_TAG);
	}

	private void ensureNBTExists(ItemStack stack) {
		if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
		}
		if (!stack.getTag().contains(MINING_START_TIME_TAG)) {
			stack.getTag().putInt(MINING_START_TIME_TAG, 0);
		}
		if (!stack.getTag().contains(IS_MINING_TAG)) {
			stack.getTag().putBoolean(IS_MINING_TAG, false);
		}
	}

	/**
	 * Called when a Block is destroyed using this Item. Return true to trigger the
	 * "Use Item" statistic.
	 */
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
}