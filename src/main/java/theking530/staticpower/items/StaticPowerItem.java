package theking530.staticpower.items;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.ITooltipProvider;
import theking530.staticpower.StaticPower;

/**
 * Base class for most static power items.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerItem extends Item implements ITooltipProvider {
	protected static final Random RANDOM = new Random();

	/**
	 * Base constructor for a static power item. Uses the default item properties
	 * and adds the item to the static power default item group.
	 * 
	 * @param name The registry name for this item sans namespace.
	 */
	public StaticPowerItem() {
		this(new Item.Properties().tab(StaticPower.CREATIVE_TAB));
	}

	/**
	 * Constructor for a static power item that takes a custom properties object.
	 * 
	 * @param name       The registry name for this item sans namespace.
	 * @param properties The properties for this item (the item group is set within
	 *                   this method, no need to set it externally).
	 */
	public StaticPowerItem(Item.Properties properties) {
		super(properties.tab(StaticPower.CREATIVE_TAB));
	}

	@Override
	@Nullable
	public CompoundTag getShareTag(ItemStack stack) {
		// Make the super call.
		CompoundTag output = super.getShareTag(stack);

		// See if we have anything to sync.
		CompoundTag syncTag = getStaticPowerSyncTag(stack);

		// If we do, add it.
		if (syncTag != null) {
			// If the tag is null, create one.
			if (output == null) {
				output = stack.getOrCreateTag();
			}
			output.put("sync_tag", syncTag);
		}

		// Return the combined NBT.
		return output;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
		// Read the input nbt.
		super.readShareTag(stack, nbt);

		// If there was an input and it contains a sync tag, use it then remove it.
		if (nbt != null && nbt.contains("sync_tag")) {
			CompoundTag syncTag = nbt.getCompound("sync_tag");
			processStaticPowerSyncTag(stack, syncTag);
		}
	}

	protected CompoundTag getStaticPowerSyncTag(ItemStack stack) {
		return (CompoundTag) stack.serializeNBT().get("ForgeCaps");
	}

	protected void processStaticPowerSyncTag(ItemStack stack, @Nullable CompoundTag nbt) {
		CompoundTag tag = stack.getTag();
		tag.put("ForgeCaps", nbt);
		stack.deserializeNBT(tag);
	}

	/**
	 * Override of onItemRickClick that calls expanded version
	 * {@link #onStaticPowerItemRightClicked(World, PlayerEntity, Hand, ItemStack)}
	 * for easier implementation.
	 */
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		return onStaticPowerItemRightClicked(worldIn, playerIn, handIn, playerIn.getItemInHand(handIn));
	}

	/**
	 * Override of onItemUse that calls expanded version
	 * {@link #onStaticPowerItemUsedOnBlock(ItemUseContext, World, BlockPos, Direction, PlayerEntity, ItemStack)}
	 * for easier implementation.
	 */
	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		return onStaticPowerItemUsedOnBlock(context, context.getLevel(), context.getClickedPos(), context.getClickedFace(), context.getPlayer(), context.getItemInHand());
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		return onPreStaticPowerItemUsedOnBlock(context, context.getLevel(), context.getClickedPos(), context.getClickedFace(), context.getPlayer(), stack);
	}

	/**
	 * Expanded version of onItemRightClick.
	 * 
	 * @param world  The world the item was right clicked in.
	 * @param player The player holding the item.
	 * @param hand   The hand the item was held in.
	 * @param item   The {@link ItemStack}.
	 * @return The result of the action.
	 */
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player, InteractionHand hand, ItemStack item) {
		if (this.isEdible()) {
			ItemStack itemstack = player.getItemInHand(hand);
			if (player.canEat(this.getFoodProperties(item, player).canAlwaysEat())) {
				player.startUsingItem(hand);
				return InteractionResultHolder.consume(itemstack);
			} else {
				return InteractionResultHolder.fail(itemstack);
			}
		} else {
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}
	}

	/**
	 * Expanded version of onItemUse.
	 * 
	 * @param context The context of the item's use.
	 * @param world   The world the item was used in.
	 * @param pos     The position that the item was used on.
	 * @param face    The face of the block that the item was used on.
	 * @param player  The player that used the item.
	 * @param item    The item stack that was used.
	 * @return The result of the action (SUCCESS, PASS, FAIL, CONSUME).
	 */
	protected InteractionResult onStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		return InteractionResult.PASS;
	}

	/**
	 * Expanded version of onItemFirstUse.
	 * 
	 * @param context
	 * @param world
	 * @param pos
	 * @param face
	 * @param player
	 * @param item
	 * @return
	 */
	protected InteractionResult onPreStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		return InteractionResult.PASS;
	}

	/**
	 * Gets the tooltip that is displayed when hovered by the user.
	 * 
	 * @param stack             The item stack hovered by the user.
	 * @param worldIn           The world the player was in when hovering the item.
	 * @param tooltip           The list of {@link ITextComponent} to add to the
	 *                          tooltip.
	 * @param isShowingAdvanced True if advanced tooltips are requested.
	 */
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13.0F - (float) stack.getDamageValue() * 13.0F / (float) stack.getMaxDamage());
	}

	@Override
	public int getBarColor(ItemStack stack) {
		float f = Math.max(0.0F, ((float) stack.getMaxDamage() - (float) stack.getDamageValue()) / (float) stack.getMaxDamage());
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
	}

	@SuppressWarnings("deprecation")
	public boolean removeEnchantment(ItemStack stack, Enchantment enchantment) {
		stack.getOrCreateTag();
		if (!stack.getTag().contains("Enchantments", 9)) {
			stack.getTag().put("Enchantments", new ListTag());
		}

		ListTag listnbt = stack.getTag().getList("Enchantments", 10);
		boolean removedFlag = false;
		for (int i = listnbt.size() - 1; i >= 0; i--) {
			CompoundTag enchantmentNbt = (CompoundTag) listnbt.get(i);
			if (enchantmentNbt.getString("id").equals(Registry.ENCHANTMENT.getKey(enchantment).toString())) {
				listnbt.remove(i);
				removedFlag = true;
			}
		}
		return removedFlag;
	}
}
